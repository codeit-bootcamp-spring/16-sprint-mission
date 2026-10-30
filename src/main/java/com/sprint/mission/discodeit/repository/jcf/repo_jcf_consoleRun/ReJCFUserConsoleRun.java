package com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.stream.Collectors;

public class ReJCFUserConsoleRun {
    /*
     * JCF Repository CRUD 테스트
     * 저장 방식: HashMap에 보관
     * 저장 로직: 메모리의 Map만 변경
     */
    public static void printUserRepositoryInformation() {

        System.out.println("\n####### 유저 JCF Service CRUD 테스트 #######\n");

        // 1. Repository와 Service 초기화
        UserRepository userRepository = new JCFUserRepository();
        UserService userService = new BasicUserService(userRepository);

        // 2. User 객체 생성 및 저장
        User user1 = new User("최명하");
        User user2 = new User("이강한");
        User user3 = new User("박코딩");
        User user4 = new User("김자바");

        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        userService.createUser(user4);

        // 3. 유저 생성 결과 확인
        System.out.println("=== 유저 생성 결과 ===");
        userService.readAllUser().forEach(user ->
                System.out.println(user.getName() + "님이 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        // 4. 단건 조회
        System.out.println("=== 단건 조회 ===");
        System.out.print(userService.readUser(user1.getId()));
        System.out.print(userService.readUser(user2.getId()));
        System.out.print(userService.readUser(user3.getId()));
        System.out.print(userService.readUser(user4.getId()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 5. 유저 수정 전 목록
        List<String> beforeNames = userService.readAllUser().stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 유저 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames));

        // 6. 유저 이름 수정
        userService.updateUser(user1.getId(), "최명함");
        userService.updateUser(user2.getId(), "이강함");

        // 7. 유저 수정 후 목록
        List<String> afterNames = userService.readAllUser().stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 유저 이름 목록 ===");
        System.out.println(String.join(", ", afterNames));

        // 8. 수정된 유저 확인
        List<User> updatedUserInfo = userService.readAllUser().stream()
                .filter(user -> !beforeNames.contains(user.getName()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 유저 명단 ===");
        updatedUserInfo.forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 유저 조회
        System.out.println("=== 전체 유저 조회 ===");
        userService.readAllUser().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 유저 삭제
        System.out.println("삭제 대상: \"" + user2.getName() + "\"");
        userService.deleteUser(user2.getId());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 유저 목록 ===");
        userService.readAllUser().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}