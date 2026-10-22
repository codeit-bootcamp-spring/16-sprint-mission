package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.List;

public class FileUserRepository {
    /*
    User savedUser(User user) {
        String fileName = "./data/users/" + user.getId() + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException exception) {
            throw new UncheckedIOException("사용자 저장에 실패했습니다.", exception);
        }

        return user;
    }
    public List<User> getAllUsers() {

    }
     */
}
