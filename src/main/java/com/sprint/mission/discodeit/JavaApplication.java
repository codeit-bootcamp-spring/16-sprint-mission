package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.file.Path;
import java.util.UUID;

public class JavaApplication {

    @FunctionalInterface
    interface PersistenceCheck {
        void verify(
                UUID userId,
                UUID channelId,
                UUID messageId,
                String username,
                String content
        );
    }

    static User setupUser(UserService userService) {
        return userService.create(
                "장치욱",
                "spsb7319@naver.com",
                "7319"
        );
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create(
                ChannelType.PUBLIC,
                "공지",
                "공지 채널입니다."
        );
    }

    static Message messageCreateTest(
            MessageService messageService,
            Channel channel,
            User author
    ) {
        Message message = messageService.create(
                "안녕하세요.",
                author.getId(),
                channel.getId()
        );

        System.out.println("메시지 생성: " + message.getId());

        return message;
    }

    public static void main(String[] args) {

        UserService jcfUsers = new JCFUserService();

        ChannelService jcfChannels =
                new JCFChannelService(jcfUsers);

        MessageService jcfMessages =
                new JCFMessageService(jcfUsers, jcfChannels);

        test(
                "기존 JCFService",
                jcfUsers,
                jcfChannels,
                jcfMessages,
                (userId, channelId, messageId, username, content) -> {}
        );

        Path fileServicePath = Path.of(
                "data",
                "file-service",
                UUID.randomUUID().toString()
        );

        UserService fileUsers =
                new FileUserService(fileServicePath);

        ChannelService fileChannels =
                new FileChannelService(fileUsers, fileServicePath);

        MessageService fileMessages =
                new FileMessageService(
                        fileUsers,
                        fileChannels,
                        fileServicePath
                );

        test(
                "FileService",
                fileUsers,
                fileChannels,
                fileMessages,
                (userId, channelId, messageId, username, content) -> {

                    UserService reloadedUsers =
                            new FileUserService(fileServicePath);

                    ChannelService reloadedChannels =
                            new FileChannelService(
                                    reloadedUsers,
                                    fileServicePath
                            );

                    MessageService reloadedMessages =
                            new FileMessageService(
                                    reloadedUsers,
                                    reloadedChannels,
                                    fileServicePath
                            );

                    User savedUser = reloadedUsers.findById(userId);
                    Channel savedChannel = reloadedChannels.findById(channelId);
                    Message savedMessage = reloadedMessages.findById(messageId);

                    verify(
                            savedUser.getUsername().equals(username),
                            "FileService 사용자 정보 복구 실패"
                    );

                    verify(
                            savedUser.getChannelIds().contains(channelId),
                            "FileService 사용자 채널 관계 복구 실패"
                    );

                    verify(
                            savedUser.getMessageIds().contains(messageId),
                            "FileService 사용자 메시지 관계 복구 실패"
                    );

                    verify(
                            savedChannel.getMemberIds().contains(userId),
                            "FileService 채널 참여 관계 복구 실패"
                    );

                    verify(
                            savedChannel.getMessageIds().contains(messageId),
                            "FileService 채널 메시지 관계 복구 실패"
                    );

                    verify(
                            savedMessage.getContent().equals(content),
                            "FileService 메시지 복구 실패"
                    );

                    System.out.println("FileService 객체 직렬화 테스트 성공");
                    System.out.println("FileService 파일 재조회 테스트 성공");
                    System.out.println("FileService 관계 정보 영속화 테스트 성공");
                }
        );

        UserRepository jcfUserRepository =
                new JCFUserRepository();

        ChannelRepository jcfChannelRepository =
                new JCFChannelRepository();

        MessageRepository jcfMessageRepository =
                new JCFMessageRepository();

        UserService basicJcfUsers =
                new BasicUserService(jcfUserRepository);

        ChannelService basicJcfChannels =
                new BasicChannelService(
                        jcfChannelRepository,
                        jcfUserRepository
                );

        MessageService basicJcfMessages =
                new BasicMessageService(
                        jcfMessageRepository,
                        jcfUserRepository,
                        jcfChannelRepository
                );

        test(
                "BasicService + JCFRepository",
                basicJcfUsers,
                basicJcfChannels,
                basicJcfMessages,
                (userId, channelId, messageId, username, content) -> {}
        );

        Path fileRepositoryPath = Path.of(
                "data",
                "file-repository",
                UUID.randomUUID().toString()
        );

        UserRepository fileUserRepository =
                new FileUserRepository(fileRepositoryPath);

        ChannelRepository fileChannelRepository =
                new FileChannelRepository(fileRepositoryPath);

        MessageRepository fileMessageRepository =
                new FileMessageRepository(fileRepositoryPath);

        UserService basicFileUsers =
                new BasicUserService(fileUserRepository);

        ChannelService basicFileChannels =
                new BasicChannelService(
                        fileChannelRepository,
                        fileUserRepository
                );

        MessageService basicFileMessages =
                new BasicMessageService(
                        fileMessageRepository,
                        fileUserRepository,
                        fileChannelRepository
                );

        test(
                "BasicService + FileRepository",
                basicFileUsers,
                basicFileChannels,
                basicFileMessages,
                (userId, channelId, messageId, username, content) -> {

                    UserRepository reloadedUsers =
                            new FileUserRepository(fileRepositoryPath);

                    ChannelRepository reloadedChannels =
                            new FileChannelRepository(fileRepositoryPath);

                    MessageRepository reloadedMessages =
                            new FileMessageRepository(fileRepositoryPath);

                    User savedUser = reloadedUsers.findById(userId);
                    Channel savedChannel = reloadedChannels.findById(channelId);
                    Message savedMessage = reloadedMessages.findById(messageId);

                    verify(
                            savedUser != null && savedUser.getUsername().equals(username),
                            "FileRepository 사용자 정보 복구 실패"
                    );

                    verify(
                            savedUser.getChannelIds().contains(channelId),
                            "FileRepository 사용자 채널 관계 복구 실패"
                    );

                    verify(
                            savedUser.getMessageIds().contains(messageId),
                            "FileRepository 사용자 메시지 관계 복구 실패"
                    );

                    verify(
                            savedChannel != null
                                    && savedChannel.getMemberIds().contains(userId),
                            "FileRepository 채널 참여 관계 복구 실패"
                    );

                    verify(
                            savedChannel.getMessageIds().contains(messageId),
                            "FileRepository 채널 메시지 관계 복구 실패"
                    );

                    verify(
                            savedMessage != null
                                    && savedMessage.getContent().equals(content),
                            "FileRepository 메시지 복구 실패"
                    );

                    System.out.println("FileRepository 객체 직렬화 테스트 성공");
                    System.out.println("FileRepository 파일 재조회 테스트 성공");
                    System.out.println("FileRepository 관계 정보 영속화 테스트 성공");
                }
        );
    }

