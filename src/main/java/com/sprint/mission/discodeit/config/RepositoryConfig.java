package com.sprint.mission.discodeit.repository.config;

import com.sprint.mission.discodeit.repository.properties.RepositoryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RepositoryProperties.class)
public class RepositoryConfig {

    // RepositoryProperties를 Spring Bean으로 등록하고
    // application.yaml의 설정값을 바인딩할 수 있도록 활성화하는 설정 클래스
}