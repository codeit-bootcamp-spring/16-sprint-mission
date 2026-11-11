package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.auth.LoginDto;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    CommandLineRunner test(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            AuthService authService,
            UserRepository userRepository
    ) {
        return args -> {

            System.out.println(
                    "현재 UserRepository: "
                            + userRepository.getClass().getSimpleName()
            );

            System.out.println();
            System.out.println("========== 1. 사용자 생성 ==========");

            UserDto user1 = userService.createUser(
                    new UserCreateDto(
                            "세훈",
                            26,
                            "sehun@test.com",
                            "1234",
                            null
                    )
            );

            UserDto user2 = userService.createUser(
                    new UserCreateDto(
                            "철수",
                            25,
                            "chulsoo@test.com",
                            "5678",
                            null
                    )
            );

            System.out.println("사용자1: " + user1);
            System.out.println("사용자2: " + user2);


            // UserDto에는 id가 없으므로 테스트를 위해 Repository에서 조회
            User sehun = userRepository.findByUsername("세훈")
                    .orElseThrow();

            User chulsoo = userRepository.findByUsername("철수")
                    .orElseThrow();

            UUID sehunId = sehun.getId();
            UUID chulsooId = chulsoo.getId();

            System.out.println("세훈 ID: " + sehunId);
            System.out.println("철수 ID: " + chulsooId);


            System.out.println();
            System.out.println("========== 2. 사용자 조회 ==========");

            System.out.println(
                    "username 조회: "
                            + userService.findByUsername("세훈")
            );

            System.out.println(
                    "email 조회: "
                            + userService.findByEmail("sehun@test.com")
            );

            System.out.println(
                    "id 조회: "
                            + userService.findById(sehunId)
            );

            System.out.println(
                    "전체 사용자: "
                            + userService.findAll()
            );


            System.out.println();
            System.out.println("========== 3. 로그인 ==========");

            UserDto loginUser = authService.login(
                    new LoginDto(
                            "세훈",
                            "1234"
                    )
            );

            System.out.println("로그인 성공: " + loginUser);


            System.out.println();
            System.out.println("========== 4. PUBLIC 채널 생성 ==========");

            ChannelDto publicChannel =
                    channelService.createPublicChannel(
                            new PublicChannelCreateDto(
                                    "일반",
                                    "자유롭게 대화하는 채널"
                            )
                    );

            System.out.println("PUBLIC 채널: " + publicChannel);


            System.out.println();
            System.out.println("========== 5. PRIVATE 채널 생성 ==========");

            ChannelDto privateChannel =
                    channelService.createPrivateChannel(
                            new PrivateChannelCreateDto(
                                    List.of(
                                            sehunId,
                                            chulsooId
                                    )
                            )
                    );

            System.out.println("PRIVATE 채널: " + privateChannel);


            System.out.println();
            System.out.println("========== 6. 채널 조회 ==========");

            System.out.println(
                    "PUBLIC 채널 ID 조회: "
                            + channelService.findById(publicChannel.id())
            );

            System.out.println(
                    "세훈이 접근 가능한 채널: "
                            + channelService.findAllByUserId(sehunId)
            );


            System.out.println();
            System.out.println("========== 7. 메시지 생성 ==========");

            MessageDto publicMessage =
                    messageService.createMessage(
                            new MessageCreateDto(
                                    "PUBLIC 채널 테스트 메시지입니다.",
                                    sehunId,
                                    publicChannel.id(),
                                    null
                            )
                    );

            System.out.println(
                    "PUBLIC 메시지: "
                            + publicMessage
            );


            MessageDto privateMessage =
                    messageService.createMessage(
                            new MessageCreateDto(
                                    "PRIVATE 채널 테스트 메시지입니다.",
                                    chulsooId,
                                    privateChannel.id(),
                                    null
                            )
                    );

            System.out.println(
                    "PRIVATE 메시지: "
                            + privateMessage
            );


            System.out.println();
            System.out.println("========== 8. 메시지 조회 ==========");

            System.out.println(
                    "메시지 ID 조회: "
                            + messageService.findMessageById(publicMessage.id())
            );

            System.out.println(
                    "PUBLIC 채널 메시지 목록: "
                            + messageService.findAllByChannelId(
                            publicChannel.id()
                    )
            );


            System.out.println();
            System.out.println("========== 9. 메시지 수정 ==========");

            MessageDto updatedMessage =
                    messageService.updateMessage(
                            new MessageUpdateDto(
                                    publicMessage.id(),
                                    "수정된 메시지입니다."
                            )
                    );

            System.out.println(
                    "수정 결과: "
                            + updatedMessage
            );


            System.out.println();
            System.out.println("========== 10. 메시지 삭제 ==========");

            messageService.deleteMessage(publicMessage.id());

            System.out.println(
                    "메시지 삭제 후 PUBLIC 채널: "
                            + messageService.findAllByChannelId(
                            publicChannel.id()
                    )
            );

            System.out.println();
            System.out.println("========== 11. 사용자 수정 ==========");

            UserDto updatedUser = userService.updateUser(
                    new UserUpdateDto(
                            sehunId,
                            "세훈수정",
                            27,
                            "4321",
                            null
                    )
            );

            System.out.println("사용자 수정 결과: " + updatedUser);


            System.out.println();
            System.out.println("========== 12. 수정된 사용자 조회 ==========");

            System.out.println(
                    "수정된 username 조회: "
                            + userService.findByUsername("세훈수정")
            );


            System.out.println();
            System.out.println("========== 13. 수정된 비밀번호로 로그인 ==========");

            UserDto updatedLoginUser = authService.login(
                    new LoginDto(
                            "세훈수정",
                            "4321"
                    )
            );

            System.out.println("수정 후 로그인 성공: " + updatedLoginUser);


            System.out.println();
            System.out.println("========== 14. PUBLIC 채널 수정 ==========");

            ChannelDto updatedChannel =
                    channelService.updateChannel(
                            new ChannelUpdateDto(
                                    publicChannel.id(),
                                    "일반-수정",
                                    "수정된 채널 설명입니다."
                            )
                    );

            System.out.println("채널 수정 결과: " + updatedChannel);


            System.out.println();
            System.out.println("========== 15. 첨부파일이 있는 메시지 생성 ==========");

            BinaryContentDto attachment =
                    new BinaryContentDto(
                            "test.txt",
                            4L,
                            "test".getBytes()
                    );

            MessageDto messageWithAttachment =
                    messageService.createMessage(
                            new MessageCreateDto(
                                    "첨부파일 테스트 메시지",
                                    sehunId,
                                    publicChannel.id(),
                                    List.of(attachment)
                            )
                    );

            System.out.println("첨부파일 메시지: " + messageWithAttachment);
            System.out.println("attachmentIds: " + messageWithAttachment.attachmentIds());


            System.out.println();
            System.out.println("========== 16. 메시지 생성 후 lastMessageAt 확인 ==========");

            ChannelDto channelAfterMessage =
                    channelService.findById(publicChannel.id());

            System.out.println(
                    "마지막 메시지 시간: "
                            + channelAfterMessage.lastMessageAt()
            );


            System.out.println();
            System.out.println("========== 17. 첨부파일 메시지 삭제 ==========");

            messageService.deleteMessage(messageWithAttachment.id());

            System.out.println(
                    "삭제 후 PUBLIC 채널 메시지: "
                            + messageService.findAllByChannelId(publicChannel.id())
            );


            System.out.println();
            System.out.println("========== 18. PRIVATE 채널 삭제 ==========");

            channelService.deleteChannel(privateChannel.id());

            System.out.println("PRIVATE 채널 삭제 완료");


            System.out.println();
            System.out.println("========== 19. 사용자 삭제 ==========");

            userService.deleteUser(chulsooId);

            System.out.println("철수 삭제 완료");


            System.out.println();
            System.out.println("========== 추가 테스트 완료 ==========");


            System.out.println();
            System.out.println("========== 테스트 완료 ==========");
        };
    }
}