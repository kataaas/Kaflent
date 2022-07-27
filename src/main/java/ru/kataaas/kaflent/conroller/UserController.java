package ru.kataaas.kaflent.conroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.entity.RoleEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.UserMapper;
import ru.kataaas.kaflent.payload.GroupResponse;
import ru.kataaas.kaflent.payload.RegisterDTO;
import ru.kataaas.kaflent.payload.UserDTO;
import ru.kataaas.kaflent.service.*;
import ru.kataaas.kaflent.utils.JwtUtil;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final JwtUtil jwtUtil;

    private final UserMapper userMapper;

    private final UserService userService;

    private final RoleService roleService;

    private final GroupService groupService;

    private final GroupUserJoinService groupUserJoinService;

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public UserController(JwtUtil jwtUtil,
                          UserMapper userMapper,
                          UserService userService,
                          RoleService roleService,
                          GroupService groupService,
                          GroupUserJoinService groupUserJoinService,
                          CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.userService = userService;
        this.roleService = roleService;
        this.groupService = groupService;
        this.groupUserJoinService = groupUserJoinService;
        this.userDetailsService = userDetailsService;
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
        Page<BigInteger> ids = groupUserJoinService.getGroupIdsByUserId(userId, pageNo, pageSize);
        return groupService.getGroupsByIds(ids);
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterDTO registerDTO, HttpServletResponse response) {
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
        user.setImage("default_" + new Random().nextInt(3) + ".jpg");
        user.setAccountNonLocked(true);
        user.setEnabled(true);

        RoleEntity roles = roleService.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(roles));

        try {
            UserEntity savedUser = userService.save(user);
            log.info("User saved successfully");
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, token);
            jwtAuthToken.setHttpOnly(true);
            jwtAuthToken.setSecure(false);
            jwtAuthToken.setPath("/");
            jwtAuthToken.setMaxAge(604800000); // 7 days
            response.addCookie(jwtAuthToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toAuthResponse(user, token));
        } catch (Exception e) {
            log.error("Error while registering user : {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
