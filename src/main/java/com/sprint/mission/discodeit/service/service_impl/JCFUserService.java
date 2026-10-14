package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private final ChannelService channelService;
    private MessageService messageService;

    public JCFUserService(ChannelService channelService) {
        this.data = new HashMap<>();
        this.channelService = channelService;
    }

    // JCFMessageService가 생성자로 UserService를 받으므로 순환 참조를 피하기 위해 setter로 주입
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    // user가 참여하고 있는 Channel들을 리스트로 반환
    public List<Channel> getJoinedChannel(UUID userId, ChannelService channelService) {
        User user = data.get(userId);
        List<Channel> joinedChannels = channelService.getUserChannels(user);
        return joinedChannels;
    }

    // JCFChannelService에서 사용되는 메서드
    @Override
    public List<User> getUsersInChannel(Channel channel) {
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

    // channel을 파라미터에 주입 시 해당 채널에 속한 유저들 목록을 리스트로 리턴
    public List<Optional<User>> findAll(Channel channel) {
        List<UUID> userUUIDs = channel.getUserUUIDs();
        List<Optional<User>> users = new ArrayList<>();

        for (UUID userId : userUUIDs) {
            users.add(findById(userId));
        }
        return users;
    }

    @Override
    public Optional<User> updatePw(UUID id, String newPw) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            System.out.print("이전 비밀번호를 입력해주세요: ");
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
            System.out.print("이전 닉네임을 입력해주세요: ");
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
            System.out.print("이전 이메일을 입력해주세요: ");
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
        User removedUser = data.get(id);
        if (removedUser == null) {
            throw new NoSuchElementException();
        }

        // User가 작성한 Message 삭제 (Channel에 남아있는 Message도 함께 정리됨)
        // messageService.delete()가 myMessages를 수정하므로 복사본으로 순회
        for (Message message : new ArrayList<>(removedUser.getMyMessages())) {
            messageService.delete(message.getId());
        }

        // 삭제된 User가 속해있던 Channel들에서도 User 정보 삭제
        for (Channel channel : channelService.getUserChannels(removedUser)) {
            channel.deleteUser(removedUser);
        }

        data.remove(id);
    }
}
