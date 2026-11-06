package com.sprint.mission.discodeit.service.file.service_fileConsoleRun;

import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/* File*Service */
// - "저장 방식": .ser 파일에 직렬화 보관, "비즈니스 방식": 기본적으로 같은 로직, "저장 로직": 파일 읽기-쓰기 코드 추가

public class FileUserConsoleRun {
    /* 1. 파일 유저 서비스 구현체를 매개변수로 생성한다. (상위 타입으로) */
    public static void fileUserInformation(UserService userService){

        System.out.println("####### 유저 CRUD 테스트 #######\n");

        /* 2. DTO를 사용하여 유저를 생성하고 정보를 등록한다. */
        UserDto user1 = userService.createUser(new UserCreateRequest(
                "최명하","Choi12@test.com", "Cmh1q2w3e4r", null));
        UserDto user2 = userService.createUser(new UserCreateRequest(
                "이강한", "Lee34@test.com", "Lgh1q2w3e4r", null));
        UserDto user3 = userService.createUser(new UserCreateRequest(
                "박코딩", "Park56@test.com", "Pkd1q2w3e4r", null));
        UserDto user4 = userService.createUser(new UserCreateRequest(
                "김자바", "Kim78@test.com", "Kjb1q2w3e4r", null));

        System.out.println("=== 유저 생성 결과 ===");
        userService.readAllUser().stream()
                .forEach(user ->
                        System.out.println(user.username() + "님이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 3. 각각의 유저 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(userService.readUser(user1.id()));
        System.out.print(userService.readUser(user2.id()));
        System.out.print(userService.readUser(user3.id()));
        System.out.print(userService.readUser(user4.id()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 4. 유저 이름을 수정한다. (수정 전과 수정 후) */
        // 수정 전 유저 이름 목록
        List<String> beforeNames = userService.readAllUser()
                .stream()
                .map(UserDto::username)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 유저 이름 리스트 ===");
        System.out.println(String.join(", " , beforeNames));

        // 여기에서 각 유저의 새 이름을 직접 지정해 수정한다.
        userService.updateUser(
                new UserUpdateRequest(user1.id(), "최명함", null, null, null)
        ); // "최명하" -> "최명함"

        userService.updateUser(
                new UserUpdateRequest(user2.id(), "이강함", null, null, null)
        ); // "이강한" -> "이강함"

        // 수정 후 유저 이름 목록
        List<String> afterNames = userService.readAllUser()
                .stream()
                .map(UserDto::username)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 유저 이름 리스트 ===");
        System.out.println(String.join(", " , afterNames));

        /* 5. 수정된 유저 데이터를 조회한다. */
        List<UserDto> updatedUserInfo = userService.readAllUser().stream()
                .filter(user -> !beforeNames.contains(user.username()))
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
        System.out.println("삭제 대상: \"" + user2.username() + "\"");
        userService.deleteUser(user2.id()); // 이강함 유저를 삭제합니다.

        /* 8. 삭제한 유저가 더 이상 조회되지 않는지 확인한다. */
        try {
            userService.readUser(user2.id());
            System.out.println("삭제 확인 실패: \""
                    + user2.username() + "\" 유저가 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: 삭제한 \""
                    + user2.username() + "\" 유저가 더 이상 조회되지 않습니다.");
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