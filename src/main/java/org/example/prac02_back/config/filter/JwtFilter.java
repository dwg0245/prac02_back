package org.example.prac02_back.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.prac02_back.user.model.AuthUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.example.prac02_back.utils.JwtUtil;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 1. 사용자가 요청한 주소(Path)를 가져와서 path라는 변수에 저장합니다.
        // 예: 사용자가 /user/login 으로 접속했다면 "/user/login"이 담깁니다.
        String path = request.getServletPath();

        // 2. 만약 접속한 주소가 아래 3가지 중 하나로 '시작'한다면 true를 반환(리턴)합니다.
        // return true의 의미: "이 요청은 필터 검사(토큰 확인)를 하지 말고 통과시켜라!"
        return path.startsWith("/user/login") ||   // 로그인 주소거나
                path.startsWith("/user/signup") || // 회원가입 주소거나
                path.startsWith("/user/verify");   // 인증 주소라면 프리패스!
    }


    // 위에서 false가 반환된 경우 토큰을 확인한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 브라우저가 보낸 쿠키들이 있는지 확인합니다. (비어있으면 검사할 게 없으니 넘어감)
        if (request.getCookies() != null) {

            // 여러 개의 쿠키 중에서 하나씩 꺼내어 반복문으로 조사합니다.
            for (Cookie cookie : request.getCookies()) {

                // 쿠키의 이름이 우리가 정한 "ATOKEN"(신분증 이름)인지 확인합니다.
                if (cookie.getName().equals("ATOKEN")) {

                    // JwtUtil 도구를 사용해서 암호화된 토큰(cookie.getValue()) 속 정보를 꺼냅니다.
                    // 회원번호(idx), 아이디(username), 권한(role)을 추출합니다.
                    Long idx = JwtUtil.getUserIdx(cookie.getValue());
                    String username = JwtUtil.getUsername(cookie.getValue());
                    String role = JwtUtil.getRole(cookie.getValue());

                    // 꺼낸 정보들을 'AuthUserDetails'라는 가방에 차곡차곡 담습니다.
                    // 이 가방은 나중에 컨트롤러에서 "지금 로그인한 애 누구야?"라고 물을 때 사용됩니다.
                    AuthUserDetails user = AuthUserDetails.builder()
                            .idx(idx)
                            .username(username)
                            .role(role)
                            .build();

                    // 스프링 시큐리티가 이해할 수 있는 '최종 신분증(Authentication)' 객체를 만듭니다.
                    // (위에서 만든 유저 가방, 비밀번호는 없으니 null, 권한 목록을 넣어줍니다.)
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

                    // [가장 중요] 결재 서류함(SecurityContextHolder)에 이 신분증을 꽂아 넣습니다.
                    // 이제부터 이 요청은 "인증된 사용자"가 되어 모든 관문을 프리패스하게 됩니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // "내 검사는 끝났으니 다음 필터로 가세요!"라고 요청을 다음 단계로 넘겨줍니다.
        filterChain.doFilter(request, response);
    }
}
