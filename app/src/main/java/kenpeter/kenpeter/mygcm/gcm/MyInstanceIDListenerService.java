package kenpeter.kenpeter.mygcm.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

// If the GCM token, which mean it may not be GCM token, it can be others.
// Intent, GcmIntentService.class
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = MyInstanceIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        Log.e(TAG, "onTokenRefresh");
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        // this is context, intent is the messenger, GcmIntentService.class is the person to talk to
        Intent intent = new Intent(this, GcmIntentService.class);
        startService(intent);
    }
}