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
    private final FileObjectStore<Message> messageStore;
    private final FileObjectStore<Channel> channelStore;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService,
                              ChannelService channelService,
                              Path directory) {
        this.messageStore = new FileObjectStore<>(directory.resolve("messages"), Message.class);
        this.channelStore = new FileObjectStore<>(directory.resolve("channels"), Channel.class);
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateContent(content);

        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("채널에 참여한 사용자만 메시지를 작성할 수 있습니다.");
        }

        Message message = messageStore.save(new Message(content, userId, channelId));

        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        userService.save(user);
        channelStore.save(channel);

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageStore.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageStore.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        validateContent(content);
        message.update(content);
        return messageStore.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        User user = userService.findById(message.getUserId());
        Channel channel = channelService.findById(message.getChannelId());

        messageStore.delete(id);
        user.removeMessage(id);
        channel.removeMessage(id);

        userService.save(user);
        channelStore.save(channel);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 합니다.");
        }
    }
}