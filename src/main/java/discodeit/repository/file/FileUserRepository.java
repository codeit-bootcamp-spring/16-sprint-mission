package discodeit.repository.file;

import discodeit.entity.User;
import discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "users.ser";

    @Override
    public User save(User user) {
        List<User> users = load();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                persist(users);
                return user;
            }
        }
        users.add(user);
        persist(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return load().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return load();
    }

    @Override
    public void deleteById(UUID userId) {
        List<User> users = load();
        users.removeIf(u -> u.getId().equals(userId));
        persist(users);
    }

    private void persist(List<User> userList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
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
