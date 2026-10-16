package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User create(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    User update(UUID id, String userName, String email, String password);

    void delete(UUID id);

    // 아래 메서드는 다른 서비스에서 User가 보관 중인 Channel/Message 복사본을 맞출 때 사용한다.
    default void refreshChannel(Channel channel) {
        findAll().stream()
                .flatMap(user -> user.getChannels().stream())
                .filter(c -> c.getId().equals(channel.getId()))
                .forEach(c -> c.sync(channel));
    }

    // 이미 저장된 User(userId)가 참여하는 Channel 목록에 새 Channel을 추가한다.
    default void addChannel(UUID userId, Channel channel) {
        findById(userId).ifPresent(user -> {
            if (user.getChannels().stream().noneMatch(c -> c.getId().equals(channel.getId()))) {
                user.addChannel(channel);
            }
        });
    }

    // 이미 저장된 User(userId)가 참여하는 Channel 목록에서 해당 Channel만 제거한다.
    default void removeChannel(UUID userId, UUID channelId) {
        findById(userId).ifPresent(user -> user.getChannels().removeIf(c -> c.getId().equals(channelId)));
    }

    default void removeChannel(UUID channelId) {
        findAll().forEach(user -> user.getChannels().removeIf(c -> c.getId().equals(channelId)));
    }

    default void removeMessage(UUID messageId) {
        findAll().forEach(user -> user.getMyMessages().removeIf(m -> m.getId().equals(messageId)));
    }

    default void addMessage(Message message) {
        findById(message.getUser().getId()).ifPresent(user -> {
            if (user.getMyMessages().stream().noneMatch(m -> m.getId().equals(message.getId()))) {
                user.addMessage(message);
            }
        });
    }

    default void refreshMessage(Message message) {
        findAll().stream()
                .flatMap(user -> user.getMyMessages().stream())
                .filter(m -> m.getId().equals(message.getId()))
                .forEach(m -> m.sync(message));
    }
}
