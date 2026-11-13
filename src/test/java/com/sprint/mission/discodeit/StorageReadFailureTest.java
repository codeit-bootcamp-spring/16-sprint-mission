package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.controller.BinaryContentQueryController;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class StorageReadFailureTest {

    @Test
    void returns500WhenStoredFileCannotBeRead(@TempDir Path directory) throws Exception {
        UUID fileId = UUID.randomUUID();

        // JUnit이 준비한 임시 폴더에 저장소가 읽을 수 없는 파일을 만듭니다.
        Files.writeString(directory.resolve(fileId + ".ser"), "일반 텍스트");

        var repository = new FileBinaryContentRepository(directory);
        var service = new BasicBinaryContentService(repository);
        var mvc = standaloneSetup(new BinaryContentQueryController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Postman에서 쓰던 조회 주소로 요청하고 오류 응답을 확인합니다.
        mvc.perform(get("/api/binaryContent/find")
                        .param("binaryContentId", fileId.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("저장소에서 데이터를 처리하지 못했습니다."));
    }
}