package com.cambodia.od4d.wastewaterleakagetracker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cambodia.od4d.wastewaterleakagetracker.R;
import com.cambodia.od4d.wastewaterleakagetracker.config.Configs;
import com.cambodia.od4d.wastewaterleakagetracker.model.AreaModel;
import com.cambodia.od4d.wastewaterleakagetracker.model.KeyValue;
import com.cambodia.od4d.wastewaterleakagetracker.model.PostModel;
import com.cambodia.od4d.wastewaterleakagetracker.rest.UploadFileToServer;
import com.cambodia.od4d.wastewaterleakagetracker.sqlite.SaveSql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportScreen extends AppCompatActivity implements View.OnClickListener{

    private final int CAMERA_REQUEST = 1001;
    private final int HISTORY = 1002;
    boolean doubleBackToExitPressedOnce = false;
    String url;
    ProgressBar progressBar;
    Uri imageUri;
    TextView textPercent;
    SaveSql saveSql;
    String selectedCondition = "1";
    ProgressBar mProgressBar;
    int selectedArea = 0;
    public static boolean checkedUpload = false;

    boolean hasInternet = true;

    ArrayList<AreaModel> areaModels;

    ImageView imageView;
    Button btn_low,btn_medium,btn_serious;
    FloatingActionButton btnSummit;
    TextInputEditText report,editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_screen);
        imageView = findViewById(R.id.take_report);
        Intent intent = getIntent();
        btn_low = findViewById(R.id.btn_low);
        btn_medium = findViewById(R.id.btn_medium);
        btn_serious = findViewById(R.id.btn_serious);
//        btn_reset = findViewById(R.id.btn_reset);
//        btn_submit = findViewById(R.id.btn_submit);
        btnSummit = findViewById(R.id.btn_report);
        report = findViewById(R.id.report);
        editTextDescription = findViewById(R.id.description);
        btn_low.setOnClickListener(this);
        btn_medium.setOnClickListener(this);
        btn_serious.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        textPercent = findViewById(R.id.text_percent);
        mProgressBar = findViewById(R.id.my_progressbar);

        btn_low.setBackgroundDrawable(getResources().getDrawable(R.drawable.style_low_click));
        btn_low.setTextColor(Color.WHITE);

        Uri uri = Uri.parse(intent.getStringExtra("img"));
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            imageUri = uri;
            thumbnail = rotateImage(thumbnail, getImageRotateAngle(getRealPathFromURI(uri)));
