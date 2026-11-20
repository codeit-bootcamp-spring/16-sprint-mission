package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class ProfileReadFailureTest {

    @Test
    void returns500WhenProfileCannotBeRead() throws Exception {
        // 실제 컨트롤러와 예외 처리기를 테스트 요청에 연결합니다.
        MockMvc mvc = standaloneSetup(new UserController(
                mock(UserService.class),
                mock(UserStatusService.class)
        )).setControllerAdvice(new GlobalExceptionHandler()).build();

        // Postman에서 보내던 form-data의 user 파트입니다.
        MockMultipartFile user = new MockMultipartFile(
                "user", "", "application/json",
                """
                {
                  "username": "profile-error-test",
                  "email": "profile-error-test@example.com",
                  "password": "test1234"
                }
                """.getBytes(StandardCharsets.UTF_8)
        );

        // 파일이 첨부된 상태에서 getBytes()를 호출하는 순간에만 실패시킵니다.
        MockMultipartFile profile = new MockMultipartFile(
                "profile", "avatar.png", "image/png", new byte[]{1}
        ) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("테스트용 파일 읽기 실패");
            }
        };

        // 회원가입 요청을 보내고 응답 상태와 메시지를 확인합니다.
        mvc.perform(multipart("/api/users").file(user).file(profile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("업로드된 파일을 읽지 못했습니다."));
    }
}