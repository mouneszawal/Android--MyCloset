package tr.yildiz.mycloset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Reminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,intent.getExtras().getString("name"))
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(intent.getExtras().getString("name"))
                .setContentText(intent.getExtras().getString("address"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(intent.getExtras().getInt("id"),builder.build());
    }
}
