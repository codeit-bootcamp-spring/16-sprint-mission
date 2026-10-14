package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.sql.SQLOutput;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        userService.createUser("Patrick1224"); // 새 사용자 생성 후 내부 컬렉션에 추가
        System.out.println("User List: " + userService.findAll()); // 모든(한 명) 사용자 출력
        userService.createUser("Young0927"); // 새 사용자 생성 후 내부 컬레션에 추가
        System.out.println("User List: " + userService.findAll()); // 모든(두 명) 사용자 출력

        UUID user1_uuid = ((JCFUserService) userService).getUserUUID(0); // 첫 번째 사용자의 UUID 저장

        System.out.println("첫 번째 User의 UUID: " + user1_uuid); // 첫 번쨰 사용자의 UUID 출력

        User user1 = userService.findById(user1_uuid); // 첫 번째 사용자 저장
        User user1_updated = userService.updateUsername(user1_uuid, "Lee1224"); // 첫 번째 사용자의 username 변경
        System.out.println("첫 번째 사용자의 변경된 username:"  + user1_updated.getUsername()); // 첫 번째 사용자의 username 출력
        System.out.println("user1과 user1_updated는 같은 객체인가요? " + (user1 == user1_updated)); // true

        userService.deleteUser(user1_uuid); // 첫 번째 사용자 삭제
        System.out.println("User List: " + userService.findAll()); // 모든(한 명) 사용자 출력
    }
}
