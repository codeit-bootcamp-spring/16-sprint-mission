package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.Scanner;

public interface BasicMessageService {

    void sendMessage(User user, Channel channel, String message);

    void deleteMessage(User user, Channel channel, Message message);

    void modifyMessage(User user, Channel channel, Message message, Scanner scanner);

    void listMessages(User user, Channel channel);

    void refreshUser(User user);

    void refreshChannel(Channel channel);
}
