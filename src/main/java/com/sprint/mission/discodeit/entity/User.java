package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;

public class User extends BaseEntity {

    private String username;
    // Spring 에서 컬렉션은 생성자를 통해 초기화하는것 보다, 필드 선언과 동시에 초기화하는 방식이 일반적입니다.
    private Set<Message> messages = new HashSet<>();
    private Set<Channel> channels = new HashSet<>();

    public User(String username) {
        super();
        this.username = username;
    }

    // 스프링에서 getXXX는 객체 자체를 반환하도록 설계하는것이 좋습니다. 단순 읽기(x), 객체 반환
    public String getUsername() { return this.username; }

    // 객체지향 설계에서는 필드를 무조건 Setter로 노출할 필요가 없습니다.
    // 오히려 updateUsername()처럼 변경 의도를 명확히 표현하는 메서드가 캡슐화에 더 적합합니다.
    public void updateUsername(String username) {
        this.update(); // updateAt을 업데이트합니다.
        this.username = username;
    }

    public Set<Message> getMessages() { return this.messages; }

    public Set<Channel> getChannels() { return this.channels; }

    public void addChannel(Channel channel) { this.channels.add(channel); }

    public void removeChannel(Channel channel) { this.channels.remove(channel); }
}
