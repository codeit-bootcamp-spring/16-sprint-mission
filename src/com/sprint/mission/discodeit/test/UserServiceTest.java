package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.ServiceFactory;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class UserServiceTest {

    public static void main(String[] args) {
        UserService userService = ServiceFactory.userService();
        ChannelService channelService = ServiceFactory.channelService();

        User user = new User("테스트 사용자", "test@example.com");

        // 1. 사용자 등록 테스트
        userService.create(user);

        if (userService.findById(user.getId()).isPresent()) {
            System.out.println("사용자 등록 테스트: 통과");
        } else {
            throw new AssertionError("사용자 등록 테스트 실패");
        }

        // 2. 사용자 조회 테스트
        User foundUser = userService.findById(user.getId())
                .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

        if ("테스트 사용자".equals(foundUser.getName())) {
            System.out.println("사용자 조회 테스트: 통과");
        } else {
            throw new AssertionError("사용자 이름이 예상과 다릅니다.");
        }

        // 3. 사용자 수정 테스트
        userService.update(
                user.getId(),
                "수정된 사용자",
                "updated@example.com"
        );

        User updatedUser = userService.findById(user.getId())
                .orElseThrow(() -> new AssertionError("수정된 사용자 조회 실패"));

        if ("수정된 사용자".equals(updatedUser.getName())
                && "updated@example.com".equals(updatedUser.getEmail())) {
            System.out.println("사용자 수정 테스트: 통과");
        } else {
            throw new AssertionError("사용자 수정 테스트 실패");
        }

        // 4. 존재하지 않는 채널 ID 추가 방지 테스트
        UUID invalidChannelId = UUID.randomUUID();
        boolean invalidChannelRejected = false;

        try {
            userService.addChannel(user.getId(), invalidChannelId);
        } catch (IllegalArgumentException e) {
            invalidChannelRejected = true;
        }

        if (invalidChannelRejected) {
            User userAfterAttempt = userService.findById(user.getId())
                    .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

            if (!userAfterAttempt.hasChannel(invalidChannelId)) {
                System.out.println("존재하지 않는 채널 추가 방지 테스트: 통과");
            } else {
                throw new AssertionError("존재하지 않는 채널 ID가 추가되었습니다.");
            }
        } else {
            throw new AssertionError("존재하지 않는 채널 ID가 거부되지 않았습니다.");
        }

        // 5. addChannel() 직접 호출 시 양쪽 관계 동기화 테스트
        Channel channel = channelService.create(
                "사용자 테스트 채널",
                "참여 관계 검증용"
        );

        // 이번 테스트의 핵심: join()이 아닌 addChannel()을 직접 호출
        userService.addChannel(user.getId(), channel.getId());

        User joinedUser = userService.findById(user.getId())
                .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

        Channel joinedChannel = channelService.findById(channel.getId())
                .orElseThrow(() -> new AssertionError("채널 조회 실패"));

        if (joinedUser.hasChannel(channel.getId())
                && joinedChannel.hasMember(user.getId())) {
            System.out.println("사용자·채널 관계 일치 테스트: 통과");
        } else {
            throw new AssertionError("사용자·채널 관계가 일치하지 않습니다.");
        }

        // 6. 중복 참여 방지 테스트
        boolean duplicateRejected = false;

        try {
            userService.addChannel(user.getId(), channel.getId());
        } catch (IllegalArgumentException e) {
            duplicateRejected = true;
        }

        if (duplicateRejected) {
            System.out.println("중복 채널 참여 방지 테스트: 통과");
        } else {
            throw new AssertionError("중복 채널 참여가 허용되었습니다.");
        }

        // 7. 테스트 데이터 정리
        channelService.leave(channel.getId(), user.getId());
        channelService.delete(channel.getId());

        // 8. 사용자 삭제 테스트
        userService.deleteById(user.getId());

        if (userService.findById(user.getId()).isEmpty()) {
            System.out.println("사용자 삭제 테스트: 통과");
        } else {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }
    }
}