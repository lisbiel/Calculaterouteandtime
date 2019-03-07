package br.com.bossini.calculaterouteandtime;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSION_GPS = 1001;
    private Button grantgpsbutton;
    private Button ongpsbutton;
    private Button offgpsbutton;
    private Button initroutebutton;
    private Button finishroutebutton;
    private TextView travelledDistText;
    private TextView travelledDIstLabel;
    private TextView travelledTimeLabel;
    private EditText proximityEditText;
    private FloatingActionButton fab;
    private Location anterior;
    double latitude;
    double longitude;
    Location crntLocation=new Location("crntlocation");
    Intent intent;

    private float distPercorrida = 0f;
    private int tickCounter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Chronometer chronometer;
    Context context;
    boolean GpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grantgpsbutton = (Button)findViewById( R.id.grantgpsbutton );
        ongpsbutton = (Button)findViewById( R.id.ongpsbutton );
        offgpsbutton = (Button)findViewById( R.id.offgpsbutton );
        initroutebutton = (Button)findViewById( R.id.initroutebutton );
        finishroutebutton = (Button)findViewById( R.id.finishroutebutton );
        travelledDistText = (TextView)findViewById( R.id.travelledDistText );
        travelledDIstLabel = (TextView)findViewById( R.id.travelledDIstLabel );
        chronometer = (Chronometer)findViewById(R.id.chronometerExample);
        proximityEditText = (EditText)findViewById( R.id.proximityEditText );
        fab = (FloatingActionButton)findViewById( R.id.fab );

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener =
                new LocationListener() {
                    @Override
                        public void onLocationChanged(Location location){
                        crntLocation.setLatitude(location.getLatitude());
                        crntLocation.setLongitude(location.getLongitude());
                        try {
                            travelledDIstLabel.setText(String.format("+" + crntLocation.distanceTo(anterior)));
                        } catch ( Exception e) {

                        }
                            String exibir =
                                    String.format(
                                            distPercorrida+"m"
                                    );

                            travelledDIstLabel.setText(exibir);
                        try {
                            anterior.setLatitude(crntLocation.getLatitude());
                            anterior.setLongitude(crntLocation.getLongitude());
                        } catch ( Exception e) {

                        }

                        }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                grantgpsbutton.setOnClickListener( this);
                ongpsbutton.setOnClickListener( this);
                offgpsbutton.setOnClickListener( this);
                initroutebutton.setOnClickListener( this);
                finishroutebutton.setOnClickListener( this);
                fab.setOnClickListener( this);

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
                grantgpsbutton.setClickable(false);
                grantgpsbutton.setTextColor(this.getResources().getColor(R.color.disabled_color));
            }
    }



    public void onClick(View v) {
        if (v == grantgpsbutton) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                grantgpsbutton.setClickable(false);
                grantgpsbutton.setTextColor(this.getResources().getColor(R.color.disabled_color));
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_PERMISSION_GPS
                );
            }
        } else if (v == ongpsbutton) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(this,
                            getString(
                                    R.string.gps_already_on
                            ),
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else {
                Toast.makeText(this,
                        getString(
                                R.string.no_gps_permission
                        ),
                        Toast.LENGTH_SHORT).show();
            }

        } else if ( v == offgpsbutton ) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else {
                Toast.makeText(this,
                getString(
                        R.string.gps_already_off
                ),
                        Toast.LENGTH_SHORT).show();
            }
        } else if ( v == initroutebutton ) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000,
                        5,
                        locationListener
                );
                travelledDIstLabel.setText("0m");
                long systemCurrTime = SystemClock.elapsedRealtime();
                chronometer.setBase(systemCurrTime);
                chronometer.start();
            } else {
                Toast.makeText(this,
                        getString(
                        R.string.gps_off
                ),
                        Toast.LENGTH_SHORT).show();
            }
        } else if ( v == finishroutebutton ) {
            locationManager.removeUpdates(locationListener);
            chronometer.stop();
            distPercorrida = 0f;

        } else if ( v == fab ) {
            Uri uri =
                    Uri.parse(
                            String.format(
                                    Locale.getDefault(),
                                    "geo:%f,%f?q=" + String.format(String.valueOf(proximityEditText.getText())),
                                    crntLocation.getLatitude(),
                                    crntLocation.getLongitude()
                            )
                    );
            Intent intent =
                    new Intent (
                            Intent.ACTION_VIEW,
                            uri
                    );
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_GPS){
            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED){
                    grantgpsbutton.setClickable(false);
                    grantgpsbutton.setTextColor(this.getResources().getColor(R.color.disabled_color));
                }
            }
            else{
                Toast.makeText(this,
                        getString(
                                R.string.no_gps_no_app
                        ),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
