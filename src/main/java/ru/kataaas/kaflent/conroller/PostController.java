package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kataaas.kaflent.entity.FileEntity;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.payload.UpdateContentDTO;
import ru.kataaas.kaflent.payload.LightPostDTO;
import ru.kataaas.kaflent.payload.PostResponse;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.PostMapper;
import ru.kataaas.kaflent.service.*;
import ru.kataaas.kaflent.utils.FileTypeEnum;
import ru.kataaas.kaflent.utils.GroupTypeEnum;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class PostController {

    private final PostMapper postMapper;

    private final PostService postService;

    private final UserService userService;

    private final GroupService groupService;

    private final EmotionService emotionService;

    private final StorageService storageService;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public PostController(PostMapper postMapper,
                          PostService postService,
                          UserService userService,
                          GroupService groupService,
                          EmotionService emotionService,
                          StorageService storageService,
                          GroupUserJoinService groupUserJoinService) {
        this.postMapper = postMapper;
        this.postService = postService;
        this.userService = userService;
        this.groupService = groupService;
        this.emotionService = emotionService;
        this.storageService = storageService;
        this.groupUserJoinService = groupUserJoinService;
    }

    @PostMapping("/{groupName}/posts")
    public ResponseEntity<?> createPost(HttpServletRequest request,
                                        @PathVariable String groupName,
                                        @RequestParam(value = "content") String content,
                                        @RequestParam(value = "files", required = false) MultipartFile[] files) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        Long groupId = groupService.findIdByName(groupName);
        if (user != null) {
            if (userService.checkIfUserIsGroupAdmin(user.getId(), groupId)) {
                PostEntity post = new PostEntity();
                post.setContent(content);
                post.setGroupId(groupId);
                if (files != null) {
                    Set<FileEntity> fileEntities = new HashSet<>();
                    try {
                        for (MultipartFile file : files) {
                            FileEntity fileEntity = storageService.store(file, FileTypeEnum.POST_FILE);
                            fileEntities.add(fileEntity);
                        }
                        post.setFiles(fileEntities);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                    }
                }
                PostEntity savedPost = postService.save(post);
                return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toPostDTO(savedPost));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{groupName}/posts")
    public ResponseEntity<?> fetchPostsByGroup(HttpServletRequest request,
                                               @PathVariable String groupName,
                                               @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_POSTS, required = false) int pageNo,
                                               @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_POSTS, required = false) int pageSize) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        Optional<GroupEntity> group = groupService.findByName(groupName);
        if (user != null) {
            if (group.isPresent()) {
                if (groupUserJoinService.checkIfUserIsNonBannedInGroup(user.getId(), group.get().getId())) {
                    PostResponse postResponse = postService.getAllPostsByGroupId(group.get().getId(), pageNo, pageSize);
                    if (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PUBLIC)) {
                        return ResponseEntity.ok(postResponse);
                    }
                    if (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PRIVATE)) {
                        if (groupUserJoinService.checkIfUserIsAuthorizedInGroup(user.getId(), group.get().getId())) {
                            return ResponseEntity.ok(postResponse);
                        }
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not in a group.");
                    }
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to the group is denied.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{groupName}/posts/{id}/emotions/put")
    public ResponseEntity<?> putEmotion(@PathVariable Long id,
                                        @RequestParam String emotion,
                                        HttpServletRequest request) {
        return doPostAction(request, id, emotion, "putEmotion");
    }

    @DeleteMapping("/{groupName}/posts/{id}/emotions/remove")
    public ResponseEntity<?> removeEmotion(@PathVariable Long id,
                                           HttpServletRequest request) {
        return doPostAction(request, id, null, "removeEmotion");
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
            if (post != null) {
                Optional<GroupEntity> group = groupService.findById(post.getGroupId());
                if (group.isPresent()) {
                    if (groupUserJoinService.checkIfUserIsNonBannedInGroup(user.getId(), post.getGroupId())) {
                        try {
                            if (userService.checkIfUserIsGroupAdmin(user.getId(), post.getGroupId())
                                    || userService.checkIfUserIsAdmin(user.getId())) {
                                if (action.equals("update")) {
                                    post.setContent(payload);
                                    PostEntity savedPost = postService.save(post);
                                    return ResponseEntity.ok().body(postMapper.toPostDTO(savedPost));
                                }
                                if (action.equals("delete")) {
                                    postService.delete(post);
                                    return ResponseEntity.ok().build();
                                }
                            }
                            if (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PUBLIC)
                                    || (group.get().getGroupTypeEnum().equals(GroupTypeEnum.PRIVATE)
                                    && groupUserJoinService.checkIfUserIsAuthorizedInGroup(user.getId(), group.get().getId()))) {
                                if (action.equals("putEmotion")) {
                                    emotionService.createEmotion(user.getId(), postId, payload);
                                    return ResponseEntity.ok().build();
                                }
                                if (action.equals("removeEmotion")) {
                                    emotionService.deleteEmotion(user.getId(), postId);
                                    return ResponseEntity.ok().build();
                                }
                            }
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        } catch (Exception e) {
                            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                        }
                    }
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
