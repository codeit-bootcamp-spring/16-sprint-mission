package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
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
    public UserDto update(UUID id, UserCreateRequest request, Optional<BinaryContentRequest> profileRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.update(request.username(), request.email(), request.password());
        UUID profileId = null;
        if (profileRequest.isPresent()) {
            BinaryContentRequest p = profileRequest.get();
            BinaryContent profile = new BinaryContent(p.fileName(), p.contentType(), p.bytes());
            binaryContentRepository.save(profile);
            profileId = profile.getId();
            user.update(profileId);
        }
        return userMapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        userRepository.deleteById(id);
        userStatusRepository.deleteByUserId(user.getId());
        binaryContentRepository.deleteById(user.getProfileId());
    }
}
