package com.gyohwan.gyohwan.security;

import com.gyohwan.gyohwan.common.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환 (예: "ROLE_USER", "ROLE_ADMIN")
        // 현재 시스템에 권한 구분이 없다면 기본값으로 설정
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // UUID 기반 로그인이라 비밀번호를 사용하지 않으므로 null 또는 빈 문자열 반환
        return null;
    }

    @Override
    public String getUsername() {
        // Spring Security에서 사용자를 식별하는 주요 ID. UUID를 사용하므로 user.getUuid()를 반환
        return user.getUuid();
    }

    // 계정 상태 관련 메서드들 (특별한 로직이 없다면 모두 true를 반환)
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호 등)이 만료되지 않았음
    }


    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되어 있음
    }
}