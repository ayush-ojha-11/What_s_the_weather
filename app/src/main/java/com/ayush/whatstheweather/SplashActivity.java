package com.ayush.whatstheweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class SplashActivity extends AppCompatActivity {
    double lat,lon;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_FINE_LOCATION_REQUEST = 100;
    String urlForMetricData = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=00b57cd25d3c916baef0cda450370eb3&units=metric";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);   //Dark Mode for this app is disabled
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
    public  void makeRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, urlForMetricData, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setWeatherFields(response);
                        Toast.makeText(getApplicationContext(), "Got the Weather response...", Toast.LENGTH_SHORT).show();

                    }
                },1500);
                System.out.println(response);
            }
        }, error -> Toast.makeText(getApplicationContext(),"Error in getting weather response!",Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(weatherRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ForLocation() {


        final  LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener((OnSuccessListener<Location>) location -> {
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        urlForMetricData = String.format(urlForMetricData, lat, lon);
                        makeRequest();
                        new Handler().postDelayed(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                TextView text = (TextView) findViewById(R.id.textView9);
                                text.setText("Latitude = "+lat+ " , "+"Longitude = "+lon);
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        },1000);
                    }
                    else
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                              if  (! Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER) ){
                                  buildAlertMessageNoGps();
                              }
                              else{
                                  Toast.makeText(getApplicationContext(),"\t\t\tProblem in getting location,\nTry Searching by typing CITY name!",Toast.LENGTH_LONG).show();
                                  Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                                  intent.putExtra("Check","false");
                                  startActivity(intent);
                              }
                            }
                        },1000);
                    }
                });

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION}, MY_FINE_LOCATION_REQUEST);
                ForLocation();
            }
        }



    public void buildAlertMessageNoGps(){
        final  AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage("It seems your GPS is off. Enable it?").setCancelable(false).setPositiveButton("YES", (dialog, which) -> {
            Intent showGPSSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(showGPSSettings);
        }).setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(this,"Search weather of any CITY by typing correct City Name",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            intent.putExtra("Check","false");
            startActivity(intent);

        });
        final AlertDialog Alert =builder.create();
        Alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        ForLocation();
    }
    public void setWeatherFields(JSONObject response){
        try {
            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            String description = jsonObjectWeather.getString("description");
            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            double temp = jsonObjectMain.getDouble("temp");
            double feelsLike = jsonObjectMain.getDouble("feels_like");
            double humidity = jsonObjectMain.getDouble("humidity");
            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
            String wind = jsonObjectWind.getString("speed");
            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
            String country =jsonObjectSys.getString("country");
            String sunrise = jsonObjectSys.getString("sunrise");
            String sunset = jsonObjectSys.getString("sunset");

            double imperialTemp = temp * 1.8 +32;
            double imperialFeelsLike = feelsLike * 1.8 + 32;
            double WindSpeedInMilesPerHour = Double.parseDouble(wind) * 2.237;

            // Rounding off to 2 decimal places

            BigDecimal bigDecimalTemp =  BigDecimal.valueOf(imperialTemp);
            double tempInF = (bigDecimalTemp.setScale(2, RoundingMode.HALF_UP)).doubleValue();

            BigDecimal bigDecimalFeels =  BigDecimal.valueOf(imperialFeelsLike);
            double feelsLikeInF = (bigDecimalFeels.setScale(2,RoundingMode.HALF_UP)).doubleValue();

            BigDecimal bigDecimalWind =  BigDecimal.valueOf(WindSpeedInMilesPerHour);
            double windSpeedInMilesPerHour = (bigDecimalWind.setScale(2,RoundingMode.HALF_UP)).doubleValue();



            //Weather call Time
            @SuppressLint("SimpleDateFormat") DateFormat cf = new SimpleDateFormat("HH:mm:ss");
            String timeOfCall = cf.format(Calendar.getInstance().getTime());

            //sunrise time
            long dv = Long.parseLong(sunrise)*1000; // it needs to be in milliseconds
            Date df = new java.util.Date(dv);
            @SuppressLint("SimpleDateFormat") String sunriseTime = new SimpleDateFormat("HH:mm:ss").format(df);

            //sunset time
            long ev = Long.parseLong(sunset)*1000;
            Date ef = new java.util.Date(ev);
            @SuppressLint("SimpleDateFormat") String sunsetTime = new SimpleDateFormat("HH:mm:ss").format(ef);

            //Checking Day or Night
            System.out.println(sunriseTime);
            System.out.println(sunsetTime);

            String[] tc = timeOfCall.split(":");
            String[] sr = sunriseTime.split(":");
            String[] ss = sunsetTime.split(":");
            String DayOrNight ="";

            if(Integer.parseInt(tc[0]) >= Integer.parseInt(sr[0])  && Integer.parseInt(tc[0]) < Integer.parseInt(ss[0]))
            {
                DayOrNight = "day";
            }
            else DayOrNight = "night";
            System.out.println(DayOrNight);

            String city = jsonResponse.getString("name");
            Intent mainactivityintent = new Intent(SplashActivity.this, MainActivity.class);
            mainactivityintent.putExtra("ImperialTemp",Double.toString(tempInF));
            mainactivityintent.putExtra("ImperialFeelsLike",Double.toString(feelsLikeInF));
            mainactivityintent.putExtra("ImperialWindSpeed",Double.toString(windSpeedInMilesPerHour));
            mainactivityintent.putExtra("CityName",city);
            mainactivityintent.putExtra("Country",country);
            mainactivityintent.putExtra("Temperature",Double.toString(temp));
            mainactivityintent.putExtra("Humidity",Double.toString(humidity));
            mainactivityintent.putExtra("Description",description);
            mainactivityintent.putExtra("WindSpeed",wind);
            mainactivityintent.putExtra("Feels",Double.toString(feelsLike));
            mainactivityintent.putExtra("DayOrNight",DayOrNight);
            mainactivityintent.putExtra("Check","true");
            startActivity(mainactivityintent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}








