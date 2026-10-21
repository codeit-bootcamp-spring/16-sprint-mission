package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.UUID;

public class JavaApplication2 {
    public static void main(String[] args) {

        UserService fileUserService = new FileUserService();
        ChannelService fileChannelService = new FileChannelService(fileUserService);
        MessageService fileMessageService = new FileMessageService(fileUserService, fileChannelService);

        /* 1. FileUserService Test */

        // User young123 = fileUserService.createUser("young123");
        // User young456 = fileUserService.createUser("young456");
        // User young789 = fileUserService.createUser("young789");
        // UUID young123UserId = UUID.fromString("1fcf802b-cb77-4047-b602-e478706f77b9");
        // UUID young789UserId = UUID.fromString("d6b4e6d8-fab5-4f3e-808f-3da2fe4983a8");
        // System.out.println("All users: " +  fileUserService.findAll()); // pass
        // System.out.println("young123's userId: " + fileUserService.findById(young123UserId).getId()); // pass
        // System.out.println("young123's username: " + fileUserService.findById(young123UserId).getUsername()); // pass
        // System.out.println("update young123 to young321: " + fileUserService.updateUsername(young123UserId, "young321").getUsername()); // pass
        // System.out.println("update young321 to young123: " + fileUserService.updateUsername(young123UserId, "young123").getUsername());
        // System.out.println("young789's userId: " + fileUserService.findById(young789UserId).getId());
        // fileUserService.deleteUser(young789UserId); // pass

        /* 2. FileMessageService Test */

        // User young123 =  fileUserService.findById(young123UserId);
        // Channel ch01 = fileChannelService.createChannel("ch01"); // pass
        // fileMessageService.createMessage("Hello, Java!", young123, ch01); // pass
        // Channel ch02 = fileChannelService.createChannel("ch02");
        // fileMessageService.createMessage("Hello, Kotlin!!", young123, ch02);
        // System.out.println("All messages: " +  fileMessageService.findAll()); // pass
        // UUID firstMessageId = UUID.fromString("47ab5494-1719-479a-9570-5365649201e8");
        // System.out.println("the messageId of the Message of Hello, Java!: " + fileMessageService.findById(firstMessageId).getId()); // pass
        // System.out.println("the content of the Message of Hello, Java!: " + fileMessageService.findById(firstMessageId).getContent()); // pass
        // System.out.println("update content of the Message of Hello, Java! to Hello, C#!: " + fileMessageService.updateContent(firstMessageId, "Hello, C#!").getContent()); // pass
        // System.out.println("update content of the Message of Hello, C#! to Hello, Java!: " + fileMessageService.updateContent(firstMessageId, "Hello, java!").getContent()); // pass
        // fileMessageService.deleteMessage(firstMessageId); // pass
        // UUID ch02Id = UUID.fromString("7b2c1b02-93c1-4e68-9388-66fda39d44a6");

        /* 3. FileChannelService Test */

        // System.out.println("All channels: " +  fileChannelService.findAll()); // pass
        // System.out.println("ch02's channel id: " + fileChannelService.findById(ch02Id).getId()); // pass
        // System.out.println("ch02's channelName: " + fileChannelService.findById(ch02Id).getChannelName()); // pass
        // System.out.println("update ch02's channelName to ch20: " + fileChannelService.updateChannelName(ch02Id, "ch20").getChannelName()); // pass
        // System.out.println("update ch20's channelName to ch02: " + fileChannelService.updateChannelName(ch02Id, "ch02").getChannelName()); // pass
        // fileChannelService.deleteChannel(ch02Id); // pass

        /* 4. FileMessageService's deleteMessageWithoutChannel() Test */
        // System.out.println("First message's channel: " +  fileMessageService.findAll().get(0).getChannel().getId());
        // System.out.println("One remaining channel's channelId: " + fileChannelService.findAll().get(0).getId());
        // fileChannelService.createChannel("ch03");
        // Channel ch10 = fileChannelService.createChannel("ch10");
        // UUID ch10id = UUID.fromString("3ec67e6b-a8b7-41f7-a218-7b3bd33429f2");
        // Channel ch10 = fileChannelService.findById(ch10id);
        //  anonymousUser = fileUserService.findAll().get(0);
        // Message messageInCh10 = fileMessageService.createMessage("This message is supposed to be deleted because there is no channel", anonymousUser, ch10);
        // UUID messageInCh10Id = UUID.fromString("a6a929ab-258d-47da-a831-7619ecc086c7");
        // fileChannelService.deleteChannel(ch10id);
        // fileMessageService.deleteMessageWithoutChannel(); // pass?

        /* 5. FileMessageService's deleteMessageWithoutChannel() Test 2 */
        // fileUserService.createUser("user1"); c977734d-7f3a-4eaa-bec3-c6cf66c69908
        // User user1 = fileUserService.findById(UUID.fromString("c977734d-7f3a-4eaa-bec3-c6cf66c69908"));
        // fileUserService.createUser("user2"); 29dd8dd9-6bea-406b-8186-c23c6713d691
        // User user2 = fileUserService.findById(UUID.fromString("29dd8dd9-6bea-406b-8186-c23c6713d691"));
        // fileChannelService.createChannel("ch01"); 9be21a6a-9645-4ecf-a27d-43ea0f844602
        // Channel ch01 = fileChannelService.findById(UUID.fromString("9be21a6a-9645-4ecf-a27d-43ea0f844602"));
        // fileChannelService.createChannel("ch02"); 8b94cf2a-a11d-4841-b93a-38e8d5fbef6e
        // Channel ch02 = fileChannelService.findById(UUID.fromString("8b94cf2a-a11d-4841-b93a-38e8d5fbef6e"));
        // fileMessageService.createMessage("Hello, Java!", user1, ch01); b3b94675-9317-4a8a-95dc-d5c27fb36f6d
        // Message msg01 = fileMessageService.findById(UUID.fromString("b3b94675-9317-4a8a-95dc-d5c27fb36f6d"));
        // fileMessageService.createMessage("Hello, Kotlin!", user2, ch02); // beb9eff6-30f6-4373-9740-61c7dd12806a
        // Message msg02 = fileMessageService.findById(UUID.fromString("beb9eff6-30f6-4373-9740-61c7dd12806a"));

        // fileChannelService.deleteChannel(ch02.getId());
        // fileMessageService.deleteMessageWithoutChannel(); // pass

        /* 6. joinChannel(), leaveChannel() Test */

        // fileUserService.createUser("user3"); // 5d2c6ef6-3513-428a-9b89-bd950fc8130f
        // User user3 = fileUserService.findById(UUID.fromString("5d2c6ef6-3513-428a-9b89-bd950fc8130f"));
        // fileChannelService.joinChannel(UUID.fromString("9be21a6a-9645-4ecf-a27d-43ea0f844602"), UUID.fromString("5d2c6ef6-3513-428a-9b89-bd950fc8130f" ));
        // System.out.println(fileChannelService.findAll().get(0).getUsers());

        /* 7. joinChannel(), leaveChannel() Test 2 */

        // fileUserService.createUser("user01"); // 5ec3c104-b10d-4e90-af27-c6b8b7dd0509
        // User user01 = fileUserService.findById(UUID.fromString("5ec3c104-b10d-4e90-af27-c6b8b7dd0509"));
        // fileUserService.createUser("user02"); // 98d76439-a142-42d9-9099-ae3224edd683
        // User user02 = fileUserService.findById(UUID.fromString("98d76439-a142-42d9-9099-ae3224edd683"));
        // fileChannelService.createChannel("channel01"); // f29708dd-ea8d-4f46-80f8-24fc0b27e7c0
        // Channel channel01 = fileChannelService.findById(UUID.fromString("f29708dd-ea8d-4f46-80f8-24fc0b27e7c0"));
        // fileChannelService.createChannel("channel02"); // d67ec0a8-b551-4ac6-a5b0-8aeb6612b19e
        // Channel channel02 = fileChannelService.findById(UUID.fromString("d67ec0a8-b551-4ac6-a5b0-8aeb6612b19e"));

        // System.out.println(channel01.getUsers()); // null

        // fileChannelService.joinChannel(UUID.fromString("f29708dd-ea8d-4f46-80f8-24fc0b27e7c0"), UUID.fromString("5ec3c104-b10d-4e90-af27-c6b8b7dd0509") );

        // System.out.println(channel01.getUsers()); //

        /* 8. joinChannel(), leaveChannel() Test 2 */

        // fileUserService.createUser("user01");
        // UUID user01UUID = UUID.fromString("d873ed51-919b-4ab8-b66a-a071d4aea0f9");
        // User user01 = fileUserService.findById(user01UUID);

        // fileChannelService.createChannel("channel01");
        // UUID channel01UUID = UUID.fromString("7b0724e6-f705-4528-8f50-ebb17317caa5");
        // Channel channel01 = fileChannelService.findById(channel01UUID);

        // System.out.println(channel01.getUsers()); // []

        // fileChannelService.joinChannel(channel01UUID, user01UUID);

        // System.out.println(channel01.getUsers());

        // fileChannelService.leaveChannel(channel01UUID, user01UUID);

        // System.out.println(channel01.getUsers());

        /* 9. joinChannel(), leaveChannel() Test 3 */

        // fileUserService.createUser("user01");
        // UUID user01Id = UUID.fromString("c0d1333b-3d26-4d14-8196-1993b4329a5d");
        // User user01 = fileUserService.findById(user01Id);

        // fileChannelService.createChannel("channel01");
        // UUID channel01Id = UUID.fromString("4a2bb82e-6f7e-485a-9ff4-8441a12ec020");
        // Channel channel01 = fileChannelService.findById(channel01Id);

        // System.out.println(channel01.getUsers()); // []

        // fileChannelService.joinChannel(channel01Id, user01Id);

        // System.out.println(channel01.getUsers()); // [com.sprint.mission.discodeit.entity.User@c853fde1]

        // fileChannelService.leaveChannel(channel01Id, user01Id);

        // System.out.println(channel01.getUsers()); // []

        /* 이하 모든 메서드 검증 완료 */
    }
}
