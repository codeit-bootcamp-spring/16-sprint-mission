package com.sprint.mission.discodeit.service.basic.v1;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BasicUserServiceV1 implements BasicUserService {

    private final UserRepository userRepository;

    public BasicUserServiceV1(UserRepository userRepository) {
        this.userRepository = userRepository;
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

            userRepository.update(user.getId(), newUsername, user.getEmail(), user.getPassword());
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

            userRepository.update(user.getId(), user.getUserName(), newEmail, user.getPassword());
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

            userRepository.update(user.getId(), user.getUserName(), user.getEmail(), newPassword);
        } else {
            System.out.println("유저명이 일치하지 않습니다.");
        }
    }


}
