/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managers;

import java.awt.HeadlessException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;
/**
 *
 * @author MAYANK
 */

public class MailManager {
    public static final int ISSUEMESSAGE = 0;
    public static final int RETURNMESSAGE = 1;
    public static void sendMail(String from,String to,String messageAttribute,int type)
    {

        String host = "localhost";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session mySession = Session.getInstance(props, new Authenticator(){
	
	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(Config.sessionEmail,Config.sessionPassword);
		
	}
        });

try
{
	MimeMessage message = new MimeMessage(mySession);
	message.setFrom(new InternetAddress(from));
	message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	message.setSubject("NIT Delhi Library");
	
        message.setText(messageAttribute);
	Transport.send(message);
	   JOptionPane.showMessageDialog(null,"E-Mail sent successfully","E-Mail sending status",JOptionPane.INFORMATION_MESSAGE);
	
}catch( HeadlessException | MessagingException e)
{
	e.printStackTrace();
	   com.nitdlibrary.Error.errorDialog(e.getMessage());
}
    }
}
