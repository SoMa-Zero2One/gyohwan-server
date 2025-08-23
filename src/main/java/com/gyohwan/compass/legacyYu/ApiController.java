package com.gyohwan.compass.legacyYu;

import com.gyohwan.compass.legacyYu.dto.*;
import com.gyohwan.compass.legacyYu.security.UserDetailsImpl;
import com.gyohwan.compass.legacyYu.service.AuthService;
import com.gyohwan.compass.legacyYu.service.UniversityService;
import com.gyohwan.compass.legacyYu.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/yu-legacy")
public class ApiController {

    private final AuthService authService;
    private final UniversityService universityService;
    private final UserService userService;

    // === Authentication =============================================
    @PostMapping("/auth/token")
    public ResponseEntity<LoginResponse> loginForAccessToken(@RequestBody UUIDLoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginWithUuid(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    // === Universities ===============================================
    @GetMapping("/public-universities")
    public ResponseEntity<List<PartnerUniversityInfo>> readPublicUniversities() {
        List<PartnerUniversityInfo> universities = universityService.getUniversities();
        return ResponseEntity.ok(universities);
    }

    @GetMapping("/universities")
    public ResponseEntity<List<PartnerUniversityInfo>> readUniversities(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<PartnerUniversityInfo> universities = universityService.getUniversitiesWithApplicantCount();
        return ResponseEntity.ok(universities);
    }

    @GetMapping("/universities/{universityId}")
    public ResponseEntity<UniversityDetailResponse> readUniversityDetails(
            @PathVariable Long universityId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        UniversityDetailResponse universityDetails = universityService.getUniversityDetails(universityId);
        return ResponseEntity.ok(universityDetails);
    }

    // === Users ======================================================
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> readMe(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        UserResponse userResponse = userService.getUserInfo(currentUser.getId());
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<PublicUserResponse> readUserById(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        PublicUserResponse publicProfile = userService.getPublicUserInfo(userId);
        return ResponseEntity.ok(publicProfile);
    }

    @PutMapping("/users/me/applications")
    public ResponseEntity<BaseResponse> updateMyApplications(
            @RequestBody UpdateApplicationsRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        userService.updateUserApplications(currentUser.getUser(), request.getApplications());
        return ResponseEntity.ok(new BaseResponse(true, "성공적으로 수정되었습니다."));
    }

    // === User Registration ===========================================
    @PostMapping("/users/register")
    public ResponseEntity<UserRegistrationResponse> registerNewUser(
            @RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse response = userService.registerNewUser(request);
        return ResponseEntity.ok(response);
    }

}