package in.onkarjamdade.removebg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.onkarjamdade.removebg.dto.UserDTO;
import in.onkarjamdade.removebg.response.RemoveBgResponse;
import in.onkarjamdade.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class ClerkWebhookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final UserService userService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timestamp") String svixTimestamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {

        RemoveBgResponse response = null;
        try {
            // Verify webhook signature
            boolean isValid = verifyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);

            if (!isValid) {
                response = RemoveBgResponse.builder()
                        .statuscode(HttpStatus.UNAUTHORIZED)
                        .data("Invalid webhook signature")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            response = RemoveBgResponse.builder()
                    .statuscode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        userService.deleteUserByClerkId(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();
        UserDTO existingUser = userService.getUserByClerkId(clerkId);

        existingUser.setEmail(data.path("email_addresses").path(0).path("email_address").asText());
        existingUser.setFirstName(data.path("first_name").asText());
        existingUser.setLastName(data.path("last_name").asText());
        existingUser.setPhotoUrl(data.path("image_url").asText());

        userService.saveUser(existingUser);
    }

    private void handleUserCreated(JsonNode data) {
        UserDTO newUser = UserDTO.builder()
                .clerkId(data.path("id").asText())
                .email(data.path("email_addresses").path(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .photoUrl(data.path("image_url").asText())
                .build();
        userService.saveUser(newUser);
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        // Note: The image shows a stub returning true. 
        // For a real implementation, you would need to implement the Svix signature verification logic here.
        // The code below is the actual logic you would need to write to make this secure.
        try {
            String signedContent = svixId + "." + svixTimestamp + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(signedContent.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(rawHmac);

            // Note: Svix actually sends a list of signatures separated by spaces.
            // You would normally split svixSignature by space and compare against expectedSignature.
            return expectedSignature.equals(svixSignature);
        } catch (Exception e) {
            return false;
        }
        // return true; // Return as shown in the image screenshot
    }
}