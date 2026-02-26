package org.example.prac02_back.user;

import lombok.RequiredArgsConstructor;
import org.example.prac02_back.user.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto.SignupReq dto){
        UserDto.SignupRes result = userService.signup(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto.loginReq dto){
        UserDto.LoginRes result = userService.login(dto);
        return ResponseEntity.ok(result);
    }
}
