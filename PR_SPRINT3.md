# [김지태] sprint3

## 요구사항

### 기본
- [x] Spring 프로젝트 초기화 (Spring Boot 4.1.2, Java 17, Gradle)
- [x] Bean 선언 및 `DiscodeitApplication` 테스트
- [x] Spring 핵심 개념 정리 (아래 첨부)
- [x] Lombok 적용 (`@Getter`)
- [x] 시간 타입 `Long` → `Instant` 변경
- [x] 새 도메인 추가: `BinaryContent`, `UserStatus`, `ReadStatus`
- [x] `User`에 `profileId`, `Message`에 `attachmentIds` 추가
- [x] DTO 작성 (`record`)
- [x] 서비스 고도화: User, Auth, Channel, Message, ReadStatus, UserStatus, BinaryContent
- [x] 새 도메인 Repository 구현체 (JCF, File)

### 심화
- [x] Bean 다루기: `discodeit.repository.type` 설정값(`jcf` / `file`)으로 Repository 구현체 선택
- [x] File Repository 저장 경로를 `discodeit.repository.file-directory` 설정값으로 지정

## 주요 변경사항

### 공통
- 베이스 코드에 Spring Boot를 적용하고 `@Repository`, `@Service`로 Bean 등록
- 엔티티에 `@Getter` 적용, `createdAt` / `updatedAt`을 `Instant`로 변경
- `BinaryContent`, `UserStatus`, `ReadStatus` 도메인과 Repository(JCF / File) 추가
- 요청 DTO(`dto/request`)와 응답 DTO(`dto/response`)를 `record`로 분리 작성하고, 각 서비스에서 엔티티를 응답 DTO로 변환해 반환

### 서비스별 변경사항
- **User**
  - 생성 시 username / email 중복 검사, 프로필 이미지(선택) 저장, `UserStatus` 자동 생성
  - 수정 시 새 프로필 저장 후 기존 프로필 삭제
  - 삭제 시 프로필과 `UserStatus` 함께 삭제
- **UserStatus**: 마지막 접속 시간(`lastActiveAt`)이 5분 이내이면 온라인으로 판단
- **Auth**: username / password로 로그인하고 `UserResponse` 반환
- **Channel**
  - PUBLIC / PRIVATE 생성 분리, PRIVATE은 참여자별 `ReadStatus` 생성
  - PRIVATE 채널 수정 시도 시 예외 발생
  - 조회 시 마지막 메시지 시간 포함
  - 유저별 조회 시 PUBLIC 전체와 참여 중인 PRIVATE만 반환
  - 삭제 시 `ReadStatus` → 메시지(+첨부파일) → 채널 순으로 함께 삭제
- **Message**
  - 생성 시 채널 / 작성자 존재 검증
  - 첨부파일(`BinaryContent`) 저장 후 id 목록 보관
  - 삭제 시 첨부파일 함께 삭제

### 설정값 기반 Bean 선택 (심화)
- `@ConditionalOnProperty`로 `discodeit.repository.type` 값에 따라 JCF / File Repository 구현체 중 하나만 Bean으로 등록
  - `type`이 없으면 JCF가 기본값 (`matchIfMissing = true`)
  - 제출한 `application.yaml`은 `type: jcf`
- `@Value`로 `discodeit.repository.file-directory` 값을 주입받아 File Repository 저장 폴더 지정 (기본값 `.discodeit`)

## Spring 핵심 개념 정리

### JavaApplication vs DiscodeitApplication
- **JavaApplication**: 개발자가 직접 `new`로 객체를 만들고 생성자에 넘겨 연결
- **DiscodeitApplication**: 객체 생성과 연결은 Spring이 하고, `context.getBean(...)`으로 꺼내 사용

