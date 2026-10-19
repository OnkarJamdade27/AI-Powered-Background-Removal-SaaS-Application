package in.onkarjamdade.removebg.controller;

import in.onkarjamdade.removebg.dto.UserDTO;
import in.onkarjamdade.removebg.response.RemoveBgResponse;
import in.onkarjamdade.removebg.service.RemoveBackgoundService;
import in.onkarjamdade.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final RemoveBackgoundService removeBackgroundService;
    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackground(@RequestParam("file") MultipartFile file,
                                              Authentication authentication) {

        RemoveBgResponse response = null;
        Map<String, Object> responseMap = new HashMap<>();
        try {
            //validation
            if (authentication.getName().isEmpty() || authentication.getName() == null) {
                response = RemoveBgResponse.builder()
                        .statuscode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User does not have permission/access to this resource")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UserDTO userDTO = userService.getUserByClerkId(authentication.getName());

            //validation
            if (userDTO.getCredits() == 0) {
                responseMap.put("message", "No credit balance");
                responseMap.put("creditBalance", userDTO.getCredits());
                response = RemoveBgResponse.builder()
                        .success(false)
                        .data(responseMap)
                        .statuscode(HttpStatus.OK)
                        .build();
                return ResponseEntity.ok(response);
            }

            byte[] imageBytes = removeBackgroundService.removeBackground(file);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            userDTO.setCredits(userDTO.getCredits() - 1);

            userService.saveUser(userDTO);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(base64Image);

        } catch (Exception e) {
            e.printStackTrace();
            response = RemoveBgResponse.builder()
                    .statuscode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Something went wrong.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}