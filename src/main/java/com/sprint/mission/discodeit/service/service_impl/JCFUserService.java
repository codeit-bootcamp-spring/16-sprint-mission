package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // user가 참여하고 있는 Channel들을 리스트로 반환
    public List<Channel> getJoinedChannel(UUID userId, ChannelService channelService) {
        User user = data.get(userId);
        List<Channel> joinedChannels = channelService.getUserChannels(user);
        return joinedChannels;
    }

    // JCFChannelService에서 사용되는 메서드
    @Override
    public List<User> getChannelsFromUser(Channel channel) {
        List<UUID> userUUIDs = channel.getUserUUIDs();
        List<User> users = new ArrayList<>();
        for (UUID userUUID : userUUIDs) {
            User user = data.get(userUUID);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public User create(String username, String email, String password) {
        User newUser = new User(username, email, password);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByName(String username) {
        Collection<User> values = data.values();
        for (User user : values) {
            if (user.getUserName().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        Collection<User> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public Optional<User> updatePw(UUID id, String newPw) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldPw = scanner.nextLine();
            if (oldPw.equals(user.getPassword())) {
                user.setPassword(newPw);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty(); // 호출하는 쪽에서 NPE 발생 위험, 아직 사용되는 부분은 없음
        }
    }

    @Override
    public Optional<User> updateUsername(UUID id, String newUsername) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldUsername = scanner.nextLine();
            if (oldUsername.equals(user.getUserName())) {
                user.setUserName(newUsername);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty();    // 호출하는 쪽에서 NPE 발생 위험
        }
    }

    @Override
    public Optional<User> updateEmail(UUID id, String newEmail) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldEmail = scanner.nextLine();
            if (oldEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty();// 호출하는 쪽에서 NPE 발생 위험
        }
    }

    @Override
    public void delete(UUID id) {
        User removedUser = data.remove(id);
        if (removedUser == null) {
            throw new NoSuchElementException();
        }
    }
}
