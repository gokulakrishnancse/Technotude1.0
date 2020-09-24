package tech.info.sasurie.sociall.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tech.info.sasurie.sociall.MainActivity;
import tech.info.sasurie.sociall.R;

/**
 * Created by Belal on 03/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                String topic = remoteMessage.getData().get("topic");
                String title = remoteMessage.getData().get("title");

                if(topic.equalsIgnoreCase("Test Deleted"))
                {

                   DatabaseReference Markdata = FirebaseDatabase.getInstance().getReference().child("Mark_Detail");
                   FirebaseAuth mAuth= FirebaseAuth.getInstance();
                   String currentUser=mAuth.getCurrentUser().getUid();
                   Markdata.child(currentUser).child(title).removeValue();

                }
               /* if(topic.equalsIgnoreCase("New Test Added"))
                {
                   DatabaseReference TotalTestTopicRef=FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");
                    FirebaseAuth mAuth= FirebaseAuth.getInstance();
                    String currentUser=mAuth.getCurrentUser().getUid();
                    TotalTestTopicRef.child(currentUser).removeValue();

                }*/

                sendPushNotification(topic, title);
                Log.e("notification", "-----" + topic + "  " + title);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
    NotificationManager mNotificationManager = null;
    NotificationCompat.Builder mBuilder = null;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    private void sendPushNotification(String topic, String title) {
        /**Creates an explicit intent for an Activity in your app**/

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.pushgg);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(topic)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setSound(defaultSoundUri)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
}