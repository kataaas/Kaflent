package ru.kataaas.kaflent.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kataaas.kaflent.entity.UserEntity;
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

    private final StorageService storageService;

    @Autowired
    public ImageController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/user/upload")
    public ResponseEntity<?> uploadUserImage(HttpServletRequest request, @RequestParam("image") MultipartFile image) {
        UserEntity user = userService.getUserEntityFromRequest(request);
        if (user != null) {
            try {
                String filename = storageService.store(image, FileTypeEnum.USER_IMAGE);
                user.setImageName(filename);
                userService.save(user);
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = "/get/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) throws IOException {
        ClassPathResource file = new ClassPathResource( fileName);
        return ResponseEntity.ok().contentLength(file.contentLength())
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(file.getInputStream()));
    }

}
