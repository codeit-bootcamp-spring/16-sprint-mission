## 진행 현황

### 1차 기본 요구사항

#### 도메인 모델링
- [x] 패키지 `com.sprint.mission.discodeit.entity` 구성
- [x] 공통 필드 정의
  - [x] `id` (UUID)
  - [x] `createdAt`, `updatedAt` (Long, 유닉스 타임스탬프)
- [x] `User`, `Channel`, `Message` 도메인 모델 구현
- [x] 생성자
  - [x] `id`, `createdAt`은 생성자에서 초기화
  - [x] 그 외 필드는 생성자 파라미터로 초기화
- [x] 메소드
  - [x] 각 필드의 `Getter` 정의
  - [x] 필드 수정용 `update` 함수 정의

#### 서비스 설계 및 구현
- [x] 도메인별 CRUD 인터페이스 선언 (`com.sprint.mission.discodeit.service`)
  - [x] 네이밍 규칙 `[도메인 모델 이름]Service` 적용
- [x] JCF 기반 구현체 작성 (`com.sprint.mission.discodeit.service.jcf`)
  - [x] 네이밍 규칙 `JCF[인터페이스 이름]` 적용
  - [x] `data` 필드를 `final`로 선언하고 생성자에서 초기화
  - [x] 생성, 조회(단건/다건), 수정, 삭제 메소드 구현

#### 메인 클래스 (`JavaApplication`)
- [x] 메인 메소드 선언
- [x] 등록 테스트
- [x] 조회(단건, 다건) 테스트
- [x] 수정 테스트
- [x] 수정된 데이터 조회 테스트
- [x] 삭제 테스트
- [x] 삭제 여부 조회 확인

### 심화 요구사항
- [x] 서비스 간 의존성 주입
- [x] 도메인 모델 간 관계 검증 로직 추가 (예: Message 생성 시 연관된 User, Channel 존재 여부 확인)
- [x] 검증 로직 테스트

### 코드 리뷰 반영 사항
  - 리뷰 내용: null 대체 객체를 반환하는 대신, `NullPointerException` 발생 시 `main`까지 던지고 `main`에서 catch하여 처리하는 방식이 더 적절함
  - [x] Null Object 클래스/로직 제거
  - [x] 서비스 계층에서 null 처리 로직 제거 (예외를 그대로 전파)
  - [x] 예외 발생 케이스(존재하지 않는 id 조회 등) 테스트

## 남은 작업
1. 코드 리뷰 반영: Null Object 제거 및 `main`에서 예외 처리
2. `JavaApplication`에 수정 → 수정 후 조회 → 삭제 → 삭제 확인 테스트 추가
3. 심화: Message 생성 시 User, Channel 존재 여부 검증 및 테스트

## 클래스 다이어그램

### Entity

```mermaid
classDiagram
    direction TB

    class Entity {
        <<abstract>>
        #UUID id
        #long createdAt
        #long updatedAt
    }

    class User {
        -String userName
        -String email
        -String password
        -List~Channel~ channels
        -List~Message~ myMessages
        +addChannel(Channel)
        +deleteChannel(Channel)
        +addMessage(Message)
        +deleteMessage(Message)
    }

    class Channel {
        -String title
        -String description
        -ChannelType channelType
        -List~User~ users
        -List~Message~ channelMessages
        +addUser(User)
        +deleteUser(User)
        +addMessage(Message)
        +deleteMessage(Message)
    }

    class Message {
        -String message
        -Channel channel
        -User user
    }

    class ChannelType {
        <<enumeration>>
        PUBLIC
        PRIVATE
    }

    Entity <|-- User
    Entity <|-- Channel
    Entity <|-- Message

    User "*" -- "*" Channel : channels / users
    User "1" -- "*" Message : user / myMessages
    Channel "1" -- "*" Message : channel / channelMessages
    Channel --> ChannelType
```

### Service

