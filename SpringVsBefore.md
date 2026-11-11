# Spring 사용 이전과 이후의 Service 초기화 방식 차이 비교

## 1. Spring 사용 이전

- 개발자가 직접 `main()` 내에서 Service에 의존성을 주입해야 했다.
- 따라서 **객체에 대한 제어권이 개발자에게 있었다.**

## 2. Spring 사용 이후

- Spring IoC Container가 의존성 주입을 개발자 대신 수행한다.
- 이처럼 객체 생성과 의존성 주입의 제어권이 개발자에서 컨테이너로 넘어가는 것을 **제어의 역전(IoC, Inversion of Control)** 이라고 한다.
- `DiscodeitApplication`에 기입된 `@SpringBootApplication`에는 `@ComponentScan`이 내장되어 있어, 하위 패키지들을 스캔하여 Bean으로 등록한다.
- Bean으로 등록된 객체들은 기본적으로 **싱글톤**이며, Spring Container가 **전 생애주기**를 관리한다.

## 3. 비교 요약

| 구분 | Spring 사용 이전 | Spring 사용 이후 |
|---|---|---|
| 의존성 주입 주체 | 개발자 (`main()` 내에서 직접) | Spring IoC Container |
| 객체 제어권 | 개발자 | 컨테이너 (제어의 역전) |
| 객체 등록 방식 | 직접 `new`로 생성 | `@ComponentScan`으로 스캔하여 Bean 등록 |
| 객체 범위 | 개발자가 직접 결정 | 기본적으로 싱글톤 |
| 생애주기 관리 | 개발자 | Spring Container |