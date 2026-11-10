
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class FileUserServiceTest {

    public static void main(String[] args) {

        FileUserService userService = new FileUserService();

        User user = userService.add(new User("신지섭"));

        System.out.println("저장한 사용자: " + user.getUsername());

        FileUserService newUserService = new FileUserService();

        User savedUser = newUserService.getById(user.getId());

        System.out.println("다시 불러온 사용자: " + savedUser.getUsername());

        System.out.println("전체 사용자 수: " + newUserService.getAll().size());
    }
}
