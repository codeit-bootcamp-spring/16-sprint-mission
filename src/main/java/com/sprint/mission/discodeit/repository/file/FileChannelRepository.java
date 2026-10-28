package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

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

/** ChannelRepository를 객체 직렬화를 이용한 개별 파일로 구현합니다. */
public class FileChannelRepository implements ChannelRepository {

    // Path는 파일/폴더 위치를 나타내는 java.nio.file 인터페이스입니다.
    // 이 필드는 채널 .ser 파일들이 들어갈 디렉터리를 가리킵니다.
    private final Path directory;

    public FileChannelRepository() {
        // 기본 저장 폴더를 실행 기준 경로 아래 data/channels로 지정합니다.
        // System.getProperty("user.dir")은 JVM이 시작된 위치를 문자열로 가져옵니다.
        // Paths.get(...)은 경로 조각들을 운영체제에 맞는 Path로 결합합니다.
        this(Paths.get(System.getProperty("user.dir"), "data", "channels"));
    }

    public FileChannelRepository(Path directory) {
        // 경로를 생성자로 주입받아 실행 환경이나 실습에 따라 저장 위치를 바꿀 수 있습니다.
        this.directory = directory;

        try {
            // 필요한 폴더를 생성합니다. 이미 존재하는 폴더여도 문제없이 진행합니다.
            Files.createDirectories(directory);
        } catch (IOException e) {
            // 폴더를 만들지 못한 원인을 보존해 런타임 예외로 전달합니다.
            throw new IllegalStateException(
                    "채널 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(Channel channel) {
        // 채널 ID에 해당하는 파일에 객체를 직렬화하도록 write에 맡깁니다.
        write(fileFor(channel.getId()), channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        // 채널 ID로 파일 경로를 계산합니다.
        Path file = fileFor(id);

        // 파일이 없다는 상태를 빈 Optional로 표현합니다.
        if (!Files.exists(file)) {
            return Optional.empty();
        }

        // 파일이 있으면 객체로 복원해 Optional에 담습니다.
        return Optional.of(read(file));
    }

    @Override
    public List<Channel> findAll() {
        // 역직렬화한 Channel 객체를 모아 둘 임시 목록입니다.
        List<Channel> channels = new ArrayList<>();

        // Files.list는 폴더 안 항목들의 Stream<Path>를 열어 줍니다.
        // try-with-resources로 폴더 스트림을 자동으로 닫습니다.
        try (var files = Files.list(directory)) {
            // 채널 데이터 파일인 .ser만 걸러냅니다.
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    // 각 파일을 Channel 객체로 복원해 목록에 담습니다.
                    .forEach(path -> channels.add(read(path)));
        } catch (IOException e) {
            // 폴더를 열거나 나열하지 못했을 때 원인을 전달합니다.
            throw new IllegalStateException(
                    "채널 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        // 호출자가 Repository 내부 결과를 수정하지 못하도록 복사본을 반환합니다.
        return List.copyOf(channels);
    }

    @Override
    public boolean existsById(UUID id) {
        // 파일 존재 여부만 확인하며 실제 파일 내용은 읽지 않습니다.
        return Files.exists(fileFor(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            // 삭제 성공이면 true, 파일이 없으면 false를 반환합니다.
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            // 권한 등으로 파일 삭제가 실패하면 예외로 알려줍니다.
            throw new IllegalStateException(
                    "채널 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        // Path.resolve로 디렉터리에 UUID.ser 파일명을 붙입니다.
        // 저장·조회·삭제에서 이 메서드를 공유해 같은 경로 규칙을 사용합니다.
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, Channel channel) {
        // Files.newOutputStream은 바이트를 쓸 통로를 열고,
        // ObjectOutputStream은 Java 객체를 직렬화합니다.
        // writeObject(channel)가 객체를 파일에 기록합니다.
        // try-with-resources가 스트림을 자동으로 닫습니다.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(channel);
        } catch (IOException e) {
            // 파일에 기록하지 못한 이유를 포함해 런타임 예외로 전달합니다.
            throw new IllegalStateException(
                    "채널 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private Channel read(Path file) {
        // Files.newInputStream은 파일에서 바이트를 읽는 통로를 엽니다.
        // ObjectInputStream의 readObject가 저장된 바이트에서 객체를 복원합니다.
        // 이 파일은 Channel을 저장했으므로 결과를 Channel로 형변환합니다.
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // 파일 읽기 오류 또는 저장된 클래스 정보를 찾지 못한 경우를 처리합니다.
            throw new IllegalStateException(
                    "채널 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
