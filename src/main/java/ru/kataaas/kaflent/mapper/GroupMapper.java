package ru.kataaas.kaflent.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.GroupDTO;
import ru.kataaas.kaflent.payload.LightGroupDTO;
import ru.kataaas.kaflent.payload.PostResponse;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.service.PostService;

@Service
public class GroupMapper {

    private final PostMapper postMapper;

    private final PostService postService;

    @Autowired
    public GroupMapper(PostMapper postMapper,
                       PostService postService) {
        this.postMapper = postMapper;
        this.postService = postService;
    }

    public GroupDTO toGroupDTO(GroupEntity group) {
        GroupDTO groupDTO = new GroupDTO();

        Page<PostEntity> postPage = postService.getAllPostsByGroupId(group.getId(), 0, 5);
        PostResponse postResponse = postMapper.toPostResponse(postPage);

        groupDTO.setId(group.getId());
        groupDTO.setName(group.getName());
        groupDTO.setType(group.getGroupTypeEnum().name());
        groupDTO.setSubscribers(group.getUserEntities().size());
        groupDTO.setPosts(postResponse);
        groupDTO.setCreatedAt(group.getCreatedAt());

        return groupDTO;
    }

    public LightGroupDTO toLightGroupDTO(GroupEntity group) {
        LightGroupDTO groupDTO = new LightGroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setType(group.getGroupTypeEnum().name());
        groupDTO.setSubscribers(group.getUserEntities().size());

        return groupDTO;
    }

}
