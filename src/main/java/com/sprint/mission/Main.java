package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.repository.channel.MapChannelRepository;
import com.sprint.mission.discodeit.service.repository.message.MapMessageRepository;
import com.sprint.mission.discodeit.service.repository.user.MapUserRepository;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("메뉴를 선택하세요.");
            System.out.println("1. 사용자 관리");
            System.out.println("2. 채널 관리");
            System.out.println("3. 메시지 관리");
            System.out.println("9. 프로그램 종료");

            int inputNum = scanner.nextInt();
            if(inputNum == 1){
                userView();
            }else if(inputNum == 2){
                channelView();
            }else if (inputNum == 3){
                messageView();
            }else if(inputNum == 9){
                System.out.println("프로그램을 종료합니다.");
                break;
            }else{
                System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            }
        }
    }

    private static void channelView() {
        ChannelService channelService = new JCFChannelService(MapChannelRepository.getInstance());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("1: 채널 생성");
            System.out.println("2: 채널 정보 조회");
            System.out.println("3: 모든 채널 조회");
            System.out.println("4: 채널 이름 수정");
            System.out.println("5: 채널 정보 삭제");
            System.out.println("9: 유저 관리 종료");
            System.out.println("선택: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if(choice == 1){
                System.out.println("채널 이름: ");
                String channelName = sc.nextLine();

                Channel channel = new Channel(channelName);
                channelService.create(channel);
                System.out.println("새로운 채널이 생성 되었습니다.");
            }else if(choice == 2){
                System.out.println("조회하실 채널의 ID를 입력하세요.");
                UUID channelId = UUID.fromString(sc.nextLine());

                Channel channel = channelService.findByChannelId(channelId);
                System.out.println(channel);
            } else if(choice == 3){
                List<Channel> channels = channelService.findAll();
                if(channels.isEmpty()){
                    System.out.println("채널이 없습니다.");
                    continue;
                }
                System.out.println("[채널 리스트]");
                for (Channel channel : channels) {
                    System.out.printf("채널 이름 : %s, ", channel.getChannelName());
                    System.out.printf("채널 ID : %s", channel.getId().toString());
                    System.out.println();
                }
            }else if(choice == 4){
                System.out.println("채널 이름을 수정 합니다.");
                System.out.println("수정할 채널의 ID를 입력하세요 : ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                System.out.println("새로운 채널 이름을 입력하세요 : ");
                String newName = sc.nextLine();

                channelService.modify(uuid, newName);
                System.out.println("채널 이름이 수정되었습니다.");
            } else if(choice == 5){
                System.out.println("삭제할 채널 ID를 입력하세요 : ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                channelService.remove(uuid);
                System.out.println("삭제 되었습니다.");
            } else if(choice == 9){
                System.out.println("프로그램을 종료합니다.");
                run = false;
            }
            else{
                System.out.println("유효하지 않은 번호입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void messageView() {

        MessageService messageService = new JCFMessageService(MapMessageRepository.getInstance());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("1: 메시지 등록");
            System.out.println("2: 메시지 정보 조회");
            System.out.println("3: 모든 메시지 조회");
            System.out.println("4: 메시지 삭제");
            System.out.println("9: 종료");
            System.out.println("선택: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if(choice == 1){
                System.out.println("보낸 사용자 ID: ");
                String senderUUIDString = sc.nextLine();
                UUID senderId = UUID.fromString(senderUUIDString);

                System.out.println("채널 ID: ");
                String channelUUIDString = sc.nextLine();
                UUID channelId = UUID.fromString(channelUUIDString);

                System.out.println("메시지 내용: ");
                String content = sc.nextLine();
                sc.nextLine();

                Message message = new Message(content, senderId, channelId);
                messageService.save(message);
            }else if(choice == 2){
                System.out.println("조회하실 메시지의 ID를 입력하세요.");
                UUID messageId =UUID.fromString(sc.nextLine());

                Message message = messageService.readById(messageId);
                System.out.println(message);
            } else if(choice == 3){
                System.out.println("모든 메시지를 출력합니다.");
                List<Message> messages = messageService.readAll();

                for (Message message : messages) {
                    System.out.printf("메시지 내용 : %s%n", message.getContent());
                    System.out.printf("보낸 사용자 ID : %s%n", message.getChannelId().toString());
                    System.out.printf("채널 ID : %s%n", message.getChannelId().toString());
                }
            } else if(choice == 4){
                System.out.println("삭제할 메시지의 ID 입력:");
                UUID messageId = UUID.fromString(sc.nextLine());
                messageService.remove(messageId);
            } else if(choice == 9){
                System.out.println("프로그램을 종료합니다.");
                run = false;
            }
            else{
                System.out.println("유효하지 않은 번호입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void userView() {
        UserService userService = new JCFUserService(MapUserRepository.getInstance());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("1: 사용자 등록");
            System.out.println("2: 사용자 정보 조회");
            System.out.println("3: 모든 사용자 정보 조회");
            System.out.println("4: 사용자 이름 수정");
            System.out.println("5: 사용자 정보 삭제");
            System.out.println("9: 사용자 관리 종료");
            System.out.println("선택: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if(choice == 1){
                System.out.println("사용자 이름: ");
                String name = sc.nextLine();

                User newUser = new User(name);
                userService.register(newUser);
                System.out.println("새로운 유저가 등록 되었습니다.");
            }else if(choice == 2){
                System.out.println("조회하실 사용자의 ID를 입력하세요.");
                UUID userId = UUID.fromString(sc.nextLine());

                User user = userService.getUserById(userId);
                System.out.println(user);
            } else if(choice == 3){
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
            }else if(choice == 4){
                System.out.println("유저 이름을 수정합니다.");

                System.out.println("사용자 ID: ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                System.out.println("새로운 이름 : ");
                String newName = sc.nextLine();

                userService.update(uuid, newName);
                System.out.println("유저 이름이 수정되었습니다.");
            }else if(choice == 5){
                System.out.println("삭제할 사용자의 ID를 입력해주세요: ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);
                userService.remove(uuid);

                System.out.println("삭제 되었습니다.");
            } else if(choice == 9){
                System.out.println("프로그램을 종료합니다.");
                run = false;
            }
            else{
                System.out.println("유효하지 않은 번호입니다. 다시 선택해주세요.");
            }
        }
    }
}