package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateDto dto) {
        // 중복 검증 (메서드 호출 시 get 생략)
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다: " + dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 존재하는 email입니다: " + dto.email());
        }

        User user = new User(dto.username(), dto.email(), dto.password());

        // 프로필 이미지 정보가 있을 경우
        if (dto.hasProfileImage()) {
            BinaryContent binaryContent = new BinaryContent(
                    dto.fileName(),
                    dto.contentType(),
                    dto.fileSize(),
                    dto.fileUrl()
            );
            binaryContentRepository.save(binaryContent);
            user.updateProfile(binaryContent.getId());
        }

        // 유저 상태(UserStatus) 같이 생성 및 저장
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElse(null);
        return UserResponseDto.excludePassword(user, userStatus);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
            return UserResponseDto.excludePassword(user, userStatus);
        }).toList();
    }

    @Override
    public User update(UUID userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 다른 유저와의 중복 검증
        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.username())) {
                throw new IllegalArgumentException("이미 존재하는 username입니다: " + dto.username());
            }
        }
        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new IllegalArgumentException("이미 존재하는 email입니다: " + dto.email());
            }
        }

        // 프로필 이미지 검증
        if (dto.profileImageId() != null) {
            boolean imageExists = binaryContentRepository.findById(dto.profileImageId()).isPresent();
            if (!imageExists) {
                throw new IllegalArgumentException("존재하지 않는 바이너리 콘텐츠 ID입니다: " + dto.profileImageId());
            }
            user.updateProfile(dto.profileImageId());
        }

        user.update(dto.username(), dto.email(), dto.password());
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userStatusRepository.deleteByUserId(userId);
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userRepository.deleteById(userId);
    }
}
