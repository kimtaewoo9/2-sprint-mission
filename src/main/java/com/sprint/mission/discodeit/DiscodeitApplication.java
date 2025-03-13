package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.MANAGE_CHANNEL;
import com.sprint.mission.discodeit.enums.MANAGE_MESSAGE;
import com.sprint.mission.discodeit.enums.MANAGE_OPTIONS;
import com.sprint.mission.discodeit.enums.MANAGE_USER;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		Scanner scanner = new Scanner(System.in);

		while(true){
			for(MANAGE_OPTIONS menu : MANAGE_OPTIONS.values()){
				System.out.println(menu.getNumber() + ". " + menu.getText());
			}

			try {
				int inputNum = scanner.nextInt();
				MANAGE_OPTIONS selected = Optional.ofNullable(MANAGE_OPTIONS.findByNumber(inputNum))
						.orElseThrow(() -> new IllegalArgumentException("[ERROR]유효하지 않은 번호입니다. 다시 입력해주세요."));

				switch (selected) {
					case USER -> userView(context);
					case CHANNEL -> channelView(context);
					case MESSAGE -> messageView(context);
					case END -> {
						System.out.println("프로그램을 종료합니다.");
						return;
					}
				}
			} catch (InputMismatchException e) {
				System.out.println("[ERROR]숫자를 입력해주세요.");
				scanner.nextLine();
			}
		}
	}

	private static void channelView(ConfigurableApplicationContext context) {

		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);

		Scanner sc = new Scanner(System.in);
		boolean run = true;

		while (run) {
			for(MANAGE_CHANNEL menu : MANAGE_CHANNEL.values()){
				System.out.println(menu.getNumber() + ". " + menu.getText());
			}

			int choice = sc.nextInt();
			sc.nextLine();
			MANAGE_CHANNEL selected = MANAGE_CHANNEL.findByNumber(choice);
			if(selected == null){
				System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
				continue;
			}

			if(selected == MANAGE_CHANNEL.CREATE_CHANNEL){
				System.out.println("채널 이름: ");
				String channelName = sc.nextLine();
				System.out.println("공개 범위 설정");
				System.out.println("1. PUBLIC");
				System.out.println("2. PRIVATE");
				int channelTypeCode = sc.nextInt();

				ChannelType channelType = switch(channelTypeCode){
					case 1 -> ChannelType.PUBLIC;
					case 2 -> ChannelType.PRIVATE;
					default -> throw new IllegalArgumentException("[ERROR]유효하지 않은 숫자 입니다.");
				};

				channelService.create(channelName, channelType);

				System.out.println("새로운 채널이 생성 되었습니다.");
			}else if(selected == MANAGE_CHANNEL.FIND_CHANNEL){
				System.out.println("조회하실 채널의 ID를 입력하세요.");
				UUID channelId = UUID.fromString(sc.nextLine());

				Channel channel = channelService.findByChannelId(channelId);
				System.out.println(channel);
			} else if(selected == MANAGE_CHANNEL.FIND_ALL_CHANNEL){
				List<Channel> channels = channelService.findAll();
				if(channels.isEmpty()){
					System.out.println("채널이 없습니다.");
					continue;
				}
				System.out.println("[채널 리스트]");
				for (Channel channel : channels) {
					System.out.printf("채널 이름 : %s, ", channel.getName());
					System.out.printf("채널 ID : %s", channel.getId().toString());
					System.out.println();
				}
			}else if(selected == MANAGE_CHANNEL.UPDATE_CHANNEL){
				System.out.println("채널 정보를 수정 합니다.");
				System.out.println("수정할 채널의 ID를 입력하세요 : ");
				String uuidString = sc.nextLine();
				UUID uuid = UUID.fromString(uuidString);

				System.out.println("새로운 채널 이름을 입력하세요 : ");
				String newName = sc.nextLine();

				System.out.println("채널 타입을 고르세요.");
				System.out.println("1. PUBLIC");
				System.out.println("2. PRIVATE");
				int typeChoice = sc.nextInt();

				ChannelType channelType = null;
				if(typeChoice == 1){
					channelType = ChannelType.PUBLIC;
				}else{
					channelType = ChannelType.PRIVATE;
				}
				channelService.update(uuid, newName, channelType);
				System.out.println("채널 정보가 수정되었습니다.");
			} else if(selected == MANAGE_CHANNEL.DELETE_CHANNEL){
				System.out.println("삭제할 채널 ID를 입력하세요 : ");
				String uuidString = sc.nextLine();
				UUID uuid = UUID.fromString(uuidString);

				channelService.remove(uuid);
				System.out.println("삭제 되었습니다.");
			} else if(selected == MANAGE_CHANNEL.END){
				System.out.println("프로그램을 종료합니다.");
				run = false;
			}
		}
	}

	private static void messageView(ConfigurableApplicationContext context) {

		MessageService messageService = context.getBean("jCFMessageService", JCFMessageService.class);

		Scanner sc = new Scanner(System.in);
		boolean run = true;

		while (run) {
			for(MANAGE_MESSAGE menu : MANAGE_MESSAGE.values()){
				System.out.println(menu.getNumber() + ". " + menu.getText());
			}

			int choice = sc.nextInt();
			sc.nextLine();
			MANAGE_MESSAGE selected = MANAGE_MESSAGE.findByNumber(choice);
			if(selected == null){
				System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
				continue;
			}

			if(selected == MANAGE_MESSAGE.CREATE_MESSAGE){
				System.out.println("보낸 사용자 ID: ");
				String senderUUIDString = sc.nextLine();
				UUID senderId = UUID.fromString(senderUUIDString);

				System.out.println("채널 ID: ");
				String channelUUIDString = sc.nextLine();
				UUID channelId = UUID.fromString(channelUUIDString);

				System.out.println("메시지 내용: ");
				String content = sc.nextLine();
				sc.nextLine();

				messageService.create(content, senderId, channelId);
			}else if(selected == MANAGE_MESSAGE.FIND_MESSAGE){
				System.out.println("조회하실 메시지의 ID를 입력하세요.");
				UUID messageId = UUID.fromString(sc.nextLine());

				Message message = messageService.readById(messageId);
				System.out.println(message);
			} else if(selected == MANAGE_MESSAGE.FIND_ALL_MESSAGE){
				System.out.println("모든 메시지를 출력합니다.");
				List<Message> messages = messageService.readAll();

				for (Message message : messages) {
					System.out.printf("메시지 내용 : %s%n", message.getContent());
					System.out.printf("보낸 사용자 ID : %s%n", message.getChannelId().toString());
					System.out.printf("채널 ID : %s%n", message.getChannelId().toString());
				}
			}else if(selected == MANAGE_MESSAGE.UPDATE_MESSAGE){
				System.out.println("메시지 정보를 수정합니다.");

				System.out.println("메시지 ID : ");
				String uuidString = sc.nextLine();
				UUID messageId = UUID.fromString(uuidString);

				System.out.println("새로운 내용 : ");
				String newContent = sc.nextLine();

				messageService.update(messageId, newContent);
				System.out.println("메시지 정보가 수정되었습니다.");
			} else if(selected == MANAGE_MESSAGE.DELETE_MESSAGE){
				System.out.println("삭제할 메시지의 ID 입력:");
				UUID messageId = UUID.fromString(sc.nextLine());
				messageService.remove(messageId);
			} else if(selected == MANAGE_MESSAGE.END){
				System.out.println("[ERROR]메시지 관리 프로그램을 종료합니다.");
				run = false;
			}
		}
	}

	private static void userView(ConfigurableApplicationContext context) {

		UserService userService = context.getBean("jCFUserService", JCFUserService.class);

		Scanner sc = new Scanner(System.in);
		boolean run = true;

		while (run) {
			for(MANAGE_USER menu : MANAGE_USER.values()){
				System.out.println(menu.getNumber() +". " + menu.getText());
			}

			int choice = sc.nextInt();
			sc.nextLine();

			MANAGE_USER selected = MANAGE_USER.findByNumber(choice);
			if(selected == null){
				System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
				continue;
			}

			if(selected == MANAGE_USER.CREATE_USER){
				System.out.println("사용자 이름: ");
				String name = sc.nextLine();

				userService.create(name);
				System.out.println("새로운 유저가 등록 되었습니다.");
			}else if(selected == MANAGE_USER.FIND_USER){
				System.out.println("조회하실 사용자의 ID를 입력하세요.");
				UUID userId = UUID.fromString(sc.nextLine());

				User user = userService.findByUserId(userId);
				System.out.println(user);
			} else if(selected == MANAGE_USER.FIND_ALL_USER){
				List<User> users = userService.findAll();
				if(users.isEmpty()) {
					System.out.println("유저가 없습니다.");
					continue;
				}
				System.out.println("모든 사용자를 출력합니다.");

				for (User user : users) {
					System.out.printf("user name : %s, ", user.getName());
					System.out.printf("user ID : %s%n", user.getId().toString());
				}
			}else if(selected == MANAGE_USER.UPDATE_USER){
				System.out.println("유저 정보를 수정합니다.");

				System.out.println("사용자 ID: ");
				String uuidString = sc.nextLine();
				UUID uuid = UUID.fromString(uuidString);

				System.out.println("새로운 이름 : ");
				String newName = sc.nextLine();

				userService.update(uuid, newName);
				System.out.println("유저 정보가 수정되었습니다.");
			}else if(selected == MANAGE_USER.DELETE_USER){
				System.out.println("삭제할 사용자의 ID를 입력해주세요: ");
				String uuidString = sc.nextLine();
				UUID uuid = UUID.fromString(uuidString);
				userService.remove(uuid);

				System.out.println("삭제 되었습니다.");
			} else if(selected == MANAGE_USER.END){
				System.out.println("사용자 관리 프로그램을 종료합니다.");
				run = false;
			}
		}
	}

}
