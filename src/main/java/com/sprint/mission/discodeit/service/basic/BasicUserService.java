package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;

import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.entity.newDomain.UserStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicUserService implements UserService {

    // 사용자 데이터 및 관련 상태를 관리하는 Repository
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    // 비밀번호 암호화 및 검증을 위한 객체
    private final PasswordEncoder passwordEncoder;

    // 여러 Repository의 데이터 변경 작업을 하나의 트랜잭션으로 관리한다.
    // 단, 현재 File/JCF 기반 Repository의 변경 사항은 자동으로 롤백되지 않는다.
    @Transactional
    @Override
    public UserDto createUser(UserCreateRequest request) {

        // 1. 사용자 이름 중복 검사
        boolean duplicatedUsername = userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(request.username()));

        if (duplicatedUsername) {
            throw new IllegalArgumentException("중복된 이름입니다.");
        }

        // 2. 이메일 중복 검사
        boolean duplicatedEmail = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(request.email()));

        if (duplicatedEmail) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // 3. 프로필 이미지가 전달된 경우 저장
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

        // 4. 비밀번호를 BCrypt 방식으로 암호화하고 사용자 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.username(), request.email(),
                encodedPassword, profileId);

        userRepository.save(user);

        // 5. 사용자 상태 생성 및 저장
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        // 6. 사용자 정보와 상태를 DTO로 변환하여 반환
        return toDto(user, userStatus);
    }

    @Override
    public UserDto readUser(UUID id) {

        // 1. 사용자 조회
        User user = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 사용자 상태 조회
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저 상태를 찾을 수 없습니다."));

        // 3. 조회 결과를 DTO로 변환하여 반환
        return toDto(user, userStatus);
    }

    @Override
    public List<UserDto> readAllUser() {

        // 전체 사용자를 조회하고 각 사용자의 상태를 포함하여 DTO로 변환한다.
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

    // 여러 Repository의 데이터 변경 작업을 하나의 트랜잭션으로 관리한다.
    // 단, 현재 File/JCF 기반 Repository의 변경 사항은 자동으로 롤백되지 않는다.
    @Transactional
    @Override
    public UserDto updateUser(UserUpdateRequest request) {

        // 1. 수정 대상 사용자 조회
        User targetUser = userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 전달되지 않은 항목은 기존 값을 유지한다.
        String username = request.username() != null
                ? request.username() : targetUser.getUsername();

        String email = request.email() != null
                ? request.email() : targetUser.getEmail();

        // 새 비밀번호가 전달된 경우 암호화하고, 그렇지 않으면 기존 비밀번호를 유지한다.
        String password = request.password() != null
                ? passwordEncoder.encode(request.password()) : targetUser.getPassword();

        // 2. 사용자 이름 중복 검사 (본인 제외)
        boolean duplicatedUsername = userRepository.findAll().stream()
                .anyMatch(otherUser ->
                        !otherUser.getId().equals(request.userId())
                                && otherUser.getUsername().equals(username));

        if (duplicatedUsername) {
            throw new IllegalArgumentException("중복된 사용자 이름입니다.");
        }

        // 3. 이메일 중복 검사 (본인 제외)
        boolean duplicatedEmail = userRepository.findAll().stream()
                .anyMatch(otherUser ->
                        !otherUser.getId().equals(request.userId())
                                && otherUser.getEmail().equals(email));

        if (duplicatedEmail) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // 4. 기존 프로필 이미지 ID 유지
        UUID oldProfileId = targetUser.getProfileId();
        UUID profileId = targetUser.getProfileId();

        // 5. 새 프로필 이미지가 전달된 경우 저장하고 ID 갱신
        if (request.profile() != null) {

            BinaryContentCreateRequest profileRequest = request.profile();

            BinaryContent newProfile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.size(),
                    profileRequest.bytes()
            );

            binaryContentRepository.save(newProfile);

            profileId = newProfile.getId();
        }

        // 6. 기존 사용자 정보를 바탕으로 수정된 사용자 생성
        User updatedUser = new User(
                targetUser,
                username,
                email,
                password,
                profileId
        );

        // 7. 수정된 사용자 정보 저장
        userRepository.save(updatedUser);

        // 새 프로필로 교체된 경우 기존 프로필 이미지 삭제
        if (request.profile() != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        // 8. 사용자 상태 조회 및 DTO 반환
        UserStatus userStatus = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("유저 상태를 찾을 수 없습니다."));

        return toDto(updatedUser, userStatus);
    }

    // 여러 Repository의 데이터 변경 작업을 하나의 트랜잭션으로 관리한다.
    // 단, 현재 File/JCF 기반 Repository의 변경 사항은 자동으로 롤백되지 않는다.
    @Transactional
    @Override
    public void deleteUser(UUID id) {

        // 1. 삭제 대상 사용자 조회
        User user = userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 프로필 이미지 ID 확인
        UUID profileId = user.getProfileId();

        // 3. 사용자 상태 삭제
        userStatusRepository.deleteByUserId(id);

        // 4. 프로필 이미지가 존재하면 삭제
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 5. 사용자 정보 삭제
        userRepository.deleteByUserId(id);
    }

    // 사용자 정보와 상태를 UserDto로 변환한다.
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