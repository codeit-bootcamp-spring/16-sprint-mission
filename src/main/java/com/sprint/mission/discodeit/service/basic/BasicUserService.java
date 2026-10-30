package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateRequest request, Optional<BinaryContentRequest> profileRequest) {
        if (userRepository.existsByEmail(request.email())) throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        if (userRepository.existsByUsername(request.username())) throw new IllegalArgumentException("이미 존재하는 이름입니다.");

        UUID profileId = null;
        if (profileRequest.isPresent()) {
            BinaryContentRequest p = profileRequest.get();
            BinaryContent profile = new BinaryContent(p.fileName(), p.contentType(), p.bytes());
            binaryContentRepository.save(profile);
            profileId = profile.getId();
        }
        User user = new User(request.username(), request.email(), request.password(), profileId);
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
    public UserDto update(UUID id, UserUpdateRequest request, Optional<BinaryContentRequest> profileRequest) {
        User user = getUserOrThrow(id);

        if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.newUsername())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        profileRequest.ifPresent(p -> {
            UUID oldProfileId = user.getProfileId();
            BinaryContent newProfile = binaryContentRepository.save(
                    new BinaryContent(p.fileName(), p.contentType(), p.bytes()));
            user.updateProfileId(newProfile.getId());
            if (oldProfileId != null) binaryContentRepository.deleteById(oldProfileId);
        });

        user.update(request.newUsername(), request.newEmail(), request.newPassword());
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        User user = getUserOrThrow(id);

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
