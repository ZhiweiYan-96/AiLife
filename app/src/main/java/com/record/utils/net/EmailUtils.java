package com.record.utils.net;

import com.record.utils.db.DbUtils;
import com.record.utils.net.MultiMailsender.MultiMailSenderInfo;

public class EmailUtils {
    public static boolean send(String title, String content) {
        try {
            MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
            mailInfo.setMailServerHost("smtp.163.com");
            mailInfo.setMailServerPort("25");
            mailInfo.setValidate(true);
            mailInfo.setUserName("ebmgxo1003@163.com");
            mailInfo.setPassword("90-+-+-+");
            mailInfo.setFromAddress("ebmgxo1003@163.com");
            mailInfo.setToAddress("itodayss@163.com");
            mailInfo.setSubject(title);
            mailInfo.setContent(content);
            new MultiMailsender().sendTextMail(mailInfo);
            return true;
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            return false;
        }
    }
}
