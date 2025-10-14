package com.gyohwan.gyohwan.common.controller;

import com.gyohwan.gyohwan.common.dto.*;
import com.gyohwan.gyohwan.common.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/me/gpas")
    public GpaResponse createMyGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateGpaRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.createGpa(userId, request);
    }

    @GetMapping("/me/languages")
    public UserLanguageResponse getMyLanguages(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUserLanguages(userId);
    }

    @PostMapping("/me/languages")
    public LanguageResponse createMyLanguage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateLanguageRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.createLanguage(userId, request);
    }
}
