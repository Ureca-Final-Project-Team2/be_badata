package com.TwoSeaU.BaData.domain.trade.dto.request;

import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveDataPostRequest {
    private String title;
    private MobileCarrier mobileCarrier;
    private LocalDateTime deadLine;
    private Integer capacity;
    private Integer price;
    private String commnent;
    private MultipartFile file;
}
