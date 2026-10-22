package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final Path filePath = Path.of("data","users.ser");

    public FileUserRepository() {
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }
    private void saveUsers(List<User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(users);
        } catch (IOException e) {
            throw new IllegalStateException("사용자 목록을 저장할 수 없습니다", e);
        }
    }
    private List<User> loadUsers() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<User>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("사용자 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public User save(User user) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveUsers(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return loadUsers().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return loadUsers();
    }

    @Override
    public void deleteById(UUID id) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getId().equals(id));
        saveUsers(users);
    }
}