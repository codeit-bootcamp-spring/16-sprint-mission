package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    User update(UUID id, String userName, String email, String password);

    void delete(UUID id);

    // 아래 메서드는 다른 서비스에서 User가 보관 중인 Channel/Message 복사본을 맞출 때 사용한다.
    // 기본 구현은 메모리상의 객체만 갱신하고, 영속화가 필요한 구현체는 오버라이드해서 저장까지 처리한다.
    default void refreshChannel(Channel channel) {
        for (User user : findAll()) {
            for (Channel c : user.getChannels()) {
                if (c.getId().equals(channel.getId())) {
                    c.sync(channel);
                }
            }
        }
    }

    default void removeChannel(UUID channelId) {
        for (User user : findAll()) {
            user.getChannels().removeIf(c -> c.getId().equals(channelId));
        }
    }

    default void removeMessage(UUID messageId) {
        for (User user : findAll()) {
            user.getMyMessages().removeIf(m -> m.getId().equals(messageId));
        }
    }
}
