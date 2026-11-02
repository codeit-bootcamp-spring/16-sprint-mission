package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.Auth.AuthUserRequest;
import com.sprint.mission.discodeit.Dto.Auth.AuthUserResponse;
import com.sprint.mission.discodeit.Dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.BinaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.BinaryContent.BinaryContentFindResponse;
import com.sprint.mission.discodeit.Dto.Channel.*;
import com.sprint.mission.discodeit.Dto.Message.*;
import com.sprint.mission.discodeit.Dto.ReadStatus.*;
import com.sprint.mission.discodeit.Dto.User.*;

import com.sprint.mission.discodeit.Dto.UserStatus.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.AuthService;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        clearDataFiles();

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        UserService userService;
        ChannelService channelService;
        MessageService messageService;
        AuthService authService;
        ReadStatusService readStatusService;
        UserStatusService userStatusService;
        BinaryContentService binaryContentService;

        userService = context.getBean(UserService.class);
        channelService = context.getBean(ChannelService.class);
        messageService = context.getBean(MessageService.class);
        authService = context.getBean(AuthService.class);
        readStatusService = context.getBean(ReadStatusService.class);
        userStatusService = context.getBean(UserStatusService.class);
        binaryContentService = context.getBean(BinaryContentService.class);

        try {
            // 등록
            UserCreateResponse user1 = userService.create(
                    new UserCreateRequest("김철수", "cheolsu1234@gmail.com", "asdfqwer!"), null);
            UserCreateResponse user2 = userService.create(
                    new UserCreateRequest("박영수", "yeongsu5678@gmail.com", "zxcvasdf@"),
                    new UserProfileImageCreateRequest(new BinaryContentCreateRequest(
                            "image", ".png", convertFileToBytes("./data/images/image.png"))));
            System.out.printf("유저 생성 --> %s\n", user1);
            System.out.printf("유저 생성 --> %s\n\n", user2);

            ChannelPublicCreateResponse channel1 = channelService.createPublic(
                    new ChannelPublicCreateRequest("QR 코드", "QR 코드를 제공하는 채널입니다."));
            ChannelPublicCreateResponse channel2 = channelService.createPublic(
                    new ChannelPublicCreateRequest("위클리 페이퍼", "매주 위클리 페이퍼를 제공하는 채널입니다."));
            ChannelPrivateCreateResponse channel3 =
                    channelService.createPrivate(new ChannelPrivateCreateRequest(List.of(user1.id(), user2.id())));

            System.out.printf("채널 생성 --> %s\n", channel1);
            System.out.printf("채널 생성 --> %s\n", channel2);
            System.out.printf("채널 생성 --> %s\n\n", channel3);

            System.out.printf("유저 및 채널 관계 설정\n\n채널 이름: %s, 유저 가입: %s, %s\n채널 이름: %s, 유저 가입: %s\n\n"
                    , channel1.name(), user1.name(), user2.name(), channel2.name(), user1.name());
            channelService.addUserToChannel(channel1.id(), user1.id());
            channelService.addUserToChannel(channel1.id(), user2.id());
            channelService.addUserToChannel(channel2.id(), user1.id());

            MessageCreateResponse message1 = messageService.create(
                    new MessageCreateRequest(user1.id(), "안녕하세요 김철수입니다!", channel1.id()), null);
            MessageCreateResponse message2 = messageService.create(
                    new MessageCreateRequest(user2.id(), "저는 박영수입니다!", channel1.id()), null);
            MessageCreateResponse message3 = messageService.create(
                    new MessageCreateRequest(user1.id(), "안녕하세요! 제가 좋아하는 고양이 짤입니다", channel1.id()),
                    new MessageAttachmentsCreateRequest(List.of(
                            new BinaryContentCreateRequest(
                                    "cat", ".jpg", convertFileToBytes("./data/images/cat.jpg")))));

            System.out.printf("메세지 생성 --> %s\n", message1);
            System.out.printf("메세지 생성 --> %s\n", message2);
            System.out.printf("메세지 생성 --> %s\n\n", message3);

            ReadStatusCreateResponse readStatus1 = readStatusService.create(
                    new ReadStatusCreateRequest(channel1.id(), user1.id()));
            ReadStatusCreateResponse readStatus2 = readStatusService.create(
                    new ReadStatusCreateRequest(channel2.id(), user2.id()));
            System.out.printf("메세지 확인 기록 생성 --> %s\n", readStatus1);
            System.out.printf("메세지 확인 기록 생성 --> %s\n", readStatus2);

            BinaryContentCreateResponse image1 = binaryContentService.create(
                    new BinaryContentCreateRequest("cat", "jpg", convertFileToBytes("./data/images/cat.jpg")));
            System.out.printf("바이너리 데이터 생성 --> %s\n", image1);

            // 유저 인증
            AuthUserResponse auth = authService.login(new AuthUserRequest(user1.name(), "asdfqwer!"));
            System.out.printf("유저: %s, 로그인 인증 완료 --> %s\n\n", user1.name(), auth);

            // 조회 (단일)
            UserFindResponse foundUser1 = userService.findById(user1.id());
            System.out.printf("유저 단일 조회 --> %s\n\n", foundUser1);

            ChannelFindResponse foundChannel1 = channelService.findById(channel1.id());
            System.out.printf("채널 단일 조회 --> %s\n\n", foundChannel1);

            MessageFindResponse foundMessage1 = messageService.findById(message1.id());
            System.out.printf("메세지 단일 조회 --> %s\n\n", foundMessage1);

            ReadStatusFindResponse foundReadStatus1 = readStatusService.findById(readStatus1.id());
            System.out.printf("메세지 확인 기록 단일 조회 --> %s\n\n", foundReadStatus1);

            UserStatusFindResponse foundUserStatus1 = userStatusService.findByUserId(user1.id());
            System.out.printf("유저 접속 기록 단일 조회 --> %s\n\n", foundUserStatus1);

            BinaryContentFindResponse foundImage1 = binaryContentService.findById(image1.id());
            System.out.printf("바이너리 데이터 단일 조회 --> %s\n\n", foundImage1);

            // 조회 (전체)
            List<UserFindResponse> foundUsers = userService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            foundUsers.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            List<ChannelFindResponse> foundChannels = channelService.findAllByUserId(user1.id());
            System.out.printf("유저: '%s'가 조회 가능한 전체 채널 --> [\n", user1.name());
            foundChannels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            List<MessageFindResponse> foundMessages = messageService.findAllByChannelId(channel1.id());
            System.out.printf("채널: '%s'의 메세지 전체 조회 --> [\n", channel1.name());
            foundMessages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            List<ReadStatusFindResponse> foundReadStatusList = readStatusService.findAllByUserId(user1.id());
            System.out.printf("유저: '%s'의 메세지 확인 기록 전체 조회 --> [\n", user1.name());
            foundReadStatusList.forEach((readStatus) -> System.out.printf("\t%s\n", readStatus));
            System.out.print("]\n\n");

            List<UserStatusFindResponse> foundUserStatusList = userStatusService.findAll();
            System.out.print("유저 접속 기록 전체 조회 --> [\n");
            foundUserStatusList.forEach((userStatus) -> System.out.printf("\t%s\n", userStatus));
            System.out.print("]\n\n");

            List<BinaryContentFindResponse> foundBinaryContents = binaryContentService.findAll();
            System.out.print("바이너리 데이터 전체 조회 --> [\n");
            foundBinaryContents.forEach((image) -> System.out.printf("\t%s\n", image));
            System.out.print("]\n\n");

            // 수정
            UserUpdateResponse updatedUser1 = userService.update(
                    new UserUpdateRequest(user1.id(), "이수철", null, null),
                    new UserProfileImageUpdateRequest(user1.id(),
                            "cat", ".jpg", convertFileToBytes("./data/images/cat.jpg")));
            System.out.printf("유저 수정 전 --> %s\n", user1);
            System.out.printf("유저 수정 후 --> %s\n\n", updatedUser1);

            ChannelUpdateResponse updatedChannel1 = channelService.update(
                    new ChannelUpdateRequest(channel1.id(), "QR코드 2", null));
            System.out.printf("채널 수정 전 --> %s\n", channel1);
            System.out.printf("채널 수정 후 --> %s\n\n", updatedChannel1);

            MessageUpdateResponse updatedMessage1 = messageService.update(
                    new MessageUpdateRequest(message1.id(), "안녕하세요 이수철로 변경했습니다!"));
            System.out.printf("메세지 수정 전 --> %s\n", message1);
            System.out.printf("메세지 수정 후 --> %s\n\n", updatedMessage1);

            ReadStatusUpdateResponse updatedReadStatus1 =
                    readStatusService.update(new ReadStatusUpdateRequest(readStatus1.id(), Instant.now()));
            System.out.printf("메세지 확인 기록 수정 전 --> %s\n", readStatus1);
            System.out.printf("메세지 확인 기록 수정 후 --> %s\n\n", updatedReadStatus1);

            UserStatusUpdateResponse updateUserStatus1 = userStatusService.updateByUserId(
                    new UserStatusUpdateByUserIdRequest(user1.id(), Instant.now()));
            System.out.printf("유저 접속 기록 수정 전 --> %s\n", foundUserStatus1);
            System.out.printf("유저 접속 기록 수정 후 --> %s\n\n", updateUserStatus1);

            // 삭제
            System.out.printf("유저 삭제 --> %s\n", updatedUser1);
            userService.delete(updatedUser1.id());
            foundUsers = userService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            foundUsers.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            System.out.printf("채널 삭제 --> %s\n", channel2);
            channelService.delete(channel2.id());
            foundChannels = channelService.findAllByUserId(user1.id());
            System.out.printf("유저: '%s'가 조회 가능한 전체 채널 --> [\n", updatedUser1.name());
            foundChannels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            System.out.printf("메세지 삭제 --> %s\n", message2);
            messageService.delete(message2.id());
            foundMessages = messageService.findAllByChannelId(channel1.id());
            System.out.printf("채널: '%s'의 메세지 전체 조회 --> [\n", updatedChannel1.name());
            foundMessages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            foundReadStatusList = readStatusService.findAllByUserId(user2.id());
            System.out.printf("유저: '%s'의 메세지 확인 기록 전체 조회 --> [\n", user2.name());
            foundReadStatusList.forEach((readStatus) -> System.out.printf("\t%s\n", readStatus));
            System.out.print("]\n\n");

            foundUserStatusList = userStatusService.findAll();
            System.out.print("유저 접속 전체 조회 --> [\n");
            foundUserStatusList.forEach((userStatus) -> System.out.printf("\t%s\n", userStatus));
            System.out.print("]\n\n");

            System.out.printf("바이너리 데이터 삭제 --> %s\n", image1);
            binaryContentService.delete(image1.id());
            foundBinaryContents = binaryContentService.findAll();
            System.out.print("바이너리 데이터 전체 조회 --> [\n");
            foundBinaryContents.forEach((image) -> System.out.printf("\t%s\n", image));
            System.out.print("]\n\n");

            // 채널 탈퇴
            System.out.printf("유저 채널 탈퇴 --> 유저 이름: %s, 채널 이름: %s\n", user2.name(), updatedChannel1.name());
            channelService.removeUserFromChannel(updatedChannel1.id(), user2.id());
            foundChannels = channelService.findAllByUserId(user2.id());
            System.out.printf("유저: '%s'가 조회 가능한 전체 채널 --> [\n", user2.name());
            foundChannels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");


        } catch (NoSuchElementException e) {
            System.out.printf("해당 개체를 찾을 수 없습니다. 이유: %s\n", e.getMessage());
        } catch (IllegalStateException e) {
            System.out.printf("요청한 작업을 수행할 수 없는 상태입니다. 이유: %s\n", e.getMessage());
        } catch (NullPointerException e) {
            System.out.printf("개체가 참조하는 값이 없습니다. 이유: %s\n", e.getMessage());
        } catch (NoClassDefFoundError e) {
            System.out.printf("정의되지 않은 클래스입니다. 이유: %s\n", e.getMessage());
        } catch (UncheckedIOException e) {
            System.out.printf("파일 입출력 예외가 발생했습니다. 이유: %s\n", e.getMessage());
        }
    }

    // 임시 바이너리 데이터 요청용
    public static byte[] convertFileToBytes(String filePath) {
        try {
            File file = new File(filePath);
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println("입출력 예외 발생: " + e.getMessage());
        }
        return null;
    }

    public static void clearDataFiles() {
        List<String> fileList = List.of(
                ".discodeit/binary_content.ser",
                ".discodeit/channel.ser",
                ".discodeit/message.ser",
                ".discodeit/read_status.ser",
                ".discodeit/user.ser",
                ".discodeit/user_status.ser"
        );

        fileList.forEach(filePath -> {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.printf("파일 삭제: %s\n", file.getName());
                }
            }
        });
    }
}
