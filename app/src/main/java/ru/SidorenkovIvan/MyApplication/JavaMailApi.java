package ru.SidorenkovIvan.MyApplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class JavaMailAPI extends AsyncTask<Void,Void,Void>  {
    //Variables
    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    private final String mEmail;
    private final String mSubject;
    private final String mMessage;

    private ProgressDialog mProgressDialog;

    public JavaMailAPI(final Context pContext, final String pEmail, final String pSubject, final String pMessage) {
        mContext = pContext;
        mEmail = pEmail;
        mSubject = pSubject;
        mMessage = pMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Отправка сообщения", "Пожалуйста, подождите...",false,false);
    }

    @Override
    protected void onPostExecute(final Void pVoid) {
        super.onPostExecute(pVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext,"Сообщение отправлено",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(final Void... pParams) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        //Authenticating the password
        Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(Config.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(mSubject);
            //Adding message
            mm.setText(mMessage);
            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Config {
    public static final String EMAIL = "tea4u.by@gmail.com";
    public static final String PASSWORD = "cDeVDen5";
}
