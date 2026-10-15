package discodeit.service.jcf;

import discodeit.entity.User;
import discodeit.service.UserService;

import java.util.*;
import java.util.function.Consumer;

public class JCFUserService implements UserService {
    private final List<User> data;
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();

    public JCFUserService(){
        this.data = new ArrayList<>();
    }

    @Override
    public User create(String name) {
        User newUser = new User(name);
        if (!data.contains(newUser)) {
            data.add(newUser);
        }
        return newUser;
    }

    @Override
    public Optional<User> find(UUID userId) {
        Optional<User> findUser = data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
        return findUser;
    }

    @Override
    public List<User> findAll() {
        System.out.println("모든 회원을 조회합니다.");
        return List.copyOf(data);
    }

    @Override
    public User update(UUID userId, String newName) {
        User target = find(userId).orElseThrow(() ->
                new NoSuchElementException("수정할 사용자를 찾을 수 없습니다."));

        if (!target.getUserName().equals(newName)) {
            target.setUserName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
        }
        return target;
    }

    @Override
    public void delete(UUID userId) {
        User target = find(userId).orElseThrow(() ->
                        new NoSuchElementException("제거하려는 사용자를 찾을 수 없습니다."));

        deleteListeners.forEach(l -> l.accept(userId));
        data.remove(target);
    }

    @Override
    public void addDeleteListener(Consumer<UUID> listener) {
        deleteListeners.add(listener);
    }
}
