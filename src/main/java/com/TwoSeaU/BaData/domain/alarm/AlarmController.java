package com.TwoSeaU.BaData.domain.alarm;

import com.TwoSeaU.BaData.global.fcm.FCMService;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final FCMService fcmService;

    @GetMapping("/test/alarm")
    public ResponseEntity<ApiResponse<String>> sendAlarm(@RequestParam("token") String token) {

         fcmService.send(NotificationRequest.of("테스트 타이틀","테스트 컨텐츠", token));

         return ResponseEntity.ok(ApiResponse.success("성공"));
    }

}
