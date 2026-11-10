package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
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
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 사용자 생성
    @Override
    public UserDto create(UserCreateRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        // 사용자 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // 프로필 이미지가 전달된 경우 생성
        if (request.getProfileData() != null) {
            BinaryContent profile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData()
            );

            binaryContentRepository.save(profile);

            // 생성한 프로필 이미지 ID를 User에 연결
            user.replaceProfile(profile.getId());
            userRepository.save(user);
        }

        return toDto(user);
    }

    // 사용자 단건 조회
    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

        return toDto(user);
    }

    // 전체 사용자 조회
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 사용자 정보 수정
    @Override
    public UserDto update(UserUpdateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + request.getUserId()));

        // 사용자 이름 중복 확인
        if (request.getUsername() != null) {
            userRepository.findByUsername(request.getUsername())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
                    });
        }

        // 이메일 중복 확인
        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                    });
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 프로필 이미지 교체
        if (request.getProfileData() != null) {

            // 기존 프로필 ID 저장
            UUID oldProfileId = user.getProfileId();

            // 새 프로필 이미지 생성
            BinaryContent newProfile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData()
            );

            binaryContentRepository.save(newProfile);

            // User가 새 프로필 ID를 참조하도록 변경
            user.replaceProfile(newProfile.getId());

            // 사용자 정보 저장
            userRepository.save(user);

            // 기존 프로필만 삭제
            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }
        } else {
            userRepository.save(user);
        }

        return toDto(user);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

        // 해당 사용자의 프로필 이미지만 삭제
        UUID profileId = user.getProfileId();

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 사용자 상태 삭제
        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        // 사용자 삭제
        userRepository.deleteById(userId);
    }

    // Entity를 DTO로 변환
    private UserDto toDto(User user) {

        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }
}