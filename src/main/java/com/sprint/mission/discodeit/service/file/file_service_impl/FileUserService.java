package com.sprint.mission.discodeit.service.file.file_service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class FileUserService implements UserService {

    private static final String FILE_PATH = "temp/users.ser";

    private final MessageService messageService;
    private final Map<UUID, User> data;

    public FileUserService(MessageService messageService) throws IOException {
        this.messageService = messageService;
        this.data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, User> userMap = (Map<UUID, User>) findObject;
            this.data.putAll(userMap);
        } catch (FileNotFoundException e) {
            Path userFile = Path.of(FILE_PATH);
            Files.createFile(userFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User create(String username, String email, String password) {
        User newUser = new User(username, email, password);
        data.put(newUser.getId(), newUser);

        save();

        return newUser;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        Collection<User> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public Optional<User> findByName(String username) {
        Collection<User> values = data.values();
        for (User user : values) {
            if (user.getUserName().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updatePw(UUID id, String newPw) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            System.out.print("이전 비밀번호를 입력해주세요: ");
            String oldPw = scanner.nextLine();
            if (oldPw.equals(user.getPassword())) {
                user.setPassword(newPw);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            save();
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> updateUsername(UUID id, String newUsername) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            System.out.print("이전 닉네임을 입력해주세요: ");
            String oldUsername = scanner.nextLine();
            if (oldUsername.equals(user.getUserName())) {
                user.setUserName(newUsername);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            save();
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty();    // 호출하는 쪽에서 NPE 발생 위험
        }
    }

    @Override
    public Optional<User> updateEmail(UUID id, String newEmail) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            System.out.print("이전 이메일을 입력해주세요: ");
            String oldEmail = scanner.nextLine();
            if (oldEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            save();
            return Optional.of(user);
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return Optional.empty();// 호출하는 쪽에서 NPE 발생 위험
        }
    }

    @Override
    public void delete(UUID id) {
        User removedUser = data.get(id);
        if (removedUser == null) {
            throw new NoSuchElementException();
        }

        for (Message message : new ArrayList<>(removedUser.getMyMessages())) {
            messageService.delete(message.getId());
        }

        for (Channel channel : removedUser.getChannels()) {
            channel.deleteUser(removedUser);
        }

        data.remove(id);
        save();
    }

    private void save() {
        try (FileOutputStream fis = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fis)
        ) {
            oos.writeObject(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
