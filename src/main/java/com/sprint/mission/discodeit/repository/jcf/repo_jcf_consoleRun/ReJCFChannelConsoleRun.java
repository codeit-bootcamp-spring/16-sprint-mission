package com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;

import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class ReJCFChannelConsoleRun {
    /*
     * JCF Repository를 사용하는 Service CRUD 테스트
     * 저장 방식: 메모리
     */
    public static void printChannelRepositoryInformation() {

        System.out.println("####### 채널 JCF Service CRUD 테스트 #######\n");

        /* 1. Repository 초기화 */
        ChannelRepository channelRepository = new JCFChannelRepository();
        UserRepository userRepository = new JCFUserRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();

        /* 1-1. UserService 초기화 */
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        /* 1-2. ChannelService, UserService 초기화 */
        UserService userService =
                new BasicUserService(
                        userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);
        ChannelService channelService =
                new BasicChannelService(
                        channelRepository, userRepository, messageRepository, readStatusRepository);

        /* 1-3. 조회 테스트에 사용할 유저 생성 */
        UserDto user = userService.createUser(new UserCreateRequest(
            "채널 테스트 유저", "channelUsertest@example.com",
                "password123", null));

        // 2. Channel 객체 생성 및 저장
        ChannelDto channel1 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("공지사항", null));
        ChannelDto channel2 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("자유게시판", null));
        ChannelDto channel3 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("질문게시판", null));
        ChannelDto channel4 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("정보공유", null));

        /* 3. 채널 생성 결과 확인 */
        System.out.println("=== 채널 생성 결과 ===");
        channelService.findAllByUserId(user.id()).forEach(channel ->
                System.out.println(channel.channelName() + " 채널이 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        /* 4. 단건 조회 */
        System.out.println("=== 단건 조회 ===");
        System.out.print(channelService.readChannel(channel1.id()));
        System.out.print(channelService.readChannel(channel2.id()));
        System.out.print(channelService.readChannel(channel3.id()));
        System.out.print(channelService.readChannel(channel4.id()));

        System.out.println("=".repeat(17));
        System.out.println();

        /* 5. 채널 수정 전 목록 */
        List<String> beforeNames =
                channelService.findAllByUserId(user.id()).stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 채널 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames));

        /* 6. 채널 이름 수정 */
        channelService.updateChannel(
                new ChannelUpdateRequest(channel1.id(), "공지사항 수정", "수정한 공지사항 내용"));
        channelService.updateChannel(
                new ChannelUpdateRequest(channel2.id(), "자유게시판 수정", "수정한 자유게시판 내용"));

        /* 7. 채널 수정 후 목록 */
        List<String> afterNames = channelService.findAllByUserId(user.id()).stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 채널 이름 목록 ===");
        System.out.println(String.join(", ", afterNames));

        /* 8. 수정된 채널 확인 */
        List<ChannelDto> updatedChannelInfo =
                channelService.findAllByUserId(user.id()).stream()
                .filter(channel ->
                        !beforeNames.contains(channel.channelName()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 채널 명단 ===");
        updatedChannelInfo.forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 채널 조회
        System.out.println("=== 전체 채널 조회 ===");
        channelService.findAllByUserId(user.id()).forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 채널 삭제
        System.out.println("삭제 대상: \"" + channel4.channelName() + "\"");
        channelService.deleteChannel(channel4.id());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 채널 목록 ===");
        channelService.findAllByUserId(user.id()).forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}