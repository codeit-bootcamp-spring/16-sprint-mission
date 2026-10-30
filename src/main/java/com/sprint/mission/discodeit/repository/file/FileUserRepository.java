package com.sprint.mission.discodeit.repository.file;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
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

    // 현재 유저 목록을 임시 파일에 직렬화한 뒤 기존 파일과 교체한다.
    private void saveData(List<User> dataToSave) {
        Path tempFile = null;

        try {
            // 기존 데이터 파일과 같은 디렉터리에 임시 파일을 생성한다.
            tempFile = Files.createTempFile(FILE_PATH.getParent(),
                    "userRepository-", ".tmp");

            // 객체를 userRepository 파일에 저장한다. (직렬화)
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {

                // 유저 목록 전체를 임시 파일에 직렬화한다.
                outputStream.writeObject(dataToSave);

                /* 원자적 이동은 파일 교체가 중간 단계로 쪼개져 보이지 않도록 한 번의 작업처럼 처리하는 것이며,
                유저 데이터를 안전하게 교체하기 위해 사용하는 것입니다.! */
                try {
                    // 임시 파일을 기존 데이터 파일과 원자적으로 교체한다.
                    Files.move(tempFile,
                            FILE_PATH,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);

                } catch (AtomicMoveNotSupportedException exception) {
                    // 원자적 이동을 지원하지 않으면 일반적인 파일 교체를 수행한다.
                    Files.move(tempFile,
                            FILE_PATH,
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("유저 데이터를 저장할 수 없습니다", exception);
        } finally {
            // 저장 또는 교체에 실패해 남은 임시 파일을 정리한다.
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // 임시 파일 정리 실패가 원래 예외를 덮지 않도록 한다.
                }
            }
        }
    }

    @Override
    public User save(User user) {
        // 변경 전 유저 목록을 보관한다.
        List<User> updatedData = new ArrayList<>(data);
        try {
            // 기존 유저 목록에서 같은 ID를 가진 유저를 찾는다.
            for (int i = 0; i < updatedData.size(); i++) {
                if (updatedData.get(i).getId().equals(user.getId())) {
                    // 같은 ID를 가진 유저를 새 유저 객체로 교체한다.
                    updatedData.set(i, user);

                    // 수정된 유저 목록을 파일에 저장한다.
                    saveData(updatedData);
                    data = updatedData;

                    return user;
                }
            }

            // 같은 ID를 가진 유저가 없으면 새 유저를 추가한다.
            updatedData.add(user);

            // 파일 저장과 교체가 성공한 뒤에만 메모리를 갱신한다.
            saveData(updatedData);
            data = updatedData;

            return user;

        } catch (RuntimeException rtException){
            // 메모리는 아직 변경되지 않았으므로 그대로 예외를 전달한다.
            System.out.println(rtException.getMessage());
            throw rtException;
        }
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
        // 기존 목록을 변경하지 않고 수정 후보 목록을 만든다.
        List<User> updatedData = new ArrayList<>(data);

        // ID가 일치하는 유저를 삭제한다. (삭제)
        boolean remove = updatedData.removeIf(user -> user.getId().equals(id));

        // 삭제된 유저가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다.");
        }

        saveData(updatedData); // 변경된 유저 목록을 파일에 저장한다.
        data = updatedData;
    }
}