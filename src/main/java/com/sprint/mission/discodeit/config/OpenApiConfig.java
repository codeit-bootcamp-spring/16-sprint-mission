package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Discodeit API 문서",
                description = "Discodeit 프로젝트의 Swagger API 문서입니다."
        ),
        servers = @Server(
                url = "http://localhost:8080",
                description = "로컬 서버"
        )
)
public class OpenApiConfig {

}
