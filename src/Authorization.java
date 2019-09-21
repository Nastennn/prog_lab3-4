
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

class Authorization {

    private static final String ENCODING = "UTF-8";
    DatabaseConnect dbConnect = new DatabaseConnect();
    Writer writer = new Writer(dbConnect.getConnection());


    String registration(String email) {
        String password = getPassword();
        String subject = "Регистрация";
        String content = "Ваш пароль: " + password;
        String smtpHost = "mail.sillver.us";
        String from = "Anastasiia";
        //String address = email;
        String login = "Ana";
        String password1 = "123456";
        String smtpPort = "25";
        try {
            sendMessage(login, password1, from, email, content, subject, smtpPort, smtpHost);
            String reply = writer.writeUser(email, getPasswordHash(password));
            return reply + "\nНа Вашу почту было отправлено письмо с паролем. ";
        } catch (MessagingException e) {
            return "Не удалось отправить сообщение.";
        }
    }

    private static void sendMessage(String login, String password, String from, String to, String content, String subject, String smtpPort, String smtpHost) throws MessagingException {
        Authenticator auth = new MyAuthenticator(login, password);
        Properties props = System.getProperties();

        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.mime.charset", ENCODING);

        Session session = Session.getDefaultInstance(props, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress((to)));
        msg.setSubject(subject);
        msg.setText(content);

        Transport.send(msg);
    }

    static class MyAuthenticator extends Authenticator {
        private String user;
        private String password;

        MyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            String user = this.user;
            String password = this.password;
            return new PasswordAuthentication(user, password);
        }
    }

    private String getPassword() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String password = sb.toString();
        //MessageDigest md = MessageDigest.getInstance("SHA-256");
        return password;
    }

    String getPasswordHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, md.digest(password.getBytes(StandardCharsets.UTF_8)));
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Ошибка при сохранении пароля.";
        }
    }

    String loginUser(String email, String password) {
        if (writer.loginUser(email, password)) {
            return "Вход выполнен.";
        } else {
            return "Неверный email или пароль.";
        }
    }
}


