package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.StorageService;
import ru.kataaas.kaflent.service.UserService;
import ru.kataaas.kaflent.utils.FileTypeEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class ImageController {

    private final UserService userService;

    private final GroupService groupService;

    private final StorageService storageService;

    @Autowired
    public ImageController(UserService userService, GroupService groupService, StorageService storageService) {
        this.userService = userService;
        this.groupService = groupService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/user/upload")
    public ResponseEntity<?> uploadUserImage(HttpServletRequest request, @RequestParam("image") MultipartFile image) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            try {
                String filename = storageService.store(image, FileTypeEnum.USER_IMAGE);
                user.setImage(filename);
                userService.save(user);
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping(value = "/{groupName}/upload")
    public ResponseEntity<?> uploadGroupImage(HttpServletRequest request,
                                              @PathVariable String groupName,
                                              @RequestParam("image") MultipartFile image) {
        GroupEntity group = groupService.findByName(groupName);
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            if (group != null) {
                if (userService.checkIfUserIsGroupAdmin(user.getId(), group.getId())) {
                    try {
                        String filename = storageService.store(image, FileTypeEnum.GROUP_IMAGE);
                        group.setImage(filename);
                        groupService.save(group);
                        return ResponseEntity.status(HttpStatus.OK).build();
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                    }
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group " + groupName + " not found!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/uploads/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) throws IOException {
        ClassPathResource file = new ClassPathResource("uploads/" + fileName);
        return ResponseEntity.ok().contentLength(file.contentLength())
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(file.getInputStream()));
    }

}
