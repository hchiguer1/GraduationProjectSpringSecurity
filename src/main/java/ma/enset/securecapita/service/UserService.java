package ma.enset.securecapita.service;

import ma.enset.securecapita.domain.User;
import ma.enset.securecapita.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO getUserByEmail(String username);




}
