package com.example.feliche.testgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import serviceGPS.ServiceGps;

public class MainActivity extends AppCompatActivity {

    Button btnStopService;
    Intent intent;
    boolean serviceActive = false;
    TextView tvCounter;
    private BroadcastReceiver mReceiver;


    static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCounter = (TextView)findViewById(R.id.tvCounter);
        btnStopService = (Button)findViewById(R.id.btnStopService);
        btnStopService.setText("Inica el servicio");
        intent = new Intent(this,ServiceGps.class);
        serviceActive = false;
    }

    public void onBtnStopService(View v){
        if(serviceActive == false) {
            startService(intent);
            serviceActive = true;
            btnStopService.setText("Para el servicio");
        }else {
            stopService(intent);
            serviceActive = false;
            btnStopService.setText("Inica el servicio");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        mReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d("Se recibio: ", "accion: " + action);
                switch (action){
                    case ServiceGps.LOCATION:
                        double lat, lon;
                        Location location;
                        Bundle b;

                        b = intent.getBundleExtra(ServiceGps.LOCATION_UPDATE);
                        if(b == null) break;
                        location = b.getParcelable(ServiceGps.LOCATION_DATA);
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        // lat = intent.getDoubleExtra(ServiceGps.LATITUDE_LOCATION, 0);
                        // lon = intent.getDoubleExtra(ServiceGps.LONGITUDE_LOCATION, 0);
                        tvCounter.setText("Posicion: " +
                                String.valueOf(lat) +
                                " - " +
                                String.valueOf(lon));
                        break;
                    default:
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter(ServiceGps.LOCATION);
        this.registerReceiver(mReceiver,filter);
    }
}
