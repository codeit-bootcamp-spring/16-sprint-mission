package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private ChannelService channelService;

    public FileUserService() {
        this.writeUserFile(new HashMap<>());
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        Map<UUID, User> users = this.readUserFile();
        users.put(user.getId(), user);
        this.writeUserFile(users);
        return user;
    }

    @Override
    public User update(UUID id, User newUser) {
        Map<UUID, User> users = this.readUserFile();
        User user = Optional.ofNullable(users.get(id)).orElseThrow(
                () -> new NoSuchElementException("수정할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        user.update(newUser);
        this.writeUserFile(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Map<UUID, User> users = this.readUserFile();
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> users = this.readUserFile();
        return users.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        // 유저 삭제 시 가입된 채널들에서도 유저 탈퇴
        user.getChannels().values().forEach(channel -> {
            channel.removeUser(id);
            this.channelService.update(channel.getId(), channel);
        });

        user.softDelete();
        this.update(id, user);
    }

    public Map<UUID, User> readUserFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/user.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, User> users = (Map<UUID, User>) ois.readObject();
            return users;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeUserFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/user.ser"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }

    public void setChannelService(ChannelService channelService) {
        if (channelService == null) throw new NullPointerException("채널 서비스가 참조하는 값이 없습니다.");
        this.channelService = channelService;
    }
}
