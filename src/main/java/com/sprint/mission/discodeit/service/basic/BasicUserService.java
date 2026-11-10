package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ChannelOwnerLeaveNotAllowedException;
import com.sprint.mission.discodeit.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.mapper.UserMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileRequest) {
        if (userRepository.existsByEmail(request.email())) throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        if (userRepository.existsByUsername(request.username())) throw new DuplicateUsernameException("이미 존재하는 이름입니다.");

        User user = new User(request.username(), request.email(), request.password(), null);

        profileRequest.ifPresent(p -> {
            BinaryContent profile = binaryContentRepository.save(
                    new BinaryContent(p.fileName(), p.contentType(), p.bytes()));
            user.updateProfileId(profile.getId());
        });

        userRepository.save(user);

        userStatusRepository.save(new UserStatus(user.getId(), Instant.now()));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto findById(UUID id) {
        User user = getUserOrThrow(id);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> findAllByChannelId(UUID channelId) {
        return userRepository.findAll().stream()
                .filter(user -> user.hasChannel(channelId))
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest request, Optional<BinaryContentCreateRequest> profileRequest) {
        User user = getUserOrThrow(id);

        String username = request.newUsername() != null ? request.newUsername() : user.getUsername();
        String email = request.newEmail() != null ? request.newEmail() : user.getEmail();
        String password = request.newPassword() != null ? request.newPassword() : user.getPassword();

        if (!username.equals(user.getUsername())
                && userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("이미 존재하는 이름입니다.");
        }

        if (!email.equals(user.getEmail())
                && userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        user.update(username, email, password);

        UUID oldProfileId = user.getProfileId();
        profileRequest.ifPresent(p -> {
            BinaryContent newProfile = binaryContentRepository.save(
                    new BinaryContent(p.fileName(), p.contentType(), p.bytes()));
            user.updateProfileId(newProfile.getId());
        });

        userRepository.save(user);

        if (profileRequest.isPresent() && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return userMapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        User user = getUserOrThrow(id);

        boolean ownsChannel = channelRepository.findAll().stream()
                .anyMatch(channel -> id.equals(channel.getOwnerId()));
        if (ownsChannel) {
            throw new ChannelOwnerLeaveNotAllowedException("소유한 채널이 있는 사용자는 삭제할 수 없습니다. 먼저 소유권을 양도하세요.");
        }

        // 2. 참여 채널에서 제거
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUsers().contains(id))
                .forEach(channel -> {
                    channel.removeUser(id);
                    channelRepository.save(channel);
                });

        // 3. 수신 정보(ReadStatus) 삭제
        readStatusRepository.deleteAllByUserId(id);

        // 4. 메시지는 보존 (authorId는 그대로 둠)

        // 5. 프로필, 접속 상태, 사용자 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
    }
}
