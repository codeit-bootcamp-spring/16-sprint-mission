package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {

    private final Path messageDirectory;

    private UserService userService;
    private ChannelService channelService;

    public FileMessageService() {
        this(Path.of("data", "messages"));
    }

    public FileMessageService(Path messageDirectory) {
        this.messageDirectory = messageDirectory;

        try {
            Files.createDirectories(messageDirectory);
        } catch (IOException e) {
            throw new RuntimeException("메시지 디렉터리 생성에 실패했습니다.", e);
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    private Path fileFor(UUID messageId) {
        return messageDirectory.resolve(messageId + ".ser");
    }

    private void writeMessage(Message message) {
        Path file = fileFor(message.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장에 실패했습니다.", e);
        }
    }

    private Message readMessage(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일을 읽는 데 실패했습니다.", e);
        }
    }

    private List<Message> readAllMessages() {
        List<Message> messages = new ArrayList<>();

        try (Stream<Path> files = Files.list(messageDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> messages.add(readMessage(path)));
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return messages;
    }

    // 참여 검증
    private void validateParticipant(User user, Channel channel, String action) {
        if (!channelService.isParticipant(channel.getId(), user.getId())) {
            throw new IllegalArgumentException("채널에 참여한 유저만 메시지를 " + action + "할 수 있습니다.");
        }
    }

    // 작성자가 본인인지 검증
    private void validateSender(Message message, UUID userId, String action) {
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 메시지만 " + action + "할 수 있습니다.");
        }
    }

    // 생성
    @Override
    public Message createMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지가 null입니다.");
        }

        if (Files.exists(fileFor(message.getId()))) {
            throw new IllegalArgumentException("이미 존재하는 메시지입니다.");
        }

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());

        validateParticipant(user, channel, "작성");

        user.addMessage(message);
        channel.addChannelMessages(message);
        writeMessage(message);

        return message;
    }

    // 조회
    @Override
    public Message getMessageById(UUID messageId) {
        Path file = fileFor(messageId);

        if (Files.notExists(file)) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        return readMessage(file);
    }

    @Override
    public List<Message> getAllMessages() {
        return List.copyOf(readAllMessages());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        userService.getUserById(userId);

        return readAllMessages().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        channelService.getChannelById(channelId);

        return readAllMessages().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> getMessagesByUserAndChannel(UUID userId, UUID channelId) {
        userService.getUserById(userId);
        channelService.getChannelById(channelId);

        return readAllMessages().stream()
                .filter(message -> message.getSender().getId().equals(userId)
                        && message.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    // 수정
    @Override
    public Message updateMessage(UUID messageId, UUID userId, String content) {
        Message message = getMessageById(messageId);
        validateSender(message, userId, "수정");

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());
        validateParticipant(user, channel, "수정");

        message.updateMessage(content);
        writeMessage(message);

        return message;
    }

    // 삭제
    private void deleteMessageFile(UUID messageId) {
        try {
            Files.delete(fileFor(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = getMessageById(messageId);
        validateSender(message, userId, "삭제");

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());
        validateParticipant(user, channel, "삭제");

        deleteMessageFile(messageId);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        channelService.getChannelById(channelId);

        for (Message message : getMessagesByChannelId(channelId)) {
            deleteMessageFile(message.getId());
        }
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        userService.getUserById(userId);

        for (Message message : getMessagesByUserId(userId)) {
            deleteMessageFile(message.getId());
        }
    }
}
