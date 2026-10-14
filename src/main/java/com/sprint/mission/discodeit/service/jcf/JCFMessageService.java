package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final List<Message> data;



    private final UserService userService;

    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
        this.data = new ArrayList<>();
    }
    @Override
    public Message create(String comment, UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if(user == null || channel == null){
            System.out.println("유저또는 채널이 유효하지않습니다");
            return null;
        }
        Message message = new Message(comment, user, channel);
        data.add(message);
        user.getMessages().add(message);
        channel.getMessages().add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String comment) {
        Message message = findById(id);
        if (message == null) {
            return null;
        }
        message.update(comment);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        if(message == null){
            return;
        }
        data.remove(message);
        message.getUser().getMessages().remove(message);
        message.getChannel().getMessages().remove(message);
    }
}
