package ma.enset.securecapita.service.implementation;

import lombok.RequiredArgsConstructor;
import ma.enset.securecapita.domain.User;
import ma.enset.securecapita.dto.UserDTO;
import ma.enset.securecapita.dtomapper.UserDTOMapper;
import ma.enset.securecapita.repository.UserRepository;
import ma.enset.securecapita.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }





}
