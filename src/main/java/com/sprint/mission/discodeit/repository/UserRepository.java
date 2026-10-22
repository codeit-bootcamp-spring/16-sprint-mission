package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface UserRepository {

    User saveUser(User user);

    List<User> getAllUsers();
}
