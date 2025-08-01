package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetPurchaseGifticonDetailResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetPurchaseGifticonImageResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final GifticonRepository gifticonRepository;

    public GetPurchaseGifticonDetailResponse getGifticonDetail(final Long gifticonId, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        final Payment payment = paymentRepository.findByUserIdAndPostIdAndPaymentStatus(user.getId(), gifticonId, PaymentStatus.PAID)
                .orElseThrow(() -> new GeneralException(TradeException.NOT_PURCHASED_GIFTICON));

        return GetPurchaseGifticonDetailResponse.from(user, gifticon, payment.getUpdatedAt());
    }

    @Transactional
    public GetPurchaseGifticonImageResponse getGifticonImage(final Long gifticonId, final String username){
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        paymentRepository.findByUserIdAndPostIdAndPaymentStatus(user.getId(), gifticonId, PaymentStatus.PAID)
                .orElseThrow(() -> new GeneralException(TradeException.NOT_PURCHASED_GIFTICON));

        if(gifticon.getBarcodeViewTime() == null) {
            gifticon.updateBarcodeViewTime(LocalDateTime.now());
        }

        return GetPurchaseGifticonImageResponse.of(gifticon.getPostImage(), gifticon.getCouponNumber());
    }
}
