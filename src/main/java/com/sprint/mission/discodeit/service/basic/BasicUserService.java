package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public User create(String name, String password, String email) {
        if(userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        User user = new User(name, password, email);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name, String password, String email) {
        User user = findById(id);

        if(userRepository.findAll().stream().anyMatch(otherUser ->
                !otherUser.getId().equals(id)&&otherUser.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        user.update(name, password, email);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        for(Channel channel : channelRepository.findAll()){
            boolean removed = channel.getUsers().removeIf(u -> u.getId().equals(id));
            if(removed){
                channelRepository.save(channel);
            }
        }

        user.getChannels().clear();
        userRepository.deleteById(id);
    }
}