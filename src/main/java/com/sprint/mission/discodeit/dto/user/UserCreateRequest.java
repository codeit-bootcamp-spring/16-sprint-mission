package com.sprint.mission.discodeit.dto.user;

// @Nullable가지고 있는 패키지 중에 이걸 선택합니다
// Spring Boot 3.x: import org.springframework.lang.Nullable;
// Spring Boot 4.x: import org.jspecify.annotations.Nullable;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

// @Getter // Lombok의 @Getter는 클래스, 열거형 또는 필드 타입에서만 제공됩니다.
public record UserCreateRequest(
        @NotBlank
        @Username
        // @Size(min = 2, max = 32) // @Username으로 사이즈에 제약을 주었기 때문에 @Size는 불필요합니다.
        String username,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Password
        // @Size(min = 12, max = 64) // @Password로 사이즈에 제약을 주었기 때문에 @Size는 불필요합니다.
        String password,
        // 프로필 이미지는 User객체 생성시 선택 사항입니다, 따라서 null 이 들어갈 수 있습니다.
        // 다만 굳이, @Nullable로 null 값이 들어갈 수 있음을 명시할 필요는 없습니다.
        // @Nullable UUID profileId
        @Nullable BinaryContentCreateRequest profile
) {
}
