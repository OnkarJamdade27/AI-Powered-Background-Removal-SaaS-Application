package in.onkarjamdade.removebg.service.impl;

import in.onkarjamdade.removebg.dto.UserDTO;
import in.onkarjamdade.removebg.entity.UserEntity;
import in.onkarjamdade.removebg.repository.UserRepository;
import in.onkarjamdade.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userDTO.getClerkId());

        if (optionalUser.isPresent()) {
            // SCENARIO A: User exists! Update profile info but protect their wallet balance.
            UserEntity existingUser = optionalUser.get();
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setPhotoUrl(userDTO.getPhotoUrl());

            // Only overwrite credits if explicitly updated by a transaction, never reset it to null or default
            if (userDTO.getCredits() != null) {
                existingUser.setCredits(userDTO.getCredits());
            }

            UserEntity updatedUser = userRepository.save(existingUser);
            return mapToDTO(updatedUser);
        }

        // SCENARIO B: Brand new user! Map fields and grant starter free tier processing credits
        UserEntity newUser = mapToEntity(userDTO);
        newUser.setCredits(5); // Automatically hand out 5 free starter credits on signup!

        UserEntity savedUser = userRepository.save(newUser);
        return mapToDTO(savedUser);
    }

    private UserEntity mapToEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .clerkId(userDTO.getClerkId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .photoUrl(userDTO.getPhotoUrl())
                // Notice we leave credits out here so we can dynamically handle it above
                .build();
    }

    private UserDTO mapToDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .clerkId(userEntity.getClerkId())
                .credits(userEntity.getCredits())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .photoUrl(userEntity.getPhotoUrl()) // Restored mapping to keep DTO complete
                .build();
    }

    @Override
    public UserDTO getUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return mapToDTO(userEntity);
    }

    @Override
    public void deleteUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        userRepository.delete(userEntity);
    }
}