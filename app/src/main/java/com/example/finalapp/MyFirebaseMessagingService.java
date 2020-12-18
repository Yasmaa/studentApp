package com.example.finalapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.finalapp.user.PrincipalActivity;
import com.example.finalapp.user.agent.ConsulterReclamationAgentFragment;
import com.example.finalapp.user.user.ConsulterReclamationUserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String recId;
    private String type;
    private String etat;
    private Intent intent;
    private String title;
    private String messageBody;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            recId = remoteMessage.getData().get("recId");
            type = remoteMessage.getData().get("type");
            etat = remoteMessage.getData().get("etat");
            sendNotification();
        }
    }

    private void sendNotification() {
        title = new String();
        messageBody = new String();

        if (type.equals("1")) {
            title = "Reclamation: ";
            if(etat.startsWith("E"))  messageBody = "Votre réclamation est en cours de traitement";
            if(etat.startsWith("T"))  messageBody = "Votre réclamation est traitée";
            startConsulterReclamationUserFragment(recId);
        }

        if (type.equals("2")) {
            title = "Reclamation: ";
            messageBody = "Nouvelle reclamation";
            startConsulterReclamationAgentFragment(recId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "FireBase1")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void startConsulterReclamationUserFragment(String recId) {
        ConsulterReclamationUserFragment.recId = recId;
        intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("type",1);
    }

    private void startConsulterReclamationAgentFragment(String recId) {
        ConsulterReclamationAgentFragment.recId = recId;
        intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("type",2);
    }
}
