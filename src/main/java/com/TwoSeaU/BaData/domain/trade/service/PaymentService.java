package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.request.GetMerchantUidRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.CreatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetValidatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PayMethod;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.CoinSource;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.CoinHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import  com.siot.IamportRestClient.request.PrepareData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CoinHistoryRepository coinHistoryRepository;
    private final IamportClient iamportClient;

    public CreatePaymentResponse createOrder(final Long postId, final String username, final GetMerchantUidRequest getMerchantUidRequest) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findByIdWithLock(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold()) {
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        if (Objects.equals(post.getSeller().getId(), user.getId())) {
            throw new GeneralException(TradeException.SELF_PAYMENT_DENIED);
        }

        if (getMerchantUidRequest.getUseCoin().intValue() > user.getCoin()) {
            throw new GeneralException(TradeException.COIN_NOT_ENOUGH);
        }

        if (getMerchantUidRequest.getUseCoin().stripTrailingZeros().scale() > 0) {
            throw new GeneralException(TradeException.COIN_DECIMAL_NOT_ALLOWED);
        }

        if (post.getPrice().compareTo(getMerchantUidRequest.getUseCoin()) < 0) {
            throw new GeneralException(TradeException.COIN_EXCEED_PRICE);
        }

        final Payment payment = Payment.of(
                user,
                post,
                generateMerchantUid(),
                PayMethod.CARD,
                post.getPrice().subtract(getMerchantUidRequest.getUseCoin()),
                getMerchantUidRequest.getUseCoin()
        );

        PrepareData prepareData = new PrepareData(
                payment.getMerchantUid(),
                post.getPrice().subtract(getMerchantUidRequest.getUseCoin())
        );

        try {
            iamportClient.postPrepare(prepareData);
        }
        catch (IamportResponseException | IOException e){
            throw new GeneralException(TradeException.PAYMENT_FAILED);
        }

        paymentRepository.save(payment);

        return CreatePaymentResponse.of(
                payment.getMerchantUid(),
                post.getPrice().subtract(getMerchantUidRequest.getUseCoin())
        );
    }

    public GetValidatePaymentResponse validateIamport(final String impUid, final Long postId, final String username) throws IamportResponseException, IOException {
        IamportResponse<com.siot.IamportRestClient.response.Payment> portOnePayment = iamportClient.paymentByImpUid(impUid);

        if(!portOnePayment.getResponse().getStatus().equals("paid")) {
            throw new GeneralException(TradeException.PAYMENT_FAILED);
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findByIdWithLock(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold()) {
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        if (Objects.equals(post.getSeller().getId(), user.getId())) {
            throw new GeneralException(TradeException.SELF_PAYMENT_DENIED);
        }

        final Payment payment = paymentRepository.findByMerchantUid(portOnePayment.getResponse().getMerchantUid())
                .orElseThrow(() -> new GeneralException(TradeException.PAYMENT_NOT_FOUND));

        if (payment.getAmount().compareTo(portOnePayment.getResponse().getAmount()) != 0) {
            throw new GeneralException(TradeException.PAYMENT_AMOUNT_MISMATCH);
        }

        if (payment.getUseCoin().intValue() > user.getCoin()) {
            throw new GeneralException(TradeException.COIN_NOT_ENOUGH);
        }

        payment.updatePaymentStatus(PaymentStatus.PAID);

        user.updateUsedCoin(payment.getUseCoin().intValue());

        if (payment.getUseCoin().intValue() > 0){
            coinHistoryRepository.save(CoinHistory.of(
                    user,
                    post instanceof Gifticon ? CoinSource.GIFTICON_PURCHASE : CoinSource.DATA_PURCHASE,
                    payment.getUseCoin().intValue(),
                    user.getCoin()
            ));
        }

        post.updateIsSold(true);

        return GetValidatePaymentResponse.of(payment.getId());
    }

    private String generateMerchantUid() {
        final String uniqueString = UUID.randomUUID().toString().replace("-", "");
        final LocalDateTime today = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String formattedDay = today.format(formatter).replace("-", "");

        return formattedDay + uniqueString;
    }
}
