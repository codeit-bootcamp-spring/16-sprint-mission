package discodeit.service;

import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface UserService {
    public User create(String name);

    public Optional<User> find(UUID userId);

    public List<User> findAll();

    public User update(UUID userId, String newName);

    public void delete(UUID userId);

    public void addDeleteListener(Consumer<UUID> listener);

    public void removeChannelFromUsers(UUID channelId);
}
