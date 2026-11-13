package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class StorageWriteFailureTest {

    @Test
    void returns500WhenProfileCannotBeSaved(@TempDir Path directory) throws Exception {
        // 저장소를 만들면 임시 경로에 binary-contents 폴더가 생깁니다.
        Path storagePath = directory.resolve("binary-contents");
        var binaryRepository = new FileBinaryContentRepository(storagePath);

        // 폴더를 옮기고 그 자리에 일반 파일을 만들어 쓰기를 실패시킵니다.
        Files.move(storagePath, directory.resolve("binary-contents-backup"));
        Files.createFile(storagePath);

        var userService = new BasicUserService(
                mock(UserRepository.class),
                binaryRepository,
                mock(UserStatusRepository.class)
        );
        var mvc = standaloneSetup(new UserController(
                userService,
                mock(UserStatusService.class)
        )).setControllerAdvice(new GlobalExceptionHandler()).build();

        var user = new MockMultipartFile(
                "user", "", "application/json",
                """
                {
                  "username": "storage-write-test",
                  "email": "storage-write-test@example.com",
                  "password": "test1234"
                }
                """.getBytes(StandardCharsets.UTF_8)
        );

        // 이번에는 getBytes()가 성공하도록 읽을 수 있는 테스트 데이터를 보냅니다.
        var profile = new MockMultipartFile(
                "profile", "avatar.png", "image/png", new byte[]{1}
        );

        mvc.perform(multipart("/api/users").file(user).file(profile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("저장소에서 데이터를 처리하지 못했습니다."));
    }
}