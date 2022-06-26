package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.AuthUserResponse;
import ru.kataaas.kaflent.payload.LoginDTO;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.UserMapper;
import ru.kataaas.kaflent.service.CustomUserDetailsService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.JwtUtil;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final UserMapper userMapper;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService userDetailsService;


    @Autowired
    public AuthController(JwtUtil jwtUtil,
                          UserMapper userMapper,
                          UserService userService,
                          AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @PostMapping("/auth")
    public AuthUserResponse createAuthenticationToken(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        authenticate(loginDTO.getLogin(), loginDTO.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getLogin());
        UserEntity user = userService.findByUsernameOrEmail(loginDTO.getLogin(), loginDTO.getLogin());
        String token = jwtUtil.generateToken(userDetails);
        Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, token);
        jwtAuthToken.setHttpOnly(true);
        jwtAuthToken.setSecure(false);
        jwtAuthToken.setPath("/");
        jwtAuthToken.setMaxAge(604800000); // 7 days
        response.addCookie(jwtAuthToken);
        return userMapper.toAuthResponse(user, token);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie(StaticVariable.SECURE_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    private void authenticate(String usernameOrEmail, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
