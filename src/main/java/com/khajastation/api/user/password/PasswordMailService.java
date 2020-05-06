package com.khajastation.api.user.password;

import com.khajastation.api.common.Constants;
import com.khajastation.api.config.MailConfig;
import com.khajastation.api.entity.Member;
import com.khajastation.api.mail.MailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordMailService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordMailService.class);
    private final Configuration template;
    private final MailConfig mailConfig;
    @Autowired
    private MailService mailService;

    @Autowired
    PasswordMailService(MailConfig mailConfig, Configuration template) {
        this.mailConfig = mailConfig;
        this.template = template;
    }

    @Async
    public void sendResetPasswordMail(Member sender, String verificationCode) {
        try {
            String subject = Constants.RESET_PASS;
            Map<String, String> map = new HashMap<>();
            Template t = template.getTemplate("reset-password.ftl");
            map.put("USER_NAME", sender.getFirstName());
            map.put("BASE_URL", mailConfig.getBaseUrl());
            map.put("RESET_PASSWORD_URL", mailConfig.getResetPasswordApi() + verificationCode);
            String body = FreeMarkerTemplateUtils.processTemplateIntoString(t, map);
            mailService.sendMail(sender.getEmail(), subject, body);
        } catch (Exception ex) {
            logger.error("Error while sending mail to email: {}", sender.getEmail(), ex);
        }
    }
}
