package com.example.voja.master01;

import android.content.Context;
import android.content.Intent;
import android.net.http.X509TrustManagerExtensions;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView myTextView;
    private Certificate[] sviSertifikati;

    private String certificateFile = "";
    private String username =  "";
    private String password = "";
    private String filename = "logfile.log";
    private Encryptor enc = new Encryptor();

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

        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

//todo: Ukljuciti networking on main thread proveru

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // povezi se
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    makeRequest();
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

        // prikazi cert
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

        // izlaz
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

        // ucitaj fajl
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Encryptor enc = new Encryptor();

                try {
                    FileInputStream inputStream = openFileInput(filename);
                    String ciphertext = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    String password = String.valueOf(passwordEditText.getText());
                    String plaintext = enc.decrypt(password, ciphertext);
                    displayExceptionMessage(plaintext);
                } catch (Exception e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });

        // snimi fajl
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {

                    String password = String.valueOf(passwordEditText.getText());
                    String plaintext = myTextView.getText().toString();
//                    Encryptor enc = new Encryptor();
                    FileOutputStream outputStream;

                    try {
                        String cyphertext = enc.encrypt(password, plaintext);
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(cyphertext.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayExceptionMessage(e.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    displayExceptionMessage(e.getMessage());
                }
            }
        });

    }

    public void displayExceptionMessage(String msg) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

    private void validatePinning(X509TrustManagerExtensions trustManagerExt, HttpsURLConnection conn, Set<String> validPins)
        throws SSLException {
            String certChainMsg = "";
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                List<X509Certificate> trustedChain = trustedChain(trustManagerExt, conn);
                for (X509Certificate cert : trustedChain) {
                    byte[] publicKey = cert.getPublicKey().getEncoded();
                    md.update(publicKey, 0, publicKey.length);
                    String pin = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                    certChainMsg += "    sha256/" + pin + " : " + cert.getIssuerDN().toString() + "\n";
                    if (validPins.contains(pin)) {
                        return;
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                throw new SSLException(e);
            }
            throw new SSLPeerUnverifiedException("Certificate pinning failure\n  Peer certificate chain:\n" + certChainMsg);
        }
        private List<X509Certificate> trustedChain(X509TrustManagerExtensions trustManagerExt, HttpsURLConnection conn) throws SSLException {
        Certificate[] serverCerts = conn.getServerCertificates();
        X509Certificate[] untrustedCerts = Arrays.copyOf(serverCerts, serverCerts.length, X509Certificate[].class);
        String host = conn.getURL().getHost();
        try {
            return trustManagerExt.checkServerTrusted(untrustedCerts,"RSA", host);
        } catch (CertificateException e) {
            throw new SSLException(e);
        }
    }

    private void makeRequest() throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, UnrecoverableKeyException {

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        X509TrustManager x509TrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManager;
                break;
            }
        }
        X509TrustManagerExtensions trustManagerExt = new X509TrustManagerExtensions(x509TrustManager);

        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());

        certificateFile = "/sdcard/Download/" + username + ".p12";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream fis = new FileInputStream(certificateFile);

        Intent intent = getIntent();
        String enteredPin = intent.getStringExtra("message");


        keyStore.load(fis, enteredPin.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, enteredPin.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, null, new SecureRandom());


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

        Set<String> validPins = Collections.singleton("PUskp2rQhKX6BOj/+vGPf61uMwE7JM18j/td0wikL3M=");
        validatePinning(trustManagerExt, urlConnection, validPins);

        InputStream inputStream = urlConnection.getErrorStream();
        if (inputStream == null) {
            inputStream = urlConnection.getInputStream();
        }

        String jsonString2 = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String jsonString3 = JsonWriter.formatJson(jsonString2);
        myTextView.setText(jsonString3);

        sviSertifikati = urlConnection.getServerCertificates();
        urlConnection.disconnect();
    }

    private void showCert() throws IOException, JSONException{
        if (sviSertifikati != null) {
            myTextView.setText(sviSertifikati[0].toString());

            Intent intent = new Intent(this, ShowCertActivity.class);
            intent.putExtra("message", sviSertifikati[0].toString());
            startActivity(intent);

        } else {
            displayExceptionMessage("Nema sertifikata");
        }
    }

    private void killIt() throws IOException, JSONException{
        System.exit(0);
    }
}