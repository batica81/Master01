package com.example.voja.master01;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView myTextView;
    private Certificate[] sviSertifikati;

//    private String certificateFile = Environment.getExternalStorageDirectory().getPath() + "batica+1434@gmail.com.p12";
    private String certificateFile = "/sdcard/Download/batica+1434@gmail.com.p12";
    private String clientCertPassword = "204700";

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
                    displayExceptionMessage(e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (CertificateException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    showCert();
                } catch (IOException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    killIt();
                } catch (IOException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });
    }



    public void displayExceptionMessage(String msg) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

    private void makeSearch() throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream fis = new FileInputStream(certificateFile);
        keyStore.load(fis, clientCertPassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, clientCertPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, null, new SecureRandom());

        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        String myUrl = String.valueOf(mSearchBoxEditText.getText());
        URL url = new URL(myUrl);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        JSONObject myJsonData = new JSONObject();
        myJsonData.put("Username", username);
        myJsonData.put("Password", password);
        String jsonString = myJsonData.toString();

        byte[] outputBytes = jsonString.getBytes("UTF-8");
        OutputStream os = urlConnection.getOutputStream();
        os.write(outputBytes);

        InputStream inputStream = urlConnection.getErrorStream();
        if (inputStream == null) {
            inputStream = urlConnection.getInputStream();
        }

        String jsonString2 = IOUtils.toString(inputStream);
        String jsonString3 = JsonWriter.formatJson(jsonString2);
        myTextView.setText(jsonString3);

        sviSertifikati = urlConnection.getServerCertificates();
        urlConnection.disconnect();
    }

    private void showCert() throws IOException, JSONException{
        if (sviSertifikati != null) {
            myTextView.setText(sviSertifikati[0].toString());
        } else {
            displayExceptionMessage("Nema sertifikata");
        }
    }

    private void killIt() throws IOException, JSONException{
        System.exit(0);
    }
}