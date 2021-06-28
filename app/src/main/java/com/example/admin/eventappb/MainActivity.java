package com.example.admin.eventappb;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.admin.eventappb.Adapter.InvitedContactAdapter;
import com.example.admin.eventappb.DTO.IconoDTO;
import com.example.admin.eventappb.DTO.LocalizacionDTO;
import com.example.admin.eventappb.Data.Remote.RemoteUtils;
import com.example.admin.eventappb.Dialog.DCreateEvent;
import com.example.admin.eventappb.Helper.PrefManager;
import com.example.admin.eventappb.Remote.EventoService;
import com.example.admin.eventappb.Remote.LocalizacionService;
import com.example.admin.eventappb.Remote.SmsService;
import com.example.admin.eventappb.Remote.UsuarioService;
import com.example.admin.eventappb.Service.BackgroundDetectedActivitiesService;
import com.example.admin.eventappb.Utils.Constants;
import com.example.admin.eventappb.Utils.ModifyText;
import com.example.admin.eventappb.Utils.MyInvitedContacts;
import com.example.admin.eventappb.Utils.MySlidingPaneLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public final static double STANDARD_RADIO_NOTIFICACTION = 300;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;

    private LocalizacionService mLocalizacionService;
    private EventoService mEventoService;
    private UsuarioService mUsuarioService;

    private int idEventoCreated, idIconoSelected;
    private IconoDTO icono = new IconoDTO();

    private IconoDTO iconoT = new IconoDTO();

    private int iconoId;
    private int localizacionId;
    private List<JsonObject> json_list = new ArrayList<>();
    private List<JsonObject> json_list_notification = new ArrayList<>();
    private ArrayList<String> telefonos_seleccionados = new ArrayList<>();
    private boolean visib;

    private Marker eventMarkerPrueba;
    private ArrayList<Marker> list_marcadores = new ArrayList<>();
    private MySlidingPaneLayout slid_panel;
    private List<LocalizacionDTO> list_localizacion;

    private Uri img;
    private Uri vid;
    private VideoView video_event;
    private CircleImageView image_event;

    private PrefManager pref;
    private ModifyText modfText = new ModifyText();

    private ArrayList<MyInvitedContacts> private_contacts;

    private String myNumber;

    SupportMapFragment sMapFragment;

    private SmsService mSmsService;

    private boolean first_instance;
    private int first_distance = 0;
    private Date start_time;

    private int last_not_event = 0;

    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new PrefManager(getApplicationContext());

        // Checking if user session
        // if not logged in, take user to sms screen

        if (!pref.isLoggedIn()){
            logout();
        }

        //distance();

        //int distancia = calculateDistanceInKilometer(-32.376011,-54.17064,-32.37563,-54.173735 );

        sMapFragment = SupportMapFragment.newInstance();

        mEventoService = RemoteUtils.getEventoService();
        mLocalizacionService = RemoteUtils.getLocalizacionService();
        mUsuarioService = RemoteUtils.getUsuarioService();

        //Get user number
        myNumber = pref.getMobileNumber();

        //Set first instance true
        first_instance = true;

        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        slid_panel = findViewById(R.id.SlidingPanel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfUserPremium(myNumber);

                //FragmentManager fragmentManager = getSupportFragmentManager();
                //new DCreateEvent().show(fragmentManager, "DCreateEvent");
            }
        });

        android.app.FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        sMapFragment.getMapAsync(this);

    }

    private void logout() {
        pref.clearSession();

        Intent intent = new Intent(MainActivity.this, SmsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout)findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // CREATE TEST MARKER, delete in future stages.
        //String valor = "89";
        //createMarkerPrueba(valor);

        //SET MyNumber if in emulator
        //pref.setMobileNumber("099946874");

        //Fill map with markers
        getLocalizacionFromDB();

        //Fil map with private markers, created by me or accepted in invitation
        getLocalizacionPrivadaFromDB(myNumber);

        //Click on marker
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                for (int i =0; i < list_marcadores.size(); i++){
                    eventMarkerPrueba = list_marcadores.get(i);
                    if (Math.abs(eventMarkerPrueba.getPosition().latitude - latLng.latitude) < 0.0004
                            && Math.abs(eventMarkerPrueba.getPosition().longitude - latLng.longitude) < 0.0004)
                        onMarkerLongClick(eventMarkerPrueba);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slid_panel.isOpen()==true){
                    slid_panel.closePane();
                }
            }
        });

        startTrackingEvents();
    }

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);

        if (type != DetectedActivity.STILL && !waitTime(2)){
            if (confidence > Constants.CONFIDENCE) {

                Log.e(TAG, "User activity: moviendose");
                Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

                mLocalizacionService.getLocalizacionesNotificacion(myNumber).enqueue(new Callback<List<JsonObject>>() {
                    @Override
                    public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {

                        if(response.isSuccessful()) {

                            JsonObject closest_evento = new JsonObject();
                            JsonObject list = new JsonObject();

                            for (int i = 0; i < response.body().size(); i++){

                                list = response.body().get(i);

                                json_list_notification.add(list);

                                double lat = list.get("Lat").getAsDouble();
                                double longitud = list.get("Long").getAsDouble();
                                int idEvento = list.get("Evento").getAsInt();
                                int idIcono = list.get("IdIcono").getAsInt();
                                String nombreEvento = list.get("NombreEvento").getAsString();

                                int distance = 0;

                                if (first_instance){
                                    first_distance = calculateDistanceInMeter(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                                            lat,longitud);
                                    closest_evento = list;

                                    first_instance = false;
                                }else{
                                    distance = calculateDistanceInMeter(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                                            lat,longitud);
                                }

                                if (first_distance > distance && distance != 0){
                                    first_distance = distance;
                                    closest_evento = list;
                                }

                                Log.d("MainActivity", "Load locations from DB");

                            }

                            sendNotification(first_distance, closest_evento.get("Evento").getAsInt(),
                                    closest_evento.get("NombreEvento").getAsString());

                        }
                        else if(response.errorBody() != null){
                            try {
                                String errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<JsonObject>> call, Throwable t) {

                    }
                });


                //By dialog test if work
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to exit?").setCancelable(
                        false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                */
            }
        }
    }

    private void sendNotification(int first_distance, int evento, String nombreEvento) {

        //Check if notificaction is not from the same event or the wait time has passed
        if (last_not_event != evento && first_distance <= STANDARD_RADIO_NOTIFICACTION
                || !waitTime(7200) && first_distance <= STANDARD_RADIO_NOTIFICACTION){

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel.

                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_menu_send)
                    .setTicker("Hearty365")
                    //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle("EventApp")
                    .setContentText("You have a " + nombreEvento + " at " + first_distance + " meter away from you")
                    .setContentInfo("Info");

            notificationManager.notify(/*notification id*/1, notificationBuilder.build());

            last_not_event = evento;

        }

    }

    private void startTrackingEvents() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void onMarkerLongClick(Marker eventMarkerPrueba) {
        Log.d(TAG, "entra aqui");

        EditText editPaneDescription = findViewById(R.id.details_event_edit_descripcion);
        EditText editPaneSubType = findViewById(R.id.details_event_edit_subtype_event);
        video_event = findViewById(R.id.details_event_cover_video);

        private_contacts = new ArrayList<>();

        modfText.disableEditText(editPaneDescription);
        modfText.disableEditText(editPaneSubType);

        slid_panel.openPane();

        String prueba = String.valueOf(eventMarkerPrueba.getTag());

        getSelectedEvento(prueba);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*
        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).commit();
        */

        if (id == R.id.nav_camera) {


            //fm.beginTransaction().replace(R.menu.activity_main_drawer, new ImportFragment()).commit();
            //} else if (id == R.id.nav_gallery) {

            //  if (!sMapFragment.isAdded())
            //    sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
            // else
            //   sFm.beginTransaction().show(sMapFragment).commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    private void updateLocationUI() {
            if (mMap == null) {
                return;
            }

            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
       /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sMapFragment.getMapAsync(this);

        */
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
        if (!sMapFragment.isAdded()){
            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventCreated(int idEvento, int idIcono, boolean visibilidad, ArrayList<String> telefonos_sel, String nombre){
        idEventoCreated = idEvento;
        idIconoSelected = idIcono;
        visib = visibilidad;
        telefonos_seleccionados = telefonos_sel;

        insertLocalizacion(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(), idEventoCreated);

        createMarker(idEventoCreated, nombre);
    }

    public void createMarkerPrueba(String valor){

        LatLng location = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

        eventMarkerPrueba = mMap.addMarker(new MarkerOptions()
                .title("Prueba")
                .position(location)
                //.flat(true)
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.checked)));

        eventMarkerPrueba.setTag(valor);

    }


    public void createMarker(int idEvento, String nombre){
        icono = icono.getItem(idIconoSelected);
        Log.i(TAG, "Obtained icon");

        LatLng location = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

        eventMarkerPrueba = mMap.addMarker(new MarkerOptions()
                .title(nombre)
                .position(location)
                //.flat(true)
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(icono.getIdDrawable()))));

        eventMarkerPrueba.setTag(idEvento);

        list_marcadores.add(eventMarkerPrueba);
    }

    public void createMarkerFromDB(int idEvento, double lat, double longitud, int idIcono, String nombreEvento){

        icono = icono.getItem(idIcono);
        Log.i(TAG, "Obtained icon");

        LatLng location = new LatLng(lat, longitud);

        eventMarkerPrueba = mMap.addMarker(new MarkerOptions()
                .title(nombreEvento)
                .position(location)
                //.flat(true)
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(icono.getIdDrawable()))));

        eventMarkerPrueba.setTag(idEvento);

        list_marcadores.add(eventMarkerPrueba);

    }

    public Bitmap resizeMapIcons(int id){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),id);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 120, 120, false);
        return resizedBitmap;
    }

    ///DB Methods //////////////////////////////////////////////////////////////////////////

    private void insertLocalizacion(double latitude, double longitude, int idEventoCreated) {
        mLocalizacionService.insert(latitude,longitude,idEventoCreated).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    //Get ID from inserted event

                    JsonObject json = new JsonObject();
                    json = response.body();

                    localizacionId = json.get("ID").getAsInt();

                    if (!visib){
                        String myNumber = pref.getMobileNumber();
                        for (int i=0; i < telefonos_seleccionados.size(); i++){
                            String telefono = telefonos_seleccionados.get(i);
                            telefono = telefono.replace(" ", "");
                            insertLocalizacionPrivada(localizacionId, myNumber, telefono);

                        }
                    }

                    Log.i(TAG, "Inserted location");
                }
                else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });

    }

    private void insertLocalizacionPrivada(int localizacionId, String myNumber, String telefono) {
        mLocalizacionService.insertPrivado(localizacionId,myNumber,telefono).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {

                    Log.i(TAG, "Inserted private location");
                }
                else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }


    public void getSelectedEvento(String idEvento){
        mEventoService.get(idEvento).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {

                    //Get ID from inserted event
                    JsonObject json = new JsonObject();
                    json = response.body().getAsJsonObject("result");

                    String nombre = json.get("Nombre").getAsString();
                    String descripcion = json.get("Descripcion").getAsString();
                    String tipo_evento = response.body().get("NombreTipoEvento").getAsString();
                    String subtipo_evento = json.get("Subtipo_evento").getAsString();

                    //Get mobile number of event to compare with user number
                    String mobileUser = json.get("Usuario_mobile").getAsString();

                    boolean visibilidad = json.get("Visibilidad").getAsBoolean();
                    if (!visibilidad && !response.body().get("List_Contacts").isJsonNull()){
                        JsonObject contact = new JsonObject();
                        JsonArray list = new JsonArray();

                        list = response.body().getAsJsonArray("List_Contacts");

                        for (int i=0; i < list.size(); i++){
                            contact = (JsonObject) list.get(i);

                            String mobile = contact.get("Mobile").getAsString();
                            String name = contact.get("Nombre").getAsString();
                            contact = contact.getAsJsonObject("Confirmacion");
                            String confirmation = contact.get("String").getAsString();

                            MyInvitedContacts invitedContact = new MyInvitedContacts(mobile,name,confirmation);

                            private_contacts.add(invitedContact);

                            Log.i(TAG, "Nombre del evento es ");

                        }
                    }

                    int icono = json.get("Icono").getAsInt();
                    //iconoId = json.get("Icono").getAsInt();
                    String imagen_profile = json.get("Imagen_profile").getAsString();
                    String video_background = json.get("Video_background").getAsString();

                    Log.i(TAG, "Nombre del evento es ");

                    fillPaneSelectedEvent(nombre,descripcion,tipo_evento,subtipo_evento,visibilidad,icono,imagen_profile,video_background, mobileUser);

                }
                else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void getLocalizacionFromDB() {
        mLocalizacionService.getAllIconos().enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {

                if(response.isSuccessful()) {

                    JsonObject list = new JsonObject();

                    for (int i = 0; i < response.body().size(); i++){

                        list = response.body().get(i);

                        json_list.add(list);

                        double lat = list.get("Lat").getAsDouble();
                        double longitud = list.get("Long").getAsDouble();
                        int idEvento = list.get("Evento").getAsInt();
                        int idIcono = list.get("IdIcono").getAsInt();
                        String nombreEvento = list.get("NombreEvento").getAsString();

                        createMarkerFromDB(idEvento,lat,longitud,idIcono,nombreEvento);

                        Log.d("MainActivity", "Load locations from DB");

                    }

                }else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
            }
        });
    }

    private void getLocalizacionPrivadaFromDB(String myNumber){
        mLocalizacionService.getAllIconosPrivados(myNumber).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {

                if(response.isSuccessful()) {

                    JsonObject list = new JsonObject();

                    for (int i = 0; i < response.body().size(); i++){

                        list = response.body().get(i);

                        json_list.add(list);

                        double lat = list.get("Lat").getAsDouble();
                        double longitud = list.get("Long").getAsDouble();
                        int idEvento = list.get("Evento").getAsInt();
                        int idIcono = list.get("IdIcono").getAsInt();
                        String nombreEvento = list.get("NombreEvento").getAsString();

                        createMarkerFromDB(idEvento,lat,longitud,idIcono,nombreEvento);

                        Log.d("MainActivity", "Load private locations from DB");

                    }

                }else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                }

            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {

            }
        });
    }

    public void checkIfUserPremium(final String myNumber){
        mUsuarioService.getStatusPremium(myNumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    JsonObject json = new JsonObject();
                    json = response.body().getAsJsonObject("premium");
                    boolean premium = json.get("Bool").getAsBoolean();
                    boolean status  = response.body().get("status").getAsBoolean();

                    if (premium && status){
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        new DCreateEvent().show(fragmentManager, "DCreateEvent");
                    }else if (!premium && status){
                        getUserPublicEvent(myNumber);
                    }else{
                        Log.i(TAG, "No active user");

                    }

                }else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getUserPublicEvent(String myNumber) {
        mUsuarioService.checkPublicEvent(myNumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    boolean result  = response.body().get("result").getAsBoolean();
                    if (result){
                        Log.i(TAG, "Active public event");

                        FragmentManager fragmentManager = getSupportFragmentManager();

                        Bundle bundle = new Bundle(); //Bundle containing data you are passing to the dialog
                        bundle.putBoolean("resultQuery", result);

                        DCreateEvent dialogCreateEvent = new DCreateEvent();
                        dialogCreateEvent.setArguments(bundle);

                        dialogCreateEvent.show(fragmentManager, "DCreateEvent");

                    }else{
                        Log.i(TAG, "No active public event");

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        new DCreateEvent().show(fragmentManager, "DCreateEvent");

                    }

                }else if(response.errorBody() != null){
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    private void fillPaneSelectedEvent(String nombre, String descripcion, String tipo_evento, String subtipo_evento, boolean visibilidad, int icono, String imagen_profile, String video_background, String mobileUser) {

        TextView title_event = findViewById(R.id.details_event_text_new_event);
        TextView type_event = findViewById(R.id.details_event_text_new_event_field);
        ImageButton icon_event = findViewById(R.id.details_event_user_icon);
        image_event = findViewById(R.id.details_event_profile_photo);
        video_event = findViewById(R.id.details_event_cover_video);
        EditText description_event = findViewById(R.id.details_event_edit_descripcion);
        EditText subtype_event = findViewById(R.id.details_event_edit_subtype_event);
        TextView visibilty_event = findViewById(R.id.text_detail_event_pane_visibility);
        Button modify_event = findViewById(R.id.button_modfy_event);
        Button delete_event = findViewById(R.id.button_delete_event);

        LinearLayout details_contact_layout = findViewById(R.id.details_event_contacts_layout);
        LinearLayout details_button_layout = findViewById(R.id.details_event_button_layout);


        title_event.setText(nombre);
        type_event.setText(tipo_evento);
        description_event.setText(descripcion);
        subtype_event.setText(subtipo_evento);

        if (visibilidad){
            visibilty_event.setText(R.string.visibility_public);

            details_contact_layout.setVisibility(View.INVISIBLE);

        }else{
            visibilty_event.setText(R.string.visibility_private);

            details_contact_layout.setVisibility(View.VISIBLE);

            ListView list_contact = findViewById(R.id.details_contacts_list);

            InvitedContactAdapter mMyInvitedContactAdapter = new InvitedContactAdapter(this,
                    private_contacts);

            int i = mMyInvitedContactAdapter.getCount();

            if (mMyInvitedContactAdapter.getCount() < 1){
                list_contact.setVisibility(View.INVISIBLE);
            }else {
                if (list_contact.getVisibility() == View.INVISIBLE){
                    list_contact.setVisibility(View.VISIBLE);
                }
                //Relacionando la lista con el adaptador
                list_contact.setAdapter(mMyInvitedContactAdapter);
            }

            mMyInvitedContactAdapter.notifyDataSetChanged();

        }

        icon_event.setImageResource(icono);

        img = Uri.parse(imagen_profile);
        image_event.setImageURI(img);

        vid = Uri.parse(video_background);
        video_event.setVideoURI(vid);

        if (mobileUser.equals(myNumber)){
           details_button_layout.setVisibility(View.VISIBLE);
        }

        //Creating thumbnail

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(vid.getPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);

        video_event.setBackgroundDrawable(bitmapDrawable);

        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                vid));


        video_event.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                play_video();
                return true;
            }
        });

        image_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_image();
            }
        });

    }

    private void play_video() {
        Intent video_playerIntent = new Intent(Intent.ACTION_VIEW , vid);
        video_playerIntent.setDataAndType(vid,"video/*");
        startActivity(video_playerIntent);
    }

    private void open_image() {
        Intent imageIntent = new Intent(Intent.ACTION_VIEW, img);
        imageIntent.setDataAndType(img, "image/*");
        startActivity(imageIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //For calculating the distance between to events
    public int calculateDistanceInMeter(double userLat, double userLng,
                                        double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //double dist = Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c);

        double dist = AVERAGE_RADIUS_OF_EARTH_KM * c;

        //kilometer to meter
        dist = dist * 1000;

        return (int) dist;

        //return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    public boolean waitTime(int time){

        Log.d("MainActivity", "Load private locations from DB");

        if (first_instance){
            start_time = new Date();
            return false;
        }

        Date running_time = new Date();

        //interval in miliseconds
        long interval = running_time.getTime() - start_time.getTime();
        //interval in seconds
        interval = interval / 1000;
        if (interval >= time){
            start_time = running_time;
            return false;
        }else{
            return true;
        }

    }

}
