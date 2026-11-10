package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private UUID profileImageId;

    public User(String username, String email, String password) {
        super();

        validateRequired(username, "사용자 이름");
        validateRequired(email, "이메일");
        validateRequired(password, "비밀번호");

        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email) {
        this(name, email, "temporary-password");
    }

    public String getName() {
        return username;
    }

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

    public void update(String name, String email) {
        update(name, email, this.password, this.profileImageId);
    }

    public void updateProfileImage(UUID profileImageId) {
        this.profileImageId = profileImageId;
        updateUpdatedAt();
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 필수입니다."
            );
        }
    }
}