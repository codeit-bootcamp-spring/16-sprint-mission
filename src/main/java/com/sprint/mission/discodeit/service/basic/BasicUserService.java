package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final CommonValidator validator;

    @Override
    public UserDto createUser(UserCreateDto dto, MultipartFile profile) {
        // 입력값 검증
        validator.validateNotNull(
                dto,
                "사용자 생성 정보는 null일 수 없습니다."
        );
        validator.validateNotBlank(
                dto.username(),
                "사용자 이름은 비어있을 수 없습니다."
        );
        validator.validateNotBlank(
                dto.email(),
                "이메일은 비어있을 수 없습니다."
        );
        validator.validateNotBlank(
                dto.password(),
                "비밀번호는 비어있을 수 없습니다."
        );
        validator.validatePositiveOrZero(
                dto.age(),
                "나이는 0 이상이어야 합니다."
        );

        // 중복검사
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new IllegalArgumentException(
                    "이미 존재하는 사용자 이름입니다."
            );
        }
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException(
                    "이미 존재하는 이메일입니다."
            );
        }

        // profileID 저장
        UUID profileId = null; // 기본 널 설정

        if (profile != null && !profile.isEmpty()) {
            byte[] bytes;
            try {
                bytes = profile.getBytes();
            } catch (IOException e) {
                throw new UncheckedIOException("프로필 파일을 읽을 수 없습니다.", e);
            }

            BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    bytes
            );

            profileId = binaryContentRepository.save(binaryContent).getId();
        }

        // User 생성
        User user = new User(
                profileId,
                dto.username(),
                dto.age(),
                dto.email(),
                dto.password()
        );
        User savedUser = userRepository.save(user);
        // UserStatus 같이 생성
        UserStatus userStatus = new UserStatus(
                savedUser.getId(),
                Instant.now()
        );
        UserStatus savedStatus = userStatusRepository.save(userStatus);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getAge(),
                savedUser.getEmail(),
                savedUser.getProfileId(),
                savedStatus.isOnline()
        );
    }

    @Override
    public UserDto findById(UUID id) {
        // 입력값 검증
        validator.validateNotNull(
                id,
                "사용자 ID는 null일 수 없습니다."
        );

        // User 조회
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );

        return toDto(user);
    }

    @Override
    public UserDto findByUsername(String username) {
        validator.validateNotBlank(
                username,
                "사용자 이름은 비어있을 수 없습니다."
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );

        return toDto(user);
    }

    @Override
    public UserDto findByEmail(String email) {
        validator.validateNotBlank(
                email,
                "이메일은 비어있을 수 없습니다."
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );

        return toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll() // 유저 전부 찾아와서
                .stream() // 스트림파이프 열고
                .map(this::toDto) // 하나씩 DTO로 바꾼다음
                .toList(); // 다시 리스트로 묶기
    }

    @Override
    public UserDto updateUser(UserUpdateDto dto, MultipartFile profile) {
        validator.validateNotNull(
                dto,
                "수정 정보는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.id(),
                "사용자 ID는 null일 수 없습니다."
        );

        User user = userRepository.findById(dto.id())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );

        if (dto.username() != null){ // 아룸 중복 방지 로직
            validator.validateNotBlank(
                    dto.username(),
                    "사용자 이름은 비어있을 수 없습니다."
            );
            if (dto.username().equals(user.getUsername())) {
                throw new IllegalArgumentException(
                        "현재 사용자 이름과 동일합니다."
                );
            }
            if (userRepository.findByUsername(dto.username()).isPresent()) {
                throw new IllegalArgumentException(
                        "이미 존재하는 사용자 이름입니다."
                );
            }
        }

        if (dto.age() != null){
            validator.validatePositiveOrZero(
                    dto.age(),
                    "나이는 0 이상이어야 합니다."
            );
        }

        if (dto.password() != null){
            validator.validateNotBlank(
                    dto.password(),
                    "비밀번호는 비어있을 수 없습니다."
            );
        }

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (profile != null && !profile.isEmpty()) {
            byte[] bytes;
            try{
                bytes = profile.getBytes();
            } catch (IOException e) {
                throw new UncheckedIOException("프로필 파일을 읽을 수 없습니다.", e);
            }

            BinaryContent newProfile = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    bytes
            );

            newProfileId = binaryContentRepository.save(newProfile).getId();
        }

        user.update(dto.username(), dto.age(), dto.password());

        if (newProfileId != null) {
            user.updateProfileId(newProfileId);
        }

        User savedUser = userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.findById(oldProfileId)
                    .ifPresent(binaryContentRepository::delete);
        }

        return toDto(savedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        validator.validateNotNull(
                id,
                "사용자 ID는 null일 수 없습니다."
        );

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );

        // 유저가 작성한 메세지 삭제
        List<Message> messages = messageRepository.findAllByUserId(id);

        for (Message message : messages) {
            // 메세지 속 첨부파일 삭제
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .ifPresent(binaryContentRepository::delete);
            }

            messageRepository.delete(message);
        }

        readStatusRepository.findAllByUserId(id) // ReadStatus 삭제
                .forEach(readStatusRepository::delete);

        userStatusRepository.findByUserId(id) // UserStatus 삭제
                .ifPresent(userStatusRepository::delete);

        if (user.getProfileId() != null) { // binary 삭제
            binaryContentRepository
                    .findById(user.getProfileId())
                    .ifPresent(binaryContentRepository::delete);
        }


        userRepository.delete(user); // user 삭제
    }

    // User -> UserDto로 변환 하는 메서드
    private UserDto toDto(User user) {

        UserStatus userStatus =
                userStatusRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "사용자 상태 정보가 존재하지 않습니다."
                                )
                        );

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getAge(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }
}
