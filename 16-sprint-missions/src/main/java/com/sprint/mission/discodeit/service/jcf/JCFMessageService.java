package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message createMessage(Message message) {
        data.add(message); // 전달받을 메시지를 목록에 추가한다.
        return message; // 추가한 메시지 객체를 반환한다.
    }

    @Override
    public Message readMessage(UUID id) {
        for(Message msg : data){
            if(msg.getId().equals(id)){
                // 해당 메시지 id를 가져와 요청받은 id와 일치하면 msg 를 반환한다.
                return msg;
            }
        }
        return null; // 해당하는 메시지 id를 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public List<Message> readAllMessage() {
        // return List.of();
        // data 에 저장된 채널을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public Message updateMessage(UUID id, Message message) {
        for(Message targetMessage : data){
            if(targetMessage.getId().equals(targetMessage)){
                // 찾고자 하는 채널 id가 맞다면 index 에 대입.
                int index = data.indexOf(targetMessage);
                // 해당 인덱스의 채널 객체를 수정한 channel 객체로 교체한다.
                data.set(index, message);
                // 그리고 수정한 channel 객체를 반환한다.
                return targetMessage;
            }
        }
        return null; // 수정할 채널 id를 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public Message deleteMessage(UUID id) {
        for(Message message : data){
            if(message.getId().equals(id)){
                // 찾은 channel 목록에서 제거한 후 반환한다.
                data.remove(message);
                return message;
            }
        }
        return null;
    }
}
