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
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.GroupUserJoinService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.GroupTypeEnum;
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
    public ResponseEntity<?> fetchGroup(@PathVariable String groupName, HttpServletRequest request) {
        Optional<GroupEntity> group = groupService.findByName(groupName);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (group.isPresent()) {
            if (groupUserJoinService.checkIfUserIsNonBannedInGroup(user.getId(), group.get().getId())) {
                if (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PUBLIC)) {
                    return ResponseEntity.ok(groupMapper.toGroupDTO(group.get()));
                }
                if (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PRIVATE)) {
                    if (groupUserJoinService.checkIfUserIsAuthorizedInGroup(user.getId(), group.get().getId())) {
                        return ResponseEntity.ok(groupMapper.toGroupDTO(group.get()));
                    }
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not in a group.");
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to the group is denied.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{groupName}/users")
    public ResponseEntity<?> fetchUsersInGroup(HttpServletRequest request,
                                               @PathVariable String groupName,
                                               @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_USERS, required = false) int pageNo,
                                               @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_USERS, required = false) int pageSize) {
        Optional<GroupEntity> group = groupService.findByName(groupName);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            if (group.isPresent()) {
                List<Long> ids = groupUserJoinService.getUserIdsByGroupId(group.get().getId());
                return getResponseEntityByUserIds(group.get(), user.getId(), ids, pageNo, pageSize);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{groupName}/users/request")
    public ResponseEntity<?> fetchAllRequestsUsers(HttpServletRequest request,
                                                   @PathVariable String groupName,
                                                   @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_USERS, required = false) int pageNo,
                                                   @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_USERS, required = false) int pageSize) {
        Optional<GroupEntity> group = groupService.findByName(groupName);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            if (group.isPresent()) {
                List<Long> ids = groupUserJoinService.getUserIdsByRequestToGroup(group.get().getId());
                return getResponseEntityByUserIds(group.get(), user.getId(), ids, pageNo, pageSize);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<?> getResponseEntityByUserIds(GroupEntity group, Long userId, List<Long> ids, int pageNo, int pageSize) {
        if (groupUserJoinService.checkIfUserIsNonBannedInGroup(userId, group.getId())) {
            if (group.getGroupTypeEnum().equals(GroupTypeEnum.PUBLIC)) {
                return ResponseEntity.ok(userService.getUsersByIds(ids, pageNo, pageSize));
            }
            if (group.getGroupTypeEnum().equals(GroupTypeEnum.PRIVATE)) {
                if (groupUserJoinService.checkIfUserIsAuthorizedInGroup(userId, group.getId())) {
                    return ResponseEntity.ok(userService.getUsersByIds(ids, pageNo, pageSize));
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not in a group.");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to the group is denied.");
    }

    @GetMapping("/{groupName}/user/add")
    public ResponseEntity<?> addUserToGroup(HttpServletRequest request, @PathVariable String groupName) {
        Long groupId = groupService.findIdByName(groupName);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (groupUserJoinService.checkIfUserIsNonBannedInGroup(user.getId(), groupId)) {
            try {
                return ResponseEntity.ok().body(groupService.addUserToGroup(user.getId(), groupId));
            } catch (Exception e) {
                log.error("Error when trying to add user to conversation : {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to the group is denied.");
    }

    @GetMapping("/{groupName}/user/leave")
    public ResponseEntity<?> leaveGroup(HttpServletRequest request, @PathVariable String groupName) {
        return doAction(request, null, groupName, "leave");
    }

    @GetMapping("/{groupName}/user/grant/{username}")
    public ResponseEntity<?> grantUserAdminInGroup(HttpServletRequest request,
                                                   @PathVariable String username,
                                                   @PathVariable String groupName) {
        return doAction(request, username, groupName, "grant");
    }

    @GetMapping("/{groupName}/user/remove/admin/{username}")
    public ResponseEntity<?> removeAdminUserFromGroup(HttpServletRequest request,
                                                      @PathVariable String username,
                                                      @PathVariable String groupName) {
        return doAction(request, username, groupName, "removeAdmin");
    }

    @GetMapping("/{groupName}/users/request/{username}/accept")
    public ResponseEntity<?> acceptRequestToGroup(HttpServletRequest request,
                                                  @PathVariable String username,
                                                  @PathVariable String groupName) {
        return doAction(request, username, groupName, "accept");
    }

    @GetMapping("/{groupName}/users/request/{username}/reject")
    public ResponseEntity<?> rejectRequestToGroup(HttpServletRequest request,
                                                  @PathVariable String username,
                                                  @PathVariable String groupName) {
        return doAction(request, username, groupName, "reject");
    }

    private ResponseEntity<?> doAction(HttpServletRequest request, String username, String groupName, String action) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        Long groupId = groupService.findIdByName(groupName);
        if (user != null) {
            if (groupUserJoinService.checkIfUserIsAuthorizedInGroup(user.getId(), groupId)) {
                if (action.equals("leave")) {
                    groupUserJoinService.removeUserFromGroup(user.getId(), groupId);
                    return ResponseEntity.ok(username + " has left the group.");
                }
                Long userIdDoAction = userService.findIdByUsername(username);
                if (userService.checkIfUserIsGroupAdmin(user.getId(), groupId)) {
                    try {
                        if (groupUserJoinService.checkIfUserIsNonBannedInGroup(userIdDoAction, groupId)) {
                            if (action.equals("grant")) {
                                groupUserJoinService.grantUserAdminInGroup(userIdDoAction, groupId);
                                return ResponseEntity.ok(username + " has been granted administrator.");
                            }
                            if (action.equals("removeAdmin")) {
                                groupUserJoinService.removeUserAdminFromGroup(userIdDoAction, groupId);
                                return ResponseEntity.ok(username + " has been removed from administrators.");
                            }
                            if (action.equals("accept")) {
                                groupUserJoinService.acceptUserToGroup(userIdDoAction, groupId);
                                return ResponseEntity.ok(username + " was accepted into the group.");
                            }
                            if (action.equals("reject")) {
                                groupUserJoinService.deleteByUserIdAndGroupId(userIdDoAction, groupId);
                                return ResponseEntity.ok("The application was rejected.");
                            }
                        }
                        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(username + " is banned.");
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                    }
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("You are not in a group.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/group/create")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupDTO groupDTO, HttpServletRequest request) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (groupService.checkIfGroupNameAlreadyUsed(groupDTO.getName())) {
            return ResponseEntity.badRequest().body("Group name: " + groupDTO.getName() + " is already used! Please try again");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(user.getId(), groupDTO.getName(), groupDTO.getType()));
    }

}
