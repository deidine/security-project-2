package com.example.springsocial.service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String userEmail, String confirmationToken){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("cheigeurdeidine@gmail.com");// input the senders email ID 
        
        mailMessage.setTo(userEmail);
        // genrateOTPAndSendOnMobile(49619609);
        mailMessage.setSubject("Account Activation!");
        mailMessage.setText("To confirm your account, please click here : "
        +"https://localhost:8080/auth/confirm-account?token="+ confirmationToken 
        + "   Note: This link will expire after 10 minutes.");
        javaMailSender.send(mailMessage);
    }
    
    public boolean sendSimpleMail(String to, String sub, String body){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(sub);
        mailMessage.setText(body);
        Boolean isSent = false;
        try
        {
            javaMailSender.send(mailMessage);
            isSent = true;
        }
        catch (Exception e) {}
        
        return isSent;
    }



 
	public String genrateOTPAndSendOnMobile(int phone ) {
		int otp =  (int) (Math.random() * 9000) + 1000;
        // GAR2N3B5MAZFBSBGTYTT5QEX
        Twilio.init("AC62214e44e31689ded90ae6aff2a83e27", "3e2db3dfdf46aa89a943abee4ab2a744");
		Message message = Message.creator(new PhoneNumber("+222"+phone), new PhoneNumber("+18562812754"),
         "This is the OTP sent for verification"+" "+ otp+ ".Please verify!")
                .create();
		if(message.getErrorCode() == null)
		return "success";
		else
		return "error";
		
	}
}