### 키워드
- **Bean**
  - Spring Container가 생성하고 관리하는 객체. `@Repository`, `@Service`가 붙은 클래스가 Bean으로 등록됨
  - User / Channel / Message 같은 도메인 객체는 요청마다 새로 만들어지는 데이터라 Bean이 아님
  - Bean은 특정 프레임워크에 종속되지 않은 POJO로 작성됨
  - 기본적으로 싱글톤이며, 애플리케이션 실행 시 생성되고 종료 시 소멸함
- **IoC Container**
  - Bean을 생성·보관·연결·소멸시키는 곳 (`ApplicationContext`)
  - 객체 생성과 연결의 제어권이 개발자에서 컨테이너로 넘어감 (제어의 역전)
  - `@SpringBootApplication`에 포함된 `@ComponentScan`이 하위 패키지를 스캔해 Bean으로 등록
- **Dependency Injection**
  - 필요한 객체를 클래스 안에서 `new`로 만들지 않고 생성자로 받아 사용하는 방식
  - 서비스는 Repository **인터페이스**에만 의존하고, 실제 구현체는 컨테이너가 설정값에 따라 주입

| 구분 | JavaApplication | DiscodeitApplication |
|------|-----------------|----------------------|
| 객체 생성 | 개발자가 `new` | Spring이 생성 |
| 의존 객체 연결 | 생성자에 직접 전달 | Spring이 Bean을 찾아 주입 |
| 객체 범위 | 개발자가 직접 결정 | 기본적으로 싱글톤 |
| 구현체 교체 | `main`의 `new` 코드 수정 | 설정값(`application.yaml`)만 변경 |

## 스크린샷

### File Repository 실행 (`type: file`)

**실행 이전**

<img width="347" height="510" alt="실행 이전" src="https://github.com/user-attachments/assets/0addf53d-4b99-4e8e-8823-71226e3d4ae5" />

**실행 이후**

<img width="347" height="371" alt="실행 이후" src="https://github.com/user-attachments/assets/3b5b8822-a255-4ee3-9ed5-0699c7c56f39" />

## 멘토에게

- `BasicMessageService`는 현재 Repository 4개를 주입받고 있습니다. 기능이 추가되거나 다른 엔티티와 연관 관계가 생기면 더 많은 의존성을 주입받아야 할 텐데, 그러면 서비스 로직이 지나치게 무거워지지 않을지 궁금합니다. 서비스를 나눠서 구현한다면 어떤 기준으로 나누는지도 궁금합니다.
- 제출한 로직에서 UserStatus, Message, PUBLIC Channel은 수정할 때 `Update*Request` record를 사용했습니다. 생성할 때 사용한 `Create*Request` record를 수정에도 같이 사용하면 안 되는 이유를 잘 모르겠습니다.
- Channel이나 Message를 삭제할 때 하위 데이터를 먼저 삭제한 후 마지막에 Channel이나 Message를 삭제하고 있습니다. Channel이나 Message를 삭제하면 하위 데이터가 알아서 함께 삭제되게 하려면 로직을 어떤 흐름으로 구성해야 하는지 감이 잡히지 않아서 알려주시면 좋겠습니다.
- 현재 Repository에는 기본적인 저장·조회·삭제 메서드만 있어서, "특정 채널의 ReadStatus"나 "같은 username / email을 가진 유저"처럼 조건이 있는 조회는 서비스에서 전체 목록을 가져온 뒤 걸러내고 있습니다. 이런 조건 조회는 Repository에 메서드로 추가하는 것이 맞는지, 그렇다면 어디까지를 Repository의 책임으로 보고 어디부터를 Service의 책임으로 보는지 기준이 궁금합니다.
- Bean은 기본적으로 싱글톤이라고 정리했는데, JCF Repository는 하나의 `HashMap`을 모든 요청이 함께 사용하는 구조입니다. 여러 요청이 동시에 들어올 경우 문제가 될 수 있는지, 그리고 싱글톤 Bean을 설계할 때 상태를 가지는 것과 관련해 주의해야 할 점이 무엇인지 궁금합니다.
