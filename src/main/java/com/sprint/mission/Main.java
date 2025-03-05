package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.MANAGE_CHANNEL;
import com.sprint.mission.discodeit.enums.MANAGE_MESSAGE;
import com.sprint.mission.discodeit.enums.MANAGE_OPTIONS;
import com.sprint.mission.discodeit.enums.MANAGE_USER;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(true){
            for(MANAGE_OPTIONS menu : MANAGE_OPTIONS.values()){
                System.out.println(menu.getNumber() + ". " + menu.getText());
            }

            int inputNum = scanner.nextInt();
            MANAGE_OPTIONS selected = MANAGE_OPTIONS.findByNumber(inputNum);
            if(selected == null){
                System.out.println("유효하지 않은 번호입니다. 다시 입력해주세요.");
            }

            if(selected == MANAGE_OPTIONS.USER){
                userView();
            }else if(selected == MANAGE_OPTIONS.CHANNEL){
                channelView();
            }else if (selected == MANAGE_OPTIONS.MESSAGE){
                messageView();
            }else if(selected == MANAGE_OPTIONS.END) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }
        }
    }

    private static void channelView() {
        ChannelService channelService = new JCFChannelService(JCFChannelRepository.getInstance());

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
                System.out.println("유효하지 않은 번호입니다. 다시 입력 해주세요.");
                continue;
            }

            if(selected == MANAGE_CHANNEL.CREATE_CHANNEL){
                System.out.println("채널 이름: ");
                String channelName = sc.nextLine();

                channelService.create(channelName);
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
                System.out.println("채널 이름을 수정 합니다.");
                System.out.println("수정할 채널의 ID를 입력하세요 : ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                System.out.println("새로운 채널 이름을 입력하세요 : ");
                String newName = sc.nextLine();

                channelService.modify(uuid, newName);
                System.out.println("채널 이름이 수정되었습니다.");
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

    private static void messageView() {

        UserService userService = new JCFUserService(JCFUserRepository.getInstance());
        ChannelService channelService = new JCFChannelService(JCFChannelRepository.getInstance());
        MessageService messageService =
                new JCFMessageService(JCFMessageRepository.getInstance(), userService, channelService);

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
                System.out.println("유효하지 않은 번호입니다. 다시 입력 해주세요.");
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
                UUID messageId =UUID.fromString(sc.nextLine());

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


            } else if(selected == MANAGE_MESSAGE.DELETE_MESSAGE){
                System.out.println("삭제할 메시지의 ID 입력:");
                UUID messageId = UUID.fromString(sc.nextLine());
                messageService.remove(messageId);
            } else if(selected == MANAGE_MESSAGE.END){
                System.out.println("메시지 관리 프로그램을 종료합니다.");
                run = false;
            }
            else{
                System.out.println("유효하지 않은 번호입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void userView() {
        UserService userService = new JCFUserService(JCFUserRepository.getInstance());

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
                System.out.println("유효하지 않은 번호입니다. 다시 입력 해주세요.");
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
                System.out.println("유저 이름을 수정합니다.");

                System.out.println("사용자 ID: ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                System.out.println("새로운 이름 : ");
                String newName = sc.nextLine();

                userService.update(uuid, newName);
                System.out.println("유저 이름이 수정되었습니다.");
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
