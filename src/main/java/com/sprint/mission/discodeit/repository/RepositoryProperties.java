package com.sprint.mission.discodeit.repository;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@ConfigurationProperties(prefix = "discodeit.repository")
public class RepositoryProperties {

    // application.yaml의 Repository 관련 설정값을 저장하는 클래스
    // prefix 이하의 설정값이 각 필드에 바인딩됨

    // Repository 구현체 선택 기준 (기본값: jcf)
    private String type = "jcf";

    // File Repository의 데이터 저장 디렉터리 (기본값: data)
    private Path fileDirectory = Paths.get("data");
}