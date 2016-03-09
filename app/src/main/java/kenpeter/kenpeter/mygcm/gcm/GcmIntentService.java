package kenpeter.kenpeter.mygcm.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import kenpeter.kenpeter.mygcm.R;
import kenpeter.kenpeter.mygcm.app.Config;

// 1. Connect to GCM and get the token
// 2. sub to topic
// 3. unsub to topic
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(TAG);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";


    // Like reactjs switch
    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(KEY);
        switch (key) {
            case SUBSCRIBE:
                // subscribe to a topic
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;
            case UNSUBSCRIBE:
                String topic1 = intent.getStringExtra(TOPIC);
                unsubscribeFromTopic(topic1);
                break;
            default:
                // if key is not specified, register with GCM
                registerGCM();
        }

    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    // This is the sharedPreference data
    // private static final String KEY_USER_ID = "user_id";
    // private static final String KEY_USER_NAME = "user_name";
    // private static final String KEY_USER_EMAIL = "user_email";
    // private static final String KEY_NOTIFICATIONS = "notifications";
    private void registerGCM() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        try {
            // App ID
            // instanceID can get token from services
            InstanceID instanceID = InstanceID.getInstance(this);
            // https://stackoverflow.com/questions/30694817/gcm-defaultsenderid
            // What is R.string.gcm_defaultSenderId? It resolves from project ID?
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.e(TAG, "GCM Registration Token: " + token);

            // sending the registration id to our server
            // After we grab the tocken from GCM, we store it in web server
            sendRegistrationToServer(token);

            // Update the internal sharePreferences, indicated it is sent.
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        // Build the complete message
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        // Put token into complete message
        registrationComplete.putExtra("token", token);
        // Broadcast the message to the phone.
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // Send the registration token to our server
        // to keep it in MySQL

    }

    /**
     * Subscribe to a topic
     */
    // Who pass the topic
    public void subscribeToTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Log.e(TAG, "Subscribed to topic: " + topic);
            } else {
                Log.e(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeFromTopic(String topic) {
        // https://developers.google.com/android/reference/com/google/android/gms/gcm/GcmPubSub
        // GcmPubSub is for GCM message
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());

        // Always need instance ID, then can get token
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());

        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                // Unsub to toke, then it unsub to topic
                pubSub.unsubscribe(token, "");

                Log.e(TAG, "Unsubscribed from topic: " + topic);
            } else {
                Log.e(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}