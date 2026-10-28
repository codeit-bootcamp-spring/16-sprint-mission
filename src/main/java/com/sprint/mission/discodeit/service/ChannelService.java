package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, ChannelType channelType, String title, String description);

    void delete(UUID id);

    // 아래 메서드는 다른 서비스에서 Channel이 보관 중인 User/Message 복사본을 맞출 때 사용한다.
    // 기본 구현은 메모리상의 객체만 갱신하고, 영속화가 필요한 구현체는 오버라이드해서 저장까지 처리한다.
    default void refreshUser(User user) {
        for (Channel channel : findAll()) {
            for (User u : channel.getUsers()) {
                if (u.getId().equals(user.getId())) {
                    u.sync(user);
                }
            }
        }
    }

    default void removeUser(UUID userId) {
        for (Channel channel : findAll()) {
            channel.getUsers().removeIf(u -> u.getId().equals(userId));
        }
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
        for (Channel channel : findAll()) {
            for (Message m : channel.getChannelMessages()) {
                if (m.getId().equals(message.getId())) {
                    m.sync(message);
                }
            }
        }
    }

    default void removeMessage(UUID messageId) {
        for (Channel channel : findAll()) {
            channel.getChannelMessages().removeIf(m -> m.getId().equals(messageId));
        }
    }
}
