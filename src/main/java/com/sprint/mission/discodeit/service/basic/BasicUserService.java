package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelService channelService;

    public BasicUserService(UserRepository userRepository, ChannelService channelService) {
        this.userRepository = userRepository;
        this.channelService = channelService;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User update(UUID id, User newUser) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("수정할 유저가 존재하지 않습니다."));
        // 갱신할 유저 정보의 상태가 활동 중이면 현재 유저도 검사
        if (!newUser.isDeleted()) {
            user.validateNotDeleted();
        }
        user.update(newUser);
        return this.userRepository.update(id, user);
    }

    @Override
    public void delete(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        // 유저 삭제 시 가입된 채널들에서도 유저 탈퇴
        Set<UUID> channels = new HashSet<>(user.getChannels());
        channels.forEach(channelId -> this.channelService.removeUserFromChannel(channelId, id));

        this.userRepository.delete(id);
    }
}
