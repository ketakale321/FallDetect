package com.a3kings.falldetect;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;

public class CountDownFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public CountDownFragment() {
    }

    TextView text_timer;
    CountDownTimer timer;
    ImageView image;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;



    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String latitude,longitude;


    private DBManager dbManager;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_countdown, container, false);
        text_timer = rootView.findViewById(R.id.text_timer);
        image = rootView.findViewById(R.id.ivStart);

        //for database initialization
        context = getActivity().getApplicationContext();
        dbManager = new DBManager(context);


        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                text_timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                //text_timer.setText("Sending message");
                //sendMessage("8668416672","Accident");
                callDataRecepients();
            }
        };
        timer.start();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                Fragment newFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "Timer Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }


    private void callDataRecepients() {
        //If Data is present in database then put it into front end edittext
        try {
//            MyGeoLocation myGeoLocation=new MyGeoLocation();

            String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
            List<HashMap<String, String>> data = dbManager.getAllData();
            //Toast.makeText(context, ""+data, Toast.LENGTH_SHORT).show();
            if (data.size() != 0) {
                try {
                    sendMessage(data.get(0).get("mobile"),"Accident Happened, check out "+url);
                    Log.d("1", url);
                } catch (Exception e) {
                }

                try {
                    sendMessage(data.get(1).get("mobile"),"Accident Happened, check out "+url);
                } catch (Exception e) {
                }

            } else {
                Toast.makeText(context, "No data in database", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Toast.makeText(context, "Exce:" + ex, Toast.LENGTH_SHORT).show();
        }

    }

    public void sendMessage(String phonenumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phonenumber, null, message, null, null);
        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            latitude=String.valueOf(location.getLatitude());
            longitude=String.valueOf(location.getLongitude());
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) try {
            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
        } catch (IntentSender.SendIntentException e) {
            // Log the error
            e.printStackTrace();
        }
        else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i("failed connnection", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    private void handleNewLocation(Location location) {
        Log.d("Location", location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        latitude=""+currentLatitude;
        longitude=""+currentLongitude;

    }
}