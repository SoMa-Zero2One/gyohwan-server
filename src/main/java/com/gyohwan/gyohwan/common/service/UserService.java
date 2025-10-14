package com.gyohwan.gyohwan.common.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.dto.MyUserResponse;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MyUserResponse findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return MyUserResponse.from(user);
    }
}
