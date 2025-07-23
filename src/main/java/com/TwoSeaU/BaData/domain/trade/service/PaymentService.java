package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.CreatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetValidatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PayMethod;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final IamportClient iamportClient;

    public CreatePaymentResponse createOrder(final Long postId, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if(post.getIsSold()){
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        final Payment payment = Payment.of(user, post, generateMerchantUid(), PayMethod.CARD, new BigDecimal(post.getPrice()));
        paymentRepository.save(payment);

        return CreatePaymentResponse.of(payment.getMerchantUid());
    }

    public GetValidatePaymentResponse validateIamport(final String impUid, final Long postId, final String username) throws IamportResponseException, IOException {
        if(!iamportClient.paymentByImpUid(impUid).getResponse().getStatus().equals("paid")) {
            throw new GeneralException(TradeException.PAYMENT_FAILED);
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if(post.getIsSold()){
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        final Payment payment = paymentRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElseThrow(() -> new GeneralException(TradeException.PAYMENT_NOT_FOUND));

        payment.updatePaymentStatus(PaymentStatus.PAID);
        post.updateIsSold(true);

        return GetValidatePaymentResponse.of(payment.getId());
    }

    private String generateMerchantUid() {
        final String uniqueString = UUID.randomUUID().toString().replace("-", "");
        final LocalDateTime today = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String formattedDay = today.format(formatter).replace("-", "");

        return formattedDay +'-'+ uniqueString;
    }
}
