package com.sprint.mission;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.enums.MANAGE_CHANNEL;
import com.sprint.mission.discodeit.enums.MANAGE_MESSAGE;
import com.sprint.mission.discodeit.enums.MANAGE_OPTIONS;
import com.sprint.mission.discodeit.enums.MANAGE_USER;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            for (MANAGE_OPTIONS menu : MANAGE_OPTIONS.values()) {
                System.out.println(menu.getNumber() + ". " + menu.getText());
            }

            try {
                int inputNum = scanner.nextInt();
                MANAGE_OPTIONS selected = Optional.ofNullable(MANAGE_OPTIONS.findByNumber(inputNum))
                    .orElseThrow(
                        () -> new IllegalArgumentException("[ERROR]유효하지 않은 번호입니다. 다시 입력해주세요."));

                switch (selected) {
                    case USER -> userView();
                    case CHANNEL -> channelView();
                    case MESSAGE -> messageView();
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

    private static void channelView() {
        ChannelService channelService = new FileChannelService(new FileChannelRepository(),
            new FileReadStatusRepository(), new JCFMessageRepository());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            for (MANAGE_CHANNEL menu : MANAGE_CHANNEL.values()) {
                System.out.println(menu.getNumber() + ". " + menu.getText());
            }

            int choice = sc.nextInt();
            sc.nextLine();
            MANAGE_CHANNEL selected = MANAGE_CHANNEL.findByNumber(choice);
            if (selected == null) {
                System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
                continue;
            }

            if (selected == MANAGE_CHANNEL.CREATE_CHANNEL) {
                System.out.println("채널 이름: ");
                String channelName = sc.nextLine();
                System.out.println("공개 범위 설정");
                System.out.println("1. PUBLIC");
                System.out.println("2. PRIVATE");
                int channelTypeCode = sc.nextInt();

                ChannelType channelType = switch (channelTypeCode) {
                    case 1 -> ChannelType.PUBLIC;
                    case 2 -> ChannelType.PRIVATE;
                    default -> throw new IllegalArgumentException("[ERROR]유효하지 않은 숫자 입니다.");
                };

                if (channelType == ChannelType.PUBLIC) {
                    channelService.createPublicChannel(
                        new CreatePublicChannelRequest(channelName,
                            "description"));
                } else {
                    channelService.createPrivateChannel(
                        new CreatePrivateChannelRequest(List.of())
                    );
                }

                System.out.println("새로운 채널이 생성 되었습니다.");
            } else if (selected == MANAGE_CHANNEL.FIND_CHANNEL) {
                System.out.println("조회하실 채널의 ID를 입력하세요.");
                UUID channelId = UUID.fromString(sc.nextLine());

                ChannelResponseDto channel = channelService.findByChannelId(channelId);
                System.out.println(channel);
            } else if (selected == MANAGE_CHANNEL.FIND_ALL_CHANNEL) {
                System.out.println("조회하실 사용자의 ID를 입력하세요.");
                UUID userId = UUID.fromString(sc.nextLine());

                List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);
                if (channels.isEmpty()) {
                    System.out.println("채널이 없습니다.");
                    continue;
                }
                System.out.println("[유저가 속한 채널 리스트]");
                for (ChannelResponseDto channel : channels) {
                    System.out.printf("채널 이름 : %s, ", channel.getName());
                    System.out.printf("채널 ID : %s", channel.getChannelId().toString());
                    System.out.println();
                }
            } else if (selected == MANAGE_CHANNEL.UPDATE_CHANNEL) {
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
                if (typeChoice == 1) {
                    channelType = ChannelType.PUBLIC;
                } else {
                    channelType = ChannelType.PRIVATE;
                }
                channelService.update(uuid, new UpdateChannelRequest(newName, "description"));
                System.out.println("채널 정보가 수정되었습니다.");
            } else if (selected == MANAGE_CHANNEL.DELETE_CHANNEL) {
                System.out.println("삭제할 채널 ID를 입력하세요 : ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);

                channelService.remove(uuid);
                System.out.println("삭제 되었습니다.");
            } else if (selected == MANAGE_CHANNEL.END) {
                System.out.println("프로그램을 종료합니다.");
                run = false;
            }
        }
    }

    private static void messageView() {
        UserService userService = new JCFUserService(new JCFUserRepository(),
            new FileBinaryContentRepository(), new FileUserStatusRepository());
        ChannelService channelService = new FileChannelService(new FileChannelRepository(),
            new FileReadStatusRepository(), new JCFMessageRepository());
        MessageService messageService = new FileMessageService(new FileMessageRepository(),
            new FileUserService(new FileUserRepository(), new FileBinaryContentRepository(),
                new FileUserStatusRepository()),
            new FileChannelService(new JCFChannelRepository(),
                new FileReadStatusRepository(), new FileMessageRepository()),
            new FileBinaryContentRepository());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            for (MANAGE_MESSAGE menu : MANAGE_MESSAGE.values()) {
                System.out.println(menu.getNumber() + ". " + menu.getText());
            }

            int choice = sc.nextInt();
            sc.nextLine();
            MANAGE_MESSAGE selected = MANAGE_MESSAGE.findByNumber(choice);
            if (selected == null) {
                System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
                continue;
            }

            if (selected == MANAGE_MESSAGE.CREATE_MESSAGE) {
                System.out.println("보낸 사용자 ID: ");
                String senderUUIDString = sc.nextLine();
                UUID senderId = UUID.fromString(senderUUIDString);

                System.out.println("채널 ID: ");
                String channelUUIDString = sc.nextLine();
                UUID channelId = UUID.fromString(channelUUIDString);

                System.out.println("메시지 내용: ");
                String content = sc.nextLine();
                sc.nextLine();

                messageService.create(new CreateMessageRequest(content, channelId, senderId));
            } else if (selected == MANAGE_MESSAGE.FIND_MESSAGE) {
                System.out.println("조회하실 메시지의 ID를 입력하세요.");
                UUID messageId = UUID.fromString(sc.nextLine());

                MessageResponseDto responseDto = messageService.findById(messageId);

                Message message = new Message(responseDto.getContent(), responseDto.getSenderId(),
                    responseDto.getChannelId());
                System.out.println(message);
            } else if (selected == MANAGE_MESSAGE.FIND_ALL_MESSAGE) {
                System.out.println("모든 메시지를 출력합니다.");
                List<MessageResponseDto> messages = messageService.findAll();

                for (MessageResponseDto message : messages) {
                    System.out.printf("메시지 내용 : %s%n", message.getContent());
                    System.out.printf("보낸 사용자 ID : %s%n", message.getChannelId().toString());
                    System.out.printf("채널 ID : %s%n", message.getChannelId().toString());
                }
            } else if (selected == MANAGE_MESSAGE.UPDATE_MESSAGE) {
                System.out.println("메시지 정보를 수정합니다.");

                System.out.println("메시지 ID : ");
                String uuidString = sc.nextLine();
                UUID messageId = UUID.fromString(uuidString);

                System.out.println("보낸 사용자 ID: ");
                String senderUUIDString = sc.nextLine();
                UUID senderId = UUID.fromString(senderUUIDString);

                System.out.println("채널 ID: ");
                String channelUUIDString = sc.nextLine();
                UUID channelId = UUID.fromString(channelUUIDString);

                System.out.println("메시지 내용: ");
                String content = sc.nextLine();
                sc.nextLine();

                messageService.update(messageId,
                    new UpdateMessageRequest(content, null));
                System.out.println("메시지 정보가 수정되었습니다.");
            } else if (selected == MANAGE_MESSAGE.DELETE_MESSAGE) {
                System.out.println("삭제할 메시지의 ID 입력:");
                UUID messageId = UUID.fromString(sc.nextLine());
                messageService.remove(messageId);
            } else if (selected == MANAGE_MESSAGE.END) {
                System.out.println("[ERROR]메시지 관리 프로그램을 종료합니다.");
                run = false;
            }
        }
    }

    private static void userView() {
        UserService userService = new JCFUserService(new JCFUserRepository(),
            new FileBinaryContentRepository(), new FileUserStatusRepository());

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            for (MANAGE_USER menu : MANAGE_USER.values()) {
                System.out.println(menu.getNumber() + ". " + menu.getText());
            }

            int choice = sc.nextInt();
            sc.nextLine();

            MANAGE_USER selected = MANAGE_USER.findByNumber(choice);
            if (selected == null) {
                System.out.println("[ERROR]유효하지 않은 번호입니다. 다시 입력 해주세요.");
                continue;
            }

            if (selected == MANAGE_USER.CREATE_USER) {
                System.out.println("사용자 이름: ");
                String name = sc.nextLine();
                System.out.println("사용자 이메일: ");
                String email = sc.nextLine();
                System.out.println("사용자 비밀번호: ");
                String password = sc.nextLine();

                CreateUserRequest request = new CreateUserRequest(name, email, password);
                userService.create(request);

                System.out.println("새로운 유저가 등록 되었습니다.");
            } else if (selected == MANAGE_USER.FIND_USER) {
                System.out.println("조회하실 사용자의 ID를 입력하세요.");
                UUID userId = UUID.fromString(sc.nextLine());

                UserResponseDto responseDto = userService.findByUserId(userId);

                System.out.println("사용자 ID: " + responseDto.getUsername());
                System.out.println("사용자 email: " + responseDto.getEmail());

            } else if (selected == MANAGE_USER.FIND_ALL_USER) {
                List<UserResponseDto> users = userService.findAll();
                if (users.isEmpty()) {
                    System.out.println("유저가 없습니다.");
                    continue;
                }
                System.out.println("모든 사용자를 출력합니다.");

                for (UserResponseDto user : users) {
                    System.out.printf("user name : %s, ", user.getUsername());
                    System.out.printf("user ID : %s%n", user.getId());
                }
            } else if (selected == MANAGE_USER.UPDATE_USER) {
                System.out.println("사용자의 ID를 입력해주세요.");
                UUID uuid = UUID.fromString(sc.nextLine());

                System.out.println("[유저 정보 수정]");
                System.out.println("사용자 이름: ");
                String name = sc.nextLine();
                System.out.println("사용자 이메일: ");
                String email = sc.nextLine();
                System.out.println("사용자 비밀번호: ");
                String password = sc.nextLine();

                UpdateUserRequest updateUserRequest = new UpdateUserRequest(name, email, password);
                userService.update(uuid, updateUserRequest);
                System.out.println("유저 정보가 수정되었습니다.");
            } else if (selected == MANAGE_USER.DELETE_USER) {
                System.out.println("삭제할 사용자의 ID를 입력해주세요: ");
                String uuidString = sc.nextLine();
                UUID uuid = UUID.fromString(uuidString);
                userService.remove(uuid);

                System.out.println("삭제 되었습니다.");
            } else if (selected == MANAGE_USER.END) {
                System.out.println("사용자 관리 프로그램을 종료합니다.");
                run = false;
            }
        }
    }
}
