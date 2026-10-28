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

/** UserRepository를 객체 직렬화를 이용한 파일 저장 방식으로 구현합니다. */
public class FileUserRepository implements UserRepository {

    // java.nio.file.Path는 파일이나 디렉터리의 위치를 나타내는 인터페이스입니다.
    // 여기서는 사용자 .ser 파일들이 들어갈 디렉터리 경로를 보관합니다.
    private final Path directory;

    // 인자 없는 생성자: 기본 저장 위치를 만들어 아래 경로 지정 생성자에 넘깁니다.
    public FileUserRepository() {
        // user.dir은 JVM이 시작된 기준 폴더입니다.
        // Paths.get(...)은 여러 경로 조각을 OS에 맞는 Path로 합칩니다.
        this(Paths.get(System.getProperty("user.dir"), "data", "users"));
    }

    // 저장할 폴더를 직접 지정할 수 있는 생성자입니다.
    public FileUserRepository(Path directory) {
        // this.directory는 저장 위치를 이후 메서드들에서 재사용하게 합니다.
        this.directory = directory;

        try {
            // Files.createDirectories는 중간 폴더까지 한 번에 만듭니다.
            // 폴더가 이미 있어도 오류 없이 진행하므로 첫 실행과 재실행 모두 가능합니다.
            Files.createDirectories(directory);
        } catch (IOException e) {
            // 파일 시스템 오류를 이 프로그램의 런타임 예외로 감싸 호출자에게 전달합니다.
            throw new IllegalStateException(
                    "사용자 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(User user) {
        // UUID를 파일명으로 바꾸고, 아래 write 메서드에 실제 파일 기록을 맡깁니다.
        write(fileFor(user.getId()), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        // ID에서 해당 사용자 파일의 경로를 계산합니다.
        Path file = fileFor(id);

        // Files.exists는 그 경로에 파일이 있는지 true/false로 알려줍니다.
        if (!Files.exists(file)) {
            // Optional.empty()는 '조회 결과 없음'을 null 대신 명시적으로 표현합니다.
            return Optional.empty();
        }

        // 파일이 있으면 read가 객체로 복원하고 Optional.of가 결과를 감쌉니다.
        return Optional.of(read(file));
    }

    @Override
    public List<User> findAll() {
        // 읽어 온 사용자 객체를 차례대로 모아 둘 임시 목록입니다.
        List<User> users = new ArrayList<>();

        // Files.list는 디렉터리 안 경로들의 Stream<Path>를 엽니다.
        // try-with-resources로 스트림을 자동 종료해 폴더 핸들을 놓아줍니다.
        try (var files = Files.list(directory)) {
            // filter는 .ser로 끝나는 경로만 남깁니다.
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    // 남은 경로마다 read로 User를 복원해 목록에 추가합니다.
                    .forEach(path -> users.add(read(path)));
        } catch (IOException e) {
            // 디렉터리 열기/나열 중 발생한 입출력 오류를 의미 있는 예외로 바꿉니다.
            throw new IllegalStateException(
                    "사용자 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        // 내부 목록을 외부에서 추가·삭제할 수 없도록 복사본을 반환합니다.
        return List.copyOf(users);
    }

    @Override
    public boolean existsById(UUID id) {
        // ID로 계산한 경로가 존재하는지 확인합니다. 객체 내용은 읽지 않습니다.
        return Files.exists(fileFor(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            // Files.deleteIfExists는 파일을 지웠으면 true, 원래 없으면 false입니다.
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            // 권한 문제나 파일 잠금 등으로 삭제하지 못한 경우 호출자에게 알립니다.
            throw new IllegalStateException(
                    "사용자 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        // Path.resolve는 디렉터리 경로 뒤에 파일명을 안전하게 이어 붙입니다.
        // 예: .../users + UUID.ser -> .../users/UUID.ser
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, User user) {
        // Files.newOutputStream은 파일에 바이트를 쓸 통로를 엽니다.
        // ObjectOutputStream은 그 통로 위에서 Java 객체를 직렬화해 기록합니다.
        // writeObject는 User와 그 필드 값을 파일에 씁니다.
        // try-with-resources가 성공/실패와 관계없이 두 스트림을 닫습니다.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(user);
        } catch (IOException e) {
            // 디스크 공간 부족, 권한 등의 파일 쓰기 오류를 런타임 예외로 전달합니다.
            throw new IllegalStateException(
                    "사용자 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private User read(Path file) {
        // Files.newInputStream은 파일에서 바이트를 읽을 통로를 엽니다.
        // ObjectInputStream은 그 바이트를 해석해 readObject로 객체를 복원합니다.
        // 파일에 기록된 객체가 User라고 알고 있으므로 User로 형변환합니다.
        // try-with-resources가 읽기 스트림을 자동으로 닫습니다.
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // 입출력 실패 또는 저장된 클래스 정보를 찾지 못한 경우를 처리합니다.
            throw new IllegalStateException(
                    "사용자 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
