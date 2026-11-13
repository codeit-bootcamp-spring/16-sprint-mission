package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.exceptions.DuplicateResourceException;
import com.sprint.mission.discodeit.exceptions.FileProcessingException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    // Repository 및 PasswordEncoder 의존성
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 의존성 주입
    public JCFUserService(
            UserRepository userRepository,
            BinaryContentRepository binaryContentRepository,
            UserStatusRepository userStatusRepository,
            ReadStatusRepository readStatusRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.userStatusRepository = userStatusRepository;
        this.readStatusRepository = readStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserCreateRequest request, MultipartFile profile) {

        // 사용자 이름 중복 검사
        boolean duplicatedUsername = userRepository.findAll().stream()
                .anyMatch(targetUser ->
                        targetUser.getUsername().equals(request.username()));

        if (duplicatedUsername) {
            throw new DuplicateResourceException("중복된 이름입니다.");
        }

        // 이메일 중복 검사
        boolean duplicatedEmail = userRepository.findAll().stream()
                .anyMatch(targetUser ->
                        targetUser.getEmail().equals(request.email()));

        if (duplicatedEmail) {
            throw new DuplicateResourceException("중복된 이메일입니다.");
        }

        // 프로필 이미지가 전달된 경우 BinaryContent로 변환하여 저장한다.
        BinaryContent profileContent = null;

        if (profile != null && !profile.isEmpty()) {
            profileContent = createBinaryContent(profile);
            binaryContentRepository.save(profileContent);
        }

        UUID profileId = profileContent != null ? profileContent.getId() : null;

        // 비밀번호를 암호화한다.
        String encodedPassword = passwordEncoder.encode(request.password());

        // 사용자 생성 및 저장
        User user = new User(
                request.username(),
                request.email(),
                encodedPassword,
                profileId
        );

        userRepository.save(user);

        // 사용자 상태 생성 및 저장
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        // DTO로 변환하여 반환
        return toDto(user, userStatus);
    }

    // MultipartFile을 BinaryContent 객체로 변환한다.
    private BinaryContent createBinaryContent(MultipartFile file) {
        try {
            return new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );
        } catch (IOException ioException) {
            throw new FileProcessingException("프로필 파일을 읽을 수 없습니다.", ioException);
        }
    }

    @Override
    public UserDto readUser(UUID id) {

        // 요청받은 ID와 일치하는 유저를 찾는다.
        User user = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        // 해당 유저의 상태 정보를 찾는다.
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저 상태를 찾을 수 없습니다."));

        return toDto(user, userStatus);
    }

    @Override
    public List<UserDto> readAllUser() {

        // 전체 유저 목록을 DTO로 변환한다.
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository
                            .findByUserId(user.getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("유저 상태를 찾을 수 없습니다."));

                    return toDto(user, userStatus);
                })
                .toList();
    }

    @Override
    public UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile profile) {

        // 수정 대상 사용자 조회
        User targetUser = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("수정할 유저를 찾을 수 없습니다."));

        // 사용자 이름 변경 시 중복 검사
        String username = request.newUsername() != null
                ? request.newUsername() : targetUser.getUsername();

        if (!targetUser.getUsername().equals(username)) {
            boolean duplicatedUsername = userRepository.findAll().stream()
                    .anyMatch(otherUser ->
                            !otherUser.getId().equals(userId)
                                    && otherUser.getUsername().equals(username));

            if (duplicatedUsername) {
                throw new DuplicateResourceException("중복된 이름입니다.");
            }
        }

        // 이메일 변경 시 중복 검사
        String email = request.newEmail() != null
                ? request.newEmail() : targetUser.getEmail();

        if (!targetUser.getEmail().equals(email)) {
            boolean duplicatedEmail = userRepository.findAll().stream()
                    .anyMatch(otherUser ->
                            !otherUser.getId().equals(userId)
                                    && otherUser.getEmail().equals(email));

            if (duplicatedEmail) {
                throw new DuplicateResourceException("중복된 이메일입니다.");
            }
        }

        // 기존 프로필 이미지 ID를 유지한다.
        UUID profileId = targetUser.getProfileId();

        // 새로운 프로필 이미지가 전달된 경우 BinaryContent로 변환하여 저장한다.
        if (profile != null && !profile.isEmpty()) {
            BinaryContent newProfile = createBinaryContent(profile);

            binaryContentRepository.save(newProfile);
            profileId = newProfile.getId();
        }

        // 전달되지 않은 수정 항목은 기존 값을 유지한다.
        String password = request.newPassword() != null
                ? passwordEncoder.encode(request.newPassword()) : targetUser.getPassword();

        // 수정된 사용자 생성 및 저장
        User updatedUser = new User(
                targetUser,
                username,
                email,
                password,
                profileId
        );

        userRepository.save(updatedUser);

        // 새로운 프로필 이미지로 교체했다면 기존 이미지를 삭제한다.
        if (profile != null && !profile.isEmpty()
                && targetUser.getProfileId() != null) {
            binaryContentRepository.deleteById(targetUser.getProfileId());
        }

        // 사용자 상태 조회
        UserStatus userStatus = userStatusRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저 상태를 찾을 수 없습니다."));

        return toDto(updatedUser, userStatus);
    }

    @Override
    public void deleteUser(UUID id) {

        // 삭제 대상 사용자 조회
        User targetUser = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("삭제할 유저를 찾을 수 없습니다."));

        // 프로필 이미지가 존재하는 경우 삭제
        UUID profileId = targetUser.getProfileId();

        // 사용자 상태와 읽음 상태 삭제
        userStatusRepository.deleteByUserId(id);
        readStatusRepository.deleteByUserId(id);

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 사용자 삭제
        userRepository.deleteByUserId(id);
    }

    // User와 UserStatus를 UserDto로 변환한다.
    private UserDto toDto(User user, UserStatus userStatus) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}