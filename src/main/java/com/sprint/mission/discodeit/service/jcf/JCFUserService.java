package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.request.otherRequest.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;
import com.sprint.mission.discodeit.dto.request.userRequest.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.entity.newDomain.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    // Repository 의존성
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    // 생성자 의존성 주입
    public JCFUserService(
            UserRepository userRepository,
            BinaryContentRepository binaryContentRepository,
            UserStatusRepository userStatusRepository) {

        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserDto createUser(UserCreateRequest request) {

        // 사용자 이름 중복 검사
        boolean duplicatedUsername = userRepository.findAll().stream()
                .anyMatch(targetUser ->
                        targetUser.getUsername().equals(request.username()));

        if (duplicatedUsername) {
            throw new IllegalArgumentException("중복된 이름입니다.");
        }

        // 이메일 중복 검사
        boolean duplicatedEmail = userRepository.findAll().stream()
                .anyMatch(targetUser ->
                        targetUser.getEmail().equals(request.email()));

        if (duplicatedEmail) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // 프로필 이미지가 전달된 경우 저장
        // 프로필 이미지 DTO를 Entity로 변환하고 저장한다.
        BinaryContentCreateRequest profileRequest = request.profile();
        BinaryContent profile = null;

        if (profileRequest != null) {
            profile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.size(),
                    profileRequest.bytes()
            );
            binaryContentRepository.save(profile);
        }

        UUID profileId = profile != null ? profile.getId() : null;

        // 사용자 생성 및 저장
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );

        userRepository.save(user);

        // 사용자 상태 생성 및 저장
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        // DTO로 변환하여 반환
        return toDto(user, userStatus);
    }

    @Override
    public UserDto readUser(UUID id) {

        // 요청받은 ID와 일치하는 유저를 찾는다.
        User user = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 해당 유저의 상태 정보를 찾는다.
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저 상태를 찾을 수 없습니다."));

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
                                    new IllegalArgumentException("유저 상태를 찾을 수 없습니다."));

                    return toDto(user, userStatus);
                })
                .toList();
    }

    @Override
    public UserDto updateUser(UserUpdateRequest request) {

        // 수정 대상 사용자 조회
        User targetUser = userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("수정할 유저를 찾을 수 없습니다."));

        // 사용자 이름 변경 시 중복 검사
        String username = request.username() != null
                ? request.username() : targetUser.getUsername();

        if (!targetUser.getUsername().equals(username)) {
            boolean duplicatedUsername = userRepository.findAll().stream()
                    .anyMatch(otherUser ->
                            !otherUser.getId().equals(request.userId())
                                    && otherUser.getUsername().equals(username));

            if (duplicatedUsername) {
                throw new IllegalArgumentException("중복된 이름입니다.");
            }
        }

        // 이메일 변경 시 중복 검사
        String email = request.email() != null
                ? request.email() : targetUser.getEmail();

        if (!targetUser.getEmail().equals(email)) {
            boolean duplicatedEmail = userRepository.findAll().stream()
                    .anyMatch(otherUser ->
                            !otherUser.getId().equals(request.userId())
                                    && otherUser.getEmail().equals(email));

            if (duplicatedEmail) {
                throw new IllegalArgumentException("중복된 이메일입니다.");
            }
        }

        // 새 프로필 이미지 DTO를 Entity로 변환하고 저장한다.
        BinaryContentCreateRequest profileRequest = request.profile();

        UUID profileId = targetUser.getProfileId();

        if (profileRequest != null) {
            BinaryContent newProfile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.size(),
                    profileRequest.bytes()
            );

            binaryContentRepository.save(newProfile);
            profileId = newProfile.getId();
        }

        // 전달되지 않은 수정 항목은 기존 값 유지
        String password = request.password() != null
                ? request.password() : targetUser.getPassword();

        // 수정된 사용자 생성 및 저장
        User updatedUser = new User(targetUser, username, email, password, profileId);

        userRepository.save(updatedUser);

        // 사용자 상태 조회
        UserStatus userStatus = userStatusRepository
                .findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("유저 상태를 찾을 수 없습니다."));

        return toDto(updatedUser, userStatus);
    }

    @Override
    public void deleteUser(UUID id) {

        // 삭제 대상 사용자 조회
        User targetUser = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다."));

        // 사용자 상태 삭제
        userStatusRepository.deleteByUserId(id);

        // 프로필 이미지가 존재하는 경우 삭제
        UUID profileId = targetUser.getProfileId();

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 4. 사용자 삭제
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