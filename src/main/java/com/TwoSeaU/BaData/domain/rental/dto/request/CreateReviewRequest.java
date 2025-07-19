package com.TwoSeaU.BaData.domain.rental.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewRequest {

    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;

    private List<Long> quickReplyIds;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String comment;

    @NotNull(message = "별점은 필수입니다.")
    @Min(value = 1, message = "별점은 최소 1점이어야 합니다.")
    @Max(value = 5, message = "별점은 최대 5점이어야 합니다.")
    private Integer rating;

    private MultipartFile file;

}
