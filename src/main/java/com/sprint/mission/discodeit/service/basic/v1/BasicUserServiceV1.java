package com.sprint.mission.discodeit.service.basic.v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class BasicUserServiceV1 implements BasicUserService {

    private final UserRepository userRepository;
    private BasicMessageService messageService;
    private BasicChannelService channelService;

    public BasicUserServiceV1(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Channel/Message 서비스도 이 서비스를 사용하므로(순환) 생성자 대신 setter로 연결한다.
    public void setChannelService(BasicChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(BasicMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void signUp(Scanner scanner) {
        System.out.println("========== 회원가입 창 ========== ");

        System.out.print("유저명을 입력하세요: ");
        String username = scanner.nextLine();
        System.out.print("이메일을 입력해주세요: ");
        String email = scanner.nextLine();
        while (true) {
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine();
            System.out.print("비밀번호를 한 번 더 입력해주세요: ");
            String confirmedPassword = scanner.nextLine();

            if (password.equals(confirmedPassword)) {
                User newUser = new User(username, email, password);
                userRepository.create(newUser);
                System.out.println("회원가입이 완료되었습니다!");
                return;
            } else {
                System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
            }
        }
    }

    @Override
    public Optional<User> login(Scanner scanner) {
        System.out.println("========== 로그인 창 ========== ");
        System.out.print("유저명을 입력하세요: ");
        String username = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                System.out.println("환영합니다. " + username + "님");
                user.setLogin(true);
                return Optional.of(user);
            }
        }
        System.out.println("유저명 또는 비밀번호가 틀렸습니다.");
        return Optional.empty();
    }

    @Override
    public void logout(User user) {
        user.setLogin(false);
    }

    @Override
    public void modifyUserInfo(User user, Scanner scanner) {
        System.out.println("========== 유저정보 변경 창 ========== ");
        System.out.print("변경할 정보를 선택하세요.\n" +
                "1. 유저명\n" +
                "2. 이메일\n" +
                "3. 비밀번호\n" +
                ": "
        );
        int menu = scanner.nextInt();
        scanner.nextLine();

        switch (menu) {
            case 1 -> modifyUsername(user, scanner);
            case 2 -> modifyEmail(user, scanner);
            case 3 -> modifyPassword(user, scanner);
        }
    }

    private void modifyUsername(User user, Scanner scanner) {
        System.out.print("현재 유저명을 입력하세요: ");
        String currentUsername = scanner.nextLine();

        if (user.getUserName().equals(currentUsername)) {
            System.out.print("변경할 유저명을 입력하세요: ");
            String newUsername = scanner.nextLine();

            saveUserUpdate(user, newUsername, user.getEmail(), user.getPassword());
        } else {
            System.out.println("유저명이 일치하지 않습니다.");
        }

    }

    private void modifyEmail(User user, Scanner scanner) {
        System.out.print("현재 사용중인 이메일을 입력하세요: ");
        String currentEmail = scanner.nextLine();

        if (user.getEmail().equals(currentEmail)) {
            System.out.print("변경할 이메일을 입력하세요: ");
            String newEmail = scanner.nextLine();

            saveUserUpdate(user, user.getUserName(), newEmail, user.getPassword());
        } else {
            System.out.println("유저명이 일치하지 않습니다.");
        }
    }

    private void modifyPassword(User user, Scanner scanner) {
        System.out.print("현재 비밀번호를 입력하세요: ");
        String currentPassword = scanner.nextLine();

        if (user.getPassword().equals(currentPassword)) {
            System.out.print("변경할 비밀번호를 입력하세요: ");
            String newPassword = scanner.nextLine();

            saveUserUpdate(user, user.getUserName(), user.getEmail(), newPassword);
        } else {
            System.out.println("유저명이 일치하지 않습니다.");
        }
    }

    // User를 수정해 저장하고, Channel/Message가 보관 중인 User 복사본도 최신 정보로 맞춘다.
    private void saveUserUpdate(User user, String userName, String email, String password) {
        User stored = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 User입니다."));
        stored.update(userName, email, password);
        userRepository.update(stored);

        if (channelService != null) {
            channelService.refreshUser(stored);
        }
        if (messageService != null) {
            messageService.refreshUser(stored);
        }
    }

    @Override
    public void addChannel(UUID userId, Channel channel) {
        userRepository.findById(userId)
                .filter(user -> user.getChannels().stream().noneMatch(c -> c.getId().equals(channel.getId())))
                .ifPresent(user -> {
                    user.addChannel(channel);
                    userRepository.update(user);
                });
    }

    @Override
    public void removeChannel(UUID userId, UUID channelId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.getChannels().removeIf(c -> c.getId().equals(channelId));
            userRepository.update(user);
        });
    }

    @Override
    public void removeChannel(UUID channelId) {
        userRepository.findAll().stream()
                .filter(user -> user.getChannels().stream().anyMatch(c -> c.getId().equals(channelId)))
                .forEach(user -> {
                    user.getChannels().removeIf(c -> c.getId().equals(channelId));
                    userRepository.update(user);
                });
    }

    @Override
    public void refreshChannel(Channel channel) {
        userRepository.findAll().stream()
                .filter(user -> user.getChannels().stream().anyMatch(c -> c.getId().equals(channel.getId())))
                .forEach(user -> {
                    user.getChannels().stream()
                            .filter(c -> c.getId().equals(channel.getId()))
                            .forEach(c -> c.sync(channel));
                    userRepository.update(user);
                });
    }

    @Override
    public void addMessage(Message message) {
        userRepository.findById(message.getUser().getId())
                .filter(user -> user.getMyMessages().stream().noneMatch(m -> m.getId().equals(message.getId())))
                .ifPresent(user -> {
                    user.addMessage(message);
                    userRepository.update(user);
                });
    }

    @Override
    public void removeMessage(UUID messageId) {
        userRepository.findAll().stream()
                .filter(user -> user.getMyMessages().stream().anyMatch(m -> m.getId().equals(messageId)))
                .forEach(user -> {
                    user.getMyMessages().removeIf(m -> m.getId().equals(messageId));
                    userRepository.update(user);
                });
    }

    @Override
    public void refreshMessage(Message message) {
        userRepository.findAll().stream()
                .filter(user -> user.getMyMessages().stream().anyMatch(m -> m.getId().equals(message.getId())))
                .forEach(user -> {
                    user.getMyMessages().stream()
                            .filter(m -> m.getId().equals(message.getId()))
                            .forEach(m -> m.sync(message));
                    userRepository.update(user);
                });
    }
}
