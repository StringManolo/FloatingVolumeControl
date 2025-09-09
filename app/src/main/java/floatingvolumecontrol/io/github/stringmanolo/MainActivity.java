package floatingvolumecontrol.io.github.stringmanolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verificar si se debe cerrar la aplicación
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        // Iniciar el servicio de volumen
        Intent serviceIntent = new Intent(this, VolumeService.class);
        startService(serviceIntent);
        
        // Configurar botón de cierre
        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detener el servicio
                Intent serviceIntent = new Intent(MainActivity.this, VolumeService.class);
                stopService(serviceIntent);
                
                // Cerrar la aplicación
                finish();
            }
        });
    }
}