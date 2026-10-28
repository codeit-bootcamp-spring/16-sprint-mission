package com.sprint.mission.discodeit.repository_file_consoleRun;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ReFileUserConsoleRun {

    /*
     * File Repository CRUD 테스트
     * 저장 방식: 파일에 직렬화하여 저장
     * 저장 로직: 파일에서 데이터를 불러오고, 변경된 목록을 파일에 저장
     */
    public static void printFileUserRepositoryInformation() {

        System.out.println("####### 유저 File Repository 테스트 #######\n");

        // 파일에 데이터를 저장하는 Repository 구현체 생성
        UserRepository userRepository = new FileUserRepository();

        // 이후 CRUD 테스트는 JCFUserRepositoryRun과 동일한 방식
        // 2. User 객체 생성 및 저장
        User user1 = new User("최명하");
        User user2 = new User("이강한");
        User user3 = new User("박코딩");
        User user4 = new User("김자바");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // 3. 유저 생성 결과 확인
        System.out.println("=== 유저 생성 결과 ===");
        userRepository.findAll().stream()
                .forEach(user ->
                        System.out.println(user.getName() + "님이 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        // 4. 단건 조회
        System.out.println("=== 단건 조회 ===");
        System.out.println(userRepository.findByUserId(user1.getId()));
        System.out.println(userRepository.findByUserId(user2.getId()));
        System.out.println(userRepository.findByUserId(user3.getId()));
        System.out.println(userRepository.findByUserId(user4.getId()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 5. 유저 수정 전 목록
        List<String> beforeNames = userRepository.findAll().stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 유저 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames));

        // 6. 유저 이름 수정
        user1.changeName("최명함");
        user2.changeName("이강함");

        userRepository.save(user1);
        userRepository.save(user2);

        // 7. 유저 수정 후 목록
        List<String> afterNames = userRepository.findAll().stream()
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 유저 이름 목록 ===");
        System.out.println(String.join(", ", afterNames));

        // 8. 수정된 유저 확인
        List<User> updatedUserInfo = userRepository.findAll().stream()
                .filter(user -> !beforeNames.contains(user.getName()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 유저 명단 ===");
        updatedUserInfo.stream()
                .forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 유저 조회
        System.out.println("=== 전체 유저 조회 ===");
        userRepository.findAll().stream()
                .forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 유저 삭제
        System.out.println("삭제 대상: \"" + user2.getName() + "\"");
        userRepository.deleteByUserId(user2.getId());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 유저 목록 ===");
        userRepository.findAll().stream()
                .forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}
