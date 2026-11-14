package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.DataCleanupService;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final DataCleanupService dataCleanupService;

    @Override
    public UserResponseDto createUser(
            UserCreateRequestDto dto,
            MultipartFile profile
    ) throws IOException {

        validateUsernameDuplicate(dto.getUsername(), null);
        validateEmailDuplicate(dto.getEmail(), null);

        User user = new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()
        );

        if (profile != null) {
            BinaryContent binaryContent =
                    new BinaryContent(
                            profile.getOriginalFilename(),
                            profile.getContentType(),
                            profile.getBytes()
                    );

            BinaryContent savedContent =
                    binaryContentRepository.save(binaryContent);

            user.changeProfileId(savedContent.getId());
        }

        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);

        return toResponseDto(savedUser);
    }

    @Override
    public UserDto findUser(UUID userId) {
        User user = findUserEntity(userId);
        return toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }

        return result;
    }

    @Override
    public UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto,
            MultipartFile profile
    ) throws IOException {
        User user = findUserEntity(userId);

        if (dto.getNewUsername() != null) {
            validateUsernameDuplicate(dto.getNewUsername(), userId);
            user.changeUsername(dto.getNewUsername());
        }

        if (dto.getNewEmail() != null) {
            validateEmailDuplicate(dto.getNewEmail(), userId);
            user.changeEmail(dto.getNewEmail());
        }

        if (dto.getNewPassword() != null) {
            user.changePassword(dto.getNewPassword());
        }

        UUID oldProfileId = user.getProfileId();

        if (profile != null && !profile.isEmpty()) {
            BinaryContent newProfile = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );

            BinaryContent savedProfile = binaryContentRepository.save(newProfile);
            user.changeProfileId(savedProfile.getId());
        }

        User savedUser = userRepository.save(user);

        if (profile != null && !profile.isEmpty() && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return toResponseDto(savedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        dataCleanupService.deleteUserData(userId);
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId)
                );
    }

    private void validateUsernameDuplicate(
            String username,
            UUID excludeUserId
    ) {
        boolean duplicated = excludeUserId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, excludeUserId);

        if (duplicated) {
            throw new BusinessLogicException(ExceptionCode.USERNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmailDuplicate(
            String email,
            UUID excludeUserId
    ) {
        if (email == null) {
            return;
        }

        boolean duplicated = excludeUserId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, excludeUserId);

        if (duplicated) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .profileId(user.getProfileId())
                .build();
    }

    private UserDto toUserDto(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return UserDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .online(online)
                .build();
    }
}