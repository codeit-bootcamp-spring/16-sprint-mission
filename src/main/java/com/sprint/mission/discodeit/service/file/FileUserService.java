package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicEntityService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path filePath = Path.of("data","users.ser");


    public FileUserService() {
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }
    private void saveUsers(List<User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(users);
        } catch (IOException e) {
            throw new IllegalStateException("사용자 목록을 저장할 수 없습니다", e);
        }
    }
    private List<User> loadUsers() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<User>)  in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("사용자 몰록을 읽을 수 없습니다",e);
        }
    }
    public void addMessage(UUID userId, Message message) {
        List<User> users = loadUsers();

        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        user.getMessages().add(message);
        saveUsers(users);
    }

    public void removeMessage(UUID userId, UUID messageId) {
        List<User> users = loadUsers();

        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        user.getMessages().removeIf(m -> m.getId().equals(messageId));
        saveUsers(users);
    }
    public void updateMessage(UUID userId, UUID messageId, String comment) {
        List<User> users = loadUsers();

        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Message message = user.getMessages().stream().filter(m -> m.getId().equals(messageId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        message.update(comment);
        saveUsers(users);
    }

    @Override
    public User create(String name, String password, String email) {
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<User> users = entities.getUsers();

        if(users.stream().anyMatch(u -> u.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        User user = new User(name, password, email);
        users.add(user);
        entities.saveAll();
        return user;
    }

    @Override
    public User findById(UUID id) {
        return loadUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없음"));
    }

    @Override
    public List<User> findAll() {
        return loadUsers();
    }

    @Override
    public User update(UUID id, String name, String password, String email) {
        BasicEntityService entities = new BasicEntityService(new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(users.stream().anyMatch(otherUser ->
                !otherUser.getId().equals(id)&&otherUser.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        user.update(name, password, email);
        entities.saveAll();
        return user;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        users.remove(user);
        entities.saveAll();
    }
    public void addChannel(UUID userId, Channel channel) {
        List<User> users = loadUsers();

        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없음"));

        boolean alreadyJoined = user.getChannels().stream().anyMatch(c -> c.getId().equals(channel.getId()));

        if (!alreadyJoined) {
            user.getChannels().add(channel);
            saveUsers(users);
        }
    }




}
