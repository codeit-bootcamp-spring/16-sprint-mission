package com.sprint.mission.discodeit.repository.file.repo_file_consoleRun;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;

import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class ReFileChannelConsoleRun {
    /*
     * File Repository를 사용하는 Service CRUD 테스트
     * 저장 방식: 파일 직렬화
     */
    public static void printFileChannelRepositoryInformation() {

        System.out.println("####### 채널 File Service CRUD 테스트 #######\n");

        /* 1. Repository 초기화 */
        ChannelRepository channelRepository = new FileChannelRepository(".discodeit");
        UserRepository userRepository = new FileUserRepository(".discodeit");
        MessageRepository messageRepository = new FileMessageRepository(".discodeit");
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository(".discodeit");

        /* 1-1. UserService 초기화 */
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(".discodeit");
        UserStatusRepository userStatusRepository = new FileUserStatusRepository(".discodeit");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new BasicUserService(
                userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);

        /* 1-2. ChannelService 초기화 */
        ChannelService channelService = new BasicChannelService(
                channelRepository, userRepository, messageRepository, readStatusRepository);

        /* 1-3. 조회 테스트에 사용할 유저 생성 */
        UserDto user = userService.createUser(new UserCreateRequest(
            "채널 테스트 유저", "channelUsertest@example.com",
                "password123", null));

        /* 2. Channel 객체 생성 및 저장 */
        ChannelDto channel1 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("공지사항", "공지사항 채널입니다."));
        ChannelDto channel2 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("자유게시판", "자유게시판 채널입니다."));
        ChannelDto channel3 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("질문게시판", "질문게시판 채널입니다."));
        ChannelDto channel4 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("정보공유", "정보공유 채널입니다."));

        /* 3. 채널 생성 결과 확인 */
        System.out.println("=== 채널 생성 결과 ===");
        channelService.findAllByUserId(user.id()).forEach(channel ->
                System.out.println(channel.channelName() + " 채널이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 4. 단건 조회 */
        System.out.println("=== 단건 조회 ===");
        System.out.println(channelService.readChannel(channel1.id()));
        System.out.println(channelService.readChannel(channel2.id()));
        System.out.println(channelService.readChannel(channel3.id()));
        System.out.println(channelService.readChannel(channel4.id()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 5. 채널 수정 전 목록 */
        List<String> beforeNames =
                channelService.findAllByUserId(user.id()).stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 채널 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames) + "\n");

        /* 6. 채널 이름 수정 */
        channelService.updateChannel(
                new ChannelUpdateRequest(channel1.id(), "공지사항 수정"));
        channelService.updateChannel(
                new ChannelUpdateRequest(channel2.id(), "자유게시판 수정"));

        /* 7. 채널 수정 후 목록 */
        List<String> afterNames = channelService.findAllByUserId(user.id()).stream()
                .map(ChannelDto::channelName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 채널 이름 목록 ===");
        System.out.println(String.join(", ", afterNames) + "\n");

        /* 8. 수정된 채널 확인 */
        List<ChannelDto> updatedChannelInfo =
            channelService.findAllByUserId(user.id()).stream()
                .filter(channel ->
                        !beforeNames.contains(channel.channelName()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 채널 명단 ===");
        updatedChannelInfo.forEach(ReFileChannelConsoleRun::printChannelInfo);
        System.out.println("=".repeat(22));
        System.out.println();

        /* 9. 전체 채널 조회 */
        System.out.println("===== 전체 채널 조회 =====");
        channelService.findAllByUserId(user.id()).forEach(ReFileChannelConsoleRun::printChannelInfo);

        System.out.println("=".repeat(22));
        System.out.println();

        /* 10. 채널 삭제 */
        System.out.println("삭제 대상: \"" + channel4.channelName() + "\"");
        channelService.deleteChannel(channel4.id());

        /* 11. 삭제 결과 확인 */
        System.out.println("===== 삭제 후 채널 목록 =====");
        channelService.findAllByUserId(user.id()).forEach(ReFileChannelConsoleRun::printChannelInfo);

        System.out.println("=".repeat(22));
        System.out.println();
    }

    // ChannelDto 정보 출력
    private static void printChannelInfo(ChannelDto channel) {
        System.out.println("ChannelDto {");
        System.out.println("    id          = " + channel.id());
        System.out.println("    channelName = " + channel.channelName());
        System.out.println("    description = " + channel.description());
        System.out.println("    createdAt   = " + channel.createdAt());
        System.out.println("    updatedAt   = " + channel.updatedAt());
        System.out.println("}\n");
    }
}