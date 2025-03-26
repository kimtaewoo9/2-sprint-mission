package com.sprint.mission.discodeit.service.file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    private FileChannelService channelService;

    @Test
    @DisplayName("public 채널 생성하기")
    void createPublicChannel() {

        // given
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("newPublicChannel",
            ChannelType.PUBLIC);

        // when
        UUID channelId = channelService.create(request);

        // then
        verify(channelRepository).save(any(Channel.class));
        Assertions.assertNotNull(channelId);
    }

    @Test
    @DisplayName("private 채널 생성하기")
    void createPrivateChannel() {

        // given
        CreatePublicChannelRequest request = new CreatePublicChannelRequest("privateChannel",
            ChannelType.PRIVATE);
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // when
        UUID channelId = channelService.create(request, userIds);

        // then
        verify(channelRepository).save(any(Channel.class));
        verify(readStatusRepository, times(userIds.size())).save(any(ReadStatus.class));

        org.assertj.core.api.Assertions.assertThat(channelId).isNotNull();
    }

    @Test
    @DisplayName("채널 조회 테스트")
    void findChannelTest() throws Exception {

        // given
        Channel channel = new Channel("myChannel", ChannelType.PUBLIC);
        UUID channelId = channel.getId();
        when(channelRepository.findByChannelId(channelId)).thenReturn(channel);

        // when
        ChannelResponseDto responseDto = channelService.findByChannelId(channelId);

        // then
        Assertions.assertEquals(channel.getName(), responseDto.getName());
        Assertions.assertEquals(channel.getType(), responseDto.getChannelType());
        Assertions.assertEquals(channel.getId(), responseDto.getChannelId());
    }

    @Test
    @DisplayName("유저가 볼 수 있는 채널 보여주기(모든 public channel + 자신이 속한 private channel")
    void findChannelByUserIdTest() {

        // given
        UUID userId = UUID.randomUUID();
        Channel channel1 = new Channel("Channel1", ChannelType.PUBLIC);
        Channel channel2 = new Channel("Channel2", ChannelType.PUBLIC);
        Channel channel3 = new Channel("Channel3", ChannelType.PUBLIC);
        Channel privateChannel1 = new Channel("PrivateChannel", ChannelType.PRIVATE);

        List<Channel> channels = new ArrayList<>();
        channels.add(channel1);
        channels.add(channel2);
        channels.add(channel3);
        channels.add(privateChannel1);

        when(channelRepository.findAll()).thenReturn(channels);

        // when
        List<ChannelResponseDto> channelDtos = channelService.findAllByUserId(userId);

        // then
        verify(channelRepository).findAll();
        org.assertj.core.api.Assertions.assertThat(channelDtos).hasSize(3);
    }

    @Test
    @DisplayName("유저 아이디를 이용해서 channel 검색. private 채널은 속한 채널만 보여줌")
    void findChannelByUserId() {

        // given
        User user = new User("김태우", "bezzi1654@naver.com", "password1");
        UUID userId = user.getId();
        Channel channel1 = new Channel("Channel1", ChannelType.PUBLIC);
        Channel channel2 = new Channel("Channel2", ChannelType.PUBLIC);
        Channel channel3 = new Channel("Channel3", ChannelType.PUBLIC);
        Channel privateChannel1 = new Channel("PrivateChannel", ChannelType.PRIVATE);

        List<Channel> channels = new ArrayList<>();
        channels.add(channel1);
        channels.add(channel2);
        channels.add(channel3);
        channels.add(privateChannel1);

        when(channelRepository.findAll()).thenReturn(channels);
        privateChannel1.addUser(userId);
        // when
        List<ChannelResponseDto> channelResponseDtos = channelService.findAllByUserId(userId);

        // then
        org.assertj.core.api.Assertions.assertThat(privateChannel1.getUserIds()).hasSize(1);
        org.assertj.core.api.Assertions.assertThat(channelResponseDtos).hasSize(4);
        verify(channelRepository).findAll();
    }

    @Test
    @DisplayName("channelService의 addUser테스트")
    void addUserTest() {

    }
}
