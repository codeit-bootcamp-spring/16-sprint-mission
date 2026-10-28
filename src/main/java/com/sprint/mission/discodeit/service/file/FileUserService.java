package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.io.*;

public class FileUserService implements UserService {

    final UserRepository userRepository;
    final MessageService messageService;

    public FileUserService(UserRepository userRepository, MessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Override
    public User save(User user) {
        if (user == null || user.getId() == null) {
            throw new NullPointerException("데이터가 이상함");
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findId(UUID id) {
        User user = userRepository.findId(id);
        if (user == null) {
            return Collections.emptyList();
        }
        List<User> result = new ArrayList<>();
        result.add(user);
        return result;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageService.findUserlId(userId);
    }

    @Override
    public User update(UUID id, String name) {
        User user = userRepository.findId(id);
        if (user == null) {
            throw new NullPointerException("해당유저 없음");
        }
        user.setName(name);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findEntityId(UUID id) {
        return userRepository.findId(id);
    }

    @Override
    public void deleteId(UUID id) {
        messageService.deleteByUserId(id);
        userRepository.deleteId(id);
    }
}
