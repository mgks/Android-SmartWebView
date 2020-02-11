package mgks.os.webview;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

public class Firebase extends FirebaseMessagingService {
	public void onNewToken(String s) {
		Log.d("TOKEN_REFRESHED ", s);		// printing new tokens in logcat
	}

	public void onMessageReceived(RemoteMessage message) {
		if (message.getNotification() != null) {
			sendMyNotification(message.getNotification().getTitle(),
				message.getNotification().getBody(),
				message.getNotification().getClickAction(),
				message.getData().get("uri")
			);
		}
	}

	private void sendMyNotification(String title, String message, String click_action, String uri) {
		//On click of notification it redirect to this Activity
		Intent intent = new Intent(click_action);
		intent.putExtra("uri", uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MainActivity.asw_fcm_channel)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title)
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(soundUri)
				.setContentIntent(pendingIntent);
		Notification noti = notificationBuilder.build();
		noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(MainActivity.ASWV_FCM_ID, notificationBuilder.build());
	}
}
