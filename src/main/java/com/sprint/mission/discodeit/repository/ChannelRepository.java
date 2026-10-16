package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel create(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, ChannelType channelType, String title, String description);

    void delete(UUID id);

    // 아래 메서드는 다른 서비스에서 Channel이 보관 중인 User/Message 복사본을 맞출 때 사용한다.
    // 기본 구현은 메모리상의 객체만 갱신하고, 영속화가 필요한 구현체는 오버라이드해서 저장까지 처리한다.
    default void refreshUser(User user) {
        findAll().stream()
                .flatMap(channel -> channel.getUsers().stream())
                .filter(u -> u.getId().equals(user.getId()))
                .forEach(u -> u.sync(user));
    }

    // 이미 저장된 Channel(channelId)의 참여자 목록에 User를 추가한다.
    // 추가했으면 true, 이미 참여 중이거나 Channel이 없어서 추가하지 않았으면 false를 반환한다.
    default boolean addUser(UUID channelId, User user) {
        return findById(channelId)
                .filter(channel -> channel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId())))
                .map(channel -> {
                    channel.addUser(user);
                    return true;
                })
                .orElse(false);
    }

    // 이미 저장된 Channel(channelId)의 참여자 목록에서 User를 제거한다.
    // 제거했으면 true, 참여 중이 아니었거나 Channel이 없으면 false를 반환한다.
    default boolean removeUser(UUID channelId, UUID userId) {
        return findById(channelId)
                .map(channel -> channel.getUsers().removeIf(u -> u.getId().equals(userId)))
                .orElse(false);
    }

    default void removeUser(UUID userId) {
        findAll().forEach(channel -> channel.getUsers().removeIf(u -> u.getId().equals(userId)));
    }

    // Message가 속한 Channel(message.getChannel())이 보관하는 Message 목록에 새 Message를 추가한다.
    default void addMessage(Message message) {
        findById(message.getChannel().getId()).ifPresent(channel -> {
            if (channel.getChannelMessages().stream().noneMatch(m -> m.getId().equals(message.getId()))) {
                channel.addMessage(message);
            }
        });
    }

    default void refreshMessage(Message message) {
        findAll().stream()
                .flatMap(channel -> channel.getChannelMessages().stream())
                .filter(m -> m.getId().equals(message.getId()))
                .forEach(m -> m.sync(message));
    }

    default void removeMessage(UUID messageId) {
        findAll().forEach(channel -> channel.getChannelMessages().removeIf(m -> m.getId().equals(messageId)));
    }
}
