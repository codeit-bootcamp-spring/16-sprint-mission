package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.status.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag; // API 그룹 이름과 설명
import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.responses.ApiResponses; // API의 응답 목록 정의
import io.swagger.v3.oas.annotations.Parameter; // PathVariable 등의 파라미터 설명

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 사용자의 온라인 상태 업데이트(PATCH)
    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "UserStatus with userId {userId} not found"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserStatus.class)
                    )
            )
    })
    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public UserStatus updateUserStatusByUserId(
            @Parameter(
                    description = "상태를 변경할 User ID",
                    required = true
            )
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        return userStatusService.updateByUserId(
                userId,
                request.newLastActiveAt());
    }
}