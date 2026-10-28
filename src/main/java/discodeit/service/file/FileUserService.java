package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class FileUserService implements UserService {

    private final UserRepository userRepository;
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        userRepository.save(user);
        return user;
    }

    @Override
    public Optional<User> find(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newName) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
        target.setUserName(newName);
        target.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID userId) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        deleteListeners.forEach(l -> l.accept(userId));
        userRepository.deleteById(userId);
    }

    @Override
    public void addDeleteListener(Consumer<UUID> listener) {
        deleteListeners.add(listener);
    }

    @Override
    public void removeChannelFromUsers(UUID channelId) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.deleteChannel(channelId);
            userRepository.save(user);
        }
    }

    @Override
    public void addChannelToUsers(Channel channel) {
        List<User> users = channel.getUserList();
        for (User user : users) {
            User lastest = userRepository.findById(user.getId()).orElseThrow(() ->
                    new NoSuchElementException("찾으려는 유저가 존재하지 않습니다."));
            lastest.setChannel(channel);
            userRepository.save(lastest);
        }
    }


}
