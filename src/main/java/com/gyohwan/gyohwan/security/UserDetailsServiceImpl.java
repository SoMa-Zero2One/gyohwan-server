package com.gyohwan.gyohwan.security;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Spring이 이 클래스를 서비스 Bean으로 인식하도록 합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String strUserId) throws UsernameNotFoundException {
        // 전달받은 username(uuid)을 이용해 UserRepository에서 사용자 정보를 조회합니다.
        Long userId = Long.valueOf(strUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID를 가진 사용자를 찾을 수 없습니다: " + userId));

        // 조회된 User 엔티티를 UserDetailsImpl 객체로 감싸서 반환합니다.
        return new UserDetailsImpl(user);
    }
}