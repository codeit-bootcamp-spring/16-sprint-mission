package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String newMessage);

    void delete(UUID id);

    // 아래 메서드는 다른 서비스에서 Message가 보관 중인 User/Channel 복사본을 맞출 때 사용한다.
    // 기본 구현은 메모리상의 객체만 갱신하고, 영속화가 필요한 구현체는 오버라이드해서 저장까지 처리한다.
    default void refreshUser(User user) {
        for (Message message : findAll()) {
            if (message.getUser().getId().equals(user.getId())) {
                message.getUser().sync(user);
            }
        }
    }

    default void refreshChannel(Channel channel) {
        for (Message message : findAll()) {
            if (message.getChannel().getId().equals(channel.getId())) {
                message.getChannel().sync(channel);
            }
        }
    }
}