    private static void test(
            String name,
            UserService users,
            ChannelService channels,
            MessageService messages,
            PersistenceCheck persistenceCheck
    ) {
        System.out.println("==== " + name + " ====");

        User user = setupUser(users);
        Channel channel = setupChannel(channels);

        System.out.println("사용자 생성: " + user.getUsername());
        System.out.println("사용자 ID: " + user.getId());

        System.out.println("채널 생성: " + channel.getName());
        System.out.println("채널 ID: " + channel.getId());

        verify(
                user.getId() != null
                        && channel.getId() != null,
                "UUID 생성 실패"
        );

        verify(
                user.getCreatedAt() != null
                        && user.getUpdatedAt() != null
                        && channel.getCreatedAt() != null
                        && channel.getUpdatedAt() != null,
                "생성 시간 및 수정 시간 초기화 실패"
        );

        verify(
                user.getUpdatedAt() >= user.getCreatedAt()
                        && channel.getUpdatedAt() >= channel.getCreatedAt(),
                "시간 정보 검증 실패"
        );

        System.out.println("UUID 자동 생성 테스트 성공");
        System.out.println("생성 시간 및 수정 시간 초기화 테스트 성공");

        verify(
                channel.getType() == ChannelType.PUBLIC,
                "채널 타입 검증 실패"
        );

        System.out.println("채널 타입 PUBLIC 생성 테스트 성공");

        User foundUser = users.findById(user.getId());
        Channel foundChannel = channels.findById(channel.getId());

        printUser(foundUser);
        printChannel(foundChannel);

        verify(
                users.findAll().size() == 1,
                "사용자 전체 조회 실패"
        );

        verify(
                channels.findAll().size() == 1,
                "채널 전체 조회 실패"
        );

        System.out.println("사용자 및 채널 단건 조회 테스트 성공");
        System.out.println("사용자 및 채널 전체 조회 테스트 성공");

        expectIllegalArgument(
                "이메일 중복 검증",
                () -> users.create(
                        "장치욱",
                        "spsb7319@naver.com",
                        "7319"
                )
        );

        expectIllegalArgument(
                "채널 이름 중복 검증",
                () -> channels.create(
                        ChannelType.PUBLIC,
                        "공지",
                        "중복 채널"
                )
        );

        expectIllegalArgument(
                "사용자 이름 유효성 검증",
                () -> users.create(
                        "",
                        "test@naver.com",
                        "1234"
                )
        );

        expectIllegalArgument(
                "채널 이름 유효성 검증",
                () -> channels.create(
                        "",
                        "잘못된 채널 이름"
                )
        );

        UUID missingUserId = UUID.randomUUID();
        UUID missingChannelId = UUID.randomUUID();
        UUID missingMessageId = UUID.randomUUID();

        expectIllegalArgument(
                "존재하지 않는 사용자 조회",
                () -> users.findById(missingUserId)
        );

        expectIllegalArgument(
                "존재하지 않는 채널 조회",
                () -> channels.findById(missingChannelId)
        );

        expectIllegalArgument(
                "존재하지 않는 메시지 조회",
                () -> messages.findById(missingMessageId)
        );

        expectIllegalArgument(
                "존재하지 않는 사용자 삭제",
                () -> users.delete(missingUserId)
        );

        expectIllegalArgument(
                "존재하지 않는 채널 삭제",
                () -> channels.delete(missingChannelId)
        );

        expectIllegalArgument(
                "존재하지 않는 메시지 삭제",
                () -> messages.delete(missingMessageId)
        );

        expectIllegalArgument(
                "채널 미참여 사용자 메시지 작성 제한",
                () -> messages.create(
                        "참여하지 않은 사용자의 메시지",
                        user.getId(),
                        channel.getId()
                )
        );

        channels.joinChannel(
                channel.getId(),
                user.getId()
        );

        verify(
                users.findById(user.getId())
                        .getChannelIds().contains(channel.getId()),
                "사용자 채널 관계 저장 실패"
        );

        verify(
                channels.findById(channel.getId())
                        .getMemberIds().contains(user.getId()),
                "채널 사용자 관계 저장 실패"
        );

        System.out.println("사용자와 채널 양방향 관계 테스트 성공");
        System.out.println("채널 참여 후 사용자 정보 저장 테스트 성공");

        expectIllegalArgument(
                "중복 채널 참여 검증",
                () -> channels.joinChannel(
                        channel.getId(),
                        user.getId()
                )
        );

        expectIllegalArgument(
                "존재하지 않는 사용자 메시지 생성",
                () -> messages.create(
                        "잘못된 사용자 메시지",
                        missingUserId,
                        channel.getId()
                )
        );

        expectIllegalArgument(
                "존재하지 않는 채널 메시지 생성",
                () -> messages.create(
                        "잘못된 채널 메시지",
                        user.getId(),
                        missingChannelId
                )
        );

        expectIllegalArgument(
                "빈 메시지 내용 검증",
                () -> messages.create(
                        "",
                        user.getId(),
                        channel.getId()
                )
        );

        Message message = messageCreateTest(
                messages,
                channel,
                user
        );

        verify(
                message.getId() != null
                        && message.getCreatedAt() != null
                        && message.getUpdatedAt() != null,
                "메시지 공통 필드 검증 실패"
        );

        verify(
                messages.findAll().size() == 1,
                "메시지 전체 조회 실패"
        );

        verify(
                users.findById(user.getId())
                        .getMessageIds().contains(message.getId()),
                "사용자 메시지 관계 저장 실패"
        );

        verify(
                channels.findById(channel.getId())
                        .getMessageIds().contains(message.getId()),
                "채널 메시지 관계 저장 실패"
        );

        System.out.println("메시지 생성 및 조회 테스트 성공");
        System.out.println("메시지 작성자 및 채널 연관관계 테스트 성공");
        System.out.println("메시지 양방향 관계 저장 테스트 성공");

        printMessage(messages.findById(message.getId()));

        expectIllegalState(
                "메시지가 남아 있는 사용자 삭제 제한",
                () -> users.delete(user.getId())
        );

        expectIllegalState(
                "메시지가 남아 있는 채널 삭제 제한",
                () -> channels.delete(channel.getId())
        );

        expectIllegalState(
                "작성한 메시지가 남아 있는 채널 탈퇴 제한",
                () -> channels.leaveChannel(
                        channel.getId(),
                        user.getId()
                )
        );

        User updatedUser = users.update(
                user.getId(),
                "장시우",
                "spsb7937@naver.com",
                "7937"
        );

        Channel updatedChannel = channels.update(
                channel.getId(),
                "공지사항",
                "변경된 공지 채널입니다."
        );

        Message updatedMessage = messages.update(
                message.getId(),
                "반갑습니다."
        );

        verify(
                updatedUser.getId().equals(user.getId())
                        && updatedUser.getUsername().equals("장시우")
                        && updatedUser.getEmail().equals("spsb7937@naver.com")
                        && updatedUser.getPassword().equals("7937"),
                "사용자 수정 및 반환값 검증 실패"
        );

        verify(
                updatedChannel.getId().equals(channel.getId())
                        && updatedChannel.getName().equals("공지사항")
                        && updatedChannel.getDescription()
                        .equals("변경된 공지 채널입니다."),
                "채널 수정 및 반환값 검증 실패"
        );

        verify(
                updatedMessage.getId().equals(message.getId())
                        && updatedMessage.getContent().equals("반갑습니다."),
                "메시지 수정 및 반환값 검증 실패"
        );

        verify(
                updatedUser.getUpdatedAt() >= updatedUser.getCreatedAt()
                        && updatedChannel.getUpdatedAt() >= updatedChannel.getCreatedAt()
                        && updatedMessage.getUpdatedAt() >= updatedMessage.getCreatedAt(),
                "수정 시간 검증 실패"
        );

        System.out.println("사용자 수정 및 객체 반환 테스트 성공");
        System.out.println("채널 수정 및 객체 반환 테스트 성공");
        System.out.println("메시지 수정 및 객체 반환 테스트 성공");
        System.out.println("수정 시간 갱신 테스트 성공");

        printUser(users.findById(user.getId()));
        printChannel(channels.findById(channel.getId()));
        printMessage(messages.findById(message.getId()));

        persistenceCheck.verify(
                user.getId(),
                channel.getId(),
                message.getId(),
                "장시우",
                "반갑습니다."
        );

        messages.delete(message.getId());

        verify(
                messages.findAll().isEmpty(),
                "메시지 삭제 실패"
        );

        verify(
                users.findById(user.getId())
                        .getMessageIds().isEmpty(),
                "메시지 삭제 후 사용자 정보 갱신 실패"
        );

        verify(
                channels.findById(channel.getId())
                        .getMessageIds().isEmpty(),
                "메시지 삭제 후 채널 정보 갱신 실패"
        );

        System.out.println("메시지 삭제 테스트 성공");
        System.out.println("메시지 삭제 후 양방향 관계 갱신 테스트 성공");

        expectIllegalArgument(
                "삭제된 메시지 조회",
                () -> messages.findById(message.getId())
        );

        channels.leaveChannel(
                channel.getId(),
                user.getId()
        );

        verify(
                users.findById(user.getId())
                        .getChannelIds().isEmpty(),
                "채널 탈퇴 후 사용자 정보 갱신 실패"
        );

        verify(
                channels.findById(channel.getId())
                        .getMemberIds().isEmpty(),
                "채널 탈퇴 후 채널 정보 갱신 실패"
        );

        System.out.println("채널 탈퇴 테스트 성공");
        System.out.println("채널 탈퇴 후 사용자 정보 재저장 테스트 성공");

        users.delete(user.getId());
        channels.delete(channel.getId());

        verify(
                users.findAll().isEmpty(),
                "사용자 삭제 실패"
        );

        verify(
                channels.findAll().isEmpty(),
                "채널 삭제 실패"
        );

        verify(
                messages.findAll().isEmpty(),
                "메시지 삭제 확인 실패"
        );

        System.out.println("사용자 삭제 테스트 성공");
        System.out.println("채널 삭제 테스트 성공");
        System.out.println("전체 데이터 삭제 확인 테스트 성공");

        expectIllegalArgument(
                "삭제된 사용자 조회",
                () -> users.findById(user.getId())
        );

        expectIllegalArgument(
                "삭제된 채널 조회",
                () -> channels.findById(channel.getId())
        );

        System.out.println(name + " CRUD 및 관계 테스트 성공");
    }

