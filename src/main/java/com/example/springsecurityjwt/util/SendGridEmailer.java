package com.example.springsecurityjwt.util;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.Data;

import java.io.IOException;

@Data
public class SendGridEmailer{

    private final String key = "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";

    private Email from = new Email("mountogether@gmail.com");

    private Mail mail;

    private static SendGridEmailer emailer;

    public static SendGridEmailer getInstance(){
        if(emailer == null){
            emailer = new SendGridEmailer();
        }
        return emailer;
    }

    private SendGridEmailer(){
    }

    public void sendEmail(String subject , String toEmail, String context) throws IOException {
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", context);
        this.mail = new Mail(from, subject, to ,content);
        SendGrid sg = new SendGrid(key);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}
