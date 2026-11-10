package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinaryContentCreateRequestDto {

    private byte[] data;
}