    private static void printUser(User user) {
        System.out.println("사용자 ID: " + user.getId());
        System.out.println("사용자 이름: " + user.getUsername());
        System.out.println("사용자 이메일: " + user.getEmail());
        System.out.println("참여 채널: " + user.getChannelIds());
        System.out.println("작성 메시지: " + user.getMessageIds());
        System.out.println("생성 시간: " + user.getCreatedAt());
        System.out.println("수정 시간: " + user.getUpdatedAt());
    }

    private static void printChannel(Channel channel) {
        System.out.println("채널 ID: " + channel.getId());
        System.out.println("채널 타입: " + channel.getType());
        System.out.println("채널 이름: " + channel.getName());
        System.out.println("채널 설명: " + channel.getDescription());
        System.out.println("참여 사용자: " + channel.getMemberIds());
        System.out.println("채널 메시지: " + channel.getMessageIds());
        System.out.println("생성 시간: " + channel.getCreatedAt());
        System.out.println("수정 시간: " + channel.getUpdatedAt());
    }

    private static void printMessage(Message message) {
        System.out.println("메시지 ID: " + message.getId());
        System.out.println("메시지 내용: " + message.getContent());
        System.out.println("작성자 ID: " + message.getUserId());
        System.out.println("채널 ID: " + message.getChannelId());
        System.out.println("생성 시간: " + message.getCreatedAt());
        System.out.println("수정 시간: " + message.getUpdatedAt());
    }

    private static void expectIllegalArgument(
            String testName,
            Runnable action
    ) {
        boolean exceptionOccurred = false;

        try {
            action.run();
        } catch (IllegalArgumentException e) {
            exceptionOccurred = true;
        }

        verify(
                exceptionOccurred,
                testName + " 검증 실패"
        );

        System.out.println(testName + " 테스트 성공");
    }

    private static void expectIllegalState(
            String testName,
            Runnable action
    ) {
        boolean exceptionOccurred = false;

        try {
            action.run();
        } catch (IllegalStateException e) {
            exceptionOccurred = true;
        }

        verify(
                exceptionOccurred,
                testName + " 검증 실패"
        );

        System.out.println(testName + " 테스트 성공");
    }

    private static void verify(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}