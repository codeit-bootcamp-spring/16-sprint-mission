package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
    private FileMessageService messageService;

    public FileUserService() { this(Path.of("data", "users")); }

    public FileUserService(Path userDirectory) {
        this.userDirectory = userDirectory;
        try { Files.createDirectories(userDirectory); }
        catch (IOException e) { throw new RuntimeException("유저 디렉터리 생성에 실패했습니다.", e); }
    }

    public void setChannelService(FileChannelService channelService) { this.channelService = channelService; }
    public void setMessageService(FileMessageService messageService) { this.messageService = messageService; }

    private Path fileFor(UUID id) { return userDirectory.resolve(id + ".ser"); }

    private void writeUser(User user) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fileFor(user.getId())))) {
            out.writeObject(user);
        } catch (IOException e) { throw new RuntimeException("사용자 저장에 실패했습니다.", e); }
    }

    void saveUser(User user) { writeUser(user); }

    private User readUser(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) { throw new RuntimeException("사용자 파일 읽기에 실패했습니다.", e); }
    }

    private User readUser(UUID id) {
        Path file = fileFor(id);
        if (Files.notExists(file)) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        return readUser(file);
    }

    private List<User> readAllUsers() {
        List<User> users = new ArrayList<>();
        try (Stream<Path> files = Files.list(userDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> users.add(readUser(path)));
        } catch (IOException e) { throw new RuntimeException("사용자 파일 목록을 읽는 데 실패했습니다.", e); }
        return users;
    }

    private void validatePassword(String origin, String entered) {
        if (origin == null || !origin.equals(entered)) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    private void validateDuplicateEmail(String email) {
        if (email == null) return;
        if (readAllUsers().stream().anyMatch(user -> email.equals(user.getEmail()))) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    @Override
    public User createUser(User user) {
        if (user == null) throw new IllegalArgumentException("유저가 null입니다.");
        if (Files.exists(fileFor(user.getId()))) throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        validateDuplicateEmail(user.getEmail());
        writeUser(user);
        return user;
    }

    @Override
    public User getUserById(UUID id) { return readUser(id); }
    @Override
    public List<User> getAllUsers() { return List.copyOf(readAllUsers()); }

    @Override
    public User updateUsername(UUID id, String username) {
        User user = getUserById(id); user.changeUsername(username); writeUser(user); return user;
    }

    @Override
    public User updatePassword(UUID id, String originPassword, String newPassword) {
        User user = getUserById(id); validatePassword(user.getPassword(), originPassword);
        user.changePassword(newPassword); writeUser(user); return user;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일을 입력해 주세요.");
        User user = getUserById(id);
        if (email.equals(user.getEmail())) throw new IllegalArgumentException("현재 사용 중인 이메일과 같습니다.");
        validateDuplicateEmail(email); user.changeEmail(email); writeUser(user); return user;
    }

    @Override
    public void deleteUser(UUID id, String password) {
        User user = getUserById(id); validatePassword(user.getPassword(), password);
        messageService.deleteMessagesByUserId(id);
        for (Channel channel : channelService.getChannelsByParticipantId(id)) {
            if (channel.getCreatorId().equals(id)) {
                channelService.deleteChannel(channel.getId(), id);
            } else {
                channel.leaveChannel(id);
                user.removeChannelId(channel.getId());
                channelService.saveChannel(channel);
                writeUser(user);
            }
        }
        try { Files.delete(fileFor(id)); }
        catch (IOException e) { throw new RuntimeException("사용자 파일 삭제에 실패했습니다.", e); }
    }

    @Override
    public void deleteEmail(UUID id) {
        User user = getUserById(id); user.deleteEmail(); writeUser(user);
    }
}
