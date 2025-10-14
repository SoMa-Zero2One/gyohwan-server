package com.gyohwan.gyohwan.common.controller;

import com.gyohwan.gyohwan.common.dto.MyUserResponse;
import com.gyohwan.gyohwan.common.dto.UserGpaResponse;
import com.gyohwan.gyohwan.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public MyUserResponse getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUser(userId);
    }

    @GetMapping("/me/gpas")
    public UserGpaResponse getMyGpas(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUserGpas(userId);
    }
}
