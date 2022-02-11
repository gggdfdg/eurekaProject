package com.ming.eureka.model.service.sysuser;

import com.ming.eureka.model.entity.MailMessage;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * 邮件发送服务类
 *
 * @author lll
 */
@Service
@Slf4j
public class MailService {

    private static final String DEFAULT_ENCODING = "utf-8";
    private @Autowired
    JavaMailSender mailSender;

    private Template template;
    private @Autowired
    FreeMarkerConfigurer configurer;

    @Value("${spring.mail.username}")
    private String senderUsername;

    /**
     * 注入Freemarker引擎配置,构造Freemarker 邮件内容模板.
     */
    @PostConstruct
    @SneakyThrows
    public void initTemplate() {
        this.template = this.configurer.getConfiguration().getTemplate("deviceState.ftl");
    }

    /**
     * 发送邮件
     *
     * @param message
     * @return
     */
    public boolean send(MailMessage message) {

        if (!StringUtils.contains(senderUsername, "@") || CollectionUtils.isEmpty(message.getRecvMails())) {
            return false;
        }

        try {
            // 设置收件人，寄件人
            String nick = javax.mail.internet.MimeUtility.encodeText("大白数字营销云平台");
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);
            helper.setFrom(new InternetAddress(nick + " <" + senderUsername + ">"));
            for (String address : message.getRecvMails()) {
                helper.addTo(address);
            }
            helper.setSubject(message.getSubject());

            String content = generateContent(message);
            helper.setText(content, true);

            mailSender.send(msg);
            log.info("邮件已发送至{}", message.getRecvMails());
            return true;
        } catch (MessagingException e) {
            log.error("构造邮件失败", e);
            return false;
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return false;
        }
    }

    /**
     * 使用Freemarker生成html格式内容.
     */
    private String generateContent(MailMessage message) throws MessagingException {
        try {
            Map context = Collections.singletonMap("message", message);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
        } catch (IOException e) {
            log.error("生成邮件内容失败, FreeMarker模板不存在", e);
            throw new MessagingException("FreeMarker模板不存在", e);
        } catch (TemplateException e) {
            log.error("生成邮件内容失败, FreeMarker处理失败", e);
            throw new MessagingException("FreeMarker处理失败", e);
        }
    }
}
