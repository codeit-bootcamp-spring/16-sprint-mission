package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private UUID profileId; // 프로필 이미지는 변경 될 수 있기 때문에 프로필 이미지의 id(profileId)는 불변이여서는 안됩니다.
    private final Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password, UUID profileId) {
        /* 엔티티의 검증 로직은 일단 사용하지 않습니다.
        // 일반적으로 username, email, password, profileId는 이미 검증된 값이
        // 생성자의 인자로 전달되기 때문에, 여기서는 최소한의 검증만 수행합니다.
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
         */
        this.id = UUID.randomUUID();
        this.profileId = profileId;
        // 아래 방식은, Instant.now()를 순서대로 호출하기 때문에,
        // 이 두 값이 미세하게 달라질 수 있습니다.
        // this.createdAt = Instant.now();
        // this.updatedAt = Instant.now();

        // 아래 방식은 updatedAt이 createdAt과 같은 참조값을 가지게 하기때문에,
        // 동일한 값을 가지게 할 수 있습니다.
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.username = username;
        this.email = email;
        this.password = password;
    }

    /* Getter를 직접 작성하는 대신에, Lombok 라이브러리의 @Getter를 클래스에 붙여 Getter를 생성합니다.
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    */

    /* 프로필 이미지 미적용 전
    public void update(String newUsername, String newEmail, String newPassword) {
        // 조건 플래그(Condition Flag) 또는 상태 플래그(Boolean Flag) 하고 합니다.
        // 특정 조건의 충족 여부나 상태를 기록하기위해 사용합니다.
        // 이를 바탕으로 후속 작업의 실행 여부를 결정합니다.
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
     */

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        // 조건 플래그(Condition Flag) 또는 상태 플래그(Boolean Flag) 하고 합니다.
        // 특정 조건의 충족 여부나 상태를 기록하기위해 사용합니다.
        // 이를 바탕으로 후속 작업의 실행 여부를 결정합니다.
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
