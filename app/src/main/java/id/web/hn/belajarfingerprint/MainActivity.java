package id.web.hn.belajarfingerprint;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView txt;
    private String msg;
    private static final String KEY_NAME = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button);
        txt = (TextView) findViewById(R.id.textView);
        ImageView img = (ImageView) findViewById(R.id.img);

        FingerprintManager.CryptoObject cryptoObject = null;

        final FingerprintHandler fph = new FingerprintHandler(txt, img, this);
        if(!autentikasi()){
            btn.setEnabled(false);
        } else {
            try{
                generateKey();
                Cipher cipher = generateCipher();
                
                cryptoObject = new FingerprintManager.CryptoObject(cipher);


            } catch (FingerprintException e) {
                e.printStackTrace();
            }
        }

        txt.setText(msg);

        final FingerprintManager.CryptoObject finalCryptoObject = cryptoObject;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fph.doAuth(fingerprintManager, finalCryptoObject);

            }
        });
    }

    private boolean autentikasi() {

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                msg = "gimana sih, diijinkan ga sih?";
                return false;
            }else {
                if (!fingerprintManager.isHardwareDetected()) {
                    msg = "Maaf perangkat anda tidak mendukung fingerprint";
                    return false;
                }
                if(!fingerprintManager.hasEnrolledFingerprints()){
                    msg = "Perangkat Fingerprint belum dikonfigurasi";
                    return false;
                }
                if(!keyguardManager.isKeyguardSecure()){
                    msg = "Pengunci telepon tidak aman";
                    return false;
                }
            }
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        msg = "Ada gambar apakah di bawah ini? \nSilakan pindai sidik jari anda";
        return true;
    }

    private void generateKey() throws FingerprintException {
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT
                                    | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | CertificateException
                | IOException
                | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new FingerprintException(e);
        }
    }

    private Cipher generateCipher() throws FingerprintException {
        try{
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (NoSuchPaddingException
                | UnrecoverableKeyException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | KeyStoreException e) {
            e.printStackTrace();
            throw new FingerprintException(e);
        }
    }
}
