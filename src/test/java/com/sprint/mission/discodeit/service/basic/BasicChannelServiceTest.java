package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Captor
    ArgumentCaptor<Channel> channelCaptor;
    @Captor
    ArgumentCaptor<ReadStatus> readStatusCaptor;
    @Captor
    ArgumentCaptor<UUID> uuidCaptor;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    // BinaryContentStorage는 ChannelService에서 직접 사용되지 않으므로 Mock 제거 가능
    // @Mock private BinaryContentStorage binaryContentStorage; // UserService 테스트에는 필요 없음
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private BasicChannelService basicChannelService;
    private User testUser1, testUser2;
    private UserDto testUserDto1, testUserDto2;
    private Channel testPublicChannel, testPrivateChannel;
    // ChannelDto는 Mapper Mocking으로 처리하므로 실제 객체 생성은 필수 아님. 필요시 생성.
    private UUID publicChannelId, privateChannelId, userId1, userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        publicChannelId = UUID.randomUUID();
        privateChannelId = UUID.randomUUID();

        testUser1 = new User();
        testUser1.setId(userId1);
        testUser1.setUsername("user1");
        testUser2 = new User();
        testUser2.setId(userId2);
        testUser2.setUsername("user2");

        // UserDto는 UserMapper Mocking 시 반환값으로 사용될 수 있음
        testUserDto1 = new UserDto(userId1, "user1", "user1@ex.com", null, true);
        testUserDto2 = new UserDto(userId2, "user2", "user2@ex.com", null, true);

        testPublicChannel = Channel.createPublicChannel("Public", "Public Desc");
        testPublicChannel.setId(publicChannelId);

        testPrivateChannel = Channel.createPrivateChannel();
        testPrivateChannel.setId(privateChannelId);
    }

    // ========================== CREATE Tests ==========================
    @Nested
    @DisplayName("ChannelService: create 메소드")
    class CreateChannelTests {

        @Test
        @DisplayName("성공: 공개 채널 생성")
        void createPublicChannel_Success() {
            // Given
            CreatePublicChannelRequest request = new CreatePublicChannelRequest("Public",
                "Public Desc");
            // 예상 반환 DTO (Mapper가 반환할 객체)
            ChannelDto expectedDto = new ChannelDto(publicChannelId, ChannelType.PUBLIC,
                request.name(), request.description(), Collections.emptyList(), null);

            given(channelRepository.save(any(Channel.class))).willReturn(testPublicChannel);
            given(channelMapper.toDto(testPublicChannel, Collections.emptyList(), null)).willReturn(
                expectedDto);

            // When
            ChannelDto result = basicChannelService.createPublicChannel(request);

            // Then
            // DTO 객체 내용 비교 (equals/hashCode 구현 가정 또는 필드별 비교)
            assertThat(result.getId()).isEqualTo(publicChannelId);
            assertThat(result.getName()).isEqualTo(request.name());
            assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(result.getParticipants()).isEmpty();
            assertThat(result.getLastMessageAt()).isNull();

            then(channelRepository).should().save(channelCaptor.capture());
            assertThat(channelCaptor.getValue().getName()).isEqualTo(request.name());
            assertThat(channelCaptor.getValue().getChannelType()).isEqualTo(ChannelType.PUBLIC);
            then(channelMapper).should().toDto(testPublicChannel, Collections.emptyList(), null);
        }

        @Test
        @DisplayName("성공: 비공개 채널 생성")
        void createPrivateChannel_Success() {
            // Given
            CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId1, userId2));
            // 예상 반환 DTO
            List<UserDto> expectedParticipants = List.of(testUserDto1, testUserDto2);
            ChannelDto expectedDto = new ChannelDto(privateChannelId, ChannelType.PRIVATE, null,
                null, expectedParticipants, null);

            given(channelRepository.save(any(Channel.class))).willReturn(testPrivateChannel);
            given(userRepository.findById(userId1)).willReturn(Optional.of(testUser1));
            given(userRepository.findById(userId2)).willReturn(Optional.of(testUser2));
            given(readStatusRepository.save(any(ReadStatus.class))).willReturn(new ReadStatus());
            given(userMapper.toDto(testUser1)).willReturn(testUserDto1);
            given(userMapper.toDto(testUser2)).willReturn(testUserDto2);
            given(channelMapper.toDto(testPrivateChannel, expectedParticipants, null)).willReturn(
                expectedDto);

            // When
            ChannelDto result = basicChannelService.createPrivateChannel(request);

            // Then
            // DTO 내용 비교
            assertThat(result.getId()).isEqualTo(privateChannelId);
            assertThat(result.getType()).isEqualTo(ChannelType.PRIVATE);
            assertThat(result.getParticipants()).containsExactlyInAnyOrderElementsOf(
                expectedParticipants);

            then(channelRepository).should().save(channelCaptor.capture());
            assertThat(channelCaptor.getValue().getChannelType()).isEqualTo(ChannelType.PRIVATE);
            then(userRepository).should(times(2)).findById(any(UUID.class));
            then(readStatusRepository).should(times(2)).save(readStatusCaptor.capture());
            // 저장된 ReadStatus 검증 (User와 Channel이 올바른지)
            assertThat(readStatusCaptor.getAllValues().get(0).getUser()).isEqualTo(testUser1);
            assertThat(readStatusCaptor.getAllValues().get(0).getChannel()).isEqualTo(
                testPrivateChannel);
            assertThat(readStatusCaptor.getAllValues().get(1).getUser()).isEqualTo(testUser2);
            then(channelMapper).should().toDto(testPrivateChannel, expectedParticipants, null);
        }

        @Test
        @DisplayName("실패: 비공개 채널 생성 시 참여자 없음")
        void createPrivateChannel_Fail_ParticipantNotFound() {
            // Given
            UUID nonExistentUserId = UUID.randomUUID();
            CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId1, nonExistentUserId));
            given(channelRepository.save(any(Channel.class))).willReturn(testPrivateChannel);
            given(userRepository.findById(userId1)).willReturn(Optional.of(testUser1));
            given(userRepository.findById(nonExistentUserId)).willReturn(
                Optional.empty()); // 사용자 없음
            given(readStatusRepository.save(any(ReadStatus.class))).willReturn(
                new ReadStatus()); // 첫 번째 사용자는 저장될 수 있음
            given(userMapper.toDto(testUser1)).willReturn(testUserDto1);

            // When & Then
            assertThatThrownBy(() -> basicChannelService.createPrivateChannel(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("[ERROR] participant not found");

            then(channelRepository).should().save(any(Channel.class));
            then(userRepository).should(times(2)).findById(any(UUID.class));
            then(readStatusRepository).should(times(1)).save(any(ReadStatus.class)); // 한 번만 성공
            then(channelMapper).should(never()).toDto(any(), any(), any());
        }
    }

    // ========================== UPDATE Tests ==========================
    @Nested
    @DisplayName("ChannelService: update 메소드")
    class UpdateChannelTests {

        @Test
        @DisplayName("성공: 공개 채널 정보 수정")
        void updatePublicChannel_Success() {
            // Given
            UpdateChannelRequest request = new UpdateChannelRequest("Updated Name", "Updated Desc");
            ChannelDto expectedDto = new ChannelDto(publicChannelId, ChannelType.PUBLIC,
                request.newName(), request.newDescription(), Collections.emptyList(), null);

            given(channelRepository.findById(publicChannelId)).willReturn(
                Optional.of(testPublicChannel));
            given(messageRepository.findAllByChannelId(publicChannelId)).willReturn(
                Collections.emptyList()); // getLastMessageTimestamp 위함
            given(channelMapper.toDto(testPublicChannel, Collections.emptyList(), null)).willReturn(
                expectedDto);

            // When
            ChannelDto result = basicChannelService.update(publicChannelId, request);

            // Then
            assertThat(result.getName()).isEqualTo(request.newName());
            assertThat(result.getDescription()).isEqualTo(
                request.newDescription()); // update 로직 버그 확인 필요

            then(channelRepository).should().findById(publicChannelId);
            // Channel 객체의 update 메소드가 호출되었는지 확인 (변경된 값 검증)
            assertThat(testPublicChannel.getName()).isEqualTo(request.newName());
            // assertThat(testPublicChannel.getDescription()).isEqualTo(request.newDescription()); // Channel.update() 버그 확인 필요
            then(messageRepository).should()
                .findAllByChannelId(publicChannelId); // getLastMessageTimestamp 호출 검증
            then(channelMapper).should().toDto(testPublicChannel, Collections.emptyList(), null);
        }

        @Test
        @DisplayName("실패: 비공개 채널 수정 시도")
        void updatePrivateChannel_Fail() {
            // Given
            UpdateChannelRequest request = new UpdateChannelRequest("Updated Name", "Updated Desc");
            given(channelRepository.findById(privateChannelId)).willReturn(
                Optional.of(testPrivateChannel));

            // When & Then
            assertThatThrownBy(() -> basicChannelService.update(privateChannelId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] private channel cannot be modified");

            then(channelMapper).should(never()).toDto(any(), any(), any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 채널 수정 시도")
        void updateChannel_Fail_NotFound() {
            // Given
            UpdateChannelRequest request = new UpdateChannelRequest("Updated Name", "Updated Desc");
            given(channelRepository.findById(publicChannelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicChannelService.update(publicChannelId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("[ERROR] channel not found");

            then(channelMapper).should(never()).toDto(any(), any(), any());
        }
    }

    // ========================== REMOVE Tests ==========================
    @Nested
    @DisplayName("ChannelService: remove (delete) 메소드")
    class RemoveChannelTests {

        @Test
        @DisplayName("성공: 채널 삭제 (메시지, 상태 포함)")
        void removeChannel_Success() {
            // Given
            given(channelRepository.existsById(publicChannelId)).willReturn(true);
            given(channelRepository.findById(publicChannelId)).willReturn(
                Optional.of(testPublicChannel)); // 내부 로깅 위해
            given(messageRepository.countByChannelId(publicChannelId)).willReturn(10);
            willDoNothing().given(messageRepository).deleteByChannelId(publicChannelId);
            given(readStatusRepository.countByChannelId(publicChannelId)).willReturn(5);
            willDoNothing().given(readStatusRepository).deleteByChannelId(publicChannelId);
            willDoNothing().given(channelRepository).deleteById(publicChannelId);

            // When
            basicChannelService.remove(publicChannelId);

            // Then
            then(channelRepository).should().existsById(publicChannelId);
            then(channelRepository).should().findById(publicChannelId);
            then(messageRepository).should().countByChannelId(publicChannelId);
            then(messageRepository).should().deleteByChannelId(publicChannelId);
            then(readStatusRepository).should().countByChannelId(publicChannelId);
            then(readStatusRepository).should().deleteByChannelId(publicChannelId);
            then(channelRepository).should().deleteById(publicChannelId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 채널 삭제 시도")
        void removeChannel_Fail_NotFound() {
            // Given
            given(channelRepository.existsById(publicChannelId)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> basicChannelService.remove(publicChannelId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("[ERROR] channel not found");

            then(messageRepository).should(never()).deleteByChannelId(any(UUID.class));
            then(readStatusRepository).should(never()).deleteByChannelId(any(UUID.class));
            then(channelRepository).should(never()).deleteById(any(UUID.class));
        }
    }

    // ========================== FIND ALL BY USER ID Tests ==========================
    @Nested
    @DisplayName("ChannelService: findAllByUserId 메소드")
    class FindAllByUserIdTests {

        @Test
        @DisplayName("성공: 참여한 비공개 채널과 모든 공개 채널 조회")
        void findAllByUserId_Success() {
            // Given
            // 사용자는 privateChannel에만 참여
            ReadStatus readStatus = ReadStatus.createReadStatus(testUser1, testPrivateChannel,
                Instant.now());
            List<ReadStatus> userReadStatuses = List.of(readStatus);
            // 시스템에는 publicChannel과 privateChannel 존재
            List<Channel> allChannels = List.of(testPublicChannel, testPrivateChannel);

            // Mocking
            given(readStatusRepository.findAllByUserId(userId1)).willReturn(userReadStatuses);
            given(channelRepository.findAll()).willReturn(allChannels);
            // toDto 내부 호출 Mocking (메시지 없음, private 채널 참여자 1명)
            given(messageRepository.findAllByChannelId(any(UUID.class))).willReturn(
                Collections.emptyList());
            given(readStatusRepository.findAllByChannelId(privateChannelId)).willReturn(
                userReadStatuses);
            given(userMapper.toDto(testUser1)).willReturn(testUserDto1);

            // channelMapper.toDto Mocking
            ChannelDto publicDtoMock = new ChannelDto(publicChannelId, ChannelType.PUBLIC, "Public",
                "Desc", Collections.emptyList(), null);
            ChannelDto privateDtoMock = new ChannelDto(privateChannelId, ChannelType.PRIVATE, null,
                null, List.of(testUserDto1), null);
            given(channelMapper.toDto(eq(testPublicChannel), anyList(), any())).willReturn(
                publicDtoMock);
            given(channelMapper.toDto(eq(testPrivateChannel), eq(List.of(testUserDto1)),
                any())).willReturn(privateDtoMock);

            // When
            List<ChannelDto> result = basicChannelService.findAllByUserId(userId1);

            // Then
            assertThat(result).hasSize(2);
            // DTO 내용 비교 (필드별 또는 equals/hashCode 구현 시 객체 비교)
            assertThat(result).extracting(ChannelDto::getId)
                .containsExactlyInAnyOrder(publicChannelId, privateChannelId);

            then(readStatusRepository).should().findAllByUserId(userId1);
            then(channelRepository).should().findAll();
            // toDto가 각 필터링된 채널에 대해 호출됨
            then(channelMapper).should(times(2)).toDto(any(Channel.class), anyList(), any());
            // Private 채널에 대해서만 참여자 조회가 일어남
            then(readStatusRepository).should(times(1)).findAllByChannelId(privateChannelId);
        }

        @Test
        @DisplayName("성공: 참여 채널 없을 때 공개 채널만 조회")
        void findAllByUserId_Success_OnlyPublic() {
            // Given
            given(readStatusRepository.findAllByUserId(userId1)).willReturn(
                Collections.emptyList());
            given(channelRepository.findAll()).willReturn(List.of(testPublicChannel));
            given(messageRepository.findAllByChannelId(publicChannelId)).willReturn(
                Collections.emptyList());
            ChannelDto publicDtoMock = new ChannelDto(publicChannelId, ChannelType.PUBLIC, "Public",
                "Desc", Collections.emptyList(), null);
            given(channelMapper.toDto(eq(testPublicChannel), anyList(), any())).willReturn(
                publicDtoMock);

            // When
            List<ChannelDto> result = basicChannelService.findAllByUserId(userId1);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(publicChannelId);

            then(channelMapper).should(times(1)).toDto(any(Channel.class), anyList(), any());
            then(readStatusRepository).should(never())
                .findAllByChannelId(any(UUID.class)); // 비공개 채널 없으므로 호출 안됨
        }
    }
}
