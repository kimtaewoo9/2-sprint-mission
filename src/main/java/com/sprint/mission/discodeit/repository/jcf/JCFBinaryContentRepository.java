package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> store = new HashMap<>();

    @Override
    public UUID save(BinaryContent binaryContent) {
        store.put(binaryContent.getId(), binaryContent);
        return binaryContent.getId();
    }

    @Override
    public BinaryContent find(UUID id) {
        return store.get(id);
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID profileImageId) {
        store.remove(profileImageId);
    }
}
