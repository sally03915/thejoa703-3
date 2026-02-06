// RetweetRequestDto.java
package com.thejoa703.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리트윗 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetweetRequestDto {
    @NotNull
    private Long originalPostId;
}
