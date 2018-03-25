package com.example.wandy.waterwastereport.activities;

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
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.wandy.waterwastereport.R;
import com.example.wandy.waterwastereport.callback.OnRequestStringResult;
import com.example.wandy.waterwastereport.config.Configs;
import com.example.wandy.waterwastereport.model.AreaModel;
import com.example.wandy.waterwastereport.model.ConditionModel;
import com.example.wandy.waterwastereport.model.KeyValue;
import com.example.wandy.waterwastereport.model.Language;
import com.example.wandy.waterwastereport.model.PostModel;
import com.example.wandy.waterwastereport.rest.NetworkRequestString;
import com.example.wandy.waterwastereport.rest.UploadFileToServer;
import com.example.wandy.waterwastereport.sqlite.LanguageSql;
import com.example.wandy.waterwastereport.sqlite.SaveSql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 1000;
    private final int CAMERA_REQUEST = 1001;
    private final int HISTORY = 1002;
    ImageView imageView;
    String url;
    EditText editTextDescription;
    ProgressBar progressBar;
    Uri imageUri;
    TextView textPercent, textPost, textViewChooseAction;
    SaveSql saveSql;
    Spinner spinner_area, spinner_condition;
    int selectedCondition = 0;
    int selectedArea = 0;

    boolean hasInternet = false;

    ArrayList<ConditionModel> conditionModels;
    ArrayList<AreaModel> areaModels;

    LanguageSql languageSql;
    ImageView imageViewLanguage;
    Button buttonCam, buttonGal, buttonSave, buttonHis, buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        editTextDescription = findViewById(R.id.edit_description);
        imageViewLanguage = findViewById(R.id.image_language);
        buttonReset = findViewById(R.id.btn_reset);

        languageSql = new LanguageSql(this);
        languageSql.check();
        final Language language = new Language(languageSql.getLanguage());

        conditionModels = new ArrayList<>();
        areaModels = new ArrayList<>();

        imageViewLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (language.currentLanguage.equalsIgnoreCase(Language.KHMER)){
                    languageSql.update(Language.ENGLISH);

                }
                else {
                    languageSql.update(Language.KHMER);

                }

                startActivity(new Intent(view.getContext(), MainActivity.class));
                finish();
            }
        });

        spinner_area = findViewById(R.id.spinner_area);
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedArea = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_condition = findViewById(R.id.spinner_condition);
        spinner_condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCondition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar = findViewById(R.id.progress_bar);
        textPercent = findViewById(R.id.text_percent);
        textPost = findViewById(R.id.text_post);
        textPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(view);
            }
        });

        saveSql = new SaveSql(this);
        @SuppressLint("InflateParams") final View v = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null, false);
        buttonCam =v.findViewById(R.id.btn_cam_dialog);
        buttonGal = v.findViewById(R.id.btn_gal_dialog);
        buttonSave = v.findViewById(R.id.btn_save_dialog);
        buttonHis = v.findViewById(R.id.btn_his_dialog);
        textViewChooseAction = v.findViewById(R.id.text_choose_action);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog builder = new AlertDialog.Builder(view.getContext()).create();

                builder.setView(v);
                builder.show();

                buttonCam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        camera(view);
                        builder.cancel();
                    }
                });

                buttonGal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gallery(view);
                        builder.cancel();
                    }
                });

                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        save(view);
                        builder.cancel();
                    }
                });

                buttonHis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        history(view);
                        builder.cancel();
                    }
                });

            }
        });

        getInfo();

        textPost.setText(language.post);
        buttonHis.setText(language.history);
        buttonSave.setText(language.save);
        buttonGal.setText(language.gallery);
        buttonCam.setText(language.camera);
        editTextDescription.setHint(language.newPost);
        buttonReset.setText(language.reset);
        textViewChooseAction.setText(language.chooseAction);

        imageViewLanguage.setImageDrawable(getResources().getDrawable(language.drawableLanguage));
//        Log.i("xxxxxx", language.newPost);

    }

    void getInfo() {
        new NetworkRequestString(this, Configs.instance.info, new HashMap<String, String>(), new OnRequestStringResult() {
            @Override
            public void onSuccess(String resultString) {
                try {

                    JSONObject result = new JSONObject(resultString);

                    int status = result.getInt("status");
                    if (status == 1) {
                        JSONObject data = result.getJSONObject("data");

                        JSONArray conditionArray = data.getJSONArray("conditions");
                        JSONArray areasArray = data.getJSONArray("areas");

                        String[] condition_array = new String[conditionArray.length()];
                        String[] area_array = new String[areasArray.length()];

                        for (int i = 0; i < conditionArray.length(); i++) {
                            JSONObject object = conditionArray.getJSONObject(i);
                            String id = object.getString("id");
                            String condition = object.getString("condition");
                            String color = object.getString("color");

                            condition_array[i] = condition;

                            conditionModels.add(new ConditionModel(id, condition, color));
                        }

                        for (int i = 0; i < areasArray.length(); i++) {
                            JSONObject object = areasArray.getJSONObject(i);
                            String id = object.getString("id");
                            String area = object.getString("area");
                            area_array[i] = area;
                            areaModels.add(new AreaModel(id, area));

                        }

                        ArrayAdapter adapter_area = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, area_array);
                        adapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_area.setAdapter(adapter_area);

                        ArrayAdapter adapter_condition = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, condition_array);
                        adapter_condition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_condition.setAdapter(adapter_condition);

                        hasInternet = true;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error) {
                hasInternet = false;
            }
        });

    }

    public void gallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void camera(View view) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
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
        arrayList.add(new KeyValue("image", url));
        arrayList.add(new KeyValue("description", editTextDescription.getText().toString()));
        arrayList.add(new KeyValue("lat", latitude + ""));
        arrayList.add(new KeyValue("lng", longitude + ""));
        arrayList.add(new KeyValue("area_id", areaModels.get(selectedArea).getId()));
        arrayList.add(new KeyValue("condition_id", conditionModels.get(selectedCondition).getId()));
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
            }
        }).execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == HISTORY) {
            if (resultCode == RESULT_OK) {
                PostModel postModel = saveSql.getPost(imageReturnedIntent.getIntExtra("id", 0));
                Bitmap b = BitmapFactory.decodeFile(postModel.getImage_url());

                if (b == null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Image not found!");
                        builder.create().show();
                        return;
                }

                imageView.setImageBitmap(b);
                editTextDescription.setText(postModel.getDescription());
                url = postModel.getImage_url();
            }
        }

        if (requestCode == PICK_IMAGE) {

            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                assert selectedImage != null;
                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                this.url = filePath;
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                if (bitmap == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Can't display this image!");
                    builder.create().show();
                    return;
                }

                imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                imageView.setImageBitmap(thumbnail);
                this.url = getRealPathFromURI(imageUri);
                galleryAddPic(this.url);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Can't display this image!");
                builder.create().show();

            }

        }
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

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void reset(){
        editTextDescription.setText("");
        url = null;
        imageView.setImageDrawable(null);
    }

    public void reset(View view) {
        reset();
    }
}
