package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

public class ChannelServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        try {
            ChannelService channelService =
                    context.getBean(ChannelService.class);

            String testId = UUID.randomUUID().toString().substring(0, 8);
            String channelName = "테스트 채널 " + testId;
            String description = "채널 테스트용 설명 " + testId;

            // 채널 등록
            ChannelDto channel = channelService.createPublic(
                    new ChannelCreateRequest(channelName, description)
            );

            ChannelDto foundChannel = channelService.findById(channel.id())
                    .orElseThrow(() ->
                            new AssertionError("채널을 찾을 수 없습니다."));

            if (channelName.equals(foundChannel.name())) {
                System.out.println("채널 등록 테스트: 통과");
            } else {
                throw new AssertionError("채널 등록 테스트 실패");
            }

            // 채널 조회
            boolean channelFound = channelService.findAll().stream()
                    .anyMatch(item ->
                            item.id().equals(channel.id())
                                    && channelName.equals(item.name())
                                    && description.equals(item.description()));

            if (channelFound) {
                System.out.println("채널 조회 테스트: 통과");
            } else {
                throw new AssertionError("채널 조회 테스트 실패");
            }

            // 채널 수정
            String updatedName = "수정된 채널 " + testId;
            String updatedDescription = "수정된 설명 " + testId;

            channelService.update(
                    new ChannelUpdateRequest(
                            channel.id(),
                            updatedName,
                            updatedDescription
                    )
            );

            ChannelDto updatedChannel =
                    channelService.findById(channel.id())
                            .orElseThrow(() ->
                                    new AssertionError(
                                            "수정된 채널을 찾을 수 없습니다."
                                    ));

            if (updatedName.equals(updatedChannel.name())
                    && updatedDescription.equals(updatedChannel.description())) {
                System.out.println("채널 수정 테스트: 통과");
            } else {
                throw new AssertionError("채널 수정 테스트 실패");
            }

            // 채널 삭제
            channelService.delete(channel.id());

            if (channelService.findById(channel.id()).isEmpty()) {
                System.out.println("채널 삭제 테스트: 통과");
            } else {
                throw new AssertionError("채널 삭제 테스트 실패");
            }

        } finally {
            context.close();
        }
    }
}