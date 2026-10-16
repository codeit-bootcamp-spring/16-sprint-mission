package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;

import java.util.Optional;
import java.util.Scanner;

public interface BasicUserService {

    void signUp(Scanner scanner);

    Optional<User> login(Scanner scanner);

    void logout(User user);

    void modifyUserInfo(User user, Scanner scanner);

}
