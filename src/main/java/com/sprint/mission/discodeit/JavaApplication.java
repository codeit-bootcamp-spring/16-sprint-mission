package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

public class JavaApplication {
    public static void main(String[] args) {

        //User 테스트
        UserService userService = new JCFUserService();

        User user = userService.create("김신중","1234");//C
        User user2 = userService.create("김길똥","4852");

        User findUser = userService.findById(user.getId()); //R
        System.out.println(findUser.getName());
        for(User allUser:userService.findAll()){
            System.out.println(allUser.getName());
        }
        System.out.println(userService.findAll().size());

        userService.update(user.getId(),"홍길동","1234");//U
        User updateUser = userService.findById(user.getId());
        System.out.println(updateUser.getName());

        userService.delete(user.getId()); //D
        System.out.println(userService.findById(user.getId()));
        System.out.println(userService.findAll().size());

        //Channel 테스트
        ChannelService channelService = new JCFChannelService(userService);

        Channel channel = channelService.create("대화채널");//C
        Channel channel2 = channelService.create("수다채널");

        Channel findChannel = channelService.findById(channel.getId());//R
        System.out.println(findChannel.getName());
        System.out.println(channelService.findAll().size());

        channelService.update(channel.getId(),"잡담채널");//U
        Channel updateChannel = channelService.findById(channel.getId());
        System.out.println(updateChannel.getName());
        for (Channel allChannel : channelService.findAll()) {
            System.out.println(allChannel.getName());
        }

        channelService.delete(channel.getId());//D
        System.out.println(channelService.findById(channel.getId()));
        System.out.println(channelService.findAll().size());



        //질문이후 추가
        channelService.join(channel2.getId(), user2.getId());

        channelService.join(channel2.getId(), user2.getId());

        System.out.println("----조인-------");
        System.out.println(channel2.getUsers().size());
        System.out.println(user2.getChannels().size());

        System.out.println("==============");


        //Message 테스트
        MessageService messageService = new JCFMessageService(userService, channelService);

        Message message = messageService.create("안녕하세요", user2.getId(), channel2.getId());//C
        Message message2 = messageService.create("반갑습니다", user2.getId(), channel2.getId());




        Message findMessage = messageService.findById(message.getId());//R
        System.out.println(findMessage.getComment());
        for(Message allMessage:messageService.findAll()){
            System.out.println(allMessage.getComment());
        }
        System.out.println(messageService.findAll().size());

        messageService.update(message.getId(),"수정된 메시지입니다");//U
        Message updateMessage = messageService.findById(message.getId());
        System.out.println(updateMessage.getComment());

        messageService.delete(message.getId());//D
        System.out.println(messageService.findById(message.getId()));
        System.out.println(messageService.findAll().size());


        Message messageA = messageService.create("반갑습니다", user.getId(), channel.getId()); // 심화요구사항 테스트
        Message messageB = messageService.create("안녕하세요", user.getId(), channel2.getId());
        Message messageC = messageService.create("잘부탁드립니다", user2.getId(), channel.getId());

        System.out.println(channel2.getMessages().size());
        System.out.println(user2.getMessages().size());

        //관계 삭제 테스트
        System.out.println("==============================");
        User userT = userService.create("테스트 삭제","1557");
        Channel channelT = channelService.create("테스트 삭제");
        channelService.join(channelT.getId(), userT.getId());
        userService.delete(userT.getId());
        System.out.println(userService.findById(userT.getId()));
        System.out.println(channelT.getUsers().size());
        System.out.println(userT.getChannels().size());



    }
}
