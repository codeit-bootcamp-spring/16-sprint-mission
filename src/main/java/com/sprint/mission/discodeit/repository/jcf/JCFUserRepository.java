package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private List<User> users;

    public JCFUserRepository() {
        this.users = new ArrayList<User>();
    }

    @Override
    public User save(User user) {
        this.users.add(user);

        return user;
    }

    /*
    @Override
    public User saveUser(User user) {
        this.users.add(user);

        return user;
    }
    */

    @Override
    public List<User> getAllUsers() {
        // return this.users;
        return List.copyOf(this.users); // 수정할 수 없는 리스트 반환
        // 다만, 리스트 안에있는 user 객체는 여전히 수정할 수 있다. 캡슐화
    }

    @Override
    public User getUser(UUID id) {
        User user = this.users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 ID의 사용자를 찾을 수 없습니다: " + id)
                );

        return user;
    }

    @Override
    public void removeUser(UUID id) {
        User user = this.getUser(id);
        this.users.remove(user);
    }

}
