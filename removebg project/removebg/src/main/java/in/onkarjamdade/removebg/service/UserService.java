package in.onkarjamdade.removebg.service;

import in.onkarjamdade.removebg.dto.UserDTO;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);

    UserDTO getUserByClerkId(String clerkId);

    void deleteUserByClerkId(String clerkId);
}
