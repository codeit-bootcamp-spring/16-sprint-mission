package com.sprint.mission.discodeit.repository.file.otherFile;

import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    // 읽음 상태 데이터 파일이 저장될 경로
    private static final Path FILE_PATH =
            Paths.get("data", "readStatusRepository.ser");

    // 파일에서 읽어 온 읽음 상태 목록
    private List<ReadStatus> data;

    // 데이터 디렉터리를 생성하고 파일에 저장된 읽음 상태 목록을 초기화한다.
    public FileReadStatusRepository() {
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException ioException) {
            throw new RuntimeException(
                    "데이터 폴더를 만들 수 없습니다.", ioException);
        }
    }

    // 파일에서 읽음 상태 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<ReadStatus> loadData() {

        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(FILE_PATH))) {

            return (List<ReadStatus>) inputStream.readObject();

        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(
                    "읽음 상태 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 읽음 상태 목록을 파일에 저장한다.
    private void saveData() {
        Path tempFile = null;

        try {
            // 기존 파일을 보호하기 위해 같은 디렉터리에 임시 파일을 생성한다.
            tempFile = Files.createTempFile(
                    FILE_PATH.getParent(), "readStatusRepository-", ".tmp");

            // 임시 파일에 읽음 상태 목록을 직렬화한다.
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {

                outputStream.writeObject(data);
            }

            // 출력 스트림이 닫힌 후 임시 파일을 기존 데이터 파일로 교체한다.
            try {
                Files.move(tempFile, FILE_PATH,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException amnsException) {
                // 원자적 이동을 지원하지 않으면 일반 파일 이동으로 대체한다.
                Files.move(tempFile, FILE_PATH,
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException exception) {
            throw new RuntimeException(
                    "읽음 상태 데이터를 저장할 수 없습니다.", exception);
        } finally {
            //
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ioException) {
                }
            }
        }
    }

    // 생성 및 수정
    @Override
    public ReadStatus save(ReadStatus readStatus) {

        List<ReadStatus> prevData = new ArrayList<>(data);

        try {
            for (int i = 0; i < data.size(); i++) {

                if (data.get(i).getId().equals(readStatus.getId())) {
                    data.set(i, readStatus);
                    saveData();
                    return readStatus;
                }
            }

            data.add(readStatus);
            saveData();
            return readStatus;

        } catch (RuntimeException rtException) {
            data = prevData;
            throw rtException;
        }
    }

    // ID로 읽음 상태 조회
    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream()
                .filter(readStatus -> readStatus.getId().equals(id))
                .findFirst();
    }

    // 특정 유저의 특정 채널 읽음 상태 조회
    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId, UUID channelId) {

        return data.stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    // 특정 채널의 모든 읽음 상태 조회
    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {

        return data.stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId))
                .toList();
    }

    // 특정 유저의 모든 읽음 상태 조회
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    // ID로 읽음 상태 삭제
    @Override
    public void deleteById(UUID id) {
        deleteIfExists(
                readStatus -> readStatus.getId().equals(id), true);
    }

    // 특정 유저의 특정 채널 읽음 상태 삭제
    @Override
    public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {

        deleteIfExists(readStatus ->
                readStatus.getUserId().equals(userId)
                        && readStatus.getChannelId().equals(channelId), true);
    }

    // 특정 채널의 모든 읽음 상태 삭제
    @Override
    public void deleteByChannelId(UUID channelId) {

        deleteIfExists(readStatus ->
                readStatus.getChannelId().equals(channelId), false);
    }

    // 특정 유저의 모든 읽음 상태 삭제
    @Override
    public void deleteByUserId(UUID userId) {

        deleteIfExists(readStatus ->
                readStatus.getUserId().equals(userId), true);
    }

    // 조건에 해당하는 읽음 상태를 삭제하고 파일에 저장한다.
    private void deleteIfExists(
            Predicate<ReadStatus> condition, boolean throwIfNotFound) {

        List<ReadStatus> prevData = new ArrayList<>(data);

        try {
            boolean removed = data.removeIf(condition);

            if (!removed && throwIfNotFound) {
                throw new IllegalArgumentException(
                        "삭제할 읽음 상태를 찾을 수 없습니다.");
            }

            if (removed) {
                saveData();
            }

        } catch (RuntimeException rtException) {
            data = prevData;
            throw rtException;
        }
    }
}