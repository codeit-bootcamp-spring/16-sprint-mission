package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

public class ChannelServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        try {
            ChannelService channelService =
                    context.getBean(ChannelService.class);

            UserService userService =
                    context.getBean(UserService.class);

            MessageService messageService =
                    context.getBean(MessageService.class);

            ReadStatusRepository readStatusRepository =
                    context.getBean(ReadStatusRepository.class);

            String testId = UUID.randomUUID().toString().substring(0, 8);

            // 테스트 사용자 3명 생성
            UserDto userA = createUser(userService, "A", testId);
            UserDto userB = createUser(userService, "B", testId);
            UserDto userC = createUser(userService, "C", testId);

            // 공개 채널 생성
            ChannelDto publicChannel = channelService.createPublic(
                    new ChannelCreateRequest(
                            "공개 채널 " + testId,
                            "공개 채널 테스트"
                    )
            );

            // A와 B만 참여하는 비공개 채널 생성
            ChannelDto privateChannel = channelService.createPrivate(
                    new PrivateChannelCreateRequest(
                            List.of(userA.id(), userB.id())
                    )
            );

            // 사용자별 채널 목록 조회
            List<ChannelDto> channelsA =
                    channelService.findAllByUserId(userA.id());

            List<ChannelDto> channelsB =
                    channelService.findAllByUserId(userB.id());

            List<ChannelDto> channelsC =
                    channelService.findAllByUserId(userC.id());

            // 공개 채널은 모든 사용자에게 표시되어야 함
            check(
                    containsChannel(channelsA, publicChannel.id())
                            && containsChannel(channelsB, publicChannel.id())
                            && containsChannel(channelsC, publicChannel.id()),
                    "공개 채널 사용자별 조회 테스트"
            );

            // 비공개 채널은 참여자인 A와 B에게만 표시되어야 함
            check(
                    containsChannel(channelsA, privateChannel.id())
                            && containsChannel(channelsB, privateChannel.id())
                            && !containsChannel(channelsC, privateChannel.id()),
                    "비공개 채널 참여자별 조회 테스트"
            );

            // 채널 삭제 테스트용 메시지 생성
            var message = messageService.create(
                    new MessageCreateRequest(
                            "채널 삭제 테스트 메시지",
                            userA.id(),
                            privateChannel.id(),
                            List.of()
                    )
            );

            // 메시지가 정상적으로 생성됐는지 확인
            check(
                    messageService.findById(message.getId()).isPresent(),
                    "채널 삭제 테스트용 메시지 생성"
            );

            // ReadStatus가 생성됐는지 확인
            check(
                    readStatusRepository.findByUserIdAndChannelId(
                            userA.id(),
                            privateChannel.id()
                    ).isPresent()
                            &&
                            readStatusRepository.findByUserIdAndChannelId(
                                    userB.id(),
                                    privateChannel.id()
                            ).isPresent(),
                    "채널 참여자 ReadStatus 생성"
            );

            // 채널 삭제
            channelService.delete(privateChannel.id());

            // 메시지 삭제 여부 확인
            check(
                    messageService.findById(message.getId()).isEmpty(),
                    "채널 삭제 시 메시지 삭제"
            );

            // ReadStatus 삭제 여부 확인
            check(
                    readStatusRepository.findByUserIdAndChannelId(
                            userA.id(),
                            privateChannel.id()
                    ).isEmpty()
                            &&
                            readStatusRepository.findByUserIdAndChannelId(
                                    userB.id(),
                                    privateChannel.id()
                            ).isEmpty(),
                    "채널 삭제 시 ReadStatus 삭제"
            );

            // 기존 공개 채널 CRUD 테스트
            String channelName = "테스트 채널 " + testId;
            String description = "채널 테스트용 설명 " + testId;

            ChannelDto channel = channelService.createPublic(
                    new ChannelCreateRequest(channelName, description)
            );

            ChannelDto foundChannel = channelService.findById(channel.id())
                    .orElseThrow(() ->
                            new AssertionError("채널을 찾을 수 없습니다."));

            check(
                    channelName.equals(foundChannel.name()),
                    "채널 등록 테스트"
            );

            boolean channelFound = channelService.findAll().stream()
                    .anyMatch(item ->
                            item.id().equals(channel.id())
                                    && channelName.equals(item.name())
                                    && description.equals(item.description())
                    );

            check(channelFound, "채널 조회 테스트");

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

            check(
                    updatedName.equals(updatedChannel.name())
                            && updatedDescription.equals(updatedChannel.description()),
                    "채널 수정 테스트"
            );

            // 채널 삭제
            channelService.delete(channel.id());

            check(
                    channelService.findById(channel.id()).isEmpty(),
                    "채널 삭제 테스트"
            );

            // 테스트 데이터 정리
            channelService.delete(publicChannel.id());

            userService.deleteById(userA.id());
            userService.deleteById(userB.id());
            userService.deleteById(userC.id());

        } finally {
            context.close();
        }
    }

    // 테스트 사용자 생성
    private static UserDto createUser(
            UserService userService,
            String name,
            String testId
    ) {
        return userService.create(
                new UserCreateRequest(
                        "테스트 사용자 " + name + testId,
                        name.toLowerCase() + testId + "@test.com",
                        "test-password",
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    // 사용자 채널 목록에 특정 채널이 있는지 확인
    private static boolean containsChannel(
            List<ChannelDto> channels,
            UUID channelId
    ) {
        return channels.stream()
                .anyMatch(channel -> channel.id().equals(channelId));
    }

    // 테스트 결과 확인
    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError(testName + " 실패");
        }

        System.out.println(testName + ": 통과");
    }
}