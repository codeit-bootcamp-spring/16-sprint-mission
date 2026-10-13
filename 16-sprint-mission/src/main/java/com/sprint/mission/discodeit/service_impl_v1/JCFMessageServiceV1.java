package com.sprint.mission.discodeit.service_impl_v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class JCFMessageServiceV1 implements MessageService {

    private final Map<String, Message> data;

    public JCFMessageServiceV1() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String message, String channelId, String userId) {
        Message newMessage = new Message(message, channelId, userId);
        data.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    @Override
    public Message read(String id) throws Exception {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            throw new Exception("NPE 발생");
        }
        return message;
    }

    @Override
    public void update(String id, String newMessage) throws Exception {
        Message message = read(id);
        message.setMessage(newMessage);
        message.setUpdatedAt(Instant.now().getEpochSecond());
    }

    @Override
    public Message delete(String id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 id입니다.");
        }
        return message;
    }
}
