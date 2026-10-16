package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.Scanner;
import java.util.UUID;

public interface BasicChannelService {

    Channel createChannel(User user, Scanner scanner);

    void listChannel(User user);

    void modifyChannelInfo(User user, Channel channel ,Scanner scanner);

    void deleteChannel(User user, Channel channel);

    void joinChannel(User user, Channel channel);

    void leaveChannel(User user, Channel channel);

    // 아래 메서드는 다른 서비스에서 Channel이 보관 중인 User/Message 목록과 복사본을 맞추고 저장할 때 사용한다.
    // 이미 저장된 Channel(channelId)의 참여자 목록에 User를 추가한다.
    // 추가했으면 true, 이미 참여 중이거나 Channel이 없어서 추가하지 않았으면 false를 반환한다.
    boolean addUser(UUID channelId, User user);

    // 이미 저장된 Channel(channelId)의 참여자 목록에서 User를 제거한다.
    // 제거했으면 true, 참여 중이 아니었거나 Channel이 없으면 false를 반환한다.
    boolean removeUser(UUID channelId, UUID userId);

    // 모든 Channel의 참여자 목록에서 해당 User를 제거한다.
    void removeUser(UUID userId);

    void refreshUser(User user);

    // Message가 속한 Channel의 메시지 목록에 Message를 추가한다. 이미 있으면 추가하지 않는다.
    void addMessage(Message message);

    void removeMessage(UUID messageId);

    void refreshMessage(Message message);
}
