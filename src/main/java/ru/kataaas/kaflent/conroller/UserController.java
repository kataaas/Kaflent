package ru.kataaas.kaflent.conroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.RegisterDTO;
import ru.kataaas.kaflent.payload.UserDTO;
import ru.kataaas.kaflent.entity.RoleEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.UserMapper;
import ru.kataaas.kaflent.service.RoleService;
import ru.kataaas.kaflent.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService,
                          UserMapper userMapper,
                          RoleService roleService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.roleService = roleService;
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
