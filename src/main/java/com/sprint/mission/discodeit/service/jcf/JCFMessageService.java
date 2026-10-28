package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자 초기화
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(Message message) {
        // 작성자와 채널이 실제로 존재하는지 확인한다.
        User planner = userService.readUser(message.getPlannerId());
        userService.readUser(message.getPlannerId());
        channelService.readChannel(message.getChannelId());

        // 작성자가 해당 채널에 참여 중인지 확인한다.
        if (!channelService.isMember(message.getChannelId(), message.getPlannerId())){
            throw new IllegalArgumentException
                    ("\'" + planner.getName() + "\' (이)가 해당 채널에 참여 중이 아닙니다.");
        }

        data.add(message);  // 메시지를 목록에 추가한다.
        return message;     // 추가한 메시지를 반환한다.
    }

    @Override
    public Message readMessage(UUID id) {
        // 요청받은 ID와 일치하는 메시지를 찾아 반환한다.
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst() // 조건에 맞는 첫 메시지를 가져온다.
                // 일치하는 메시지가 없으면 예외를 발생시킨다.
                .orElseThrow(
                        () -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> readAllMessage() {
        // data에 저장된 메시지들을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        // 수정할 메시지를 찾는다.
        Message targetMessage = readMessage(id);

        // 기존 메시지 내용과 수정할 내용이 동일한지 확인한다.
        if (targetMessage.getContent().equals(content)){
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }
        targetMessage.changeContent(content); // 내용이 다를 때만 변경한다.
        return targetMessage;   // 내용을 변경한 메시지 객체를 반환한다.
    }

    // 전달받은 ID와 일치하는 메시지를 찾아 삭제한다.
    @Override
    public void deleteMessage(UUID id) {
        Message targetMessage = data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다."));

        // stream 탐색이 끝난 후 해당 메시지를 목록에서 삭제한다.
        data.remove(targetMessage);
    }
}