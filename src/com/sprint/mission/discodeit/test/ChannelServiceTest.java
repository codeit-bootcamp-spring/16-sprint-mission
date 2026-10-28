package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.ServiceFactory;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class ChannelServiceTest {

    public static void main(String[] args) {
        ChannelService channelService =
                ServiceFactory.channelService();
        UserService userService =
                ServiceFactory.userService();

        // 채널 생성 테스트
        Channel channel = channelService.create(
                "테스트 채널",
                "채널 테스트용 설명"
        );

        Channel foundChannel = channelService.findById(channel.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if ("테스트 채널".equals(foundChannel.getName())) {
            System.out.println("채널 등록 테스트: 통과");
        } else {
            throw new AssertionError("채널 등록 테스트 실패");
        }

        // 채널 조회 테스트
        boolean channelFound = channelService.findAll().stream()
                .anyMatch(item ->
                        item.getId().equals(channel.getId())
                                && "테스트 채널".equals(item.getName())
                                && "채널 테스트용 설명".equals(
                                item.getDescription()));

        if (channelFound) {
            System.out.println("채널 조회 테스트: 통과");
        } else {
            throw new AssertionError("채널 조회 테스트 실패");
        }

        // 채널 수정 테스트
        channelService.update(
                channel.getId(),
                "수정된 채널",
                "수정된 설명"
        );

        Channel updatedChannel = channelService.findById(channel.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if ("수정된 채널".equals(updatedChannel.getName())
                && "수정된 설명".equals(updatedChannel.getDescription())) {
            System.out.println("채널 수정 테스트: 통과");
        } else {
            throw new AssertionError("채널 수정 테스트 실패");
        }

        // 사용자 생성 및 채널 참여 관계 테스트
        User user = userService.create(
                new User("채널 테스트 사용자",
                        "channel-test@example.com")
        );

        channelService.join(channel.getId(), user.getId());

        Channel joinedChannel = channelService.findById(channel.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        User joinedUser = userService.findById(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (joinedChannel.hasMember(user.getId())
                && joinedUser.hasChannel(channel.getId())) {
            System.out.println("채널 참여 관계 테스트: 통과");
        } else {
            throw new AssertionError("채널 참여 관계 테스트 실패");
        }

        // 채널 나가기 후 양쪽 관계 제거 테스트
        channelService.leave(channel.getId(), user.getId());

        Channel leftChannel = channelService.findById(channel.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        User leftUser = userService.findById(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!leftChannel.hasMember(user.getId())
                && !leftUser.hasChannel(channel.getId())) {
            System.out.println("채널 나가기 관계 테스트: 통과");
        } else {
            throw new AssertionError("채널 나가기 관계 테스트 실패");
        }

        // 채널 삭제 테스트
        channelService.delete(channel.getId());

        if (channelService.findById(channel.getId()).isEmpty()) {
            System.out.println("채널 삭제 테스트: 통과");
        } else {
            throw new AssertionError("채널 삭제 테스트 실패");
        }

        // 테스트 사용자 정리
        userService.deleteById(user.getId());
    }
}