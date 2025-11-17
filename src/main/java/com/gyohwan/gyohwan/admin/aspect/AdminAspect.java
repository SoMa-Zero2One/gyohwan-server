package com.gyohwan.gyohwan.admin.aspect;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @AdminOnly 어노테이션이 붙은 메서드의 관리자 권한을 체크하는 Aspect
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAspect {

    private final UserRepository userRepository;

    @Before("@annotation(com.gyohwan.gyohwan.admin.annotation.AdminOnly)")
    public void checkAdminPermission() {
        // SecurityContext에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getIsAdmin())) {
            log.warn("Admin access denied. UserId: {}", userId);
            throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        log.info("Admin access granted. UserId: {}", userId);
    }
}

