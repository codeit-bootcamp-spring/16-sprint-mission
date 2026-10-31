
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

    @Override
    public UserDto create(UserCreateRequest request) {

        // 사용자 이름 중복 검사
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성 및 저장
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        // 사용자 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // 프로필 이미지가 전달된 경우 저장
        if (request.getProfileData() != null) {
            BinaryContent profile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(profile);
        }

        return toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

        return toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto update(UserUpdateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + request.getUserId()));

        // 다른 사용자가 같은 이름을 사용하는지 검사
        userRepository.findByUsername(request.getUsername())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
                });

        // 다른 사용자가 같은 이메일을 사용하는지 검사
        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                });

        // 사용자 정보 수정
        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        // 새로운 프로필 이미지가 전달된 경우 기존 이미지 교체
        if (request.getProfileData() != null) {

            binaryContentRepository.findByUserId(user.getId())
                    .ifPresent(profile ->
                            binaryContentRepository.deleteById(profile.getId()));

            BinaryContent newProfile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(newProfile);
        }

        return toDto(user);
    }

    @Override
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

        // 프로필 이미지 삭제
        binaryContentRepository.findByUserId(userId)
                .ifPresent(profile ->
                        binaryContentRepository.deleteById(profile.getId()));

        // 사용자 상태 삭제
        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        // 사용자 삭제
        userRepository.deleteById(userId);
    }

    // User 엔티티를 UserDto로 변환
    private UserDto toDto(User user) {

        // 사용자 상태를 조회하여 온라인 여부 확인
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
