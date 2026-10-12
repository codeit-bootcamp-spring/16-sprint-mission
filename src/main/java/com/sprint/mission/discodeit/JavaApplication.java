package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();

        // 등록
        User createdUser = jcfUserService.create("김철수");
        System.out.println("가입 완료 --> %s".formatted(createdUser));

        // 조회 (단건)
        Optional<User> foundUser = jcfUserService.findById(createdUser.getId());
        foundUser.ifPresentOrElse(
                user -> System.out.println("조회 완료 --> %s".formatted(user)),
                () -> System.out.println("조회 실패 (유저 없음)")
        );
    }
}
