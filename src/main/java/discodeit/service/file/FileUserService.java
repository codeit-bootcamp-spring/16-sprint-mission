package discodeit.service.file;

import discodeit.entity.User;
import discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class FileUserService implements UserService {

    private static final String FILE_PATH = "users.ser";
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();

    @Override
    public User create(String name) throws IOException {
        List<User> users = load();
        User user = new User(name);
        users.add(user);
        save(users);

        return user;
    }

    @Override
    public Optional<User> find(UUID userId) {
        return load().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();

    }

    @Override
    public List<User> findAll() {
        return load();
    }

    @Override
    public User update(UUID userId, String newName) {
        List<User> users = load();
        User target = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("수정할 사용자를 찾을 수 없습니다."));

        if (!target.getUserName().equals(newName)) {
            target.setUserName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
            save(users);
        }
        return target;
    }

    @Override
    public void delete(UUID userId) {
        List<User> users = load();
        User target = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("제거하려는 사용자를 찾을 수 없습니다."));

        deleteListeners.forEach(l -> l.accept(userId));
        users.remove(target);
        this.save(users);
    }

    @Override
    public void addDeleteListener(Consumer<UUID> listener) {
        deleteListeners.add(listener);
    }

    private void save(List<User> userList) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userList);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> load() {
        List<User> userList = new ArrayList<>();
        if (!Files.exists(Path.of(FILE_PATH))) {
            return userList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            userList = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
