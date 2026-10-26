package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자: 메시지 저장 공간과 사용자·채널 서비스를 연결
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();

        // 사용자 서비스와 채널 서비스가 null인지 확인
        if (userService == null || channelService == null) {
            throw new IllegalArgumentException("사용자 서비스와 채널 서비스는 null일 수 없습니다.");
        }

        this.userService = userService;
        this.channelService = channelService;
    }

    // 메시지 생성
    @Override
    public Message create(Message message) {

        // 메시지가 null인지 확인
        if (message == null) {
            throw new IllegalArgumentException("메시지는 null일 수 없습니다.");
        }

        // 메시지 내용이 null인지 확인
        if (message.getContent() == null) {
            throw new IllegalArgumentException("메시지 내용은 null일 수 없습니다.");
        }

        // 메시지 작성자와 채널 가져오기
        User user = message.getUser();
        Channel channel = message.getChannel();

        // 작성자가 존재하는지 확인
        if (user == null || userService.findById(user.getId()) == null) {
            throw new IllegalArgumentException("작성자가 존재하지 않습니다.");
        }

        // 채널이 존재하는지 확인
        if (channel == null || channelService.findById(channel.getId()) == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        // 검증을 통과한 메시지를 ID를 기준으로 저장
        data.put(message.getId(), message);

        // 생성한 메시지 반환
        return message;
    }

    // ID로 메시지 조회
    @Override
    public Message findById(UUID id) {

        // ID가 null인지 확인
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        // 해당 ID의 메시지 반환
        return data.get(id);
    }

    // 모든 메시지 조회
    @Override
    public List<Message> findAll() {

        // 저장된 메시지를 리스트로 변환하여 반환
        return new ArrayList<>(data.values());
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, Message message) {

        // ID가 null인지 확인
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        // 수정할 메시지가 null인지 확인
        if (message == null) {
            throw new IllegalArgumentException("메시지는 null일 수 없습니다.");
        }

        // 메시지 내용이 null인지 확인
        if (message.getContent() == null) {
            throw new IllegalArgumentException("메시지 내용은 null일 수 없습니다.");
        }

        // ID로 기존 메시지 객체 찾기
        Message existingMessage = data.get(id);

        // 수정할 메시지가 존재하는지 확인
        if (existingMessage == null) {
            throw new IllegalArgumentException("수정할 메시지가 존재하지 않습니다.");
        }

        // 기존 메시지의 내용만 수정하고 수정 시간을 갱신
        existingMessage.update(message.getContent());

        // 수정된 기존 메시지 객체 반환
        return existingMessage;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {

        // ID가 null인지 확인
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        // 삭제할 메시지가 존재하는지 확인
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 메시지가 존재하지 않습니다.");
        }

        // 해당 ID의 메시지 삭제
        data.remove(id);
    }
}