```mermaid
classDiagram
    direction TB

    class UserService {
        <<interface>>
        +create(username, email, password) User
        +findById(UUID) Optional~User~
        +findAll() List~User~
        +findByName(String) Optional~User~
        +updatePw(UUID, String) Optional~User~
        +updateUsername(UUID, String) Optional~User~
        +updateEmail(UUID, String) Optional~User~
        +delete(UUID)
    }

    class ChannelService {
        <<interface>>
        +create(User, ChannelType, title, description) Channel
        +findById(UUID) Optional~Channel~
        +findAll() List~Channel~
        +updateChannelType(UUID, ChannelType) Optional~Channel~
        +updateTitle(UUID, String) Optional~Channel~
        +updateDescription(UUID, String) Optional~Channel~
        +delete(UUID)
    }

    class MessageService {
        <<interface>>
        +create(message, Channel, User) Message
        +findById(UUID) Optional~Message~
        +findAll() List~Message~
        +update(UUID, String) Optional~Message~
        +delete(UUID)
    }

    class JCFUserService {
        -Map~UUID, User~ data
        -MessageService messageService
    }

    class JCFChannelService {
        -Map~UUID, Channel~ data
        -UserService userService
        -MessageService messageService
    }

    class JCFMessageService {
        -Map~UUID, Message~ data
    }

    UserService <|.. JCFUserService
    ChannelService <|.. JCFChannelService
    MessageService <|.. JCFMessageService

    JCFUserService --> MessageService : 삭제 시 Message 정리
    JCFChannelService --> MessageService : 삭제 시 Message 정리
    JCFChannelService --> UserService : 생성 시 User 등록 여부 검증
```

### 관계 요약

| 관계 | 종류 | 근거 |
|---|---|---|
| `User`, `Channel`, `Message` → `Entity` | 상속 | `extends Entity` |
| `Channel` → `ChannelType` | 단방향 연관 | `channelType` 필드 |
| `User` ↔ `Channel` | 다대다 양방향 | `User.channels`, `Channel.users` |
| `User` ↔ `Message` | 일대다 양방향 | `Message.user`(final), `User.myMessages` |
| `Channel` ↔ `Message` | 일대다 양방향 | `Message.channel`(final), `Channel.channelMessages` |
| `JCF*Service` → `*Service` | 구현 | `implements` |
| `JCFUserService`, `JCFChannelService` → `MessageRepository` | 의존 (생성자 주입) | 삭제 시 연관 Message를 `messageService.delete()`로 정리 |
| `JCFChannelService` → `UserRepository` | 의존 (생성자 주입) | Channel 생성 시 `userService.findById()`로 등록된 User인지 검증 |

### Service 의존 방향 규칙

Service 간 의존은 `ChannelService → UserService → MessageService` 한 방향으로만 둡니다.

- `MessageRepository`는 다른 Service에 의존하지 않습니다.
- `UserRepository`는 `ChannelRepository`에 의존하지 않습니다.
- 역방향 의존이 필요해지면(예: Message 생성 시 User/Channel 존재 여부 검증) 순환 참조가 생기므로, Service 간 직접 의존 대신 Repository 계층 분리를 검토합니다.


# 요구사항

## 2차 기본 요구사항

### File IO를 통한 데이터 영속화

- [ ] 다음 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - [ ] 클래스 패키지명: `com.sprint.mission.discodeit.service.file`
  - [ ] 클래스 네이밍 규칙: `File[인터페이스 이름]`
  - [ ] JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요.
    - 참고: 객체 직렬화/역직렬화 가이드
- [ ] `Application`에서 서비스 구현체를 `File*Service`로 바꾸어 테스트해보세요.

### 서비스 구현체 분석

- [ ] `JCF*Service` 구현체와 `File*Service` 구현체를 비교하여 공통점과 차이점을 발견해보세요.
  - [ ] "비즈니스 로직"과 관련된 코드를 식별해보세요.
  - [ ] "저장 로직"과 관련된 코드를 식별해보세요.

### 레포지토리 설계 및 구현

- [ ] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
  - [ ] 인터페이스 패키지명: `com.sprint.mission.discodeit.repository`
  - [ ] 인터페이스 네이밍 규칙: `[도메인 모델 이름]Repository`
- [ ] 다음 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요. (JCF)
  - [ ] 클래스 패키지명: `com.sprint.mission.discodeit.repository.jcf`
  - [ ] 클래스 네이밍 규칙: `JCF[인터페이스 이름]`
  - [ ] 기존에 구현한 `JCF*Service` 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
- [ ] 다음 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요. (File)
  - [ ] 클래스 패키지명: `com.sprint.mission.discodeit.repository.file`
  - [ ] 클래스 네이밍 규칙: `File[인터페이스 이름]`
  - [ ] 기존에 구현한 `File*Service` 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

---

## 심화 요구사항

### 관심사 분리를 통한 레이어 간 의존성 주입

- [ ] 다음 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - [ ] 클래스 패키지명: `com.sprint.mission.discodeit.service.basic`
  - [ ] 클래스 네이밍 규칙: `Basic[인터페이스 이름]`
  - [ ] 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.
  - [ ] 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.
  - [ ] "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)
