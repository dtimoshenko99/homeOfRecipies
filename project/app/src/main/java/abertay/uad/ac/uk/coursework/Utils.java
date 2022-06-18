package abertay.uad.ac.uk.coursework;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utils {

    public static final String TAG = "lab8debug";

    public static boolean checkAllPermissions(Context c, String[] permissions){
        Boolean result = false;
        for (String p:permissions) {
            result &= (c.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }
}