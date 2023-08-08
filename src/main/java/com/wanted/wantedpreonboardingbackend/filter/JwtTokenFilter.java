package com.wanted.wantedpreonboardingbackend.filter;

import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import com.wanted.wantedpreonboardingbackend.service.UserService;
import com.wanted.wantedpreonboardingbackend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final String secretKey;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String authroizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // authorizationHeader에 "Bearer + JwtToken"이 제대로 들어왔는지 체크
            if(authroizationHeader == null || !authroizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authroizationHeader.split(" ")[1];

            // 토큰이 만료되었는지 check
            if(JwtTokenUtil.isExpired(token, secretKey)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Token에서 userEmail 꺼내기
            String userEmail = JwtTokenUtil.getEmail(token, secretKey);

            // userEmail로 User 찾아오기
            User loginUser = userRepository.findByEmail(userEmail).get();

            // 권한을 주거나 안주기
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginUser.getEmail(), null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 권한 부여
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            filterChain.doFilter(request, response);
        }
    }
}