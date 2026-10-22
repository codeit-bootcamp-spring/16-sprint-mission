package discodeit.service;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    public Message create(User user, Channel channel, String comment);

    public Optional<Message> find(UUID messageId);

    public List<Message> findAll();

    public Message update(UUID messageId, String newMessage);

    public void delete(UUID messageId);

    public void deleteMessageByUserId(UUID userId);

    public void deleteMessagesByChannelId(UUID channelId);
}