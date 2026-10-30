package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		String testId = UUID.randomUUID().toString();

		//User 테스트
		System.out.println("===== User 테스트 =====");

		User user = userService.create(new UserCreateRequest("김신중","1234",testId+"@example.com"), null);
		User user2 = userService.create(new UserCreateRequest("김길똥","4852",testId+"-2@example.com"), null);

		User findUser = userService.findById(user.getId());
		System.out.println(findUser.getName());

		for(User allUser : userService.findAll()){
			System.out.println(allUser.getName());
		}
		System.out.println(userService.findAll().size());

		userService.update(user.getId(),"김무열","4885",testId+"-updated@example.com");

		User updateUser = userService.findById(user.getId());
		System.out.println(updateUser.getName());

		userService.delete(user.getId());

		try{
			userService.update(user.getId(),"김무열","4885",testId+"@example.com");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			userService.delete(user.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			userService.findById(user.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
		System.out.println(userService.findAll().size());

		//Channel 테스트
		System.out.println("===== Channel 테스트 =====");

		Channel channel = channelService.create("대화채널-"+testId);
		Channel channel2 = channelService.create("수다채널-"+testId);

		Channel findChannel = channelService.findById(channel.getId());
		System.out.println(findChannel.getName());
		System.out.println(channelService.findAll().size());

		channelService.update(channel.getId(),"잡담채널-"+testId);

		Channel updateChannel = channelService.findById(channel.getId());
		System.out.println(updateChannel.getName());

		for(Channel allChannel : channelService.findAll()){
			System.out.println(allChannel.getName());
		}

		channelService.delete(channel.getId());

		try{
			channelService.update(channel.getId(),"뒷담채널-"+testId);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			channelService.delete(channel.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			channelService.findById(channel.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
		System.out.println(channelService.findAll().size());

		//참여 테스트
		System.out.println("===== 참여 테스트 =====");

		channelService.join(channel2.getId(),user2.getId());

		try{
			channelService.join(channel2.getId(),user2.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println(channelService.findById(channel2.getId()).getUsers().size());
		System.out.println(userService.findById(user2.getId()).getChannels().size());

		//Message 테스트
		System.out.println("===== Message 테스트 =====");

		Message message = messageService.create("안녕하세요",user2.getId(),channel2.getId());
		Message message2 = messageService.create("반갑습니다",user2.getId(),channel2.getId());

		Message findMessage = messageService.findById(message.getId());
		System.out.println(findMessage.getComment());

		for(Message allMessage : messageService.findAll()){
			System.out.println(allMessage.getComment());
		}
		System.out.println(messageService.findAll().size());

		messageService.update(message.getId(),"수정된 메시지입니다");

		Message updateMessage = messageService.findById(message.getId());
		System.out.println(updateMessage.getComment());

		messageService.delete(message.getId());

		try{
			messageService.update(message.getId(),"모르는 메시지입니다");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			messageService.delete(message.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			messageService.findById(message.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
		System.out.println(messageService.findAll().size());

		//존재하지 않는 사용자·채널로 메시지 생성
		System.out.println("===== 메시지 생성 실패 테스트 =====");

		try{
			messageService.create("반갑습니다",user.getId(),channel.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			messageService.create("안녕하세요",user.getId(),channel2.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		try{
			messageService.create("잘부탁드립니다",user2.getId(),channel.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println(channelService.findById(channel2.getId()).getMessages().size());
		System.out.println(userService.findById(user2.getId()).getMessages().size());

		//사용자 삭제 후 채널의 참여자 목록 확인
		System.out.println("===== 관계 삭제 테스트 =====");

		User userT = userService.create(new UserCreateRequest("테스트 삭제","1557",testId+"-delete@example.com"),null);
		Channel channelT = channelService.create("테스트 삭제-"+testId);

		channelService.join(channelT.getId(),userT.getId());
		userService.delete(userT.getId());

		try{
			userService.findById(userT.getId());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println(channelService.findById(channelT.getId()).getUsers().size());

		//이메일 중복 테스트
		System.out.println("===== 이메일 중복 테스트 =====");

		try{
			userService.create(new UserCreateRequest("김예외","4885",user2.getEmail()),null);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		User userE = userService.create(new UserCreateRequest("김예외","4885",testId+"-exception@example.com"),null);

		try{
			userService.update(userE.getId(),"김창호","4885",user2.getEmail());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		User savedUserE = userService.findById(userE.getId());
		System.out.println(savedUserE.getName()+" / "+savedUserE.getEmail());

		//자신의 이메일을 그대로 사용하는 수정은 성공
		userService.update(userE.getId(),savedUserE.getName(),savedUserE.getPassword(),savedUserE.getEmail());
		System.out.println(userService.findById(userE.getId()).getEmail());

		//채널명 중복 테스트
		System.out.println("===== 채널명 중복 테스트 =====");

		try{
			channelService.create(channel2.getName());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		Channel channelE = channelService.create("노가리 채널-"+testId);

		try{
			channelService.update(channelE.getId(),channel2.getName());
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println(channelService.findById(channelE.getId()).getName());

		//자신의 채널명을 그대로 사용하는 수정은 성공
		channelService.update(channelE.getId(),channelE.getName());
		System.out.println(channelService.findById(channelE.getId()).getName());

		//채널 삭제 후 사용자의 참여채널 목록 확인
		//메시지를 먼저 삭제해 테스트 데이터 정리
		messageService.delete(message2.getId());
		channelService.delete(channel2.getId());

		System.out.println("삭제 후 참여채널 수: "+userService.findById(user2.getId()).getChannels().size());

		//이번 실행에서 생성한 나머지 테스트 데이터 정리
		channelService.delete(channelT.getId());
		channelService.delete(channelE.getId());
		userService.delete(user2.getId());
		userService.delete(userE.getId());

		System.out.println("===== Spring CRUD 테스트 완료 =====");
	}
}