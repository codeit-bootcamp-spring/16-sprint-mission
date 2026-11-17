package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Discodeit API 문서",
                description = "Discodeit 프로젝트의 Swagger API 문서입니다.",
                version = "1.0"
        )
)
public class OpenApiConfig {
}
