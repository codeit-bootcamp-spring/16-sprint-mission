package discodeit.service.jcf;

import discodeit.entity.User;
import discodeit.repository.UserRepository;
import discodeit.service.UserService;

import java.util.*;
import java.util.function.Consumer;

public class JCFUserService implements UserService {
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();
    private UserRepository userRepository;

    public JCFUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> find(UUID userId) {
        Optional<User> findUser = userRepository.findById(userId);
        return findUser;
    }

    @Override
    public List<User> findAll() {
        System.out.println("모든 회원을 조회합니다.");
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newName) {
        User target = find(userId).orElseThrow(() ->
                new NoSuchElementException("수정할 사용자를 찾을 수 없습니다."));

        if (!target.getUserName().equals(newName)) {
            target.setUserName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
        }

        userRepository.save(target);

        return target;
    }

    @Override
    public void delete(UUID userId) {
        User target = find(userId).orElseThrow(() ->
                        new NoSuchElementException("제거하려는 사용자를 찾을 수 없습니다."));

        deleteListeners.forEach(l -> l.accept(userId));
        userRepository.deleteById(target.getId());
    }

    @Override
    public void addDeleteListener(Consumer<UUID> listener) {
        deleteListeners.add(listener);
    }

    @Override
    public void removeChannelFromUsers(UUID channelId) {

    }
}