//            Toast.makeText(this, "Angle : " +  getImageRotateAngle(getRealPathFromURI(uri)), Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        btnSummit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                askPermission(Manifest.permission.ACCESS_FINE_LOCATION,7);
                InputMethodManager inputManager = (InputMethodManager)
                        ReportScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(btnSummit.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
//                showDialog(getApplicationContext());

                if (report.getText().toString().isEmpty()){
                    report.setText("Please title here");
                }
                post(view);
            }
        });
       /* btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });*/



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_low :
                btnChangeCol(view);
                selectedCondition ="1";
                break;
            case R.id.btn_medium:
                btnChangeCol(view);
                selectedCondition ="2";
                break;
            case R.id.btn_serious :
                btnChangeCol(view);
                selectedCondition ="3";
                break;
        }
    }
    private void btnChangeCol(View view){

       if(R.id.btn_low == view.getId()){
            btn_low.setBackgroundDrawable(getResources().getDrawable(R.drawable.style_low_click));
            btn_low.setTextColor(Color.WHITE);
        }else {
            btn_low.setBackgroundResource(R.drawable.style_low);
            btn_low.setTextColor(getResources().getColor(R.color.low));
        }
        if(R.id.btn_medium == view.getId()){
            btn_medium.setBackgroundDrawable(getResources().getDrawable(R.drawable.style_medium_click));
            btn_medium.setTextColor(Color.WHITE);
        }else {
            btn_medium.setBackgroundResource(R.drawable.style_medium);
            btn_medium.setTextColor(getResources().getColor(R.color.medium));
        }
        if(R.id.btn_serious == view.getId()){
            btn_serious.setBackgroundResource(R.drawable.style_serious_click);
            btn_serious.setTextColor(Color.WHITE);
        }else {
            btn_serious.setBackgroundResource(R.drawable.style_serious);
            btn_serious.setTextColor(getResources().getColor(R.color.serious));
        }

    }
    public void save(View view) {
        if (url != null) {

            Location location = getLocation(view);
            if (location == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please turn on you location");
                builder.create().show();
                return;
            }
            String longitude = location.getLongitude() + "";
            String latitude = location.getLatitude() + "";
            saveSql.add(new PostModel(
                    0,
                    url,
                    editTextDescription.getText().toString(),
                    latitude,
                    longitude,
                    DateFormat.format("yyyy-MM-dd", new java.util.Date()) + ""
            ));

//            Toast.makeText(this, "Saved to history!!!", Toast.LENGTH_SHORT).show();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Add An Image");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    }

    public void history(View view) {
        startActivityForResult(new Intent(this, History.class), HISTORY);
    }

    private Location getLocation(final View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    post(view);
                }
            });
            builder.setTitle("Please Allow Location Service!!!");
            builder.setMessage("Location Service can let us find out clear location of report");
            builder.create().show();
            return null;
        }

        Location location = getLastKnownLocation();
        Log.e("location", "getLocation: "+location );
        if (location == null) {
            statusCheck();
            return null;
        }
        return location;
    }

    public void post(View view) {

        Location location = getLocation(view);

        if (!hasInternet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection, you post saved to history!!!");
            builder.create().show();
            return;

        }

        if (location == null) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please turn on you location");
            builder.create().show();*/
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        ArrayList<KeyValue> arrayList = new ArrayList<>();
        arrayList.add(new KeyValue("image", getRealPathFromURI(imageUri)));
        arrayList.add(new KeyValue("description", editTextDescription.getText().toString()));
        arrayList.add(new KeyValue("lat", latitude + ""));
        arrayList.add(new KeyValue("lng", longitude + ""));
        arrayList.add(new KeyValue("area_id", "1"));
        arrayList.add(new KeyValue("condition_id", selectedCondition));
//        arrayList.add(new KeyValue("title", report.getText().toString()));

        new UploadFileToServer(arrayList, Configs.instance.upload, this, new UploadFileToServer.OnExecutionProgress() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                textPercent.setVisibility(View.VISIBLE);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressUpdating(int progress) {
                progressBar.setProgress(progress);
                textPercent.setText(progress + " %");

            }

            @Override
            public void onCompleteExecution(String response) {
                Log.i("----------", response);
//                Toast.makeText(ReportScreen.this, "Report submitted.", Toast.LENGTH_SHORT).show();
                reset();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        startActivity(new Intent(ReportScreen.this,HomeScreen.class));
                        progressBar.setVisibility(View.GONE);
                        textPercent.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (checkedUpload)
                            showDialog("Upload file successfully");
                        else
                            showDialog("Upload file failure");
                    }
                },3000);

            }
        }).execute();

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert mLocationManager != null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {

                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

//    void getInfo() {
//        new NetworkRequestString(this, Configs.instance.info, new HashMap<String, String>(), new OnRequestStringResult() {
//            @Override
//            public void onSuccess(String resultString) {
//                try {
//
//                    JSONObject result = new JSONObject(resultString);
//
//                    int status = result.getInt("status");
//                    if (status == 1) {
//                        JSONObject data = result.getJSONObject("data");
//
//                        JSONArray conditionArray = data.getJSONArray("conditions");
//                        JSONArray areasArray = data.getJSONArray("areas");
//
//                        String[] condition_array = new String[conditionArray.length()];
//                        String[] area_array = new String[areasArray.length()];
//
//                        for (int i = 0; i < conditionArray.length(); i++) {
//                            JSONObject object = conditionArray.getJSONObject(i);
//                            String id = object.getString("id");
//                            String condition = object.getString("condition");
//                            String color = object.getString("color");
//
//                            condition_array[i] = condition;
//
//                            conditionModels.add(new ConditionModel(id, condition, color));
//                        }
//
//                        for (int i = 0; i < areasArray.length(); i++) {
//                            JSONObject object = areasArray.getJSONObject(i);
//                            String id = object.getString("id");
//                            String area = object.getString("area");
//                            area_array[i] = area;
//                            areaModels.add(new AreaModel(id, area));
//
//                        }
//
//                        ArrayAdapter adapter_area = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, area_array);
//                        adapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner_area.setAdapter(adapter_area);
//
//                        ArrayAdapter adapter_condition = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, condition_array);
//                        adapter_condition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner_condition.setAdapter(adapter_condition);
//
//                        hasInternet = true;
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                hasInternet = false;
//            }
//        });
//
//    }

    private void reset(){
        editTextDescription.setText("");
        report.setText("");
        url = null;
        imageView.setImageDrawable(null);
    }

    public void camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                thumbnail = rotateImage(thumbnail, getImageRotateAngle(getRealPathFromURI(imageUri)));
                imageView.setImageBitmap(thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Can't display this image!");
                builder.create().show();
            }
        }
    }

    public int getImageRotateAngle(String imagePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION
                    , ExifInterface.ORIENTATION_NORMAL);
            int orientation = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = -90;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    break;
            }
//            Toast.makeText(this, "Inmethod: " + orientation, Toast.LENGTH_SHORT).show();
            return orientation;
        }
        return 0;
    }

    public Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    private void askPermission(String string,int n){
        if (ContextCompat.checkSelfPermission(getApplication(),
                string)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    string)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{string},n);
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{string},n);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted


        }
    }

    public void showDialog(String status){
        AlertDialog alertDialog = new AlertDialog.Builder(
                this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Status");

        // Setting Dialog Message
        alertDialog.setMessage(status);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                startActivity(new Intent(ReportScreen.this,HomeScreen.class));
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}

