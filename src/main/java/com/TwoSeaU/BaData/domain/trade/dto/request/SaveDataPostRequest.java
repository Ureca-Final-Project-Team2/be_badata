package com.TwoSeaU.BaData.domain.trade.dto.request;

import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveDataPostRequest {
    @NotNull(message = "제목은 필수 작성입니다.")
    private String title;

    @NotNull(message = "통신사는 필수 선택입니다.")
    private MobileCarrier mobileCarrier;

    @NotNull(message = "유효기간은 필수 입력입니다.")
    private LocalDate deadLine;

    @NotNull(message = "데이터 용량은 필수 입력입니다.")
    private Integer capacity;

    @NotNull(message = "가격은 필수 작성입니다.")
    @PositiveOrZero(message = "가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "상세 설명은 필수 작성입니다.")
    private String comment;
}
