package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.mapper.PostMapper;
import ru.kataaas.kaflent.payload.PostResponse;
import ru.kataaas.kaflent.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostMapper postMapper;

    private final PostRepository postRepository;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public PostService(PostMapper postMapper,
                       PostRepository postRepository,
                       GroupUserJoinService groupUserJoinService) {
        this.postMapper = postMapper;
        this.postRepository = postRepository;
        this.groupUserJoinService = groupUserJoinService;
    }

    public PostEntity save(PostEntity post) {
        return postRepository.save(post);
    }

    public void delete(PostEntity post) {
        postRepository.delete(post);
    }

    public PostEntity findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public PostResponse getAllPosts(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<GroupUser> groupUsers = groupUserJoinService.getAllByUserId(userId);
        List<Long> ids = groupUsers.stream().map(GroupUser::getGroupId).collect(Collectors.toList());
        Page<PostEntity> posts = postRepository.findAllByGroupIdIsInOrderByCreatedAtDesc(ids, pageable);
        return postMapper.toPostResponse(posts);
    }

    public Page<PostEntity> getAllPostsByGroupId(Long groupId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return postRepository.findAllByGroupIdOrderByCreatedAtDesc(groupId, pageable);
    }

}
