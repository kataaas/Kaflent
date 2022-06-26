package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.PostResponse;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.PostMapper;
import ru.kataaas.kaflent.service.PostService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final UserService userService;

    private final PostService postService;

    private final PostMapper postMapper;

    @Autowired
    public ApiController(UserService userService, PostService postService, PostMapper postMapper) {
        this.userService = userService;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping("/feed")
    public PostResponse fetchAllPosts(HttpServletRequest request,
                                      @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_POSTS, required = false) int pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_POSTS, required = false) int pageSize) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        return postService.getAllPosts(user.getId(), pageNo, pageSize);
    }

}
