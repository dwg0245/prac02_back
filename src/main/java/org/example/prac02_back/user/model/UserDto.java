package org.example.prac02_back.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {
    @Getter
    public static class SignupReq{
        private String email;
        private String name;
        private String password;

        public User toEntity(){
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.password)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SignupRes{
        private String email;
        private String name;
        private String password;


        public static User from(User entity){
            return User.builder()
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .password(entity.getPassword())
                    .enable(true)
                    .role("ROLE_USER")
                    .build();
        }
    }

    @Getter
    public static class LoginReq{
        private String email;
        private String password;

    }

    @Getter
    @Builder
    public static class LoginRes{
        private Long idx;
        private String email;
        private String name;


        public static UserDto.LoginRes from(User entity){
            return UserDto.LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .build();
        }
    }
}
