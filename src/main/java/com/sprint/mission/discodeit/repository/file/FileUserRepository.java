package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class FileUserRepository implements UserRepository {
    // 유저 데이터 파일이 저장될 경로
    private static final Path FILE_PATH = Paths.get("data", "userRepository.ser");

    // 파일에서 읽어 온 유저 목록
    private List<User> data;

    public FileUserRepository() {
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory() {
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException exception) {
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다", exception);
        }
    }

    // 파일에서 유저 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<User> loadData(){
        // 파일이 없으면 빈 목록을 반환한다.
        if (Files.notExists(FILE_PATH)){
            return new ArrayList<>();
        }

        // 파일에 저장된 객체를 읽는다. (역직렬화)
        try (ObjectInputStream inputStream =
                new ObjectInputStream(Files.newInputStream(FILE_PATH))){

            // 파일에 저장된 유저 목록을 읽어 반환한다.
            return (List<User>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception){
            throw new RuntimeException("유저 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 유저 목록을 파일에 저장한다.
    private void saveData(){
        // 객체를 userRepository 파일에 저장한다. (직렬화)
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(Files.newOutputStream(FILE_PATH))){

            // 현재 유저 목록 전체를 파일에 저장한다.
            outputStream.writeObject(data);
        } catch (IOException exception){
            throw new RuntimeException("유저 데이터를 저장할 수 없습니다", exception);
        }
    }

    @Override
    public User save(User user) {

        // 같은 ID를 가진 유저가 존재하면 수정한다.
        if (existsByUserId(user.getId())){
            int index = IntStream.range(0, data.size())
                    .filter(i -> data.get(i).getId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("수정할 유저를 찾을 수 없습니다."));

            data.set(index, user); // 기존 유저를 새 유저 정보로 교체한다. (수정)
        } else {
            data.add(user); // 새로운 유저를 목록에 추가한다. (생성)
        }
        // 생성 또는 수정된 유저 목록을 파일에 저장한다.
        saveData();
        return user;
    }

    @Override
    public Optional<User> findByUserId(UUID id) {
        // ID가 일치하는 유저를 찾아 반환한다. (단건 조회)
        // 일치하는 유저가 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        // data에 저장된 유저들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByUserId(UUID id) {
        // 해당 유저의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(user -> user.getId().equals(id));
    }

    @Override
    public void deleteByUserId(UUID id) {
        // ID가 일치하는 유저를 삭제한다. (삭제)
        boolean remove = data.removeIf(user -> user.getId().equals(id));

        // 삭제된 유저가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다.");
        }

        saveData(); // 변경된 유저 목록을 파일에 저장한다.
    }
}