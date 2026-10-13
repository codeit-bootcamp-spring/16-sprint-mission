package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        try {
            // 등록
            User user1 = jcfUserService.create("김철수");
            User user2 = jcfUserService.create("박영수");
            System.out.println("가입 완료 --> %s".formatted(user1));
            System.out.println("가입 완료 --> %s".formatted(user2));

            // 조회 (단일)
            Optional<User> foundUser = jcfUserService.findById(user1.getId());
            foundUser.ifPresentOrElse(
                    user -> System.out.println("단일 조회 완료 --> %s".formatted(user)),
                    () -> System.out.println("조회 실패 (유저 없음)")
            );

            // 조회 (전체)
            Optional<List<User>> userList = jcfUserService.findAll();
            userList.ifPresentOrElse(
                    list -> {
                        System.out.println("전체 조회 완료 --> [");
                        list.forEach((user) -> System.out.println("\t%s".formatted(user)));
                        System.out.println("]");
                    },
                    () -> System.out.println("조회 실패 (유저 없음)")
            );

            // 수정
            System.out.println("수정 전 --> %s".formatted(user1));
            System.out.println("수정 후 --> %s".formatted(user1.update("이수철")));

            // 삭제
            System.out.println("삭제 시작 --> %s".formatted(user1));
            jcfUserService.delete(user1.getId());
            userList = jcfUserService.findAll();
            userList.ifPresentOrElse(
                    list -> {
                        System.out.println("전체 조회 완료 --> [");
                        list.forEach((user) -> System.out.println("\t%s".formatted(user)));
                        System.out.println("]");
                    },
                    () -> System.out.println("조회 실패 (유저 없음)")
            );


        } catch (IllegalArgumentException e) {
            System.out.println("오류 발생: %s".formatted(e.getMessage()));
        }
    }
}
