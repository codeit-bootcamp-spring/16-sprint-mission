package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class JCFUserService implements UserService {

    final Map<UUID, User> data;
    final MessageService messageService;

    public JCFUserService(MessageService messageService){
        //HashMap,Hashtable,ConcurrentHashMap 차이확인
        this.data=new ConcurrentHashMap<>();
        this.messageService = messageService;
    }

    @Override
    public User save(User user) {
        if (user == null || user.getId() == null) {
            throw new NullPointerException("데이터가 이상함");
        }
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findId(UUID id) {
        User user = data.get(id);

        if (user == null) {
            //데이터 없을 때
            return Collections.emptyList();
        }
        // 데이터가 있을 때
        List<User> result = new ArrayList<>();
        result.add(user);
        return result;
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageService.findUserlId(userId); // messageService 기능 활용
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id,String name) {
        User user=data.get(id);
        if(user==null){
            throw  new NullPointerException("해당유저 없음");
        }
        user.setName(name);
        return user;
    }

    @Override
    public User findEntityId(UUID id) {
        return data.get(id);
    }

    @Override
    public void deleteId(UUID id) {
        messageService.deleteByUserId(id); // 연쇄 삭제
        data.remove(id);
    }
}
