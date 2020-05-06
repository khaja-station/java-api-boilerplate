package com.khajastation.api.user.auth;

import com.khajastation.api.common.enums.ContactType;
import com.khajastation.api.redis.AuthTokenService;
import com.khajastation.api.security.CurrentUser;
import com.khajastation.api.security.UserPrincipal;
import com.khajastation.api.user.auth.dto.AccessTokenRequest;
import com.khajastation.api.user.auth.dto.AccessTokenResponse;
import com.khajastation.api.user.auth.dto.AuthResponse;
import com.khajastation.api.user.auth.dto.LoginRequest;
import com.khajastation.api.user.auth.dto.Oauth2SignupRequest;
import com.khajastation.api.user.auth.dto.SignUpRequest;
import com.khajastation.api.user.member.dto.MemberResponse;
import com.khajastation.api.user.password.ForgotPasswordRequest;
import com.khajastation.api.user.password.PasswordService;
import com.khajastation.api.user.password.ResetPasswordRequest;
import com.khajastation.api.user.verification.ContactVerificationService;
import com.khajastation.api.user.verification.VerificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private ContactVerificationService contactVerificationService;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/signin")
    public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/users")
    public AuthResponse registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @GetMapping("/verify/{type}/{token}")
    public void verify(@CurrentUser UserPrincipal userPrincipal,
                       @PathVariable("type") ContactType contactType, @PathVariable("token") String token) {
        contactVerificationService.verifyToken(userPrincipal.getId(), token, contactType);
    }

    @GetMapping("/resend-verification/{type}")
    @PreAuthorize("hasRole('USER')")
    public void resendVerification(@CurrentUser UserPrincipal userPrincipal,
                                   @PathVariable("type") ContactType contactType) {
        contactVerificationService.createDeviceVerification(userPrincipal.getId(), contactType);
    }

    @GetMapping("/guest")
    @PreAuthorize("hasRole('READ')")
    public MemberResponse getGuestInfo(@CurrentUser UserPrincipal userPrincipal) {
        return authService.getGuestInfo(userPrincipal.getId());
    }

    @PostMapping("/oauth")
    @PreAuthorize("hasRole('GUEST')")
    public MemberResponse completeOauth2Signup(@Valid @RequestBody Oauth2SignupRequest oauth2SignupRequest,
                                               @CurrentUser UserPrincipal userPrincipal) {
        return authService.completeOauth2SignUp(oauth2SignupRequest, userPrincipal);
    }

    @GetMapping("/signout")
    @PreAuthorize("hasRole('READ')")
    public void logOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String referenceToken = authHeader.replace("Bearer", "").trim();
            authTokenService.deleteAuthTokenByReferenceToken(referenceToken);
        }
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        passwordService.sendResetPasswordMail(forgotPasswordRequest.getEmail());
    }

    @PutMapping("/reset-password/{recoveryToken}")
    public VerificationResponse resetPassword(@PathVariable String recoveryToken,
                                              @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        return passwordService.resetPassword(recoveryToken, resetPasswordRequest.getNewPassword());
    }

    @PutMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public VerificationResponse resetLoggedInUserPassword(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        return passwordService.resetPassword(userPrincipal, resetPasswordRequest.getNewPassword(),
                resetPasswordRequest.getOldPassword());
    }

    @PostMapping("/token")
    public AccessTokenResponse refreshAccessToken(@Valid @RequestBody AccessTokenRequest accessTokenRequest) {
        return authService.refreshAccessToken(accessTokenRequest);
    }
}