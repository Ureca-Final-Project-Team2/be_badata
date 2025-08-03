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

         fcmService.testSend(NotificationRequest.forSingleToken("찜한 와이파이 왔어요!"," 지금 아니면 또 놓칠지도 몰라요, 파도처럼 \uD83C\uDF0A", token,null));

         return ResponseEntity.ok(ApiResponse.success("성공"));
    }

}
