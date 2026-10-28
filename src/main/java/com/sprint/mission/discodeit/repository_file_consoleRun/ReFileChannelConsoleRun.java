package com.sprint.mission.discodeit.repository_file_consoleRun;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;

public class ReFileChannelConsoleRun {

    /*
     * File Repository CRUD 테스트
     * 저장 방식: 파일에 직렬화하여 저장
     * 저장 로직: 파일에서 데이터를 불러오고, 변경된 목록을 파일에 저장
     */
    public static void printFileChannelRepositoryInformation() {
        System.out.println("####### 채널 File Repository 테스트 #######\n");

        ChannelRepository channelRepository = new FileChannelRepository();

        // 이후 테스트 코드는 JCFChannelRepositoryRun과 동일
        // 1. 채널 생성
        Channel channel1 = new Channel("공지사항");
        Channel channel2 = new Channel("자유게시판");
        Channel channel3 = new Channel("질문게시판");
        Channel channel4 = new Channel("정보공유");

        channelRepository.save(channel1);
        channelRepository.save(channel2);
        channelRepository.save(channel3);
        channelRepository.save(channel4);

        // 2. 전체 채널 조회
        System.out.println("1. 전체 채널 조회");
        channelRepository.findAll().stream()
                .forEach(System.out::println);

        // 3. 특정 채널 조회
        System.out.println("\n2. 특정 채널 조회");
        channelRepository.findByChannelId(channel1.getId())
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("채널을 찾을 수 없습니다.")
                );

        // 4. 채널 존재 여부 확인
        System.out.println("\n3. 채널 존재 여부 확인");
        System.out.println(
                channelRepository.existsByChannelId(channel1.getId())
        );

        // 5. 채널 이름 수정
        System.out.println("\n4. 채널 수정");

        channel1.changeName("공지사항 수정");
        channelRepository.save(channel1);

        channel2.changeName("자유게시판 수정");
        channelRepository.save(channel2);

        channelRepository.findAll().stream()
                .forEach(System.out::println);

        // 6. 채널 삭제
        System.out.println("\n5. 채널 삭제");
        channelRepository.deleteByChannelId(channel4.getId());

        channelRepository.findAll().stream()
                .forEach(System.out::println);

        // 7. 삭제한 채널의 존재 여부 확인
        System.out.println("\n6. 삭제한 채널 존재 여부");
        System.out.println(
                channelRepository.existsByChannelId(channel4.getId()));
    }
}
