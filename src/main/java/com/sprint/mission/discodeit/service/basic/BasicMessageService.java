package com.sprint.mission.discodeit.service.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicMessageService implements MessageService {

    // 필드
    private final MessageRepository messageRepository; // 메시지 데이터의 저장과 조회를 담당할 Repository
    private final UserService userService; // 작성자의 존재 여부를 확인하기 위한 UserService
    private final ChannelService channelService; // 채널의 존재 여부와 멤버 여부를 확인하기 위한 ChannelService

    @Override
    public Message createMessage(Message message) {

        // 작성자가 실제로 존재하는지 확인한다.
        User planner = userService.readUser(message.getPlannerId());

        // 메시지가 작성될 채널이 존재하는지 확인한다.
        channelService.readChannel(message.getChannelId());

        // 작성자가 해당 채널의 멤버인지 확인한다.
        if (!channelService.isMember(
                message.getChannelId(), message.getPlannerId())) {
            throw new IllegalArgumentException(
                    "'" + planner.getName() + "' (이)가 해당 채널에 참여 중이 아닙니다.");
        }

        // Repository에 메시지 저장을 요청한다.
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        // ID에 해당하는 메시지를 조회하고, 없으면 예외를 발생시킨다.
        return messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> readAllMessage() {
        // Repository에 전체 메시지 목록 조회를 요청한다.
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID id, String content) {

        Message targetMessage = readMessage(id); // 수정할 메시지를 조회한다.

        // 기존 내용과 변경할 내용이 같으면 예외를 발생시킨다.
        if (targetMessage.getContent().equals(content)) {
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }

        targetMessage.changeContent(content); // 메시지 내용을 변경한다.

        // 변경된 메시지 정보를 Repository에 저장한다.
        messageRepository.save(targetMessage);
        return targetMessage;
    }

    @Override
    public void deleteMessage(UUID id) {
        // 삭제할 메시지가 존재하는지 확인한다.
        readMessage(id);

        // Repository에 해당 메시지의 삭제를 요청한다.
        messageRepository.deleteByMessageId(id);
    }
}