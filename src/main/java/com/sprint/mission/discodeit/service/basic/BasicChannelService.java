package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.Scanner;

public interface BasicChannelService {

    Channel createChannel(User user, Scanner scanner);

    void listChannel(User user);

    void modifyChannelInfo(User user, Channel channel ,Scanner scanner);

    void deleteChannel(User user, Channel channel);

    void joinChannel(User user, Channel channel);

    void leaveChannel(User user, Channel channel);
}
