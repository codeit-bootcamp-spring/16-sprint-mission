package com.sprint.mission.discodeit.practice;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private int point;

    // TODO: 포인트 충전 메서드를 구현하세요.
    // 힌트: '검증' 로직이 Service가 아닌 이곳에 들어와야 합니다.
    public void chargePoint(int amount) {

    }
}