package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.FileObjectStore;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final FileObjectStore<Message> messages;
    private final FileObjectStore<Channel> channels;

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(
            UserService userService,
            ChannelService channelService,
            Path directory
    ) {
        this.userService = userService;
        this.channelService = channelService;

        this.messages = new FileObjectStore<>(
                directory.resolve("messages"),
                Message.class
        );

        this.channels = new FileObjectStore<>(
                directory.resolve("channels"),
                Channel.class
        );
    }

    @Override
    public Message create(
            String content,
            UUID userId,
            UUID channelId
    ) {
        validate(content);

        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException(
                    "채널에 참여해야 메시지를 작성할 수 있습니다."
            );
        }

        Message message = messages.save(
                new Message(
                        content,
                        userId,
                        channelId
                )
        );

        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        userService.save(user);
        channels.save(channel);

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = messages.findById(id);

        if (message == null) {
            throw new IllegalArgumentException(
                    "메시지를 찾을 수 없습니다."
            );
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return messages.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);

        validate(content);

        message.update(content);

        return messages.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        User user = userService.findById(
                message.getUserId()
        );

        Channel channel = channelService.findById(
                message.getChannelId()
        );

        messages.delete(id);

        user.removeMessage(id);
        channel.removeMessage(id);

        userService.save(user);
        channels.save(channel);
    }

    private void validate(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용이 없습니다."
            );
        }
    }
}