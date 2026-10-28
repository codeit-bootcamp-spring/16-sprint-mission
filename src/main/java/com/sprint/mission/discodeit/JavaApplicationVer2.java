package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service_file_consoleRun.FileUserConsoleRun;
import com.sprint.mission.discodeit.service_file_consoleRun.FileChannelConsoleRun;
import com.sprint.mission.discodeit.service_file_consoleRun.FileMessageConsoleRun;

/* File*Service */
// - "저장 방식": .ser 파일에 직렬화 보관, "비즈니스 방식": 기본적으로 같은 로직, "저장 로직": 파일 읽기-쓰기 코드 추가

public class JavaApplicationVer2 {
    public static void main(String[] args) {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService(userService);
        MessageService messageService = new FileMessageService(userService, channelService);

        FileUserConsoleRun.fileUserInformation(userService);
        FileChannelConsoleRun.fileChannelInformation(channelService);
        FileMessageConsoleRun.fileMessageInformation(userService, channelService, messageService);
    }
}