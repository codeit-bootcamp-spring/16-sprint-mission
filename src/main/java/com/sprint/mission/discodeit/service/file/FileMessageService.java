package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {

    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User sender, Channel channel) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        if (sender == null || channel == null) {
            throw new IllegalArgumentException("메시지 작성자와 채널은 필수입니다.");
        }

        User validSender = this.userService.findById(sender.getId());
        Channel validChannel = this.channelService.findById(channel.getId());

        Message message = new Message(content, validSender, validChannel);

        String fileName = "./data/messages/" + message.getId() + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        List<UUID> messageIds;
        List<Message> messages;

        try (Stream<Path> files = Files.list(Path.of("./data/messages/"))) {
            messageIds = files.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".ser"))
                    .map(name -> name.substring(0, name.length() - 4))
                    .map(UUID::fromString)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        messages = messageIds.stream()
                .map(this::findById)
                .toList();

        // System.out.println("All messages : " + messages);

        return messages;

    }

    @Override
    public Message findById(UUID uuid) {
        Message message = null;

        try (FileInputStream fis = new FileInputStream("./data/messages/" + uuid + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            message = (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return message;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        Message message = this.findById(id);
        message.updateContent(content);

        String fileName = "./data/messages/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(message); // 수정된 객체로 덮어쓰기

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return message;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        try {
            Files.deleteIfExists(Path.of("./data/messages/" + uuid + ".ser"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteMessageWithoutChannel() {

        List<Channel> channelList = channelService.findAll();
        List<UUID> channelIds = channelList.stream()
                .map(Channel::getId)
                .toList();

        List<UUID> messageIds;
        List<Message> messages;

        try (Stream<Path> files = Files.list(Path.of("./data/messages/"))) {
            messageIds = files.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".ser"))
                    .map(name -> name.substring(0, name.length() - 4))
                    .map(UUID::fromString)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        messages = messageIds.stream()
                .map(this::findById)
                .toList();

        messages.stream()
                .filter(msg -> msg.getChannel() == null
                        || !channelIds.contains(msg.getChannel().getId()))
                .forEach(msg -> this.deleteMessage(msg.getId()));

    }
}
