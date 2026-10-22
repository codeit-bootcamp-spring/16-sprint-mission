package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    private final Path userDirectory;

    private FileChannelService channelService;
    private MessageService messageService;

    public FileUserService() {
        this(Path.of("data", "users"));
    }

    public FileUserService(Path userDirectory) {
        this.userDirectory = userDirectory;

        try {
            Files.createDirectories(userDirectory);
        } catch (IOException e) {
            throw new RuntimeException("유저 디렉터리 생성에 실패했습니다.", e);
        }
    }

    public void setChannelService(FileChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private Path fileFor(UUID userId) {
        return userDirectory.resolve(userId + ".ser");
    }

    private void writeUser(User user) {
        Path file = fileFor(user.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("사용자 저장에 실패했습니다.", e);
        }
    }

    // 같은 패키지의 File 서비스가 연관 객체 변경 후 사용자 파일을 저장할 때 사용
    void saveUser(User user) {
        writeUser(user);
    }

    private User readUser(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 파일 읽기에 실패했습니다.", e);
        }
    }

    private User readUser(UUID userId) {
        Path file = fileFor(userId);

        if (Files.notExists(file)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return readUser(file);
    }

    private List<User> readAllUsers() {
        List<User> users = new ArrayList<>();

        try (Stream<Path> files = Files.list(userDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> users.add(readUser(path)));
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return users;
    }

    private void validatePassword(String origin, String entered) {
        if (origin == null || !origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void validateDuplicateEmail(String email) {
        if (email == null) {
            return;
        }

        boolean exists = readAllUsers().stream()
                .anyMatch(user -> email.equals(user.getEmail()));

        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저가 null입니다.");
        }

        Path file = fileFor(user.getId());

        if (Files.exists(file)) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        validateDuplicateEmail(user.getEmail());
        writeUser(user);
        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return readUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(readAllUsers());
    }

    @Override
    public User updateUsername(UUID userId, String username) {
        User user = getUserById(userId);
        user.changeUsername(username);
        writeUser(user);
        return user;
    }

    @Override
    public User updatePassword(
            UUID userId,
            String originPassword,
            String newPassword
    ) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), originPassword);
        user.changePassword(newPassword);
        writeUser(user);
        return user;
    }

    @Override
    public User updateEmail(UUID userId, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }

        User user = getUserById(userId);

        if (email.equals(user.getEmail())) {
            throw new IllegalArgumentException("현재 사용 중인 이메일과 같습니다.");
        }

        validateDuplicateEmail(email);
        user.changeEmail(email);
        writeUser(user);
        return user;
    }

    @Override
    public void deleteUser(UUID userId, String password) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), password);

        // 사용자가 작성한 메시지를 지우면서 연관된 사용자, 채널 파일도 갱신
        messageService.deleteMessagesByUserId(userId);

        List<Channel> channels =
                channelService.getChannelsByParticipantId(userId);

        for (Channel channel : channels) {
            if (channel.getCreator().getId().equals(userId)) {
                channelService.deleteChannel(channel.getId(), userId);
            } else {
                channel.leaveChannel(user);

                // 퇴장한 채널과 사용자 파일을 모두 갱신
                channelService.saveChannel(channel);
                writeUser(user);
            }
        }

        try {
            Files.delete(fileFor(userId));
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteEmail(UUID userId) {
        User user = getUserById(userId);
        user.deleteEmail();
        writeUser(user);
    }
}