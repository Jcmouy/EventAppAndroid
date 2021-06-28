package com.example.admin.eventappb.Dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.admin.eventappb.DTO.EventoDTO;
import com.example.admin.eventappb.DTO.Tipo_eventoDTO;
import com.example.admin.eventappb.Data.Remote.RemoteUtils;
import com.example.admin.eventappb.Helper.PrefManager;
import com.example.admin.eventappb.MainActivity;
import com.example.admin.eventappb.R;
import com.example.admin.eventappb.Remote.EventoService;
import com.example.admin.eventappb.Remote.Tipo_eventoService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Admin on 13/09/2017.
 */

public class DCreateEvent extends DialogFragment {
    Bitmap myBitmap;
    Uri picUri;
    Uri videoUri;
    Uri outputFileUri;
    Uri outputVideoUri;

    Button contactButton;
    TextView text_NumbCont;
    private LinearLayout contacts_layout;
    private boolean text_click = false;

    private static String mCurrentPhotoPath;
    private static String mCurrentVideoPath;

    private boolean eventoCreado = false;
    private boolean videoCreado = false;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final String IMAGE_DIRECTORY_NAME = "EventApp";

    private Intent chooserIntent;
    private List<Intent> allIntents = new ArrayList<>();

    private String nombre, descripcion, subtipo_evento, imagen_prof, usuario_mobile;
    private int icono, idEvento;
    private long tipo_evento;
    private Boolean visibilidad, status;
    private String video_background;

    private int icon_image;
    private ArrayList<String> telefonos_sel = new ArrayList<>();

    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();

    private List<Tipo_eventoDTO> list_tipo_evento;

    private final static int ALL_PERMISSIONS_RESULT = 107;

    private Spinner spinner_TypeEvent, spinner_Visibility;
    private VideoView videoView;

    private static final String TAG = DCreateEvent.class.getSimpleName();

    private View v = null;

    private EventoService mEventoService;
    private Tipo_eventoService mTipo_eventoService;

    private PrefManager pref;
    private boolean hasActivePublicEvent;

