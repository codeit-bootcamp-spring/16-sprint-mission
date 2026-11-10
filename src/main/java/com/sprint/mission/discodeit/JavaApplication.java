package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.AuthService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import com.sprint.mission.discodeit.service.basic.UserStatusService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 스프링 없이 직접 조립해서 테스트한다. (저장소: JCF 구현체 - 디스크를 쓰지 않는다)
 */
public class JavaApplication {
    public static void main(String[] args) {
        // 레포지토리 초기화
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserMapper userMapper = new UserMapper();
        ChannelMapper channelMapper = new ChannelMapper(messageRepository, readStatusRepository);
        MessageMapper messageMapper = new MessageMapper();
        BinaryContentMapper binaryContentMapper = new BinaryContentMapper();

    }

}