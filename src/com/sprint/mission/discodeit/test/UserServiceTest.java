package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.ServiceFactory;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class UserServiceTest {

    public static void main(String[] args) {
        UserService userService = ServiceFactory.userService();

        User user = new User("테스트 사용자", "test@example.com");

        // 사용자 등록
        userService.create(user);
        if (userService.findById(user.getId()).isPresent()) {
            System.out.println("사용자 등록 테스트: 통과");
        }else {
            throw new AssertionError("사용자 등록 테스트 실패");
        }

        // 사용자 조회
        User foundUser = userService.findById(user.getId())
                .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

        if ("테스트 사용자".equals(foundUser.getName())) {
            System.out.println("사용자 조회 테스트: 통과");
        }else {
            throw new AssertionError("사용자 이름이 예상과 다릅니다.");
        }

        // 사용자 수정
        userService.update(user.getId(), "수정된 사용자", "updated@example.com");

        User updatedUser = userService.findById(user.getId())
                .orElseThrow(() -> new AssertionError("수정된 사용자 조회 실패"));

        if ("수정된 사용자".equals(updatedUser.getName())
                && "updated@example.com".equals(updatedUser.getEmail())) {
            System.out.println("사용자 수정 테스트: 통과");
        } else {
            throw new AssertionError("사용자 수정 테스트 실패");
        }

        // 사용자 삭제
        userService.deleteById(user.getId());
        if (userService.findById(user.getId()).isEmpty()) {
            System.out.println("사용자 삭제 테스트: 통과");
        }else {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }
    }
}