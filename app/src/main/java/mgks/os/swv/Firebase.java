package mgks.os.swv;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

public class Firebase extends FirebaseMessagingService {
	private Context appContext;
	public Firebase() { // default constructor (no arguments)
	}
	public Firebase(Context context) { // context constructor
		this.appContext = context;
	}

	private final int fcm_id = SmartWebView.ASWV_FCM_ID;
	private final String fcm_channel = SmartWebView.asw_fcm_channel;

	public void onNewToken(String s) {
		super.onNewToken(s);
		if (!s.isEmpty()) {
			Log.d("TOKEN_REFRESHED ", s);        // printing new tokens in logcat
		}
	}
	public void onMessageReceived(RemoteMessage message) {
		if (message.getNotification() != null) {
			String uri = message.getData().get("uri");
			String click_action = message.getNotification().getClickAction();
			if (uri == null) {
				uri = SmartWebView.ASWV_URL; // Set a default URI (your app's main URL) if it's missing in the notification data
			}
			if(click_action==null){
				click_action = "OPEN_URI";
			}
			sendMyNotification(message.getNotification().getTitle(), message.getNotification().getBody(), click_action, uri, message.getData().get("tag"), message.getData().get("nid"));
		}
	}
	public void sendMyNotification(String title, String message, String click_action, String uri, String tag, String nid) {
		Intent intent;
		if (uri == null || uri.isEmpty() || uri.startsWith("file://")) { //Check for empty, null, or file://
			intent = new Intent(appContext, MainActivity.class); // open MainActivity for these cases
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri)); // use the provided URI for other cases
		}
		if(click_action == null) {
			click_action = "OPEN_URI"; // default click action
		}
		intent.setAction(click_action); // Set click action to intent
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Add flags

		final int flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0);
		@SuppressLint("UnspecifiedImmutableFlag")
		PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, intent, flag);

		int notification_id = nid!=null ? Integer.parseInt(nid) : fcm_id;

		Uri sound_uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(appContext, fcm_channel)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title+" "+notification_id)
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(sound_uri)
				.setContentIntent(pendingIntent);
		Notification notification_builder = notificationBuilder.build();
		notification_builder.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notification_id, notification_builder);
	}
}
