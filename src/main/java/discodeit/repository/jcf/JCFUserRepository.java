package discodeit.repository.jcf;

import discodeit.entity.User;
import discodeit.repository.UserRepository;

import java.util.*;
import java.util.function.Consumer;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        int idx = data.indexOf(user);
        if (idx >= 0) {
            data.set(idx, user);
        } else {
            data.add(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void deleteById(UUID userId) {
        User target = data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("삭제하려는 유저를 찾을 수 없습니다."));
        data.remove(target);
    }
}
