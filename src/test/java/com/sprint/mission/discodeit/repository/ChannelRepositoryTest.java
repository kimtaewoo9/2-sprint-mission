package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // 테스트 데이터 설정 및 영속성 컨텍스트 관리에 사용

    @Autowired
    private ChannelRepository channelRepository;

    private Channel channel1;
    private Channel channel2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 미리 생성하지 않고 각 테스트 메서드에서 필요시 생성하도록 변경
        // 또는 여기에 공통적으로 사용될 최소한의 데이터만 생성
    }

    @Test
    @DisplayName("채널 저장 및 ID로 조회 - 성공")
    void saveAndFindById_whenChannelExists_shouldReturnChannel() {
        // Given
        Channel newChannel = Channel.createPublicChannel("Test Channel 1",
            "Description for channel 1");

        Channel persistedChannel = entityManager.persistAndFlush(newChannel);
        entityManager.clear(); // 영속성 컨텍스트를 비워 1차 캐시가 아닌 DB에서 조회하도록 함

        // When
        Optional<Channel> foundChannelOptional = channelRepository.findById(
            persistedChannel.getId());

        // Then
        assertThat(foundChannelOptional).isPresent();
        Channel foundChannel = foundChannelOptional.get();
        assertThat(foundChannel.getId()).isEqualTo(persistedChannel.getId());
        assertThat(foundChannel.getName()).isEqualTo("Test Channel 1");
        assertThat(foundChannel.getDescription()).isEqualTo("Description for channel 1");
        assertThat(foundChannel.getChannelType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("ID로 조회 - 채널이 존재하지 않을 때 빈 Optional 반환")
    void findById_whenChannelDoesNotExist_shouldReturnEmptyOptional() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Channel> foundChannelOptional = channelRepository.findById(nonExistentId);

        // Then
        assertThat(foundChannelOptional).isNotPresent();
    }

    @Test
    @DisplayName("findAll - 저장된 모든 채널 조회")
    void findAll_whenChannelsExist_shouldReturnAllChannels() {
        // Given
        channel1 = Channel.createPublicChannel("General Channel", "Public discussions");
        channel2 = Channel.createPrivateChannel(); // Private 채널은 이름/설명 없이 생성 가능
        channel2.setName("Dev Zone"); // 필요시 이름 설정

        entityManager.persist(channel1);
        entityManager.persist(channel2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Channel> channels = channelRepository.findAll();

        // Then
        assertThat(channels).isNotNull();
        assertThat(channels).hasSize(2);
        // 저장 순서나 ID 순서에 따라 결과 순서가 달라질 수 있으므로, 내용 포함 여부로 검증
        assertThat(channels).extracting(Channel::getName)
            .containsExactlyInAnyOrder("General Channel", "Dev Zone");
        assertThat(channels).extracting(Channel::getChannelType)
            .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("findAll - 저장된 채널이 없을 때 빈 리스트 반환")
    void findAll_whenNoChannelsExist_shouldReturnEmptyList() {
        // Given: 아무 채널도 저장하지 않은 상태

        // When
        List<Channel> channels = channelRepository.findAll();

        // Then
        assertThat(channels).isNotNull();
        assertThat(channels).isEmpty();
    }

    @Test
    @DisplayName("채널 삭제 - 성공")
    void deleteChannel_success() {
        // Given
        Channel channelToDelete = Channel.createPublicChannel("Channel to Delete",
            "Will be deleted");
        Channel persistedChannel = entityManager.persistAndFlush(channelToDelete);
        UUID persistedId = persistedChannel.getId();
        entityManager.clear(); // 삭제 전 조회하여 존재 확인 후, 삭제 후 다시 조회하여 없는지 확인

        assertThat(channelRepository.findById(persistedId)).isPresent(); // 삭제 전 존재 확인

        // When
        channelRepository.deleteById(persistedId);
        entityManager.flush(); // 삭제 작업 DB 반영
        entityManager.clear(); // 영속성 컨텍스트 비우기

        // Then
        Optional<Channel> deletedChannelOptional = channelRepository.findById(persistedId);
        assertThat(deletedChannelOptional).isNotPresent();
    }
}
