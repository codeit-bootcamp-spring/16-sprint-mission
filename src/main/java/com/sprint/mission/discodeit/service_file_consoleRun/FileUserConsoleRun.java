package com.sprint.mission.discodeit.service_file_consoleRun;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/* File*Service */
// - "저장 방식": .ser 파일에 직렬화 보관, "비즈니스 방식": 기본적으로 같은 로직, "저장 로직": 파일 읽기-쓰기 코드 추가

public class FileUserConsoleRun {

    /* 1. 파일 유저 서비스 구현체를 매개변수로 생성한다. (상위 타입으로) */
    public static void fileUserInformation(UserService userService){

        System.out.println("####### 유저 CRUD 테스트 #######\n");
        /* 2. User 객체를 생성하고 유저 이름을 등록한다. */
        User user1 = new User("최명하");
        User user2 = new User("이강한");
        User user3 = new User("박코딩");
        User user4 = new User("김자바");
        userService.createUser(user1); // "최명함" 유저 등록
        userService.createUser(user2); // "이강한" 유저 등록
        userService.createUser(user3); // "박코딩" 유저 등록
        userService.createUser(user4); // "김자바" 유저 등록

        System.out.println("=== 유저 생성 결과 ===");
        userService.readAllUser().stream()
                .forEach(user ->
                        System.out.println(user.getName() + "님이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 3. 각각의 유저 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(userService.readUser(user1.getId()));
        System.out.print(userService.readUser(user2.getId()));
        System.out.print(userService.readUser(user3.getId()));
        System.out.print(userService.readUser(user4.getId()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 4. 유저 이름을 수정한다. (수정 전과 수정 후) */
        // 수정 전 유저 이름 목록
        List<String> beforeNames = userService.readAllUser()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 유저 이름 리스트 ===");
        System.out.println(String.join(", " , beforeNames));

        // 여기에서 각 유저의 새 이름을 직접 지정해 수정한다.
        userService.updateUser(user1.getId(), "최명함"); // "최명하" -> "최명함"
        userService.updateUser(user2.getId(), "이강함"); // "이강한" -> "이강함"

        // 수정 후 유저 이름 목록
        List<String> afterNames = userService.readAllUser()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 유저 이름 리스트 ===");
        System.out.println(String.join(", " , afterNames));

        /* 5. 수정된 유저 데이터를 조회한다. */
        List<User> updatedUserInfo = userService.readAllUser().stream()
                .filter(user -> !beforeNames.contains(user.getName()))
                .collect(Collectors.toList());

        System.out.println("====== 수정된 유저 명단 ======");
        updatedUserInfo.stream()
                .forEach(user -> System.out.print(user));

        System.out.println("=".repeat(26));
        System.out.println();

        /* 6. 수정 이후 전체 유저를 조회한다 */
        System.out.println("=== 수정 이후 전체 유저 조회 ===");
        userService.readAllUser().stream()
                .forEach(user -> System.out.print(user));

        System.out.println("=".repeat(27));
        System.out.println();

        /* 7. 유저 정보를 삭제한다. */
        System.out.println("삭제 대상: \"" + user2.getName() + "\"");
        userService.deleteUser(user2.getId()); // 이강함 유저를 삭제합니다.

        /* 8. 삭제한 유저가 더 이상 조회되지 않는지 확인한다. */
        try {
            userService.readUser(user2.getId());
            System.out.println("삭제 확인 실패: \""
                    + user2.getName() + "\" 유저가 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: 삭제한 \""
                    + user2.getName() + "\" 유저가 더 이상 조회되지 않습니다.");
        }

        /* 9. 삭제 후 전체 유저를 조회한다. */
        System.out.println("=== 삭제 후 유저 목록 ===");
        userService.readAllUser().stream()
                .forEach(user ->
                        System.out.print(user));

        System.out.println("=".repeat(22));
        System.out.println();
    }
}