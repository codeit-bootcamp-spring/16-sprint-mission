package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        // 1. 서비스 구현체를 생성한다.
        JCFUserService userService = new JCFUserService();

        // 2. User 객체 생성에 필요한 값을 각각 준비한다.
        UUID id = UUID.randomUUID();
        Long createAt = System.currentTimeMillis();
        Long updateAt = System.currentTimeMillis();
        User updatedUser = new User(id, createAt, updateAt);

        // 3. User 객체를 생성.
        User user = new User(id, createAt, updateAt);

        // 4. 유저 등록.
        userService.createUser(user); // 유저 등록

        // 5. 단건/다건 조회.
        System.out.println(userService.readUser(id)); // 조회(단건)
        System.out.println(userService.readAllUser()); // 조회(다건)

        // 6. 유저 정보 수정.
        userService.updateUser(id, updatedUser); // 수정

        // 7. 수정된 유저 데이터 조회.
        User updatedUserInfo = userService.readUser(id);
        System.out.println(updatedUserInfo); // 수정된 데이터 조회

        // 8. 유저 정보 삭제.
        userService.deleteUser(id);

        // 9. 유저 정보를 올바르게 삭제 되었는지 확인.
        User checkedUserInfo = userService.readUser(id);
        System.out.println(checkedUserInfo);

    }
}
