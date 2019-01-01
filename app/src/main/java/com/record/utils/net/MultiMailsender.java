package com.record.utils.net;

import com.record.utils.MyAuthenticator;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MultiMailsender {

    public static class MultiMailSenderInfo extends MailSenderInfo {
        private String[] ccs;
        private String[] receivers;

        public String[] getCcs() {
            return this.ccs;
        }

        public void setCcs(String[] ccs) {
            this.ccs = ccs;
        }

        public String[] getReceivers() {
            return this.receivers;
        }

        public void setReceivers(String[] receivers) {
            this.receivers = receivers;
        }
    }

    public boolean sendTextMail(MultiMailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        try {
            Address[] tos;
            Message mailMessage = new MimeMessage(Session.getDefaultInstance(pro, authenticator));
            mailMessage.setFrom(new InternetAddress(mailInfo.getFromAddress()));
            String[] receivers = mailInfo.getReceivers();
            if (receivers != null) {
                tos = new InternetAddress[(receivers.length + 1)];
                tos[0] = new InternetAddress(mailInfo.getToAddress());
                for (int i = 0; i < receivers.length; i++) {
                    tos[i + 1] = new InternetAddress(receivers[i]);
                }
            } else {
                tos = new InternetAddress[]{new InternetAddress(mailInfo.getToAddress())};
            }
            mailMessage.setRecipients(RecipientType.TO, tos);
            mailMessage.setSubject(mailInfo.getSubject());
            mailMessage.setSentDate(new Date());
            mailMessage.setText(mailInfo.getContent());
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean sendMailtoMultiReceiver(MultiMailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        try {
            Address[] tos;
            Message mailMessage = new MimeMessage(Session.getInstance(mailInfo.getProperties(), authenticator));
            mailMessage.setFrom(new InternetAddress(mailInfo.getFromAddress()));
            String[] receivers = mailInfo.getReceivers();
            if (receivers != null) {
                tos = new InternetAddress[(receivers.length + 1)];
                tos[0] = new InternetAddress(mailInfo.getToAddress());
                for (int i = 0; i < receivers.length; i++) {
                    tos[i + 1] = new InternetAddress(receivers[i]);
                }
            } else {
                tos = new InternetAddress[]{new InternetAddress(mailInfo.getToAddress())};
            }
            mailMessage.setRecipients(RecipientType.TO, tos);
            mailMessage.setSubject(mailInfo.getSubject());
            mailMessage.setSentDate(new Date());
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(mailInfo.getContent(), "text/html; charset=GBK");
            mainPart.addBodyPart(html);
            mailMessage.setContent(mainPart);
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean sendMailtoMultiCC(MultiMailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        try {
            Message mailMessage = new MimeMessage(Session.getInstance(mailInfo.getProperties(), authenticator));
            mailMessage.setFrom(new InternetAddress(mailInfo.getFromAddress()));
            mailMessage.setRecipient(RecipientType.TO, new InternetAddress(mailInfo.getToAddress()));
            String[] ccs = mailInfo.getCcs();
            if (ccs != null) {
                Address[] ccAdresses = new InternetAddress[ccs.length];
                for (int i = 0; i < ccs.length; i++) {
                    ccAdresses[i] = new InternetAddress(ccs[i]);
                }
                mailMessage.setRecipients(RecipientType.CC, ccAdresses);
            }
            mailMessage.setSubject(mailInfo.getSubject());
            mailMessage.setSentDate(new Date());
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(mailInfo.getContent(), "text/html; charset=GBK");
            mainPart.addBodyPart(html);
            mailMessage.setContent(mainPart);
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
