package mgks.os.swv;

/*
 * Smart WebView 7.0 (May 2023)
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT).
 * Please mention project source or developer credits in your Application's License(s) Wiki.
 * Contribute to the project (https://github.com/mgks/Android-SmartWebView/discussions)
 * Sponsor the project (https://github.com/sponsors/mgks)
 * Giving right credits to developers encourages them to keep improving their projects :)
 */

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

public class Firebase extends FirebaseMessagingServices {
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
			sendMyNotification(message.getNotification().getTitle(), message.getNotification().getBody(), message.getNotification().getClickAction(), message.getData().get("uri"), message.getData().get("tag"), message.getData().get("nid"));
		}
	}
	private void sendMyNotification(String title, String message, String click_action, String uri, String tag, String nid) {
		//On click of notification it redirect to this Activity
		Intent intent = new Intent(click_action);
		intent.putExtra("uri", uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent;
		final int flag =  Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_ONE_SHOT;
		pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

		int notification_id = nid!=null ? Integer.parseInt(nid) : fcm_id;

		Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, fcm_channel)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title+" "+notification_id)
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(soundUri)
				.setContentIntent(pendingIntent);
		Notification noti = notificationBuilder.build();
		noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(notification_id, notificationBuilder.build());
	}
}
