package floatingvolumecontrol.io.github.stringmanolo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        if (intent.getAction().equals("ACTION_VOLUME_UP")) {
            // Aumentar volumen
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int newVolume = Math.min(maxVolume, currentVolume + 1);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI);
            
        } else if (intent.getAction().equals("ACTION_VOLUME_DOWN")) {
            // Disminuir volumen
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int newVolume = Math.max(0, currentVolume - 1);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI);
            
        } else if (intent.getAction().equals("ACTION_CLOSE")) {
            // Detener el servicio
            Intent serviceIntent = new Intent(context, VolumeService.class);
            context.stopService(serviceIntent);
            
            // Cerrar la actividad principal si está abierta
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.putExtra("EXIT", true);
            context.startActivity(mainIntent);
            System.exit(0);
        }
        
        // Actualizar la notificación
        Intent serviceIntent = new Intent(context, VolumeService.class);
        context.startService(serviceIntent);
    }
}