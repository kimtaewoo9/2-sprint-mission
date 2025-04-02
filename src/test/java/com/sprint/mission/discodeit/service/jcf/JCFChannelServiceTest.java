package com.sprint.mission.discodeit.service.jcf;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JCFChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private JCFChannelService channelService;

//    @Test
//    @DisplayName("공개 채널 생성 테스트")
//    void CreatePublicChannelTest() throws Exception {
//
//        // given
//        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
//            "newPublicChannel",
//            ChannelType.PUBLIC
//        );
//
//        // when
//        UUID channelId = channelService.create(request);
//
//        // then
//        verify(channelRepository).save(any(Channel.class));
//        Assertions.assertThat(channelId).isNotNull();
//    }

//    @Test
//    @DisplayName("비공개 채널 생성")
//    void CreatePrivateChannelTest() throws Exception {
//
//        // given
//        CreatePublicChannelRequest request = new CreatePublicChannelRequest("newPrivateChannel",
//            ChannelType.PRIVATE);
//
//        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
//
//        // when
//        UUID channelId = channelService.create(request, userIds);
//
//        // then
//        verify(channelRepository).save(any(Channel.class));
//        verify(readStatusRepository, times(userIds.size())).save(
//            any(ReadStatus.class)); // user별로 read status가 잘 생성되었는지 확인 해보기 .
//        Assertions.assertThat(channelId).isNotNull();
//    }

    @Test
    @DisplayName("채널 조회 테스트")
    void findPublicChannelByChannelId() {

        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("테스트 채널", ChannelType.PUBLIC);

        when(channelRepository.findByChannelId(channelId)).thenReturn(channel);

        // when
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        // then
        Assertions.assertThat(response.getName()).isEqualTo("테스트 채널");
        Assertions.assertThat(response.getChannelType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("채널에 유저를 추가하기")
    void addUser() throws Exception {

        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();
        Channel channel = new Channel("newChannel", ChannelType.PUBLIC);

        // when
        channel.addUser(userId1);
        channel.addUser(userId2);
        channel.addUser(userId3);

        // then
        Assertions.assertThat(channel.getUserIds()).hasSize(3);
    }

    @Test
    @DisplayName("유저 ID로 전체 채널 조회")
    void 유저ID로_전체채널조회() throws Exception {
        // findAll()로 전체 리스트 받아서, 모든 public channel + 자신이 속한 private channel 보여주기

        //given
        UUID userId = UUID.randomUUID();
        List<Channel> channels = Arrays.asList(
            new Channel("채널1", ChannelType.PUBLIC),
            new Channel("채널2", ChannelType.PUBLIC),
            new Channel("채널3", ChannelType.PUBLIC),
            new Channel("채널4", ChannelType.PRIVATE)
        );
        when(channelRepository.findAll()).thenReturn(channels);

        // when
        List<ChannelResponseDto> result = channelService.findAllByUserId(userId);

        // then
        verify(channelRepository).findAll(); // findAll이 실행되어야하고.
        Assertions.assertThat(result).hasSize(3); // result는 Public만 보여야함.
    }

    @Test
    @DisplayName("channel에 접속한 경우 private도 보여야함")
    void 유저ID로_전체채널조회2() throws Exception {
        // findAll()로 전체 리스트 받아서, 모든 public channel + 자신이 속한 private channel 보여주기

        //given
        UUID userId = UUID.randomUUID();
        Channel channel4 = new Channel("채널4", ChannelType.PRIVATE);
        List<Channel> channels = Arrays.asList(
            new Channel("채널1", ChannelType.PUBLIC),
            new Channel("채널2", ChannelType.PUBLIC),
            new Channel("채널3", ChannelType.PUBLIC),
            channel4 // private channel
        );

        channel4.addUser(userId); // 채널4에 user를 넣어줌 .
        when(channelRepository.findAll()).thenReturn(channels);

        // when
        List<ChannelResponseDto> result = channelService.findAllByUserId(userId);

        // then
        Assertions.assertThat(channel4.getUserIds()).hasSize(1); // 채널4에 user가 존재해야함 .
        verify(channelRepository).findAll(); // findAll이 실행되어야하고.
        Assertions.assertThat(result).hasSize(4); // 이 경우는 private channel도 보여야함
    }

//    @Test
//    @DisplayName("채널을 수정해보자")
//    void 채널수정_테스트() throws Exception {
//
//        // given
//        Channel channel = new Channel("수정전_채널이름", ChannelType.PUBLIC);
//        when(channelRepository.findByChannelId(channel.getId())).thenReturn(channel);
//        UpdateChannelRequest updateChannelRequest = new UpdateChannelRequest(
//            "수정후_채널이름", // 이름 수정
//            ChannelType.PRIVATE // 타입도 수정
//        );
//
//        // when
//        channelService.update(channel.getId(), updateChannelRequest);
//
//        // then (이름과 타입이 잘 수정 되었는가)
//        Assertions.assertThat(channel.getName()).isEqualTo("수정후_채널이름");
//        Assertions.assertThat(channel.getType()).isEqualTo(ChannelType.PRIVATE);
//    }

//    @Test
//    @DisplayName("존재하지 않는 채널 수정시 ERROR 발생")
//    void 에러나는경우() throws Exception {
//
//        // given
//        UUID channelId = UUID.randomUUID();
//        Channel channel = new Channel("수정전_채널이름", ChannelType.PUBLIC);
//        UpdateChannelRequest request = new UpdateChannelRequest(
//            "수정후_채널이름", // 이름 수정
//            ChannelType.PRIVATE // 타입도 수정
//        );
//        when(channelRepository.findByChannelId(channelId)).thenReturn(null);
//
//        // when + then
//        Assertions.assertThatThrownBy(() -> channelService.update(channelId, request))
//            .isInstanceOf(IllegalArgumentException.class)
//            .hasMessage("[ERROR] channel not found");
//    }

    @Test
    @DisplayName("채널 삭제 테스트")
    void 채널삭제() throws Exception {

        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("newChannel", ChannelType.PUBLIC);

        List<Message> messages = Collections.emptyList();
        List<ReadStatus> readStatuses = Collections.emptyList();

        when(channelRepository.findByChannelId(channelId)).thenReturn(channel);
        when(messageRepository.findAllByChannelId(channelId)).thenReturn(messages);
        when(readStatusRepository.findAllByChannelId(channelId)).thenReturn(readStatuses);

        // when
        channelService.remove(channelId);

        // then
        verify(channelRepository).delete(channelId); // channelRepository의 delete가 실행 됐는가 .
    }

    @Test
    @DisplayName("존재하지 않는 채널이면 에러 발생 .")
    void 채널삭제2() throws Exception {

        // given
        UUID channelId = UUID.randomUUID();
        when(channelRepository.findByChannelId(channelId)).thenReturn(null); // 없으니까 null을 반환하게함 .

        // when + then
        Assertions.assertThatThrownBy(() -> channelService.remove(channelId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] channel not found"); // 없는 채널을 삭제할 수는 없다.

    }


}
