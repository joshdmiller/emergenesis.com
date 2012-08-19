package controllers

import java.util.Properties
import javax.mail

case class Message(email: String, name: String, subject: String, message: String)

object Email extends Environmentalist {

  def send(msg: Message) = {
    val host = "email-smtp.us-east-1.amazonaws.com"
    val user = environment("SMTP_USER")
    val pass = environment("SMTP_PASS")
    val to   = new mail.internet.InternetAddress("josh.miller@emergenesis.com")
    val from = "no-reply@emergenesis.com"
    
    val props: Properties = System.getProperties()
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.user", user)
    props.put("mail.smtp.password", pass)
    props.put("mail.smtp.port", "587")
    props.put("mail.smtp.auth", "true")

    val session = mail.Session.getDefaultInstance(props, null)
    val transport = session.getTransport("smtp");
    val message = new mail.internet.MimeMessage(session)
    
    message.setFrom(new mail.internet.InternetAddress(from))
    message.addRecipient(mail.Message.RecipientType.TO, to);
    message.setSubject(msg.subject)
    message.setText(msg.message)
    
    transport.connect(host, user, pass)
    transport.sendMessage(message, message.getAllRecipients())
    transport.close()
  }
}
