package com.gyohwan.compass.legacyYu.service;

import com.gyohwan.compass.domain.User;
import com.gyohwan.compass.legacyYu.security.UserDetailsImpl;
import com.gyohwan.compass.repository.UserRepository;
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

    /**
     * Spring Security가 인증 과정에서 호출하는 메서드입니다.
     * @param username JWT에서 추출한 사용자의 UUID가 이 파라미터로 전달됩니다.
     * @return UserDetails - Spring Security가 사용할 사용자 정보 (우리가 만든 UserDetailsImpl)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 던져지는 표준 예외
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 전달받은 username(uuid)을 이용해 UserRepository에서 사용자 정보를 조회합니다.
        User user = userRepository.findByUuid(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 UUID를 가진 사용자를 찾을 수 없습니다: " + username));

        // 조회된 User 엔티티를 UserDetailsImpl 객체로 감싸서 반환합니다.
        return new UserDetailsImpl(user);
    }
}