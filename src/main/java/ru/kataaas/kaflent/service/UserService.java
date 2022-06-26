package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.entity.RoleEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.repository.UserRepository;
import ru.kataaas.kaflent.utils.JwtUtil;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public UserService(JwtUtil jwtUtil,
                       UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       GroupUserJoinService groupUserJoinService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupUserJoinService = groupUserJoinService;
    }

    public String passwordEncoder(String password) {
        return passwordEncoder.encode(password);
    }

    @Transactional
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkIfUsernameAlreadyUsed(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkIfEmailAlreadyUsed(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkIfUserIsAdmin(Long userId) {
        return findById(userId).getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    public boolean checkIfUserIsAdminGroup(Long userId, Long groupId) {
        GroupUser groupUser = groupUserJoinService.findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null)
            return groupUser.getRole() == 1;
        return false;
    }

    public UserEntity getUserEntityFromRequest(HttpServletRequest request) {
        String jwtToken;
        UserEntity user = null;
        String usernameOrEmail;
        Cookie cookie = WebUtils.getCookie(request, StaticVariable.SECURE_COOKIE);
        if (cookie != null) {
            jwtToken = cookie.getValue();
            usernameOrEmail = jwtUtil.getUsernameFromJwtToken(jwtToken);
            user = findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        }
        return user;
    }

}
