package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public interface BasicUserService {

    void signUp(Scanner scanner);

    Optional<User> login(Scanner scanner);

    void logout(User user);

    void modifyUserInfo(User user, Scanner scanner);

    // 아래 메서드는 다른 서비스에서 User가 보관 중인 Channel/Message 목록과 복사본을 맞추고 저장할 때 사용한다.
    // 이미 저장된 User(userId)의 채널 목록에 Channel을 추가한다. 이미 있으면 추가하지 않는다.
    void addChannel(UUID userId, Channel channel);

    // 이미 저장된 User(userId)의 채널 목록에서 해당 Channel만 제거한다.
    void removeChannel(UUID userId, UUID channelId);

    // 모든 User의 채널 목록에서 해당 Channel을 제거한다.
    void removeChannel(UUID channelId);

    void refreshChannel(Channel channel);

    // Message를 작성한 User의 메시지 목록에 Message를 추가한다. 이미 있으면 추가하지 않는다.
    void addMessage(Message message);

    void removeMessage(UUID messageId);

    void refreshMessage(Message message);

}
