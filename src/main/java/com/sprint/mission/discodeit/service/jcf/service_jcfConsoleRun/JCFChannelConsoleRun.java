package com.sprint.mission.discodeit.service.jcf.service_jcfConsoleRun;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/* JCF*Service */
// - "저장 방식": ArrayList 에 보관, "비즈니스 방식": (중복 확인, ID로 조회, 입력 수정, 삭제), "저장 로직": 메모리 목록만 변경

public class JCFChannelConsoleRun {
    /*
     * 채널 CRUD 테스트를 위해 구현한 메서드입니다.
     */
    public static void printChannelInformation() {

        System.out.println("####### 채널 CRUD 테스트 #######\n");

        /* 1. Repository 구현체를 생성한다. */
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();

        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();

        /* 2. Repository를 주입하여 Service 구현체를 생성한다. */
        UserService userService = new JCFUserService(
                        userRepository, binaryContentRepository, userStatusRepository);

        // 테스트에 사용할 유저 생성
        String uniqueId = UUID.randomUUID().toString();
        UserDto user = userService.createUser(
                new UserCreateRequest(
                        "channelUser-" + uniqueId,
                        "channelUser-" + uniqueId + "@example.com",
                        "password",
                        null
                )
        );
        UUID userId = user.id();

        ChannelService channelService = new JCFChannelService(
                        userRepository, readStatusRepository, messageRepository);

        /* 3. Channel 객체를 생성하고 채널 이름을 등록한다. */
        ChannelDto channel1 = channelService.createPublicChannel(new PublicChannelCreateRequest(
                "공지사항-" + uniqueId, null));
        ChannelDto channel2 = channelService.createPublicChannel(new PublicChannelCreateRequest(
                "자유게시판-" + uniqueId, null));
        ChannelDto channel3 = channelService.createPublicChannel(new PublicChannelCreateRequest(
                "질문답변-" + uniqueId, null));

        System.out.println("=== 채널 생성 결과 ===");
        channelService.findAllByUserId(userId).stream()
                .forEach(channel ->
                        System.out.println(channel.channelName() + " 채널이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 4. 각각의 채널 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(channelService.readChannel(channel1.id()));
        System.out.print(channelService.readChannel(channel2.id()));
        System.out.print(channelService.readChannel(channel3.id()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 5. 채널 이름을 수정한다. (수정 전과 수정 후) */
        // 수정 전 채널 이름 목록
        List<String> beforeNames = channelService.findAllByUserId(userId)
                .stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 채널 이름 리스트 ===");
        System.out.println(String.join(", ", beforeNames));

        // 여기에서 각 채널의 새 이름을 직접 지정해 수정한다.
        channelService.updateChannel(new ChannelUpdateRequest(channel1.id(), "공지"));
        channelService.updateChannel(new ChannelUpdateRequest(channel2.id(), "자유"));

        // 수정 후 채널 이름 목록
        List<String> afterNames = channelService.findAllByUserId(userId)
                .stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 채널 이름 리스트 ===");
        System.out.println(String.join(", ", afterNames));

        /* 6. 수정된 채널 데이터를 조회한다. */
        List<ChannelDto> updatedChannels = channelService.findAllByUserId(userId).stream()
                .filter(channel -> !beforeNames.contains(channel.channelName()))
                .collect(Collectors.toList());

        System.out.println("====== 수정된 채널 명단 ======");
        updatedChannels.stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(26));
        System.out.println();

        /* 7. 수정 이후 전체 채널을 조회한다. */
        System.out.println("=== 수정 이후 전체 채널 조회 ===");
        channelService.findAllByUserId(userId).stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(27));
        System.out.println();

        /* 8. 채널 정보를 삭제한다. */
        System.out.println("삭제 대상: \"" + channel2.channelName() + "\"");
        channelService.deleteChannel(channel2.id()); // 자유 채널을 삭제합니다.

        /* 9. 삭제한 채널이 더 이상 조회되지 않는지 확인한다. */
        try {
            channelService.readChannel(channel2.id());
            System.out.println("삭제 확인 실패: \""
                    + channel2.channelName() + "\" 채널이 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: 삭제한 \""
                    + channel2.channelName() + "\" 채널이 더 이상 조회되지 않습니다.");
        }

        /* 10. 삭제 후 전체 채널을 조회한다. */
        System.out.println("=== 삭제 후 채널 목록 ===");
        channelService.findAllByUserId(userId).stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(22));
        System.out.println();
    }
}