    public DCreateEvent() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            outputFileUri = savedInstanceState.getParcelable("outputFileUri");
            picUri = savedInstanceState.getParcelable("picUri");
            outputVideoUri = savedInstanceState.getParcelable("outputVideoUri");
            videoUri = savedInstanceState.getParcelable("videoUri");
        }

        Bundle bundle = getArguments();
        if (bundle != null){
            hasActivePublicEvent = bundle.getBoolean("resultQuery");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_new_event, null);

        videoView = v.findViewById(R.id.header_cover_video);

        return createDCreateEvent(v);


    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de login
     *
     * @return Diálogo
     */
    @SuppressLint("ClickableViewAccessibility")
    public AlertDialog createDCreateEvent(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText txtNombre = v.findViewById(R.id.edit_nombre);
        final EditText txtDescripcion = v.findViewById(R.id.edit_descripcion);
        spinner_TypeEvent = v.findViewById(R.id.spinner_event_type);

        //Get types of events from DB
        mTipo_eventoService = RemoteUtils.getTipo_eventoService();
        getAllTipo_evento();

        final EditText txtSubtipo_evento = (EditText) v.findViewById(R.id.edit_subtype_event);

        spinner_Visibility =  v.findViewById(R.id.spinner_visibility);
        List<String> list_visibility = new ArrayList<>();
        list_visibility.add(getString(R.string.visibility_public));
        list_visibility.add(getString(R.string.visibility_private));

        ArrayAdapter<String> dataAdapter_V;

        //disable first item of spinner as user already has an active event
        if (hasActivePublicEvent){
            dataAdapter_V = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list_visibility){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
        }else{
            dataAdapter_V = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list_visibility);
        }

        dataAdapter_V.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Visibility.setAdapter(dataAdapter_V);

        //Set spinner in second item as first is disabled
        if (hasActivePublicEvent){
            spinner_Visibility.setSelection(1);
        }

        contacts_layout = v.findViewById(R.id.contacts_layout);

        spinner_Visibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner_Visibility.getSelectedItem().equals("Public")){
                    contacts_layout.setVisibility(View.INVISIBLE);
                }else{
                    contacts_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        builder.setView(v);

        //Buttons

        videoView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (videoCreado == true && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    startActivityForResult(getPickVideoChooserIntent(), 100);
                }else if(videoCreado == false && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    outputVideoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                    PackageManager packageManager = getActivity().getPackageManager();

                    List<ResolveInfo> listCam = packageManager.queryIntentActivities(intent, 0);
                    for (ResolveInfo res : listCam) {
                        String packageName = res.activityInfo.packageName;
                        getContext().grantUriPermission(packageName,outputVideoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    // 1- High quality video
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputVideoUri);

                    startActivityForResult(intent,100);

                }
                return true;
                }
        });

        CircleImageView profileButton = v.findViewById(R.id.user_profile_photo);
        profileButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivityForResult(getPickImageChooserIntent(), 200);
                    }
                });

        ImageButton iconButton = v.findViewById(R.id.user_icon);
        iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DSelectIconPrueba().show(getChildFragmentManager(), "DSelectIconPrueba");
            }
        });

        Button cancelButton = v.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        contactButton = v.findViewById(R.id.button_select_contacts);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DSelectContact().show(getChildFragmentManager(), "DSelectContact");
            }
        });

        mEventoService = RemoteUtils.getEventoService();

        Button acceptButton = v.findViewById(R.id.button_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nombre = txtNombre.getText().toString();
                descripcion = txtDescripcion.getText().toString();
                subtipo_evento = txtSubtipo_evento.getText().toString();
                tipo_evento = spinner_TypeEvent.getSelectedItemId();

                if (spinner_Visibility.getSelectedItemId() == 0){
                    visibilidad = true;
                }else if (spinner_Visibility.getSelectedItemId() == 1){
                    visibilidad = false;
                }

                //imagen_prof = convertBase64Image(myBitmap);

                status = true;
                icono = icon_image;
                imagen_prof = mCurrentPhotoPath;
                video_background = mCurrentVideoPath;
                usuario_mobile = pref.getMobileNumber();


                insertEvent(nombre,descripcion,tipo_evento, subtipo_evento,visibilidad,icono,imagen_prof,video_background, usuario_mobile, status);

                eventoCreado = false;

                sendSMS(telefonos_sel, getResources().getString(R.string.sms_standard_message));

                dismiss();

                Toast.makeText(getActivity(), "Event created",
                        Toast.LENGTH_LONG).show();

            }
        });


        //Permissions
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(RECORD_AUDIO);
        permissions.add(READ_CONTACTS);
        permissions.add(SEND_SMS);
        permissions.add(RECEIVE_SMS);
        permissions.add(READ_SMS);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        return builder.create();
    }

    private void sendSMS(ArrayList<String> telefonos_sel, String message) {

        Intent intent = new Intent(getContext(), DSelectContact.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);

        SmsManager sms = SmsManager.getDefault();

        for (int i=0; i < telefonos_sel.size(); i++){
            String tel = telefonos_sel.get(i);
            sms.sendTextMessage(tel, null, message, pi, null);
        }
    }


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        //Uri outputFileUri = getCaptureImageOutputUri();
        outputFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        allIntents = new ArrayList<>();

        PackageManager packageManager = getActivity().getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);

            //Permission
            String packageName = res.activityInfo.packageName;
            getContext().grantUriPermission(packageName,outputFileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (outputFileUri != null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);

        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);

            //Permission
            String packageName = res.activityInfo.packageName;
            getContext().grantUriPermission(packageName,outputFileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        eventoCreado = true;

        return chooserIntent;
    }

    public Intent getPickVideoChooserIntent() {

        // Determine Uri of camera image to save.
        //Uri outputFileUri = getCaptureImageOutputUri();

        if (!videoCreado){
            outputVideoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        }

        allIntents = new ArrayList<>();

        PackageManager packageManager = getActivity().getPackageManager();

        // collect all camera video intents
        Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);

            //Permission
            String packageName = res.activityInfo.packageName;
            getContext().grantUriPermission(packageName,outputVideoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (outputVideoUri != null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputVideoUri);
            }

            allIntents.add(intent);

        }

        // collect all video intents
        Intent video_playerIntent = new Intent(Intent.ACTION_VIEW , outputVideoUri);
        video_playerIntent.setDataAndType(outputVideoUri,"video/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(video_playerIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(video_playerIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);

            //Permission
            String packageName = res.activityInfo.packageName;
            getContext().grantUriPermission(packageName,outputVideoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        eventoCreado = true;

        return chooserIntent;

    }

    public Uri getOutputMediaFileUri(int type) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return FileProvider.getUriForFile(getContext(),
                    getContext().getApplicationContext().getPackageName() + ".provider",getOutputMediaFile(type));
        }else{
            return Uri.fromFile(getOutputMediaFile(type));
        }

    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }else{
            Log.d(IMAGE_DIRECTORY_NAME, "Directory created ");
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
            mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
            mCurrentVideoPath = "file:" + mediaFile.getAbsolutePath();
        } else {
            return null;
        }


        return mediaFile;
    }

    //

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100){
            if (resultCode == Activity.RESULT_OK) {
                captureVideo();
                /*
            }else if(resultCode == Activity.RESULT_OK && videoCreado == true) {
                showVideo();
                */
            }else if (resultCode == Activity.RESULT_CANCELED){
                Log.i(TAG, "User cancelled video recording.");
            }else{
                Log.i(TAG, "Failed to record video.");
            }
        }else if (requestCode == 200){
            if (resultCode == Activity.RESULT_OK) {
                captureImage();
            }else if (resultCode == Activity.RESULT_CANCELED){
                Log.i(TAG, "User cancelled photo.");
            }else{
                Log.i(TAG, "Failed to take photo.");
            }
        }
    }

    private void captureImage(){
        picUri = Uri.parse(mCurrentPhotoPath);
        Bitmap bitmap;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8;

            ContentResolver cr = getActivity().getContentResolver();
            InputStream input = null;

            input = cr.openInputStream(outputFileUri);

            bitmap = BitmapFactory.decodeStream(input,null,options);

            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            myBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
                    bitmap.getHeight(),matrix,false);


            //Code for func rotateImageIfRequired
                    /*
                    //myBitmap = bitmap;

                    //myBitmap = rotateImageIfRequired(myBitmap, input,null);
                    */

            CircleImageView croppedImageView = v.findViewById(R.id.user_profile_photo);

            croppedImageView.setImageBitmap(null);
            croppedImageView.setImageBitmap(myBitmap);

            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    picUri));


        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void captureVideo() {
        videoUri = Uri.parse(mCurrentVideoPath);
        try {
            videoView.setVideoURI(outputVideoUri);

            //Creating thumbnail

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoUri.getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);

            videoView.setBackgroundDrawable(bitmapDrawable);

            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    videoUri));

            videoCreado = true;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* Ideal func - compatibility problems with andorid os versions
    private static Bitmap rotateImageIfRequired(Bitmap img, InputStream selectedImageINSTREAM, Uri selectedImageURI) throws IOException {

        ExifInterface ei = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ei = new ExifInterface(selectedImageINSTREAM);
        }else{
            ei = new ExifInterface(selectedImageURI.getPath());
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_FLIP_VERTICAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    */

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static String convertBase64Image(Bitmap img){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("picUri", picUri);
        outState.putParcelable("outputFileUri", outputFileUri);
        outState.putParcelable("outputVideoUri", outputVideoUri);
        outState.putParcelable("videoUri", videoUri);
    }

    public void onIconSelected (int item_image){
        icon_image = item_image;
        ImageView icon_image = v.findViewById(R.id.user_icon);
        icon_image.setImageResource((item_image));
    }

    public void insertEvent(final String nombre, String descripcion, long tipo_evento, String subTipo_evento, boolean visibilidad, int icono, String imagen_profile, String video_background, String usuario_mobile, Boolean status) {
        mEventoService.insert(nombre, descripcion, tipo_evento, subTipo_evento, visibilidad, icono, imagen_profile, video_background, usuario_mobile, status).enqueue(new Callback<EventoDTO>() {
            @Override
            public void onResponse(Call<EventoDTO> call, Response<EventoDTO> response) {

                if(response.isSuccessful()) {
                    //Get ID from inserted event
                    idEvento = response.body().getId();
                    sendEventIDMap(nombre);
                    Log.i(TAG, "post submitted to API.");
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
            public void onFailure(Call<EventoDTO> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void getAllTipo_evento() {
        mTipo_eventoService.getAll().enqueue(new Callback<List<Tipo_eventoDTO>>() {
            @Override
            public void onResponse(Call<List<Tipo_eventoDTO>> call, Response<List<Tipo_eventoDTO>> response) {

                if(response.isSuccessful()) {
                    Log.d("MainActivity", "Load from API");
                    response.body();
                    list_tipo_evento = response.body();

                    spinnerTipoEvento();

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
            public void onFailure(Call<List<Tipo_eventoDTO>> call, Throwable t) {
                t.printStackTrace();
                Log.d("MainActivity", "error loading from API");

            }
        });
    }

    public void spinnerTipoEvento(){

        ArrayAdapter<Tipo_eventoDTO> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list_tipo_evento);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_TypeEvent.setAdapter(dataAdapter);

    }

    public void sendEventIDMap(String nombre){

        //java.lang.NullPointerException: Attempt to invoke virtual method 'void com.example.admin.eventappb.MainActivity.onEventCreated(int, int)' on a null object reference

        ((MainActivity)(DCreateEvent.this.getContext())).onEventCreated(idEvento,icono,visibilidad, telefonos_sel, nombre);
        Log.i(TAG, "IdEvent and IdIcono sent to MainActivity");
    }

    public void onContactSelected(ArrayList<String> telefonos_seleccionados) {

        if (text_click){
            ((LinearLayout) contacts_layout).removeView(text_NumbCont);
        }

        telefonos_sel = telefonos_seleccionados;
        Log.i(TAG, "Got selected nº phones");

        contactButton.setVisibility(View.GONE);
        final int cantidad = telefonos_sel.size();

        text_NumbCont = new TextView(getContext());
        text_NumbCont.setText(cantidad + " Selected");
        text_NumbCont.setGravity(Gravity.CENTER);
        text_NumbCont.setTextColor(Color.BLACK);

        text_NumbCont.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ((LinearLayout) contacts_layout).addView(text_NumbCont);

        text_NumbCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_click = true;

                new DSelectContact().show(getChildFragmentManager(), "DSelectContact");
            }
        });

    }
}
