package ru.kataaas.kaflent.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.payload.GroupDTO;
import ru.kataaas.kaflent.payload.GroupResponse;
import ru.kataaas.kaflent.payload.LightGroupDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupMapper {

    public GroupDTO toGroupDTO(GroupEntity group) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(group.getId());
        groupDTO.setName(group.getName());
        groupDTO.setImage(group.getImage());
        groupDTO.setType(group.getGroupTypeEnum().name());
        groupDTO.setSubscribers(group.getUserEntities().size());
        groupDTO.setCreatedAt(group.getCreatedAt());

        return groupDTO;
    }

    public GroupResponse toGroupResponse(Page<GroupEntity> groups) {
        GroupResponse groupResponse = new GroupResponse();
        List<LightGroupDTO> groupDTOList = new ArrayList<>();
        List<GroupEntity> groupEntities = groups.getContent();
        groupEntities.forEach(group -> groupDTOList.add(toLightGroupDTO(group)));

        groupResponse.setGroups(groupDTOList);
        groupResponse.setPageNo(groups.getNumber());
        groupResponse.setPageSize(groups.getSize());
        groupResponse.setTotalElements(groups.getTotalElements());
        groupResponse.setTotalPages(groups.getTotalPages());
        groupResponse.setLast(groups.isLast());

        return groupResponse;
    }

    public LightGroupDTO toLightGroupDTO(GroupEntity group) {
        LightGroupDTO groupDTO = new LightGroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setImage(group.getImage());
        groupDTO.setType(group.getGroupTypeEnum().name());
        groupDTO.setSubscribers(group.getUserEntities().size());

        return groupDTO;
    }

}
