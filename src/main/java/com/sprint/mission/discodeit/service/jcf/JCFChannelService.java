package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel create(Channel channel) {
        userService.findById(channel.getOwnerId()); // 검증 (사용자 유무)
        data.put(channel.getId(), channel);
        userService.addChannel(channel.getOwnerId(), channel.getId()); // 사용자의 채널 목록에 추가
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, UUID requesterId, String channelName, String description) {
        Channel channel = findById(id);
        requireOwner(channel, requesterId, "채널 수정 권한이 없습니다.");
        channel.update(channelName, description);
        return channel;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = findById(id);
        requireOwner(channel, requesterId, "채널 삭제 권한이 없습니다.");
        if (!channel.getMessages().isEmpty()) {
            throw new IllegalArgumentException("메시지가 남아 있어 삭제할 수 없습니다.");
        }

        for (UUID userId : channel.getUsers()) {           // 아래(User) 호출
            userService.removeChannel(userId, id);
        }
        data.remove(id);
    }

    @Override
    public void join(UUID id, UUID userId) {
        Channel channel = findById(id);
        userService.findById(userId);       // 1. 검증
        channel.addUser(userId);            // 2. 변경 (이미 가입이면 제외)
        userService.addChannel(userId, id);
    }

    @Override
    public void leave(UUID id, UUID userId) {
        Channel channel = findById(id);
        userService.findById(userId);
        channel.removeUser(userId);          // 소유자·비멤버는 여기서 예외
        userService.removeChannel(userId, id);
    }

    @Override
    public void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId) {
        Channel channel = findById(id);
        requireOwner(channel, currentOwnerId, "소유자만 양도 가능합니다.");
        channel.transferOwnership(newOwnerId); // 멤버가 아니면 여기서 예외발생
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        findById(id).addMessage(messageId);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        findById(id).removeMessage(messageId);
    }

    // 채널의 소유자와 requesterId가 다르면 예외발생을 메서드 처리
    private void requireOwner(Channel channel, UUID requesterId, String message) {
        if (!channel.isOwner(requesterId)) {
            throw new IllegalArgumentException(message);
        }
    }
}
