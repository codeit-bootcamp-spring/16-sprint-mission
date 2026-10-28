package com.sprint.mission.discodeit.service_file_consoleRun;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.stream.Collectors;

/* File*Service */
// - "저장 방식": .ser 파일에 직렬화 보관, "비즈니스 방식": 기본적으로 같은 로직, "저장 로직": 파일 읽기-쓰기 코드 추가

public class FileChannelConsoleRun {

    /* 1. 파일 채널 서비스 구현체를 매개변수로 생성한다. (상위 타입으로) */
    public static void fileChannelInformation(ChannelService channelService){

        System.out.println("####### 채널 CRUD 테스트 #######\n");
        /* 2. Channel 객체를 생성하고 채널 이름을 등록한다. */
        Channel channel1 = new Channel("공지사항");
        Channel channel2 = new Channel("자유게시판");
        Channel channel3 = new Channel("질문답변");
        channelService.createChannel(channel1); // "공지사항" 채널 등록
        channelService.createChannel(channel2); // "자유게시판" 채널 등록
        channelService.createChannel(channel3); // "질문답변" 채널 등록

        System.out.println("=== 채널 생성 결과 ===");
        channelService.readAllChannel().stream()
                .forEach(channel ->
                        System.out.println(channel.getName() + " 채널이 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 3. 각각의 채널 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(channelService.readChannel(channel1.getId()));
        System.out.print(channelService.readChannel(channel2.getId()));
        System.out.print(channelService.readChannel(channel3.getId()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 4. 채널 이름을 수정한다. (수정 전과 수정 후) */
        // 수정 전 채널 이름 목록
        List<String> beforeNames = channelService.readAllChannel()
                .stream()
                .map(Channel::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 채널 이름 리스트 ===");
        System.out.println(String.join(", ", beforeNames));

        // 여기에서 각 채널의 새 이름을 직접 지정해 수정한다.
        channelService.updateChannel(channel1.getId(), "공지");
        channelService.updateChannel(channel2.getId(), "자유");

        // 수정 후 채널 이름 목록
        List<String> afterNames = channelService.readAllChannel()
                .stream()
                .map(Channel::getName)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 채널 이름 리스트 ===");
        System.out.println(String.join(", ", afterNames));

        /* 5. 수정된 채널 데이터를 조회한다. */
        List<Channel> updatedChannels = channelService.readAllChannel().stream()
                .filter(channel -> !beforeNames.contains(channel.getName()))
                .collect(Collectors.toList());

        System.out.println("====== 수정된 채널 명단 ======");
        updatedChannels.stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(26));
        System.out.println();

        /* 6. 수정 이후 전체 채널을 조회한다. */
        System.out.println("=== 수정 이후 전체 채널 조회 ===");
        channelService.readAllChannel().stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(27));
        System.out.println();

        /* 7. 채널 정보를 삭제한다. */
        System.out.println("삭제 대상: \"" + channel2.getName() + "\"");
        channelService.deleteChannel(channel2.getId()); // 자유 채널을 삭제합니다.

        /* 8. 삭제한 채널이 더 이상 조회되지 않는지 확인한다. */
        try {
            channelService.readChannel(channel2.getId());
            System.out.println("삭제 확인 실패: \""
                    + channel2.getName() + "\" 채널이 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: 삭제한 \""
                    + channel2.getName() + "\" 채널이 더 이상 조회되지 않습니다.");
        }

        /* 9. 삭제 후 전체 채널을 조회한다. */
        System.out.println("=== 삭제 후 채널 목록 ===");
        channelService.readAllChannel().stream()
                .forEach(channel -> System.out.print(channel));

        System.out.println("=".repeat(22));
        System.out.println();
    }
}
