
package com.sprint.mission.discodeit.repository.file.otherFile;

import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class FileBinaryContentRepository implements BinaryContentRepository {

    // 바이너리 콘텐츠 데이터 파일이 저장될 경로
    private static final Path FILE_PATH =
            Paths.get("data", "binaryContentRepository.ser");

    // 파일에서 읽어 온 바이너리 콘텐츠 목록
    private List<BinaryContent> data;

    // 데이터 디렉터리를 생성하고 파일에 저장된 바이너리 콘텐츠 목록을 초기화한다.
    public FileBinaryContentRepository() {
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException exception) {
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", exception);
        }
    }

    // 파일에서 바이너리 콘텐츠 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<BinaryContent> loadData() {
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(FILE_PATH))) {

            return (List<BinaryContent>) inputStream.readObject();

        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("바이너리 콘텐츠 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 바이너리 콘텐츠 목록을 파일에 저장한다.
    private void saveData(List<BinaryContent> dataToSave) {
        Path tempFile = null;

        try {
            // 기존 파일을 보호하기 위해 같은 디렉터리에 임시 파일을 생성한다.
            tempFile = Files.createTempFile(
                    FILE_PATH.getParent(), "binaryContentRepository-", ".tmp");

            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {

                outputStream.writeObject(dataToSave);

                // 임시 파일에 데이터 저장을 완료한 후 기존 파일을 교체한다.
                try {
                    Files.move(tempFile, FILE_PATH,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException exception) {
                    // 원자적 이동을 지원하지 않으면 일반 파일 이동으로 대체한다.
                    Files.move(tempFile, FILE_PATH,
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("바이너리 콘텐츠 데이터를 저장할 수 없습니다.", exception);
        } finally {
            // 저장 과정에서 남은 임시 파일을 정리한다.
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    // 바이너리 콘텐츠 ID를 기준으로 기존 데이터를 수정하거나 새로운 데이터를 추가한다.
    @Override
    public void save(BinaryContent binaryContent) {
        // 기존 데이터에 영향을 주지 않도록 복사본을 생성한다.
        List<BinaryContent> updatedData = new ArrayList<>(data);

        for (int i = 0; i < updatedData.size(); i++) {
            if (updatedData.get(i).getId().equals(binaryContent.getId())) {
                updatedData.set(i, binaryContent);
                saveData(updatedData);
                data = updatedData;
                return;
            }
        }

        updatedData.add(binaryContent);
        saveData(updatedData);
        data = updatedData;
    }

    // 바이너리 콘텐츠 ID와 일치하는 데이터를 조회한다.
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream()
                .filter(binaryContent -> binaryContent.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return data.stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    // 바이너리 콘텐츠 ID와 일치하는 데이터를 삭제하고 변경된 목록을 파일에 저장한다.
    @Override
    public void deleteById(UUID id) {
        List<BinaryContent> updatedData = new ArrayList<>(data);

        boolean removed = updatedData.removeIf(
                binaryContent -> binaryContent.getId().equals(id));

        if (!removed) {
            throw new IllegalArgumentException("삭제할 바이너리 콘텐츠를 찾을 수 없습니다.");
        }

        saveData(updatedData);
        data = updatedData;
    }
}