# Spring 핵심 개념 이해하기

- [ ] JavaApplication과 DiscodeitApplication에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.

  - IoC Container
  - Dependency Injection
  - Bean

Java Application은 Service 객체를 생성할 때, new를 사용하여 생성자에 Repository 객체를 전달하여 생성합니다.
따라서 Service객체 생성시 의존성 주입(DI)이 강제됩니다.

반면에, DiscodeitApplication은 Service 객체(스프링 빈)를 스프링(IoC)컨테이너 등록 된 빈들 중에서 찾아서 가져옵니다.
Service 빈과 Repository 빈 객체가 스프링 컨테이너(Spring/IoC Container)에 각각 독립적으로 존재하며, 의존성 주입(DI)은 스프링이 대신 처리합니다. 





