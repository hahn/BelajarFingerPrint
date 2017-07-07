package id.web.hn.belajarfingerprint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hahn on 07.07.17.
 * Project: BelajarFingerPrint
 */

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private TextView tv;
    private ImageView iv;
    private Context _ctx;

    public FingerprintHandler(TextView tv, ImageView iv, Context ctx) {
        this.tv = tv;
        this.iv = iv;
        this._ctx = ctx;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        tv.setText(String.format("Error, Code: %d string: %s", errorCode, errString));
        iv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        tv.setText("Pemindaian berhasil!");
        iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        tv.setText("Pemindaian gagal");
    }

    public void doAuth(FingerprintManager manager, FingerprintManager.CryptoObject obj) {
        CancellationSignal signal = new CancellationSignal();

        try {
            if (ActivityCompat.checkSelfPermission(_ctx, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                manager.authenticate(obj, signal, 0, this, null);
            }
        } catch (SecurityException e){
            Log.d("FINGER", "ada exception");
            e.printStackTrace();
        }
    }
}
