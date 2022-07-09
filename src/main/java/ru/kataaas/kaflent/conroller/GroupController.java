package ru.kataaas.kaflent.conroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.CreateGroupDTO;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.GroupMapper;
import ru.kataaas.kaflent.payload.UserResponse;
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.GroupUserJoinService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class GroupController {

    private final UserService userService;

    private final GroupMapper groupMapper;

    private final GroupService groupService;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public GroupController(UserService userService,
                           GroupMapper groupMapper,
                           GroupService groupService,
                           GroupUserJoinService groupUserJoinService) {
        this.userService = userService;
        this.groupMapper = groupMapper;
        this.groupService = groupService;
        this.groupUserJoinService = groupUserJoinService;
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<?> fetchGroup(@PathVariable String groupName) {
        Optional<GroupEntity> group = groupService.findByName(groupName);
        if (group.isPresent()) {
            return ResponseEntity.ok(groupMapper.toGroupDTO(group.orElse(null)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{groupName}/users")
    public UserResponse fetchUsersInGroup(@PathVariable String groupName,
                                          @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_USERS, required = false) int pageNo,
                                          @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_USERS, required = false) int pageSize) {
        Long groupId = groupService.findIdByName(groupName);
        List<Long> ids = groupUserJoinService.getUserIdsByGroupId(groupId);
        return userService.getUsersByIds(ids, pageNo, pageSize);
    }

    @GetMapping("/{groupName}/user/add")
    public ResponseEntity<?> addUserToGroup(HttpServletRequest request, @PathVariable String groupName) {
        Long groupId = groupService.findIdByName(groupName);
        Long userId = userService.getUserEntityFromRequest(request).getId();
        try {
            return ResponseEntity.ok().body(groupService.addUserToConversation(userId, groupId));
        } catch (Exception e) {
            log.error("Error when trying to add user to conversation : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }

    @PostMapping("/group/create")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupDTO groupDTO, HttpServletRequest request) {
        if (groupService.checkIfGroupNameAlreadyUsed(groupDTO.getName())) {
            return ResponseEntity.badRequest().body("Group name: " + groupDTO.getName() + " is already used! Please try again");
        }
        UserEntity user = userService.getUserEntityFromRequest(request);
        GroupEntity group = groupService.createGroup(user.getId(), groupDTO.getName(), groupDTO.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(groupMapper.toGroupDTO(group));
    }

//    @DeleteMapping("/{groupName}")
//    public ResponseEntity<?> deleteGroup(@PathVariable String groupName, HttpServletRequest request) {
//        Long groupId = groupService.findIdByName(groupName);
//        UserEntity user = userService.getUserEntityFromRequest(request);
//        if (user != null) {
//            if (userService.checkIfUserIsAdminGroup(user.getId(), groupId)
//                    || userService.checkIfUserIsAdmin(user.getId())) {
//                try {
//                    groupService.delete(groupId);
//                    return ResponseEntity.ok().build();
//                } catch (Exception e) {
//                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
//                }
//            }
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }

}
