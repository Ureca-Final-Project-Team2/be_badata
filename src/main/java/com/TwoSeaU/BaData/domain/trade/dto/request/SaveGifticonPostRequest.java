package com.TwoSeaU.BaData.domain.trade.dto.request;

import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveGifticonPostRequest {
    private String title;
    private String category;
    private String partner;
    private String couponNumber;
    private LocalDateTime deadLine;
    private LocalDateTime issueDate;
    private Integer price;
    private String comment;
    private MultipartFile file;
}
