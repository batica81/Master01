package com.example.voja.master01;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView myTextView;
    private Certificate[] sviSertifikati;

    private String username =  "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.editText01);
        usernameEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
        myTextView = (TextView) findViewById(R.id.tv_url_display);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

//todo: Ukljuciti networking on main thread proveru

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    makeSearch();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    showCert();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    killIt();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void makeSearch() throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustManagers = new TrustManager[] { new TrustManagerManipulator() };        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory noSSLv3Factory = new TLSSocketFactory(sslContext.getSocketFactory());


        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        String myUrl = String.valueOf(mSearchBoxEditText.getText());
        URL url = new URL(myUrl);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setSSLSocketFactory(noSSLv3Factory);

        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        JSONObject myJsonData = new JSONObject();
        myJsonData.put("Username", username);
        myJsonData.put("Password", password);
        String jsonString = myJsonData.toString();

        byte[] outputBytes = jsonString.getBytes("UTF-8");
        OutputStream os = urlConnection.getOutputStream();
        os.write(outputBytes);

        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String jsonString2 = IOUtils.toString(in);
        String jsonString3 = JsonWriter.formatJson(jsonString2);
        myTextView.setText(jsonString3);

        sviSertifikati = urlConnection.getServerCertificates();
        urlConnection.disconnect();
    }

    private void showCert() throws IOException, JSONException{
        myTextView.setText(sviSertifikati[0].toString());
    }

    private void killIt() throws IOException, JSONException{
        System.exit(0);
    }
}