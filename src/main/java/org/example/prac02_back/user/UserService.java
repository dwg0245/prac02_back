package org.example.prac02_back.user;

import lombok.RequiredArgsConstructor;
import org.example.prac02_back.user.model.User;
import org.example.prac02_back.user.model.UserDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserDto.SignupRes signup(UserDto.SignupReq dto) {
        User user = dto.toEntity();
        userRepository.save(user);

        return UserDto.SignupRes.from(user);
    }

    public UserDto.LoginRes login(UserDto.loginReq dto) {
        User user = dto.toEntity();
        Optional<User> result = userRepository.findByEmail(user.getEmail());

        if(result.isPresent()){
            User user01 = result.get();
            return UserDto.LoginRes.from(user01);
        }

        return null;
    }
}
