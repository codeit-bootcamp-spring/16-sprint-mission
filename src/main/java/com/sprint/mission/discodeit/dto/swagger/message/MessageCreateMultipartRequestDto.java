package com.sprint.mission.discodeit.dto.swagger.message;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "MessageCreateMultipartRequest")
public class MessageCreateMultipartRequestDto {

    @NotNull
    @Schema(description = "Message 생성 정보")
    private MessageCreateRequestDto messageCreateRequest;

    @ArraySchema(
            arraySchema = @Schema(description = "Message 첨부 파일들"),
            schema = @Schema(type = "string", format = "binary")
    )
    private List<MultipartFile> attachments;
}
