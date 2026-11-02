package com.sprint.mission.discodeit.repository.file.repo_file_consoleRun;

import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserUpdateRequest;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReFileUserConsoleRun {
    /*
     * File Repository CRUD 테스트
     * 저장 방식: 파일에 직렬화하여 저장
     * 저장 로직: 파일에서 데이터를 불러오고, 변경된 목록을 파일에 저장
     */
    public static void printFileUserRepositoryInformation() {

        System.out.println("####### 유저 File Service CRUD 테스트 #######\n");

        // 1. Repository와 Service 초기화
        UserRepository userRepository = new FileUserRepository(".discodeit");
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(".discodeit");
        UserStatusRepository userStatusRepository = new FileUserStatusRepository(".discodeit");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new BasicUserService(
                userRepository,
                binaryContentRepository,
                userStatusRepository,
                passwordEncoder
        );

        // 이후 CRUD 테스트는 JCFUserRepositoryRun과 동일한 방식
        // 2. User 객체 생성 및 저장
        UserDto user1 = userService.createUser(new UserCreateRequest(
                "최명하","Choi12@test.com", "Chm1q2w3e4r", null));
        UserDto user2 = userService.createUser(new UserCreateRequest(
                "이강한", "Lee34@test.com", "Lmh1q2w3e4r", null));
        UserDto user3 = userService.createUser(new UserCreateRequest(
                "박코딩", "Park56@test.com", "Pkd1q2w3e4r", null));
        UserDto user4 = userService.createUser(new UserCreateRequest(
                "김자바", "Kim78@test.com", "Kjb1q2w3e4r", null));

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
        System.out.println(String.join(", ", beforeNames) + "\n");

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
        System.out.println(String.join(", ", afterNames) + "\n");

        // 8. 수정된 유저 확인
        List<UUID> updatedIds = List.of(user1.id(), user2.id());

        List<UserDto> updatedUserInfo = userService.readAllUser().stream()
                .filter(user -> updatedIds.contains(user.id()))
                .collect(Collectors.toList());

        System.out.println("===== 수정된 유저 명단 =====");
        updatedUserInfo.forEach(ReFileUserConsoleRun::printUserInfo);

        // 9. 전체 유저 조회
        System.out.println("===== 수정된 후 전체 유저 조회 =====");
        userService.readAllUser().forEach(ReFileUserConsoleRun::printUserInfo);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 유저 삭제대상 확인 및 삭제
        UserDto deleteTarget = userService.readUser(user2.id());

        System.out.println("삭제 대상: \"" + deleteTarget.username() + "\"");
        userService.deleteUser(user2.id());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 유저 목록 ===");
        userService.readAllUser().forEach(ReFileUserConsoleRun::printUserInfo);

        System.out.println("=".repeat(22));
        System.out.println();
    }

    // UserDto 정보 출력
    private static void printUserInfo(UserDto user) {
        System.out.println("UserDto {");
        System.out.println("    id        = " + user.id());
        System.out.println("    username  = " + user.username());
        System.out.println("    email     = " + user.email());
        System.out.println("    profileId = " + user.profileId());
        System.out.println("    createdAt = " + user.createdAt());
        System.out.println("    updatedAt = " + user.updatedAt());
        System.out.println("    online    = " + user.online());
        System.out.println("}\n");
    }
}