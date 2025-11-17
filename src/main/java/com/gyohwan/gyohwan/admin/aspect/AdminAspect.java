package com.gyohwan.gyohwan.admin.aspect;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.security.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
        Long userId = AuthenticationUtil.getCurrentUserId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getIsAdmin())) {
            log.warn("Admin access denied. UserId: {}", userId);
            throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        log.info("Admin access granted. UserId: {}", userId);
    }
}

