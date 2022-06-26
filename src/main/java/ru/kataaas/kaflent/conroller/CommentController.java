package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kataaas.kaflent.payload.UpdateContentDTO;
import ru.kataaas.kaflent.payload.CommentResponse;
import ru.kataaas.kaflent.entity.CommentEntity;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.CommentMapper;
import ru.kataaas.kaflent.service.*;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/{groupName}")
public class CommentController {

    private final UserService userService;

    private final PostService postService;

    private final GroupService groupService;

    private final CommentMapper commentMapper;

    private final CommentService commentService;

    private final GroupUserJoinService groupUserJoinService;


    @Autowired
    public CommentController(UserService userService,
                             PostService postService,
                             GroupService groupService,
                             CommentMapper commentMapper,
                             CommentService commentService,
                             GroupUserJoinService groupUserJoinService) {
        this.userService = userService;
        this.postService = postService;
        this.groupService = groupService;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.groupUserJoinService = groupUserJoinService;
    }

    @GetMapping("/posts/{postId}/comments")
    public CommentResponse fetchComments(@PathVariable Long postId,
                                         @RequestParam(value = "pageNo", defaultValue = StaticVariable.DEFAULT_PAGE_NUMBER_COMMENTS, required = false) int pageNo,
                                         @RequestParam(value = "pageSize", defaultValue = StaticVariable.DEFAULT_PAGE_SIZE_COMMENTS, required = false) int pageSize) {
        return commentService.getAllCommentsByPostId(postId, pageNo, pageSize);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(HttpServletRequest request,
                                           @PathVariable Long postId,
                                           @RequestBody UpdateContentDTO updateContentDTO) {
        CommentEntity comment = new CommentEntity();
        PostEntity post = postService.findById(postId);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            if (groupUserJoinService.checkIfUserIsAuthorizedInGroup(user.getId(), post.getGroupId())) {
                comment.setContent(updateContentDTO.getContent());
                comment.setUserId(user.getId());
                comment.setPostId(postId);
                try {
                    CommentEntity savedComment = commentService.save(comment);
                    return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDTO(savedComment));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not in group");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(HttpServletRequest request,
                                           @PathVariable Long commentId,
                                           @RequestBody UpdateContentDTO updateContentDTO) {
        return doCommentAction(request, commentId, updateContentDTO.getContent(), "update");
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> removeComment(HttpServletRequest request, @PathVariable Long commentId) {
        return doCommentAction(request, commentId, null, "remove");
    }


    private ResponseEntity<?> doCommentAction(HttpServletRequest request, Long commentId, String content, String action) {
        CommentEntity comment = commentService.findById(commentId);
        PostEntity post = postService.findById(comment.getPostId());
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            try {
                if (user.getId().equals(comment.getUserId())) {
                    if (action.equals("update")) {
                        comment.setContent(content);
                        CommentEntity savedComment = commentService.save(comment);
                        return ResponseEntity.ok().body(commentMapper.toCommentDTO(savedComment));
                    }
                }
                if (user.getId().equals(comment.getUserId()) || userService.checkIfUserIsAdminGroup(user.getId(), post.getGroupId())) {
                    if (action.equals("remove")) {
                        commentService.delete(comment);
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
