package floatingvolumecontrol.io.github.stringmanolo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

public class VolumeService extends Service {

    private static final String CHANNEL_ID = "VolumeControlChannel";
    private static final int NOTIFICATION_ID = 1;
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Actualizar la notificación si se reinicia el servicio
        Notification notification = createNotification();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
        
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Volume Control",
                    NotificationManager.IMPORTANCE_HIGH // Usar IMPORTANCE_HIGH para Huawei
            );
            serviceChannel.setDescription("Control de volumen flotante");
            serviceChannel.setShowBadge(false);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        // Crear diseño personalizado para la notificación
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_volume);
        
        // Obtener volumen actual
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volumePercent = (int) ((currentVolume / (float) maxVolume) * 100);
        
        // Configurar el texto de volumen
        notificationLayout.setTextViewText(R.id.volumeText, "Volumen: " + volumePercent + "%");
        
        // Intent para aumentar volumen
        Intent volumeUpIntent = new Intent(this, NotificationReceiver.class);
        volumeUpIntent.setAction("ACTION_VOLUME_UP");
        PendingIntent volumeUpPendingIntent = PendingIntent.getBroadcast(
                this, 0, volumeUpIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notificationLayout.setOnClickPendingIntent(R.id.btnVolumeUp, volumeUpPendingIntent);
        
        // Intent para disminuir volumen
        Intent volumeDownIntent = new Intent(this, NotificationReceiver.class);
        volumeDownIntent.setAction("ACTION_VOLUME_DOWN");
        PendingIntent volumeDownPendingIntent = PendingIntent.getBroadcast(
                this, 0, volumeDownIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notificationLayout.setOnClickPendingIntent(R.id.btnVolumeDown, volumeDownPendingIntent);
        
        // Intent para cerrar la aplicación
        Intent closeIntent = new Intent(this, NotificationReceiver.class);
        closeIntent.setAction("ACTION_CLOSE");
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(
                this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notificationLayout.setOnClickPendingIntent(R.id.btnClose, closePendingIntent);
        
        // Construir la notificación para Huawei
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        
        return builder
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
              
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout) // Para Huawei
                .setStyle(new Notification.DecoratedCustomViewStyle()) // Estilo para custom view
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX) // Máxima prioridad
                .setVisibility(Notification.VISIBILITY_PUBLIC) // Visible en pantalla bloqueada
                .setOnlyAlertOnce(true) // No alertar cada vez que se actualice
                .build();
    }
}