
package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerDocumentationConfig {

    private static final String USER_ID = "11111111-1111-4111-8111-111111111111";
    private static final String USER_2_ID = "33333333-3333-4333-8333-333333333333";
    private static final String CHANNEL_ID = "22222222-2222-4222-8222-222222222222";
    private static final String MESSAGE_ID = "44444444-4444-4444-8444-444444444444";
    private static final String READ_STATUS_ID = "55555555-5555-4555-8555-555555555555";
    private static final String FILE_ID = "66666666-6666-4666-8666-666666666666";
    private static final String USER_STATUS_ID = "77777777-7777-4777-8777-777777777777";
    private static final String NOW = "2026-11-16T09:00:00Z";

    @Bean
    public OpenApiCustomizer discodeitSwaggerCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            addErrorSchema(openApi);
            addPropertyExamples(openApi);
            addParameterExamples(openApi);
            addRequestExamples(openApi);
            addResponseDocumentation(openApi);
        };
    }

    private static void addErrorSchema(OpenAPI openApi) {
        ObjectSchema schema = new ObjectSchema();
        schema.setDescription("API 요청 실패 시 반환되는 공통 오류 형식");
        schema.addProperty("timestamp", new StringSchema().format("date-time").example(NOW));
        schema.addProperty("status", new IntegerSchema().example(400));
        schema.addProperty("error", new StringSchema().example("Bad Request"));
        schema.addProperty("message", new StringSchema().example("요청 데이터가 올바르지 않습니다."));
        schema.addProperty("path", new StringSchema().example("/api/users"));
        openApi.getComponents().addSchemas("ApiErrorResponse", schema);
    }

    private static void addPropertyExamples(OpenAPI openApi) {
        Map<String, Schema> schemas = openApi.getComponents().getSchemas();
        if (schemas == null) return;

        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            Schema<?> model = entry.getValue();
            if (model.getProperties() == null) continue;

            for (Object value : model.getProperties().entrySet()) {
                @SuppressWarnings("unchecked")
                Map.Entry<String, Schema> property = (Map.Entry<String, Schema>) value;
                if (property.getValue() == null || property.getValue().getExample() != null) continue;
                Object example = exampleFor(property.getKey());
                if (example != null) property.getValue().setExample(example);
            }
        }
    }

    private static Object exampleFor(String fieldName) {
        return switch (fieldName) {
            case "id" -> USER_ID;
            case "username", "newUsername" -> "jessie";
            case "email", "newEmail" -> "jessie@example.com";
            case "password", "newPassword" -> "password1234";
            case "name", "newName" -> "general";
            case "description", "newDescription" -> "자유롭게 대화하는 공개 채널입니다.";
            case "content", "newContent" -> "안녕하세요! 첫 번째 메시지입니다.";
            case "userId", "authorId" -> USER_ID;
            case "channelId" -> CHANNEL_ID;
            case "messageId" -> MESSAGE_ID;
            case "readStatusId" -> READ_STATUS_ID;
            case "profileId", "binaryContentId" -> FILE_ID;
            case "binaryContentIds", "attachmentIds" -> List.of(FILE_ID);
            case "participantIds" -> List.of(USER_ID, USER_2_ID);
            case "createdAt", "updatedAt", "lastReadAt", "newLastReadAt",
                 "lastActiveAt", "newLastActiveAt", "lastMessageAt" -> NOW;
            case "type" -> "PUBLIC";
            case "online" -> true;
            case "fileName" -> "profile.png";
            case "size" -> 1024;
            case "contentType" -> "image/png";
            case "bytes" -> "AAECAwQ=";
            default -> null;
        };
    }

    private static void addParameterExamples(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;
        openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getParameters() == null) return;
                    for (Parameter parameter : operation.getParameters()) {
                        if (parameter.getExample() != null) continue;
                        Object example = exampleFor(parameter.getName());
                        if (example != null) parameter.setExample(example);
                    }
                })
        );
    }

    private static void addRequestExamples(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;
        openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperationsMap().forEach((method, operation) -> {
                    if (operation.getRequestBody() == null || operation.getRequestBody().getContent() == null) return;
                    MediaType json = operation.getRequestBody().getContent().get("application/json");
                    if (json == null || json.getExample() != null) return;
                    Object example = jsonRequestExample(method.name() + " " + path);
                    if (example != null) json.setExample(example);
                })
        );
        // multipart/form-data는 기존 @RequestPart 및 JSON 파트 인코딩 설정을 그대로 보존한다.
    }

    private static Object jsonRequestExample(String endpoint) {
        return switch (endpoint) {
            case "POST /api/auth/login" -> Map.of(
                    "username", "jessie", "password", "password1234");
            case "POST /api/channels/public" -> Map.of(
                    "name", "general", "description", "자유롭게 대화하는 공개 채널입니다.");
            case "POST /api/channels/private" -> Map.of(
                    "participantIds", List.of(USER_ID, USER_2_ID));
            case "PATCH /api/channels/{channelId}" -> Map.of(
                    "newName", "announcements", "newDescription", "공지사항 채널입니다.");
            case "PATCH /api/messages/{messageId}" -> Map.of(
                    "newContent", "수정한 메시지입니다.");
            case "POST /api/readStatuses" -> Map.of(
                    "userId", USER_ID, "channelId", CHANNEL_ID, "lastReadAt", NOW);
            case "PATCH /api/readStatuses/{readStatusId}" -> Map.of(
                    "newLastReadAt", NOW);
            case "PATCH /api/users/{userId}/userStatus" -> Map.of(
                    "newLastActiveAt", NOW);
            default -> null;
        };
    }

    private static void addResponseDocumentation(OpenAPI openApi) {
        describe(openApi, "/api/users", PathItem.HttpMethod.GET, "200", "사용자 목록 조회 성공", "UserDto[]");
        describe(openApi, "/api/users", PathItem.HttpMethod.POST, "201", "사용자 생성 성공", "UserView",
                fail("400", "필수 입력값 오류 또는 사용자 이름·이메일 중복"),
                fail("413", "프로필 파일 크기 제한 초과"), fail("415", "지원하지 않는 요청 형식"));
        describe(openApi, "/api/users/{userId}", PathItem.HttpMethod.PATCH, "200", "사용자 수정 성공", "UserView",
                fail("400", "잘못된 사용자 수정 요청"), fail("404", "사용자를 찾을 수 없음"),
                fail("413", "프로필 파일 크기 제한 초과"), fail("415", "지원하지 않는 요청 형식"));
        describe(openApi, "/api/users/{userId}", PathItem.HttpMethod.DELETE, "204", "사용자 삭제 성공", null,
                fail("404", "사용자를 찾을 수 없음"));
        describe(openApi, "/api/users/{userId}/userStatus", PathItem.HttpMethod.PATCH,
                "200", "사용자 접속 상태 수정 성공", "UserStatusView", fail("404", "사용자를 찾을 수 없음"));

        describe(openApi, "/api/auth/login", PathItem.HttpMethod.POST, "200", "로그인 성공", "UserView",
                fail("400", "비밀번호가 일치하지 않음"), fail("404", "사용자가 존재하지 않음"));

        describe(openApi, "/api/channels", PathItem.HttpMethod.GET, "200", "참여 채널 목록 조회 성공", "ChannelSummary[]");
        describe(openApi, "/api/channels/public", PathItem.HttpMethod.POST, "201", "공개 채널 생성 성공", "ChannelView",
                fail("400", "채널 이름 등 잘못된 요청"));
        describe(openApi, "/api/channels/private", PathItem.HttpMethod.POST, "201", "비공개 채널 생성 성공", "ChannelView",
                fail("400", "참여자 목록이 비어 있거나 올바르지 않음"), fail("404", "참여자를 찾을 수 없음"));
        describe(openApi, "/api/channels/{channelId}", PathItem.HttpMethod.PATCH,
                "200", "채널 정보 수정 성공", "ChannelView",
                fail("400", "잘못된 채널 수정 요청"), fail("404", "채널을 찾을 수 없음"));
        describe(openApi, "/api/channels/{channelId}", PathItem.HttpMethod.DELETE,
                "204", "채널 삭제 성공", null, fail("404", "채널을 찾을 수 없음"));

        describe(openApi, "/api/messages", PathItem.HttpMethod.GET, "200", "메시지 목록 조회 성공", "MessageView[]");
        describe(openApi, "/api/messages", PathItem.HttpMethod.POST, "201", "메시지 생성 성공", "MessageView",
                fail("400", "잘못된 메시지 생성 요청"), fail("404", "작성자 또는 채널을 찾을 수 없음"),
                fail("409", "채널에 참여하지 않은 사용자는 메시지를 작성할 수 없음"),
                fail("413", "첨부파일 크기 제한 초과"), fail("415", "지원하지 않는 요청 형식"));
        describe(openApi, "/api/messages/{messageId}", PathItem.HttpMethod.PATCH,
                "200", "메시지 수정 성공", "MessageView", fail("404", "메시지를 찾을 수 없음"));
        describe(openApi, "/api/messages/{messageId}", PathItem.HttpMethod.DELETE,
                "204", "메시지 삭제 성공", null, fail("404", "메시지를 찾을 수 없음"));

        describe(openApi, "/api/readStatuses", PathItem.HttpMethod.GET,
                "200", "읽음 상태 목록 조회 성공", "ReadStatus[]");
        describe(openApi, "/api/readStatuses", PathItem.HttpMethod.POST,
                "201", "읽음 상태 생성 성공", "ReadStatus",
                fail("400", "이미 존재하는 읽음 상태 또는 잘못된 요청"),
                fail("404", "사용자 또는 채널을 찾을 수 없음"));
        describe(openApi, "/api/readStatuses/{readStatusId}", PathItem.HttpMethod.PATCH,
                "200", "읽음 상태 갱신 성공", "ReadStatus", fail("404", "읽음 상태를 찾을 수 없음"));

        describe(openApi, "/api/binaryContents", PathItem.HttpMethod.GET,
                "200", "첨부파일 목록 조회 성공", "BinaryContent[]");
        describe(openApi, "/api/binaryContents/{binaryContentId}", PathItem.HttpMethod.GET,
                "200", "첨부파일 조회 성공", "BinaryContent", fail("404", "첨부파일을 찾을 수 없음"));
    }

    private record Failure(String code, String description) {}

    private static Failure fail(String code, String description) {
        return new Failure(code, description);
    }

    private static void describe(OpenAPI openApi, String path, PathItem.HttpMethod method,
                                 String successCode, String successDescription,
                                 String responseSchema, Failure... failures) {
        if (openApi.getPaths() == null || openApi.getPaths().get(path) == null) return;
        Operation operation = openApi.getPaths().get(path).readOperationsMap().get(method);
        if (operation == null) return;

        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }

        ApiResponse success = responses.get(successCode);
        if (success == null) {
            success = new ApiResponse();
            responses.addApiResponse(successCode, success);
        }
        success.setDescription(successDescription);
        if ("204".equals(successCode)) {
            success.setContent(null);
        } else {
            applySuccessExample(openApi, success, responseSchema, path);
        }

        for (Failure failure : failures) {
            ApiResponse response = responses.get(failure.code());
            if (response == null) {
                response = new ApiResponse();
                responses.addApiResponse(failure.code(), response);
            }
            response.setDescription(failure.description());
            response.setContent(new Content().addMediaType("application/json", new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                    .example(errorExample(failure.code(), failure.description(), path))));
        }
    }

    private static void applySuccessExample(OpenAPI openApi, ApiResponse response, String type, String path) {
        Object example = "/api/channels/private".equals(path) && "ChannelView".equals(type)
                ? privateChannel() : responseExample(type);
        if (example == null) return;

        Content content = response.getContent();
        if (content == null) {
            content = new Content();
            response.setContent(content);
        }
        MediaType json = content.get("application/json");
        if (json == null) {
            json = new MediaType();
            content.addMediaType("application/json", json);
        }
        if (json.getSchema() == null && type != null) {
            String component = type.replace("[]", "");
            if (openApi.getComponents().getSchemas() != null &&
                    openApi.getComponents().getSchemas().containsKey(component)) {
                Schema<?> ref = new Schema<>().$ref("#/components/schemas/" + component);
                json.setSchema(type.endsWith("[]") ? new ArraySchema().items(ref) : ref);
            }
        }
        json.setExample(example);
    }

    private static Object responseExample(String type) {
        if (type == null) return null;
        return switch (type) {
            case "UserView" -> user(false);
            case "UserDto[]" -> List.of(user(true));
            case "ChannelView" -> channel(false);
            case "ChannelSummary[]" -> List.of(channel(true));
            case "MessageView" -> message();
            case "MessageView[]" -> List.of(message());
            case "ReadStatus" -> readStatus();
            case "ReadStatus[]" -> List.of(readStatus());
            case "UserStatusView" -> Map.of(
                    "id", USER_STATUS_ID, "createdAt", NOW, "updatedAt", NOW,
                    "userId", USER_ID, "lastActiveAt", NOW, "online", true);
            case "BinaryContent" -> binaryContent();
            case "BinaryContent[]" -> List.of(binaryContent());
            default -> null;
        };
    }

    private static Map<String, Object> user(boolean withOnline) {
        Map<String, Object> base = new java.util.LinkedHashMap<>();
        base.put("id", USER_ID);
        base.put("createdAt", NOW);
        base.put("updatedAt", NOW);
        base.put("username", "jessie");
        base.put("email", "jessie@example.com");
        base.put("profileId", FILE_ID);
        if (withOnline) base.put("online", true);
        return base;
    }

    private static Map<String, Object> channel(boolean withMembers) {
        Map<String, Object> base = new java.util.LinkedHashMap<>();
        base.put("id", CHANNEL_ID);
        if (!withMembers) {
            base.put("createdAt", NOW);
            base.put("updatedAt", NOW);
        }
        base.put("type", "PUBLIC");
        base.put("name", "general");
        base.put("description", "자유롭게 대화하는 공개 채널입니다.");
        if (withMembers) {
            base.put("participantIds", List.of(USER_ID, USER_2_ID));
            base.put("lastMessageAt", NOW);
        }
        return base;
    }

    private static Map<String, Object> privateChannel() {
        return Map.of("id", CHANNEL_ID, "createdAt", NOW, "updatedAt", NOW,
                "type", "PRIVATE", "name", "비공개 채널", "description", "참여자 전용 대화 채널입니다.");
    }

    private static Map<String, Object> message() {
        return Map.of(
                "id", MESSAGE_ID, "createdAt", NOW, "updatedAt", NOW,
                "content", "안녕하세요! 첫 번째 메시지입니다.",
                "channelId", CHANNEL_ID, "authorId", USER_ID,
                "attachmentIds", List.of(FILE_ID));
    }

    private static Map<String, Object> readStatus() {
        return Map.of(
                "id", READ_STATUS_ID, "createdAt", NOW, "updatedAt", NOW,
                "userId", USER_ID, "channelId", CHANNEL_ID, "lastReadAt", NOW);
    }

    private static Map<String, Object> binaryContent() {
        return Map.of(
                "id", FILE_ID, "createdAt", NOW, "fileName", "profile.png",
                "size", 1024, "contentType", "image/png", "bytes", "AAECAwQ=");
    }

    private static Map<String, Object> errorExample(String code, String description, String path) {
        int status = Integer.parseInt(code);
        String reason = switch (code) {
            case "400" -> "Bad Request";
            case "404" -> "Not Found";
            case "409" -> "Conflict";
            case "413" -> "Payload Too Large";
            case "415" -> "Unsupported Media Type";
            default -> "Error";
        };
        return Map.of(
                "timestamp", NOW,
                "status", status,
                "error", reason,
                "message", description,
                "path", path.replace("{userId}", USER_ID)
                        .replace("{channelId}", CHANNEL_ID)
                        .replace("{messageId}", MESSAGE_ID)
                        .replace("{readStatusId}", READ_STATUS_ID)
                        .replace("{binaryContentId}", FILE_ID)
        );
    }
}
