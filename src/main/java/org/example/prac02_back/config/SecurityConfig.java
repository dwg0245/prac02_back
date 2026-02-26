package org.example.prac02_back.config;

/*
기존 스프링 시큐리티의 로그인 처리 방식

1. UsernamePasswordAuthenticationFilter 에서 ID(username), PW(password) 를 받아서             (컨트롤러 역할)
2. UsernamePasswordAuthenticationToken 객체에 담아서                                          (Dto 역할)
3. AuthenticationManager 인터페이스를 상속받은 ProviderManager 객체의 authenticate 메소드 실행    (서비스 메소드 실행 역할)

4. 3번에서 실행된 메소드에서 AbstractUserDetailsAuthenticationProvider 객체의 authenticate 메소드 실행
5. 4번에서 실행된 메소드에서 retrieveUser 메소드 실행하고 retrieveUser메소드에서 InMemoryUserDetailsManager 객체의 loadUserByUsername 메소드 실행
6. loadUserByUsername 메소드에서 사용자 정보를 조회해서 해당 하는 사용자가 있으면 UserDetails 객체를 반환
7. 8. 9. 반환받은 걸 확인해서 세션에 사용자 인증 정보 저장
 */

/*

요청 바꾸기 : UsernamePasswordAuthenticationFilter의 attemptAuthentication 메소드를 재정의
사용자 확인 : UserDetailsService의 loadUserByUsername 메소드를 재정의
응답 바꾸기 : UsernamePasswordAuthenticationFilter의 successfulAuthentication 메소드 재정의
*/

import lombok.RequiredArgsConstructor;
import org.example.prac02_back.config.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티의 웹 보안 기능을 이 클래스 설정을 기반으로 활성화
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    // 비밀번호 암호화
    @Bean // 스프링 컨테이너에 관리되는 객체로 등록
    // 비밀번호 암호화 하는 인터페이스
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    // 인증 매니저 실행
    @Bean
    // 스프링 시큐리티 인증 매니저 생성
    // 로그인 컨트롤러에서 아이디와 비밀번호 가 맞는지 실행하면 인증 정보가 들어있는 객체를 생성해 준다.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 핵심 보안 필터
    @Bean
    // 어떤 URL을 막고 어떤 URL을 열어줄지 규칙을 정한다.
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (auth) -> auth
                        // 로그인, 회원가입, 인증번호 확인은 로그인 안 해도 누구나 접근 가능(permitAll)
                        .requestMatchers("/user/login", "/user/signup", "/user/verify").permitAll()
                        // 게시글 등록(/board/reg)은 반드시 인증(로그인)된 사용자만 접근 가능
                        .requestMatchers("/board/reg").authenticated()
                        // 그 외 나머지 요청은 로그인 된 (인증된)상태에서 접근 허용
                        // .anyRequest().authenticated()
                        // 그 외 나머지 모든 요청은 로그인 없이도 일단 허용 (개발 편의상 설정하신 듯합니다)
                        .anyRequest().permitAll()
        );

        // REST API 환경이므로 불필요한 기본 기능들을 끕니다.
        http.csrf(AbstractHttpConfigurer::disable); // CSRF 공격 방어 기능 OFF (토큰 방식에선 보통 끕니다)
        http.httpBasic(AbstractHttpConfigurer::disable); // 브라우저 팝업 로그인창 OFF
        http.formLogin(AbstractHttpConfigurer::disable); // 스프링이 제공하는 기본 로그인 페이지 OFF

        // 핵심 포인트!
        // 사용자가 보낸 요청이 'UsernamePasswordAuthenticationFilter'(기본 아이디/비번 필터)에 도달하기 전에
        // 내가 만든 'jwtFilter'를 먼저 실행해서 토큰이 유효한지 검사합니다.
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // 14. 설정한 내용들을 바탕으로 보안 필터를 빌드하여 반환합니다.
    }
}
