package serviceGPS;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class ServiceGps extends Service implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{

    public static final String LOCATION = "com.example.feliche.location";
    public static final String LOCATION_DATA = "com.example.feliche.location_data";
    public static final String LOCATION_UPDATE = "com.example.feliche.location_update";
    public static final String LATITUDE_LOCATION = "com.example.feliche.update_latitude";
    public static final String LONGITUDE_LOCATION = "com.example.feliche.update_longitude";


    private static final String TAG = "Location Service";
    private static final int LOCATION_INTERVAL = 2000;
    private Context mContext;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        mContext = this;
        getLocation();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getLocation(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitud = location.getLongitude();

        Bundle b= new Bundle();
        b.putParcelable(ServiceGps.LOCATION_DATA,location);

        Intent intent = new Intent(ServiceGps.LOCATION);
        intent.setPackage(mContext.getPackageName());
        intent.putExtra(ServiceGps.LOCATION_UPDATE,b);
        //intent.putExtra(ServiceGps.LATITUDE_LOCATION, latitude);
        //intent.putExtra(ServiceGps.LONGITUDE_LOCATION, longitud);
        mContext.sendBroadcast(intent);
    }
}