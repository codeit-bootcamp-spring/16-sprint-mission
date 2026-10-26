package discodeit.service;

import discodeit.entity.Channel;
import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface ChannelService {
    public Channel create(List<User> userList, String channelName);

    public Optional<Channel> find(UUID channelId);

    public List<Channel> findAll();

    public Channel update(UUID channelId, String newName);

    public void delete(UUID channelId);

    public void deleteUserFromChannels(UUID userId);

    public void addDeleteListener(Consumer<UUID> consumer);
}
