package onipractice.mahmoud.com.fitnessapp.CloudMessaging;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseInstanceIdService extends FirebaseMessagingService {

    public static final String REG_TOKEN = "REG_TOKEN";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

    }
}
