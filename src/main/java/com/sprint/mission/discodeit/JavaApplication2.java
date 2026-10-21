package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.UUID;

public class JavaApplication2 {
    public static void main(String[] args) {
        FileUserService fileUserService = new FileUserService();
        // fileUserService.createUser("young927");
        System.out.println("UserId of young927: " + fileUserService.deseriailize(UUID.fromString("15f87985-3fc4-4478-95ff-a10e04d47783")).getId());
        // fileUserService.deseriailize(UUID.fromString("aab158d2-25bf-481a-88d7-ed21423baf00"));
        System.out.println("UserId of young927 by findById(): " + fileUserService.findById(UUID.fromString("15f87985-3fc4-4478-95ff-a10e04d47783")).getId());
    }
}
