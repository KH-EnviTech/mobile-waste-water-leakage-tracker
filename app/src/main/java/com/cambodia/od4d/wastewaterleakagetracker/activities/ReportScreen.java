package com.cambodia.od4d.wastewaterleakagetracker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cambodia.od4d.wastewaterleakagetracker.R;
import com.cambodia.od4d.wastewaterleakagetracker.callback.OnRequestStringResult;
import com.cambodia.od4d.wastewaterleakagetracker.config.Configs;
import com.cambodia.od4d.wastewaterleakagetracker.model.AreaModel;
import com.cambodia.od4d.wastewaterleakagetracker.model.ConditionModel;
import com.cambodia.od4d.wastewaterleakagetracker.model.KeyValue;
import com.cambodia.od4d.wastewaterleakagetracker.model.PostModel;
import com.cambodia.od4d.wastewaterleakagetracker.rest.NetworkRequestString;
import com.cambodia.od4d.wastewaterleakagetracker.rest.UploadFileToServer;
import com.cambodia.od4d.wastewaterleakagetracker.sqlite.SaveSql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportScreen extends AppCompatActivity implements View.OnClickListener{

    private final int CAMERA_REQUEST = 1001;
    private final int HISTORY = 1002;
    String url;
    ProgressBar progressBar;
    Uri imageUri;
    TextView textPercent;
    SaveSql saveSql;
    String selectedCondition = "";
    int selectedArea = 0;

    boolean hasInternet = true;

    ArrayList<AreaModel> areaModels;

    ImageView imageView;
    Button btn_low,btn_medium,btn_serious,btn_reset,btn_submit;
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
        btn_reset = findViewById(R.id.btn_reset);
        btn_submit = findViewById(R.id.btn_submit);
        report = findViewById(R.id.report);
        editTextDescription = findViewById(R.id.description);
        btn_low.setOnClickListener(this);
        btn_medium.setOnClickListener(this);
        btn_serious.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        textPercent = findViewById(R.id.text_percent);
        Uri uri = Uri.parse(intent.getStringExtra("img"));
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            imageView.setImageBitmap(thumbnail);
            imageUri = uri;
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                askPermission(Manifest.permission.ACCESS_FINE_LOCATION,7);
                post(view);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });



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

            Location location = getLocation();
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

            Toast.makeText(this, "Saved to history!!!", Toast.LENGTH_SHORT).show();

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

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", null);
            builder.setTitle("Please Allow Location Service!!!");
            builder.setMessage("Location Service can let us find out clear location of report");
            builder.create().show();
            return null;
        }

        Location location = getLastKnownLocation();
        if (location == null) {
            statusCheck();
            return null;
        }
        return location;
    }

    public void post(View view) {

        Location location = getLocation();

        if (!hasInternet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection, you post saved to history!!!");
            builder.create().show();
            return;

        }

        if (location == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please turn on you location");
            builder.create().show();
            return;
        }
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
                progressBar.setVisibility(View.GONE);
                textPercent.setVisibility(View.GONE);
                reset();
                Intent intent= new Intent(getApplication(),HomeScreen.class);
                finish();
                getApplication().startActivity(intent);
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
            imageView.setImageBitmap(thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Can't display this image!");
                builder.create().show();

            }

        }
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

}

