package com.khajastation.api.user.password;

import com.khajastation.api.common.exception.BadRequestException;
import com.khajastation.api.common.exception.TokenExpiredException;
import com.khajastation.api.entity.Member;
import com.khajastation.api.entity.ResetPassword;
import com.khajastation.api.entity.User;
import com.khajastation.api.security.UserPrincipal;
import com.khajastation.api.user.member.MemberService;
import com.khajastation.api.user.verification.VerificationResponse;
import com.khajastation.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private MemberService senderService;

    @Autowired
    private PasswordMailService passwordMailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResetPassword create(Member sender) {
        ResetPassword previousRequest =
                resetPasswordRepository.findByUserIdAndExpiryDateIsAfter(sender.getId(), LocalDateTime.now());
        if (previousRequest != null) {
            previousRequest.setExpiryDate(LocalDateTime.now());
        }

        ResetPassword resetPassword = new ResetPassword();

        resetPassword.setUser(sender);
        resetPassword.setToken(UUID.randomUUID().toString());
        resetPassword.setExpiryDate(LocalDateTime.now().plusMinutes(30L));
        resetPassword.setResetAttempt(0);
        return resetPasswordRepository.save(resetPassword);
    }

    public void sendResetPasswordMail(String email) {
        Member sender = senderService.findByEmail(email);
        ResetPassword resetPassword = create(sender);
        passwordMailService.sendResetPasswordMail(sender, resetPassword.getToken());
    }

    public VerificationResponse resetPassword(String recoveryToken, String newPassword) {
        ResetPassword resetPassword = resetPasswordRepository.findByToken(recoveryToken);
        VerificationResponse verificationResponse = new VerificationResponse();

        if (resetPassword == null) {
            throw new TokenExpiredException("Invalid password reset link. Please send a new request.");
        }
        if (resetPassword.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Your password reset link has expired. Please send a new request.");
        }

        int resetAttempt = resetPassword.getResetAttempt() + 1;
        resetPassword.setResetAttempt(resetAttempt);
        User user = resetPassword.getUser();
        userService.resetPassword(user, newPassword);
        resetPassword.setExpiryDate(LocalDateTime.now());
        resetPasswordRepository.save(resetPassword);

        verificationResponse.setStatus(true);
        verificationResponse.setMessage("Your password has been reset. Please sign in");

        return verificationResponse;
    }

    public VerificationResponse resetPassword(UserPrincipal userPrincipal, String newPassword,
                                              String oldPassword) {
        VerificationResponse verificationResponse = new VerificationResponse();
        User user = userService.findById(userPrincipal.getId());
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Your old password does not match");
        }
        userService.resetPassword(user, newPassword);

        verificationResponse.setStatus(true);
        verificationResponse.setMessage("Your password has been reset");

        return verificationResponse;
    }
}
