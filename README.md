### **기본**

Git과 GitHub을 통해 프로젝트를 관리

채팅 서비스의 도메인 모델을 설계하고, Java로 구현

각 도메인별 Service 인터페이스 및 JCF기반 구현체 작성

JavaApplication에서 데이터를 생성/수정/삭제 검증

### **심화**

모듈 간 의존 관계 관리
User가 삭제 시 채널목록에서의 UserId가 함꼐 삭제 되는지 검증

### **주요 변경사항**

• BaseEntity 도메인 클래스를 만들어  User, Channel, Message, 이 공통 필드를 상속받도록 리팩토링
• JCFUserService, JCFChannelService 구현체간의 순환참조 구조 변경





