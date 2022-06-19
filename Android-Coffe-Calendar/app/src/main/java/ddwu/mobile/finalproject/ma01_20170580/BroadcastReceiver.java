package ddwu.mobile.finalproject.ma01_20170580;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {

		// 채널 생성
		createNotificationChannel(context);

		// Notification 출력 (알림 생성)
		NotificationCompat.Builder builder
				= new NotificationCompat.Builder(context, context.getResources().getString(R.string.CHANNEL_ID))
				.setSmallIcon(R.drawable.coffee)
				.setContentTitle("카페인 주의!")
				.setContentText("벌써 커피를 3잔 이상 드셨습니다. 카페인 과다 섭취에 조심하세요!")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setAutoCancel(true);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		int notificationId = 100;
		notificationManager.notify(notificationId, builder.build());
	}

	private void createNotificationChannel(Context context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getResources().getString(R.string.channel_name);       // strings.xml 에 채널명 기록
			String description = context.getResources().getString(R.string.channel_description);       // strings.xml에 채널 설명 기록
			int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 알림의 우선순위 지정
			NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.CHANNEL_ID), name, importance);    // CHANNEL_ID 지정
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);  // 채널 생성
			notificationManager.createNotificationChannel(channel);
		}
	}

}