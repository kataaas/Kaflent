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

import javax.servlet.http.HttpServletRequest;

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
        GroupEntity group = groupService.findByName(groupName);
        if (group != null) {
            return ResponseEntity.ok(groupMapper.toGroupDTO(group));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
