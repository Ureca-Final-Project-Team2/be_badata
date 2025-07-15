package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.CreatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetValidatePaymentResponse;
import com.TwoSeaU.BaData.domain.trade.service.PaymentService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createOrder(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(paymentService.processPaymentBefore(postId, user.getUsername())));
    }

    @PostMapping("/order/payment/{impUid}/{postId}")
    public ResponseEntity<ApiResponse<GetValidatePaymentResponse>> validateIamport(@PathVariable String impUid, @PathVariable Long postId, @AuthenticationPrincipal User user) throws IamportResponseException, IOException {
        return ResponseEntity.ok().body(ApiResponse.success(paymentService.processPaymentDone(impUid, postId, user.getUsername())));
    }
}
