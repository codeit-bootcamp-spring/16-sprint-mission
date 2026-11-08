package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

// JCFReadStatusRepository와 FileReadStatusRepository가 모두 @Repository로 등록되어 있다면,
// Spring 컨테이너가 ReadStatusRepository 타입의 빈을 주입할 때 어떤 구현체를 사용할 지 모르기 때문에,
// @Primary, @Qualifier, @Profile등을 이용해 우선순위를 지정해주거나, 주입할 빈을 명시할 수 있습니다.
// @Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                // 런타임(실행 중)에서 발생하는 예외를 나타냅니다.
                throw new RuntimeException(e);
            }
        }
    }

    // 기존 경로를 기준으로 다른 경로를 결합해 새로운 Path 객체를 반환합니다.
    // 즉, 기존 경로 뒤에 새로운 경로를 붙여주어, 파일의 전체 경로값을 생성합니다.
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        // ReadStatus 객체의 id를 사용하여 파일의 전체 경로값을 생성합니다.
        Path path = this.resolvePath(readStatus.getId());
        // try-with-resources 구문입니다.
        // try()의 괄호 안에는, 사용이 끝난 후 자동으로 닫아야 하는 리소스를 선언합니다.
        // try 블럭 종료 시(예외 발생 여부와 상관없이), 자동으로 스트림을 닫아줍니다.
        // 즉, 자동으로 close()를 호출합니다.
        // AutoCloseable 인터페이스를 구현한 객체만, 자동으로 close()가 호출될 수 있습니다.
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public List<ReadStatus> saveAll(List<ReadStatus> readStatusList) {
        readStatusList.forEach(this::save);

        return readStatusList;
    }


    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatusNullable = null;
        Path path = this.resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                readStatusNullable = (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(readStatusNullable);
    }

    @Override
    public List<ReadStatus> findAll() {
        // Files.list()는 Stream<Path>를 반환합니다.
        // 이 스트림은 디렉터리를 읽는 시스템 리소스를 사용하기 때문에, 사용 후 close()를 호출해야 합니다.
        // 따라서, 디렉터리를 읽는 시스템 리소스를 닫기 위해,
        // try-catch문 대신에, try-with-resources문으로 리소스를 자동으로 닫고
        // catch문을 함께 사용해 예외 처리를 해주어야 합니다.
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            // 특정 디렉터리의 특정 익스텐션(확장자)를 가진 파일들의 전체 경로를 불러옵니다.
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    // 각 파일들의 전체 경로를 이용하여, ReadStatus 객체를 생성하고
                    // 그 객체들이 담긴 리스트를 반환합니다.
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        // this.findAll()을 사용해서 이 메서드를 구현하는 경우에 발생할 수 있는 문제
        // this.findAll()에서 예외가 발생하면 해당 메서드가 제대로 동작하지 않습니다.
        // 근데 찾으려는 ReadStatus에 담긴 userId가 애시당초 예외를 발생시키지않는 경우라면,
        // findAll()을 사용하는 것은 적절하지 못할 수 있습니다.
        return this.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = this.resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = this.resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}