package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

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

/** MessageRepository를 객체 직렬화를 이용한 개별 파일로 구현합니다. */
public class FileMessageRepository implements MessageRepository {

    // Path는 파일/폴더 위치를 나타내는 java.nio.file 인터페이스입니다.
    // 이 필드에는 메시지 .ser 파일을 담을 디렉터리 위치가 들어갑니다.
    private final Path directory;

    public FileMessageRepository() {
        // 인자 없는 생성자는 실행 기준 폴더 아래 data/messages를 기본 저장 위치로 사용합니다.
        // System.getProperty("user.dir")은 JVM이 시작된 폴더를 문자열로 돌려줍니다.
        // Paths.get(...)은 경로 조각들을 운영체제에 맞는 Path로 합칩니다.
        this(Paths.get(System.getProperty("user.dir"), "data", "messages"));
    }

    public FileMessageRepository(Path directory) {
        // Path를 직접 전달받으면 테스트나 실행 코드가 저장 폴더를 선택할 수 있습니다.
        this.directory = directory;

        try {
            // 디렉터리와 필요한 중간 디렉터리를 만듭니다. 이미 있으면 그대로 둡니다.
            Files.createDirectories(directory);
        } catch (IOException e) {
            // 파일 시스템의 점검 예외를 애플리케이션에서 처리할 런타임 예외로 바꿉니다.
            throw new IllegalStateException(
                    "메시지 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(Message message) {
        // 메시지 ID로 파일 경로를 계산하고 write에 직렬화 작업을 맡깁니다.
        // 같은 ID로 다시 저장하면 Files.newOutputStream의 기본 동작으로 기존 파일을 덮어씁니다.
        write(fileFor(message.getId()), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        // ID가 가리키는 파일의 위치를 계산합니다.
        Path file = fileFor(id);

        // 파일이 없으면 빈 Optional을 반환해 '조회 결과 없음'을 표현합니다.
        if (!Files.exists(file)) {
            return Optional.empty();
        }

        // 파일이 있으면 역직렬화한 Message를 Optional에 넣어 반환합니다.
        return Optional.of(read(file));
    }

    @Override
    public List<Message> findAll() {
        // 읽은 Message 객체들을 모아 반환하기 위한 임시 목록입니다.
        List<Message> messages = new ArrayList<>();

        // Files.list는 디렉터리 항목을 나열하는 Stream<Path>를 엽니다.
        // try-with-resources는 이 스트림을 사용 후 자동으로 닫습니다.
        try (var files = Files.list(directory)) {
            // .ser 확장자를 가진 파일만 골라냅니다.
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    // 파일마다 read로 Message 객체를 복원해 목록에 추가합니다.
                    .forEach(path -> messages.add(read(path)));
        } catch (IOException e) {
            // 디렉터리를 읽거나 나열할 수 없는 상황을 호출자에게 전달합니다.
            throw new IllegalStateException(
                    "메시지 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        // 내부에서 만든 목록을 외부에서 수정하지 못하도록 불변 복사본을 반환합니다.
        return List.copyOf(messages);
    }

    @Override
    public boolean existsById(UUID id) {
        // 파일이 존재하는지만 확인합니다. 파일 안의 객체는 역직렬화하지 않습니다.
        return Files.exists(fileFor(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            // 파일이 지워지면 true, 해당 ID 파일이 애초에 없으면 false입니다.
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            // 권한 등의 문제로 파일을 삭제할 수 없으면 런타임 예외로 알립니다.
            throw new IllegalStateException(
                    "메시지 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        // Path.resolve는 디렉터리 경로에 파일명을 이어 붙입니다.
        // 이 규칙을 메서드 하나에 두어 저장·조회·삭제가 같은 경로를 사용합니다.
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, Message message) {
        // Files.newOutputStream은 파일 쓰기 통로를 열고,
        // ObjectOutputStream은 객체를 바이트 형태로 직렬화해 기록합니다.
        // writeObject(message)가 Message 객체를 파일에 씁니다.
        // try-with-resources가 작업이 끝나면 스트림을 자동으로 닫습니다.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
        } catch (IOException e) {
            // 디스크 쓰기 실패 등의 문제를 애플리케이션 예외로 전달합니다.
            throw new IllegalStateException(
                    "메시지 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private Message read(Path file) {
        // Files.newInputStream은 파일 읽기 통로를 엽니다.
        // ObjectInputStream은 저장된 바이트를 해석하고 readObject가 객체를 복원합니다.
        // 파일에 Message를 저장했으므로 결과를 Message로 형변환합니다.
        // 저장 당시 클래스 정보를 현재 실행 환경에서 찾을 수 있어야 합니다.
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // 파일 읽기 실패 또는 클래스 정보를 찾지 못한 경우를 처리합니다.
            throw new IllegalStateException(
                    "메시지 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
