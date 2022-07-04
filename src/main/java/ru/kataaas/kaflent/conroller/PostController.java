package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kataaas.kaflent.payload.UpdateContentDTO;
import ru.kataaas.kaflent.payload.LightPostDTO;
import ru.kataaas.kaflent.payload.PostResponse;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.PostMapper;
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.PostService;
import ru.kataaas.kaflent.service.StorageService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.FileTypeEnum;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class PostController {

    private final PostMapper postMapper;

    private final PostService postService;

    private final UserService userService;

    private final GroupService groupService;

    private final StorageService storageService;

    @Autowired
    public PostController(PostMapper postMapper,
                          PostService postService,
                          UserService userService,
                          GroupService groupService,
                          StorageService storageService) {
        this.postMapper = postMapper;
        this.postService = postService;
        this.userService = userService;
        this.groupService = groupService;
        this.storageService = storageService;
    }

    @PostMapping("/{groupName}/posts")
    public ResponseEntity<?> createPost(HttpServletRequest request,
                                        @PathVariable String groupName,
                                        @RequestParam("content") String content,
                                        @RequestParam("files") MultipartFile[] files) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        Long userId = user.getId();
        Long groupId = groupService.findIdByName(groupName);
        if (userService.checkIfUserIsGroupAdmin(userId, groupId)) {
            PostEntity post = new PostEntity();
            post.setContent(content);
            post.setGroupId(groupId);
            PostEntity savedPost = postService.save(post);
            if (files != null) {
                for (MultipartFile file : files) {
                    storageService.store(file, FileTypeEnum.POST_FILE);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toPostDTO(savedPost));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission");
    }

    @GetMapping("/{groupName}/posts")
    public PostResponse fetchPostsByGroup(
            @PathVariable String groupName,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_POSTS, required = false) int pageSize) {
        Long groupId = groupService.findIdByName(groupName);
        Page<PostEntity> posts = postService.getAllPostsByGroupId(groupId, pageNo, pageSize);
        return postMapper.toPostResponse(posts);
    }

    @PutMapping("/{groupName}/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestBody UpdateContentDTO updateContentDTO,
                                        HttpServletRequest request) {
        return doPostAction(request, id, updateContentDTO.getContent(), "update");
    }

    @DeleteMapping("/{groupName}/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                        HttpServletRequest request) {
        return doPostAction(request, id, null, "delete");
    }

    private ResponseEntity<?> doPostAction(HttpServletRequest request, Long postId, String payload, String action) {
        PostEntity post = postService.findById(postId);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            if (userService.checkIfUserIsGroupAdmin(user.getId(), post.getGroupId())
                    || userService.checkIfUserIsAdmin(user.getId())) {
                try {
                    if (action.equals("update")) {
                        post.setContent(payload);
                        PostEntity savedPost = postService.save(post);
                        return ResponseEntity.ok().body(postMapper.toPostDTO(savedPost));
                    }
                    if (action.equals("delete")) {
                        postService.delete(post);
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
