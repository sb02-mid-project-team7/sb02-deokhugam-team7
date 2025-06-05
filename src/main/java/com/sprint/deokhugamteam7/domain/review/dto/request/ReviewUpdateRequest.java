package com.sprint.deokhugamteam7.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewUpdateRequest(
    @NotBlank String content,
    @Min(0) @Max(5)
    int rating
) {

}
