package org.example.prac02_back.user;

import lombok.RequiredArgsConstructor;
import org.example.prac02_back.user.model.AuthUserDetails;
import org.example.prac02_back.user.model.UserDto;
import org.example.prac02_back.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto.SignupReq dto){
        UserDto.SignupRes result = userService.signup(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto.LoginReq dto) {
        // 사용자 요청한 로그인 정보를 UsernamePasswordAuthenticationToken 객체를 생성해 변수에 담아준다.
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);

        // authenticationManagerdml 메소드에 담아온 변수를 매개변수로 실행하고 변수에 담아준다.
        Authentication authentication = authenticationManager.authenticate(token);

        // AuthUserDetails는 스프링 시큐리티가 이해할 수 있는 형태의 사용자 정보 가방에 담아준다.
        // 누구인지, 권한을 꺼내서 AuthUserDetails 에 담아준다.
        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();

        // 변수에 정보가 담아져 있다면 실행을 해라
        if(user != null) {
            // jwtUtil을 실행해서 토큰을 만들어서 jwt 변수에 담아준다.
            String jwt = jwtUtil.createToken(user.getIdx(), user.getUsername(), "ROLE_USER");
            // 헤더에 토큰을 담아서 보낸다.
            return ResponseEntity.ok().header("Set-Cookie", "ATOKEN=" + jwt + "; Path=/").build();
        }

        return ResponseEntity.ok("로그인 실패");

    }
}
