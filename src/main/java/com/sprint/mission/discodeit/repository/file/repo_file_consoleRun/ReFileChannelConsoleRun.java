package com.sprint.mission.discodeit.repository.file.repo_file_consoleRun;

import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.stream.Collectors;

public class ReFileChannelConsoleRun {
    /*
     * File Repository를 사용하는 Service CRUD 테스트
     * 저장 방식: 파일 직렬화
     */
    public static void printFileChannelRepositoryInformation() {

        System.out.println("####### 채널 File Service CRUD 테스트 #######\n");

        // 1. Repository와 Service 초기화
        ChannelRepository channelRepository = new FileChannelRepository();
        UserRepository userRepository = new FileUserRepository();
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService =
                new BasicChannelService(channelRepository, userService);

        // 이후 CRUD 테스트는 JCFUserRepositoryRun과 동일한 방식
        // 2. Channel 객체 생성 및 저장
        Channel channel1 = new Channel("공지사항");
        Channel channel2 = new Channel("자유게시판");
        Channel channel3 = new Channel("질문게시판");
        Channel channel4 = new Channel("정보공유");

        channelService.createChannel(channel1);
        channelService.createChannel(channel2);
        channelService.createChannel(channel3);
        channelService.createChannel(channel4);

        // 3. 채널 생성 결과 확인
        System.out.println("=== 채널 생성 결과 ===");
        channelService.readAllChannel().forEach(channel ->
                System.out.println(channel.getName() + " 채널이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        // 4. 단건 조회
        System.out.println("=== 단건 조회 ===");
        System.out.print(channelService.readChannel(channel1.getId()));
        System.out.print(channelService.readChannel(channel2.getId()));
        System.out.print(channelService.readChannel(channel3.getId()));
        System.out.print(channelService.readChannel(channel4.getId()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 5. 채널 수정 전 목록
        List<String> beforeNames = channelService.readAllChannel().stream()
                .map(Channel::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 채널 이름 목록 ===");
        System.out.println(String.join(", ", beforeNames));

        // 6. 채널 이름 수정
        channelService.updateChannel(channel1.getId(), "공지사항 수정");
        channelService.updateChannel(channel2.getId(), "자유게시판 수정");

        // 7. 채널 수정 후 목록
        List<String> afterNames = channelService.readAllChannel().stream()
                .map(Channel::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 채널 이름 목록 ===");
        System.out.println(String.join(", ", afterNames));

        // 8. 수정된 채널 확인
        List<Channel> updatedChannelInfo = channelService.readAllChannel().stream()
                .filter(channel -> !beforeNames.contains(channel.getName()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 채널 명단 ===");
        updatedChannelInfo.forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 채널 조회
        System.out.println("=== 전체 채널 조회 ===");
        channelService.readAllChannel().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 채널 삭제
        System.out.println("삭제 대상: \"" + channel4.getName() + "\"");
        channelService.deleteChannel(channel4.getId());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 채널 목록 ===");
        channelService.readAllChannel().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}