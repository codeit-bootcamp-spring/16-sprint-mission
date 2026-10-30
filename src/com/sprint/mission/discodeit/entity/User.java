package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private UUID profileImageId;
    private List<UUID> channelIds = new ArrayList<>();

    // 새 구조: 사용자 이름, 이메일, 비밀번호
    public User(String username, String email, String password) {
        super();

        validateRequired(username, "사용자 이름");
        validateRequired(email, "이메일");
        validateRequired(password, "비밀번호");

        this.username = username;
        this.email = email;
        this.password = password;
    }

    // 기존 코드와의 호환을 위한 생성자
    public User(String name, String email) {
        this(name, email, "temporary-password");
    }

    // 기존 코드에서 사용하는 getName() 호환 메서드
    public String getName() {
        return username;
    }

    // 사용자 정보 수정
    public void update(
            String username,
            String email,
            String password,
            UUID profileImageId
    ) {
        validateRequired(username, "사용자 이름");
        validateRequired(email, "이메일");
        validateRequired(password, "비밀번호");

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImageId = profileImageId;

        updateUpdatedAt();
    }

    // 기존 서비스와의 호환을 위한 수정 메서드
    public void update(String name, String email) {
        update(name, email, this.password, this.profileImageId);
    }

    // 프로필 이미지 수정
    public void updateProfileImage(UUID profileImageId) {
        this.profileImageId = profileImageId;
        updateUpdatedAt();
    }

    // 채널 참여
    public void addChannel(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        if (!channelIds.contains(channelId)) {
            channelIds.add(channelId);
            updateUpdatedAt();
        }
    }

    // 채널 탈퇴
    public void removeChannel(UUID channelId) {
        if (channelIds.remove(channelId)) {
            updateUpdatedAt();
        }
    }

    // 채널 참여 여부 확인
    public boolean hasChannel(UUID channelId) {
        return channelIds.contains(channelId);
    }

    // 필수값 검증
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 필수입니다."
            );
        }
    }
}