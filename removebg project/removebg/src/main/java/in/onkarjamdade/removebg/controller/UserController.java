package in.onkarjamdade.removebg.controller;

import in.onkarjamdade.removebg.dto.UserDTO;
import in.onkarjamdade.removebg.response.RemoveBgResponse;
import in.onkarjamdade.removebg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // FIX 1: Clean Spring Security Import
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDTO userDTO, Authentication authentication) {

        RemoveBgResponse response;
        try {
            // FIX 2: authentication.getName() gets the authenticated Clerk User ID string safely
            if (authentication == null || !authentication.getName().equals(userDTO.getClerkId())){
                response = RemoveBgResponse.builder()
                        .success(false)
                        .data("User does not have permission to access the resource")
                        .statuscode(HttpStatus.FORBIDDEN)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UserDTO user = userService.saveUser(userDTO);

            response = RemoveBgResponse.builder()
                    .success(true)
                    .data(user)
                    .statuscode(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception exception) {
            response = RemoveBgResponse.builder()
                    .success(false)
                    .data(exception.getMessage())
                    .statuscode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(Authentication authentication) {
        RemoveBgResponse bgResponse = null;
        try {
            if (authentication.getName().isEmpty() || authentication.getName() == null) {
                bgResponse = RemoveBgResponse.builder()
                        .statuscode(HttpStatus.FORBIDDEN)
                        .data("User does not have permission/access to this resource")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(bgResponse);
            }

            String clerkId = authentication.getName();
            UserDTO existingUser = userService.getUserByClerkId(clerkId);
            Map<String, Integer> map = new HashMap<>();
            map.put("credits", existingUser.getCredits());
            bgResponse = RemoveBgResponse.builder()
                    .statuscode(HttpStatus.OK)
                    .data(map)
                    .success(true)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(bgResponse);
        } catch (Exception e) {
            bgResponse = RemoveBgResponse.builder()
                    .statuscode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong.")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(bgResponse);
        }
    }
}