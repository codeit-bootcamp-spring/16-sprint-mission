package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service_jcf_consoleRun.JCFUserConsoleRun;
import com.sprint.mission.discodeit.service_jcf_consoleRun.JCFChannelConsoleRun;
import com.sprint.mission.discodeit.service_jcf_consoleRun.JCFMessageConsoleRun;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/* JCF*Service */
// - "저장 방식": ArrayList 에 보관, "비즈니스 방식": (중복 확인, ID로 조회, 입력 수정, 삭제), "저장 로직": 메모리 목록만 변경


public class JavaApplicationVer1 {
    public static void main(String[] args) {

        JCFUserConsoleRun.printUserInformation(); // 유저 서비스 CRUD 테스트 실행
        JCFChannelConsoleRun.printChannelInformation(); // 채널 서비스 CRUD 테스트 실행
        JCFMessageConsoleRun.printMessageInformation(); // 메시지 서비스 CRUD 테스트 실행
    }
}