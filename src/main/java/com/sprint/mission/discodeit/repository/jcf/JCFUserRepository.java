package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class JCFUserRepository implements UserRepository {

    List<User> users;

    public JCFUserRepository() {
        this.users = new ArrayList<User>();
    }

    public User saveUser(User user) {
        this.users.add(user);

        return user;
    }

    public List<User> getAllUsers() {}

}
