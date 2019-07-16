package mgks.os.webview;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.iid.FirebaseInstanceIdService;

public class FBServices extends FirebaseMessagingService {
	public void onTokenRefresh() {
		//For registration of token
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		//To displaying token on logcat
		Log.d("TOKEN REFRESHED: ", refreshedToken);
	}
}
