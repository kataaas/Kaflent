package ru.kataaas.kaflent.conroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.GroupResponse;
import ru.kataaas.kaflent.payload.RegisterDTO;
import ru.kataaas.kaflent.payload.UserDTO;
import ru.kataaas.kaflent.entity.RoleEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.UserMapper;
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.GroupUserJoinService;
import ru.kataaas.kaflent.service.RoleService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserMapper userMapper;

    private final UserService userService;

    private final RoleService roleService;

    private final GroupService groupService;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public UserController(UserMapper userMapper,
                          UserService userService,
                          RoleService roleService,
                          GroupService groupService,
                          GroupUserJoinService groupUserJoinService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.roleService = roleService;
        this.groupService = groupService;
        this.groupUserJoinService = groupUserJoinService;
    }

    @GetMapping("/user/fetch")
    public UserDTO fetch(HttpServletRequest request) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        return userMapper.toUserDTO(user);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserDTO> fetchUser(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(userMapper.toUserDTO(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/user/{username}/groups")
    public GroupResponse fetchGroupsByUser(@PathVariable String username,
                                           @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_GROUPS, required = false) int pageNo,
                                           @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_GROUPS, required = false) int pageSize) {
        Long userId = userService.findIdByUsername(username);
        List<Long> ids = groupUserJoinService.getGroupIdsByUserId(userId);
        return groupService.getGroupsByIds(ids, pageNo, pageSize);
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterDTO registerDTO) {
        if (userService.checkIfUsernameAlreadyUsed(registerDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username: " + registerDTO.getUsername() + " already used");
        }
        if (userService.checkIfEmailAlreadyUsed(registerDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email: " + registerDTO.getEmail() + " already used");
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(userService.passwordEncoder(registerDTO.getPassword()));
        user.setAccountNonLocked(true);
        user.setEnabled(true);

        RoleEntity roles = roleService.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(roles));

        try {
            userService.save(user);
            log.info("User saved successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error while registering user : {}", e.getMessage());
        }
        return ResponseEntity.status(500).build();
    }

}
