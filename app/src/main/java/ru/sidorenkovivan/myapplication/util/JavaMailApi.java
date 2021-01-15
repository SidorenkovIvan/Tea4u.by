package ru.sidorenkovivan.myapplication.util;

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

import ru.sidorenkovivan.myapplication.BuildConfig;

public class JavaMailApi extends AsyncTask<Void,Void,Void>  {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final String mEmail;
    private final String mSubject;
    private final String mMessage;

    private ProgressDialog mProgressDialog;

    public JavaMailApi(final Context pContext, final String pEmail, final String pSubject, final String pMessage) {
        mContext = pContext;
        mEmail = pEmail;
        mSubject = pSubject;
        mMessage = pMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (BuildConfig.DEBUG) {
            mProgressDialog = ProgressDialog.show(mContext, "Отправка сообщения", "Пожалуйста, подождите...", false, false);
        }
    }

    @Override
    protected void onPostExecute(final Void pVoid) {
        super.onPostExecute(pVoid);

        mProgressDialog.dismiss();

        if (BuildConfig.DEBUG) {
            Toast.makeText(mContext, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Void doInBackground(final Void... pParams) {
        final Properties props = new Properties();

        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        final Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            final MimeMessage message = new MimeMessage(mSession);

            message.setFrom(new InternetAddress(Config.EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            message.setSubject(mSubject);
            message.setText(mMessage);
            Transport.send(message);
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
