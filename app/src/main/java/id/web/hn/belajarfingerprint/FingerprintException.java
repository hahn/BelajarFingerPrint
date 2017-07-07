package id.web.hn.belajarfingerprint;

/**
 * Created by hahn on 07.07.17.
 * Project: BelajarFingerPrint
 */

class FingerprintException extends Exception {


    public FingerprintException(Exception e) {
        e.printStackTrace();
    }
}
