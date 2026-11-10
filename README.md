# Sprint 4 구현사항 체크리스트

- [x] 충족  
- [ ] 미충족 또는 부분 충족 (비고에 사유 기재)

API 동작 근거: `postman/discodeit.postman_collection.json`으로 Postman 테스트 수행.
요청 36개, 단언 62개가 모두 통과했습니다. 실행 결과는 `postman/reports/`에 있습니다.

## 컨트롤러 레이어 구현

| | 요구사항 | 구현 위치 / 비고 |
|---|---|---|
| [x] | DiscodeitApplication의 테스트 로직 삭제 | `DiscodeitApplication.main`은 `SpringApplication.run`만 호출 (사용하지 않는 import는 남아 있음) |
| [x] | 서비스 로직을 활용해 웹 API 구현 | `controller/*Controller` → `service/basic/Basic*Service` |
| [x] | `@RequestMapping`만 사용해 구현 | 클래스 단위 `@RequestMapping` + 메서드 단위 `@GetMapping`, `@PostMapping`, `@PatchMapping`, `@DeleteMapping` |

## 웹 API 요구사항

| | 요구사항 | 구현 위치 / 비고 |
|---|---|---|
| [x] | 웹 API의 예외를 전역으로 처리 | `GlobalExceptionHandler` (`@RestControllerAdvice`) 404, 400, 409, 500 → `ErrorResponse` |

### 사용자 관리

| | 요구사항 | API |
|---|---|---|
| [x] | 사용자를 등록할 수 있다 | `POST /api/user/create` (multipart, 프로필 이미지 선택) |
| [x] | 사용자 정보를 수정할 수 있다 | `POST /api/user/update` (로그인 세션의 사용자) |
| [x] | 사용자를 삭제할 수 있다 | `DELETE /api/user/delete` (로그인 세션의 사용자) |
| [x] | 모든 사용자를 조회할 수 있다 | `GET /api/user/findAll` |
| [x] | 사용자의 온라인 상태를 업데이트할 수 있다 | `POST /api/user-status` (로그인 세션의 사용자) |

### 권한 관리

| | 요구사항 | API |
|---|---|---|
| [x] | 사용자는 로그인할 수 있다 | `POST /api/auth/login` (성공 시 세션에 `userId` 저장) |

### 채널 관리

| | 요구사항 | API |
|---|---|---|
| [x] | 공개 채널을 생성할 수 있다 | `POST /api/channel/public` |
| [x] | 비공개 채널을 생성할 수 있다 | `POST /api/channel/private` |
| [x] | 공개 채널의 정보를 수정할 수 있다 | `PATCH /api/channel/update` (비공개 채널을 수정하면 409) |
| [x] | 채널을 삭제할 수 있다 | `DELETE /api/channel/delete/{channel-id}` |
| [x] | 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다 | `GET /api/channel/user/{user-id}` (공개 채널 + 참여 중인 비공개 채널) |

### 메시지 관리

| | 요구사항 | API |
|---|---|---|
| [x] | 메시지를 보낼 수 있다 | `POST /api/message/send` (multipart, 첨부파일 선택) |
| [x] | 메시지를 수정할 수 있다 | `PATCH /api/message/update/{message-id}` |
| [x] | 메시지를 삭제할 수 있다 | `DELETE /api/message/delete/{message-id}` (첨부파일도 함께 삭제) |
| [x] | 특정 채널의 메시지 목록을 조회할 수 있다 | `GET /api/message/{channel-id}` |

### 메시지 수신 정보 관리

| | 요구사항 | API |
|---|---|---|
| [x] | 특정 채널의 메시지 수신 정보를 생성할 수 있다 | `POST /api/read-status/create` |
| [x] | 특정 채널의 메시지 수신 정보를 수정할 수 있다 | `PATCH /api/read-status/update` |
| [x] | 특정 사용자의 메시지 수신 정보를 조회할 수 있다 | `GET /api/read-status/{user-id}` |

### 바이너리 파일 다운로드

| | 요구사항 | API |
|---|---|---|
| [x] | 바이너리 파일을 1개 또는 여러 개 조회할 수 있다 | 1개: `GET /api/binaryContent/find/{content-id}`, 여러 개: `GET /api/binaryContent/list?contentIds=...`, 다운로드: `GET /api/binaryContent/{content-id}` |

## API 테스트

| | 요구사항 | 비고 |
|---|---|---|
| [x] | Postman을 활용해 컨트롤러 테스트 | `postman/discodeit.postman_collection.json` (정상 경우 + 400, 404, 409 오류 경우) |
| [x] | 테스트 결과를 export하여 PR에 첨부 | `postman/reports/discodeit-run.html`, `.json`, `.xml` |

## 정적 리소스 서빙

| | 요구사항 | 구현 위치 / 비고 |
|---|---|---|
| [x] | 사용자 목록 조회: `/api/user/findAll`, `ResponseEntity<List<UserDto>>` | `GET /api/user/findAll` → `ResponseEntity<List<UserResponse>>` (`userId`, `createdAt`, `updatedAt`, `username`, `email`, `profileId`, `isOnline`) |
| [x] | BinaryContent 파일 조회: `/api/binaryContent/find?binaryContentId=`, `ResponseEntity<BinaryContent>` | `GET /api/binaryContent/find/{content-id}` → `ResponseEntity<BinaryContentResponse>` |
| [x] | 사용자 목록 화면 서빙 | `src/main/resources/static/index.html` (`/`에서 서빙, `/api/user/findAll` 호출) |

## 생성형 AI 활용

| | 요구사항 | 비고 |
|---|---|---|
| [x] | 생성형 AI로 비슷한 화면을 생성 후 서빙 | `static/index.html` |
