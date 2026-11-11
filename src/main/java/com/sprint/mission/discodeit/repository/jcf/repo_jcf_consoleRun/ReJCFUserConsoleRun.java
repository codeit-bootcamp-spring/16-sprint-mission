package com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun;

import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserUpdateRequest;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new BasicUserService(
                userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);

        // 2. User 객체 생성 및 저장
        UserDto user1 = userService.createUser(new UserCreateRequest(
                        "최명하", "user1@test.com", "Password1q2w3e4r", null));
        UserDto user2 = userService.createUser(new UserCreateRequest(
                        "이강한", "user2@test.com", "Password1q2w3e4r", null));
        UserDto user3 = userService.createUser(new UserCreateRequest(
                        "박코딩", "user3@test.com", "Password1q2w3e4r", null));
        UserDto user4 = userService.createUser(new UserCreateRequest(
                        "김자바", "user4@test.com", "Password1q2w3e4r", null));

        // 3. 유저 생성 결과 확인
        System.out.println("=== 유저 생성 결과 ===");
        userService.readAllUser().forEach(user ->
                System.out.println(user.username() + "님이 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        // 4. 단건 조회
        System.out.println("=== 단건 조회 ===");
        System.out.println(userService.readUser(user1.id()));
        System.out.println(userService.readUser(user2.id()));
        System.out.println(userService.readUser(user3.id()));
        System.out.println(userService.readUser(user4.id()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 5. 유저 수정 전 목록
        List<String> beforeNames = userService.readAllUser().stream()
                .map(UserDto::username)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 유저 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames));

        // 6. 유저 이름 수정
        userService.updateUser(new UserUpdateRequest(
                user1.id(), "최명함", user1.email(), "Password1234!", null));
        userService.updateUser(new UserUpdateRequest(
                user2.id(), "이강함", user2.email(), "Password1212!", null));

        // 7. 유저 수정 후 목록
        List<String> afterNames = userService.readAllUser().stream()
                .map(UserDto::username)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 유저 이름 목록 ===");
        System.out.println(String.join(", ", afterNames));

        // 8. 수정된 유저 확인
        List<UserDto> updatedUserInfo = userService.readAllUser().stream()
                .filter(user -> !beforeNames.contains(user.username()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 유저 명단 ===");
        updatedUserInfo.forEach(System.out::println);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 유저 조회
        System.out.println("=== 전체 유저 조회 ===");
        userService.readAllUser().forEach(System.out::println);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 유저 삭제
        System.out.println("삭제 대상: \"" + userService.readUser(user2.id()).username() + "\"");
        userService.deleteUser(user2.id());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 유저 목록 ===");
        userService.readAllUser().forEach(System.out::println);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}