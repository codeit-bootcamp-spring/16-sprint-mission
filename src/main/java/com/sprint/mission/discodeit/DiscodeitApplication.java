package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) {

        // 1. 사용자 5명 생성
        System.out.println("\n========== 1. 사용자 생성 ==========");

        String runId = UUID.randomUUID().toString().substring(0, 8);

        List<UserResponseDto> createdUsers = new ArrayList<>();

        String[] usernames = {
                "앨리스", "밥", "찰리", "데이비드", "이브"
        };

        for (String username : usernames) {
            UserCreateRequestDto request = UserCreateRequestDto.builder()
                    .username(username + "-" + runId)
                    .password("password123")
                    .build();

            UserResponseDto user = userService.createUser(request);
            createdUsers.add(user);

            System.out.println(
                    "생성 완료: " + user.getUsername()
                            + " / ID: " + user.getId()
            );
        }

        // 사용자 수정
        System.out.println("\n========== 사용자 수정 ==========");

        String updatedUsername = "이브-수정-" + UUID.randomUUID()
                .toString().substring(0, 8);
        String updatedEmail = UUID.randomUUID() + "@example.com";

        UserResponseDto updatedUser = userService.updateUser(
                createdUsers.get(4).getId(),
                UserUpdateRequestDto.builder()
                        .username(updatedUsername)
                        .password("newPassword123")
                        .email(updatedEmail)
                        .build()
        );

        System.out.println("수정된 이름: " + updatedUser.getUsername());
        System.out.println("수정된 이메일: " + updatedUser.getEmail());


        // 사용자 이름 중복 검사
        System.out.println("\n========== 사용자 이름 중복 검사 ==========");

        expectException("중복된 사용자 이름으로 생성",
                () -> userService.createUser(
                        UserCreateRequestDto.builder()
                                .username(updatedUsername)
                                .password("password123")
                                .build()
                )
        );

        expectException("중복된 사용자 이름으로 수정",
                () -> userService.updateUser(
                        createdUsers.get(2).getId(),
                        UserUpdateRequestDto.builder()
                                .username(updatedUsername)
                                .build()
                )
        );


        // 이메일 중복 검사
        System.out.println("\n========== 이메일 중복 검사 ==========");

        expectException("중복된 이메일로 생성",
                () -> userService.createUser(
                        UserCreateRequestDto.builder()
                                .username("새로운 사용자")
                                .password("password123")
                                .email(updatedEmail)
                                .build()
                )
        );

        expectException("중복된 이메일로 수정",
                () -> userService.updateUser(
                        createdUsers.get(2).getId(),
                        UserUpdateRequestDto.builder()
                                .email(updatedEmail)
                                .build()
                )
        );

        // 2. 전체 사용자 조회
        System.out.println("\n========== 2. 사용자 목록 조회 ==========");

        for (UserResponseDto user : userService.findAllUsers()) {
            System.out.println(
                    "이름: " + user.getUsername()
                            + " / ID: " + user.getId()
                            + " / 온라인: " + user.isOnline()
            );
        }

        // 3. 채널 생성
        System.out.println("\n========== 3. 채널 생성 ==========");

        // 공개 채널 1 생성
        ChannelResponseDto javaChannel = channelService.createPublicChannel(
                createdUsers.get(0).getId(),
                PublicChannelCreateRequestDto.builder()
                        .channelName("Java 스터디")
                        .description("Java 공부를 위한 채널")
                        .build()
        );

        // 공개 채널 2 생성
        ChannelResponseDto springChannel = channelService.createPublicChannel(
                createdUsers.get(1).getId(),
                PublicChannelCreateRequestDto.builder()
                        .channelName("Spring 스터디")
                        .description("Spring 공부를 위한 채널")
                        .build()
        );

        // 비공개 채널 생성
        ChannelResponseDto freeChannel = channelService.createPrivateChannel(
                createdUsers.get(2).getId(),
                PrivateChannelCreateRequestDto.builder()
                        .participantIds(List.of(
                                createdUsers.get(0).getId(),
                                createdUsers.get(3).getId()
                        ))
                        .build()
        );

        List<ChannelResponseDto> createdChannels = List.of(
                javaChannel, springChannel, freeChannel
        );

        for (ChannelResponseDto channel : createdChannels) {
            System.out.println(
                    "채널 생성 완료: " + channel.getChannelName()
                            + " / 종류: " + channel.getType()
                            + " / ID: " + channel.getId()
            );
        }

        // 4. 사용자 채널 참여
        System.out.println("\n========== 4. 사용자 채널 참여 ==========");

        // Java 스터디
        channelService.joinChannel(
                javaChannel.getId(),
                createdUsers.get(1).getId()
        );

        channelService.joinChannel(
                javaChannel.getId(),
                createdUsers.get(3).getId()
        );

        // Spring 스터디
        channelService.joinChannel(
                springChannel.getId(),
                createdUsers.get(2).getId()
        );

        channelService.joinChannel(
                springChannel.getId(),
                createdUsers.get(4).getId()
        );

        // 참여 결과 출력
        for (ChannelResponseDto channel : createdChannels) {
            ChannelResponseDto result =
                    channelService.findChannel(channel.getId());

            System.out.println("\n채널: " + result.getChannelName());
            System.out.println("참여자 ID:");

            for (UUID participantId : result.getParticipantIds()) {
                System.out.println("- " + participantId);
            }
        }

        System.out.println("\n========== 잘못된 요청 및 예외 처리 ==========");

        // 이미 참여 중인 사용자가 다시 참여
        expectException("이미 참여 중인 채널에 재참여",
                () -> channelService.joinChannel(
                        freeChannel.getId(),
                        createdUsers.get(2).getId() // 찰리: 채널 생성자
                )
        );

        // 존재하지 않는 사용자
        expectException("존재하지 않는 사용자 조회",
                () -> userService.findUser(UUID.randomUUID())
        );

        // 존재하지 않는 채널
        expectException("존재하지 않는 채널 조회",
                () -> channelService.findChannel(UUID.randomUUID())
        );

        // 존재하지 않는 메시지
        expectException("존재하지 않는 메시지 조회",
                () -> messageService.findMessage(UUID.randomUUID())
        );

        // 5. 사용자별 채널 조회
        System.out.println("\n========== 5. 사용자별 채널 조회 ==========");

        for (UserResponseDto user : createdUsers) {
            List<ChannelResponseDto> channels =
                    channelService.findAllChannelsByUserId(user.getId());

            System.out.println("\n사용자: " + user.getUsername());

            for (ChannelResponseDto channel : channels) {
                System.out.println(
                        "- " + channel.getChannelName()
                                + " (" + channel.getType() + ")"
                );
            }
        }

        // 공개 채널 수정
        System.out.println("\n========== 공개 채널 수정 ==========");

        ChannelResponseDto updatedChannel = channelService.updateChannel(
                javaChannel.getId(),
                ChannelUpdateRequestDto.builder()
                        .channelName("Java 심화 스터디")
                        .description("Java 심화 내용을 공부하는 채널")
                        .build()
        );

        System.out.println("수정된 채널 이름: " + updatedChannel.getChannelName());
        System.out.println("수정된 채널 설명: " + updatedChannel.getDescription());

        // 6. 메시지 생성
        System.out.println("\n========== 6. 메시지 생성 ==========");

        MessageResponseDto message1 = messageService.createMessage(
                MessageCreateRequestDto.builder()
                        .userId(createdUsers.get(0).getId()) // 앨리스
                        .channelId(javaChannel.getId())
                        .content("안녕하세요! Java 스터디 시작합니다.")
                        .build()
        );

        MessageResponseDto message2 = messageService.createMessage(
                MessageCreateRequestDto.builder()
                        .userId(createdUsers.get(1).getId()) // 밥
                        .channelId(javaChannel.getId())
                        .content("반갑습니다!")
                        .build()
        );

        MessageResponseDto message3 = messageService.createMessage(
                MessageCreateRequestDto.builder()
                        .userId(createdUsers.get(2).getId()) // 찰리
                        .channelId(springChannel.getId())
                        .content("Spring 공부 시작해 봅시다.")
                        .build()
        );

        System.out.println("\n========== 빈 메시지 검증 ==========");

        expectException("빈 문자열 메시지 생성",
                () -> messageService.createMessage(
                        MessageCreateRequestDto.builder()
                                .userId(createdUsers.get(2).getId())
                                .channelId(javaChannel.getId())
                                .content("")
                                .build()
                )
        );

        expectException("공백만 있는 메시지 생성",
                () -> messageService.createMessage(
                        MessageCreateRequestDto.builder()
                                .userId(createdUsers.get(2).getId())
                                .channelId(javaChannel.getId())
                                .content("   ")
                                .build()
                )
        );

        List<MessageResponseDto> createdMessages = List.of(
                message1, message2, message3
        );

        for (MessageResponseDto message : createdMessages) {
            System.out.println(
                    "메시지 생성 완료: " + message.getContent()
                            + " / 작성자 ID: " + message.getSenderId()
                            + " / 채널 ID: " + message.getChannelId()
                            + " / 메시지 ID: " + message.getId()
            );
        }

        // 채널 나가기
        System.out.println("\n========== 채널 나가기 ==========");

        UUID davidId = createdUsers.get(3).getId();

        channelService.leaveChannel(
                freeChannel.getId(),
                davidId
        );

        // 채널 참여자 확인
        ChannelResponseDto afterLeave =
                channelService.findChannel(freeChannel.getId());

        boolean isParticipant = afterLeave.getParticipantIds()
                .contains(davidId);

        System.out.println("참여자 목록에 존재하는가: " + isParticipant);


        // 사용자의 채널 ID 확인
        User savedDavid = userRepository.findById(davidId)
                .orElseThrow();

        boolean hasChannelId = savedDavid.getMyChannelIds()
                .contains(freeChannel.getId());

        System.out.println("사용자의 채널 목록에 존재하는가: " + hasChannelId);


        // 사용자별 채널 조회 확인
        List<ChannelResponseDto> davidChannels =
                channelService.findAllChannelsByUserId(davidId);

        boolean canFindPrivateChannel = davidChannels.stream()
                .anyMatch(channel ->
                        channel.getId().equals(freeChannel.getId())
                );

        System.out.println("비공개 채널 조회 가능 여부: " + canFindPrivateChannel);


        // 읽음 상태 확인
        boolean hasReadStatus = readStatusRepository
                .findByUserIdAndChannelId(davidId, freeChannel.getId())
                .isPresent();

        System.out.println("읽음 상태가 남아 있는가: " + hasReadStatus);


        // 7. 메시지 단건 조회
        System.out.println("\n========== 7. 메시지 단건 조회 ==========");

        MessageResponseDto foundMessage =
                messageService.findMessage(message1.getId());

        System.out.println("조회한 메시지: " + foundMessage.getContent());


        // 8. 채널별 메시지 조회
        System.out.println("\n========== 8. 채널별 메시지 조회 ==========");

        List<MessageResponseDto> javaMessages =
                messageService.findAllMessagesByChannelId(javaChannel.getId());

        System.out.println("\nJava 스터디 메시지:");

        for (MessageResponseDto message : javaMessages) {
            System.out.println("- " + message.getContent());
        }

        List<MessageResponseDto> springMessages =
                messageService.findAllMessagesByChannelId(springChannel.getId());

        System.out.println("\nSpring 스터디 메시지:");

        for (MessageResponseDto message : springMessages) {
            System.out.println("- " + message.getContent());
        }


        // 9. 메시지 수정
        System.out.println("\n========== 9. 메시지 수정 ==========");

        MessageResponseDto updatedMessage = messageService.updateMessage(
                message1.getId(),
                MessageUpdateRequestDto.builder()
                        .content("안녕하세요! Java 스터디 시작하겠습니다.")
                        .build()
        );

        System.out.println("수정된 메시지: " + updatedMessage.getContent());


        // 10. 메시지 삭제
        System.out.println("\n========== 10. 메시지 삭제 ==========");

        messageService.deleteMessage(message2.getId());

        System.out.println("메시지 삭제 완료: " + message2.getId());

        List<MessageResponseDto> afterDeleteMessages =
                messageService.findAllMessagesByChannelId(javaChannel.getId());

        System.out.println("\n삭제 후 Java 스터디 메시지:");

        for (MessageResponseDto message : afterDeleteMessages) {
            System.out.println("- " + message.getContent());
        }

        boolean deletedMessageExists = afterDeleteMessages.stream()
                .anyMatch(message -> message.getId().equals(message2.getId()));

        System.out.println("삭제한 메시지 존재 여부: " + deletedMessageExists);

        // 11. 사용자 2명 삭제
        System.out.println("\n========== 11. 사용자 삭제 ==========");

        List<UUID> deletedUserIds = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            UserResponseDto user = createdUsers.get(i);

            userService.deleteUser(user.getId());
            deletedUserIds.add(user.getId());

            System.out.println("삭제 완료: " + user.getUsername());
        }

        // 12-1. 사용자 삭제 후 채널 확인
        System.out.println("\n========== 12-1. 사용자 삭제 후 채널 확인 ==========");

        for (ChannelResponseDto channel : createdChannels) {
            try {
                channelService.findChannel(channel.getId());
                System.out.println("채널 남아 있음: " + channel.getId());
            } catch (IllegalArgumentException e) {
                System.out.println("채널 삭제됨: " + channel.getId());
            }
        }

        // 12-2. 사용자 삭제 후 메시지 확인
        System.out.println("\n========== 12-2. 사용자 삭제 후 메시지 확인 ==========");

        for (MessageResponseDto message : createdMessages) {
            try {
                messageService.findMessage(message.getId());
                System.out.println("메시지 남아 있음: " + message.getContent());
            } catch (IllegalArgumentException e) {
                System.out.println("메시지 삭제됨: " + message.getContent());
            }
        }

        // 13. 삭제 결과 확인
        System.out.println("\n========== 13. 삭제 결과 확인 ==========");

        List<UserResponseDto> remainingUsers = userService.findAllUsers();

        for (UUID deletedId : deletedUserIds) {
            boolean exists = remainingUsers.stream()
                    .anyMatch(user -> user.getId().equals(deletedId));

            System.out.println(
                    "삭제한 사용자 ID: " + deletedId
                            + " / 존재 여부: " + exists
            );
        }

        System.out.println("\n========== 이미 삭제된 데이터 접근 ==========");

        // 이미 삭제된 사용자 조회
        expectException("삭제된 사용자 조회",
                () -> userService.findUser(createdUsers.get(0).getId())
        );

        // 이미 삭제된 사용자 재삭제
        expectException("삭제된 사용자 재삭제",
                () -> userService.deleteUser(createdUsers.get(0).getId())
        );

        // 이미 삭제된 공개 채널 조회
        expectException("삭제된 채널 조회",
                () -> channelService.findChannel(javaChannel.getId())
        );

        // 이미 삭제된 메시지 조회
        expectException("삭제된 메시지 조회",
                () -> messageService.findMessage(message1.getId())
        );

        // 이미 삭제된 메시지 재삭제
        expectException("삭제된 메시지 재삭제",
                () -> messageService.deleteMessage(message2.getId())
        );

        System.out.println("\n========== 14. 현재 실행에서 생성한 사용자 ==========");

        for (UserResponseDto user : createdUsers) {
            boolean exists = remainingUsers.stream()
                    .anyMatch(savedUser -> savedUser.getId().equals(user.getId()));

            if (exists) {
                System.out.println("남아 있음: " + user.getUsername());
            } else {
                System.out.println("삭제됨: " + user.getUsername());
            }
        }
    }

    private void expectException(String testName, Runnable action) {
        try {
            action.run();
            System.out.println("[실패] " + testName
                    + " - 예외가 발생하지 않았습니다.");

        } catch (IllegalArgumentException e) {
            System.out.println("[성공] " + testName
                    + " - " + e.getMessage());
        }
    }
}