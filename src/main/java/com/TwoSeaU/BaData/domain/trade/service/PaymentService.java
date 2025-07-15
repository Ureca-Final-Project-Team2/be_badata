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

    public CreatePaymentResponse createOrder(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if(post.getIsSold()){
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        Payment payment = Payment.of(user, post, generateMerchantUid(), PayMethod.CARD, new BigDecimal(post.getPrice()));
        paymentRepository.save(payment);

        return CreatePaymentResponse.of(payment.getMerchantUid());
    }

    public GetValidatePaymentResponse validateIamport(String impUid, Long postId, String username) throws IamportResponseException, IOException {
        if(!iamportClient.paymentByImpUid(impUid).getResponse().getStatus().equals("paid")) {
            throw new GeneralException(TradeException.PAYMENT_FAILED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if(post.getIsSold()){
            throw new GeneralException(TradeException.PAYMENT_DUPLICATE);
        }

        Payment payment = paymentRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElseThrow(() -> new GeneralException(TradeException.PAYMENT_NOT_FOUND));

        payment.updatePaymentStatus(PaymentStatus.PAID);
        post.updateIsSold(true);

        return GetValidatePaymentResponse.of(payment.getId());
    }

    private String generateMerchantUid() {
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = today.format(formatter).replace("-", "");

        return formattedDay +'-'+ uniqueString;
    }
}
