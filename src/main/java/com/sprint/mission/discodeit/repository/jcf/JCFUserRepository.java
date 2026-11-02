package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)

public class JCFUserRepository implements UserRepository {

    private final List<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        data.removeIf(u -> u.getId().equals(user.getId()));
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}