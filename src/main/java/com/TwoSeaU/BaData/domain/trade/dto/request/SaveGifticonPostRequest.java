package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveGifticonPostRequest {
    @NotNull(message = "제목은 필수 작성입니다.")
    private String title;

    @NotNull(message = "카테고리는 필수 선택입니다.")
    private String category;

    @NotNull(message = "제휴사는 필수 선택입니다.")
    private String partner;

    @NotNull(message = "쿠폰 번호는 필수입니다.")
    private String couponNumber;

    @NotNull(message = "유효 기간은 필수입니다.")
    private LocalDate deadLine;

    @NotNull(message = "발급일은 필수입니다.")
    private LocalDateTime issueDate;

    @NotNull(message = "가격은 필수 작성입니다.")
    @PositiveOrZero(message = "가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "상세 설명은 필수 작성입니다.")
    private String comment;

    @NotNull(message = "쿠폰 이미지 등록은 필수입니다.")
    private MultipartFile file;
}
