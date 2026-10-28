package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.ServiceFactory;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

public class ChannelServiceTest {
    public static void main(String[] args) {
        ChannelService channelService =
                ServiceFactory.channelService();

        Channel channel = channelService.create(
                "테스트 채널",
                "채널 테스트용 설명"
        );
        Channel foundChannel = channelService.findById(channel.getId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (foundChannel != null
                &&"테스트 채널".equals(foundChannel.getName())) {
            System.out.println("채널 등록 테스트: 통과");
        }else {
            throw new AssertionError("채널 등록 테스트 실패");
        }

        boolean channelFound = channelService.findAll().stream()
                .anyMatch(item ->
                        item.getId().equals(channel.getId())
                                && "테스트 채널".equals(item.getName())
                                && "채널 테스트용 설명".equals(item.getDescription()));

        if (channelFound) {
            System.out.println("채널 조회 테스트: 통과");
        } else {
            throw new AssertionError("채널 조회 테스트 실패");
        }

        channelService.update(channel.getId(), "수정된 채널", "수정된 설명");

        Channel updatedChannel = channelService.findById(channel.getId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (updatedChannel != null
                && "수정된 채널".equals(updatedChannel.getName())
                && "수정된 설명".equals(updatedChannel.getDescription())) {
            System.out.println("채널 수정 테스트: 통과");
        } else {
            throw new AssertionError("채널 수정 테스트 실패");
        }


        channelService.delete(channel.getId());

        if (channelService.findById(channel.getId()).isEmpty()) {
            System.out.println("채널 삭제 테스트: 통과");
        }else {
            throw new AssertionError("채널 삭제 테스트 실패");
        }
    }
}
