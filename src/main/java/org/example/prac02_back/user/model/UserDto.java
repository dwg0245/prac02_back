package org.example.prac02_back.user.model;

import lombok.Builder;
import lombok.Getter;

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
        private Long idx;
        private String email;
        private String name;
        private String password;


        public static UserDto.SignupRes from(User entity){
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .password(entity.getPassword())
                    .build();
        }
    }
}
