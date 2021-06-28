package com.ayush.whatstheweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AdView adView;
    private FrameLayout adContainerView;

    String C1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    String C2 = "&appid=00b57cd25d3c916baef0cda450370eb3&units=metric";
    String CityUrl;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(222,222,222));
        setInitialWeather();

      //new one mediation

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                loadAd();
            }
        });
    }
    public void loadAd(){

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.ad_view_container);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-2994133956240875/7595589571");
        adContainerView.addView(adView);
        loadBanner();

    }

    private void loadBanner() {

        AdSize adSize = getAdSize();

        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();



        searchView.setQueryHint("Search with City name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CityUrl = C1 + query + C2;
                makeCityWeatherRequest();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_refresh: {
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_exit: {
                finishAffinity();
                System.exit(0);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }

    public void makeCityWeatherRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, CityUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                searchCityWeather(response);
                Toast.makeText(getApplicationContext(), "Wow! Got Searched City Weather Response", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to get Response!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(weatherRequest);
    }

    @SuppressLint("SetTextI18n")
    public void searchCityWeather(JSONObject response) {

        try {
            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
            TextView cityName = findViewById(R.id.city);
            TextView tempView = findViewById(R.id.temp);
            TextView feelsView = findViewById(R.id.feels);
            TextView humidView = findViewById(R.id.humid);
            TextView windView = findViewById(R.id.wspeed);
            TextView desView = findViewById(R.id.des);
            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            String description = jsonObjectWeather.getString("description");

            String iconDecider = jsonObjectWeather.getString("icon");
            ImageView weatherIcon = findViewById(R.id.weathericon);

            String iconUrl = "https://openweathermap.org/img/wn/"+iconDecider+"@2x.png";

            System.out.println("Icon : "+ iconDecider);

            Picasso.get().load(iconUrl).into(weatherIcon);


            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            double temp = jsonObjectMain.getDouble("temp");
            double feelsLike = jsonObjectMain.getDouble("feels_like");
            double humidity = jsonObjectMain.getDouble("humidity");
            Double.toString(temp);
            Double.toString(feelsLike);
            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
            String wind = jsonObjectWind.getString("speed");
            JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");

            String country = jsonObjectSys.getString("country");
            String city = jsonResponse.getString("name");
            cityName.setText(city + ", " + country);
            tempView.setText(temp + "°C");
            feelsView.setText(feelsLike + "°C");
            humidView.setText(Double.toString(humidity)+"%");
            windView.setText(wind+" m/s");
            desView.setText(description);
            TextView timeView = findViewById(R.id.timeview);
            DateFormat df = new SimpleDateFormat("dd-MMM, h:mm a");
            String time = df.format(Calendar.getInstance().getTime());
            timeView.setText("  Last Updated : \n" + time);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setInitialWeather() { //Locaation Based Weather data

        TextView cityName = findViewById(R.id.city);
        TextView tempView = findViewById(R.id.temp);
        TextView feelsView = findViewById(R.id.feels);
        TextView humidView = findViewById(R.id.humid);
        TextView windView = findViewById(R.id.wspeed);
        TextView desView = findViewById(R.id.des);
        TextView timeView = findViewById(R.id.timeview);
        String sunriseTimeOfInitial = getIntent().getStringExtra("LocSunrise");
        String sunsetTimeOfInitial = getIntent().getStringExtra("LocSunset");
        String coun = getIntent().getStringExtra("Country");
        String a = getIntent().getStringExtra("CityName");
        cityName.setText(a + "," + coun);
        String b = getIntent().getStringExtra("Temperature");
        tempView.setText(b + "°C");
        String c = getIntent().getStringExtra("Feels");
        feelsView.setText(c + "°C");
        String d = getIntent().getStringExtra("Humidity");
        humidView.setText(d + "%");
        String e = getIntent().getStringExtra("Description");
        desView.setText(e);
        String f = getIntent().getStringExtra("WindSpeed");
        windView.setText(f+" m/s");
        DateFormat df = new SimpleDateFormat("dd-MMM, h:mm a");
        String timeOfCall = df.format(Calendar.getInstance().getTime());
        timeView.setText("  Last Updated : \n" + timeOfCall);
        ImageView weatherIcon = findViewById(R.id.weathericon);
        String dayNight = getIntent().getStringExtra("DayOrNight");
        if (dayNight.equals("day")) {

            switch (e) {
                case "clear sky":
                    weatherIcon.setImageResource(R.drawable.sunny);
                    break;
                case "few clouds":
                    weatherIcon.setImageResource(R.drawable.cloud);
                    break;
                case "broken clouds":
                    weatherIcon.setImageResource(R.drawable.pcloud);
                    break;
                case "scattered clouds":
                    weatherIcon.setImageResource(R.drawable.pcloud);
                    break;
                case "overcast clouds":
                    weatherIcon.setImageResource(R.drawable.cloud3);
                    break;
                case "light rain":
                case "drizzle":
                case "light intensity drizzle":
                case "light intensity drizzle rain":
                case "drizzle rain":
                    weatherIcon.setImageResource(R.drawable.sunrain);
                    break;
                case "moderate rain":
                case "shower rain":
                case "light intensity shower rain":
                case "heavy intensity shower rain":
                case "ragged shower rain":
                    weatherIcon.setImageResource(R.drawable.littlerain);
                    break;
                case "heavy intensity rain":
                case "very heavy rain":
                case "extreme rain":
                case "heavy intensity drizzle":
                case "shower rain and drizzle":
                case "heavy shower rain and drizzle":
                case "shower drizzle":
                    weatherIcon.setImageResource(R.drawable.raining);
                    break;
                case "freezing rain":
                    weatherIcon.setImageResource(R.drawable.rain_or_snow);
                    break;

                case "thunderstorm":
                case "light thunderstorm":
                    weatherIcon.setImageResource(R.drawable.thundercloud);
                    break;

                case "heavy thunderstorm":
                case "ragged thunderstorm":
                    weatherIcon.setImageResource(R.drawable.heavythunder);
                    break;

                case "thunderstorm with rain":
                case "thunderstorm with light rain":
                case "thunderstorm with heavy rain":
                case "thunderstorm with drizzle":
                case "thunderstorm with light drizzle":
                case "thunderstorm with heavy drizzle":
                    weatherIcon.setImageResource(R.drawable.thunderrain);
                    break;

                case "mist":
                case "squalls":
                    weatherIcon.setImageResource(R.drawable.mistyyy);
                    break;

                case "sand/dust whirls":
                case "sand":
                case "dust":
                    weatherIcon.setImageResource(R.drawable.sandstormsun);
                    break;

                case "smoke":
                    weatherIcon.setImageResource(R.drawable.smoke);
                    break;

                case "haze":
                    weatherIcon.setImageResource(R.drawable.haze);
                    break;
                case "fog":
                    weatherIcon.setImageResource(R.drawable.fog);
                    break;

                case "volcanic ash":
                    weatherIcon.setImageResource(R.drawable.volc);
                    break;

                case "tornado":
                    weatherIcon.setImageResource(R.drawable.tornado2);
                    break;

                case "Snow":
                case "light snow":
                case "Heavy snow":
                case "Sleet":
                    weatherIcon.setImageResource(R.drawable.snowcloud);
                    break;

                case "Light shower sleet":
                case "Shower sleet":
                case "Light rain and snow":
                case "Rain and snow":
                case "Light shower snow":
                case "Shower snow":
                case "Heavy shower snow":
                    weatherIcon.setImageResource(R.drawable.snowfall);
                    break;
            }
        }
        else { // for night
            switch (e) {
                case "clear sky":
                    weatherIcon.setImageResource(R.drawable.nightsky);
                    break;
                case "few clouds":
                    weatherIcon.setImageResource(R.drawable.cloud2);
                    break;
                case "broken clouds":
                    weatherIcon.setImageResource(R.drawable.cloudnight);
                    break;
                case "scattered clouds":
                    weatherIcon.setImageResource(R.drawable.cloudnight);
                    break;
                case "overcast clouds":
                    weatherIcon.setImageResource(R.drawable.cloud3);
                    break;
                case "light rain":
                case "drizzle":
                case "light intensity drizzle":
                case "light intensity drizzle rain":
                case "drizzle rain":
                    weatherIcon.setImageResource(R.drawable.nightrain);
                    break;
                case "moderate rain":
                case "shower rain":
                case "light intensity shower rain":
                case "heavy intensity shower rain":
                case "ragged shower rain":
                    weatherIcon.setImageResource(R.drawable.nightrain);
                    break;

                case "heavy intensity rain":
                case "very heavy rain":
                case "extreme rain":
                case "heavy intensity drizzle":
                case "shower rain and drizzle":
                case "heavy shower rain and drizzle":
                case "shower drizzle":
                    weatherIcon.setImageResource(R.drawable.raining);
                    break;
                case "freezing rain":
                    weatherIcon.setImageResource(R.drawable.rain_or_snow);
                    break;

                case "thunderstorm":
                case "light thunderstorm":
                    weatherIcon.setImageResource(R.drawable.nightthunder);
                    break;

                case "heavy thunderstorm":
                case "ragged thunderstorm":
                    weatherIcon.setImageResource(R.drawable.heavythunder);
                    break;

                case "thunderstorm with rain":
                case "thunderstorm with light rain":
                case "thunderstorm with heavy rain":
                case "thunderstorm with drizzle":
                case "thunderstorm with light drizzle":
                case "thunderstorm with heavy drizzle":
                    weatherIcon.setImageResource(R.drawable.thunderrain);
                    break;

                case "mist":
                case "squalls":
                    weatherIcon.setImageResource(R.drawable.mistyyy);
                    break;

                case "sand/dust whirls":
                case "sand":
                case "dust":
                    weatherIcon.setImageResource(R.drawable.sandnight);
                    break;

                case "smoke":
                    weatherIcon.setImageResource(R.drawable.smoke);
                    break;

                case "haze":
                    weatherIcon.setImageResource(R.drawable.haze);
                    break;

                case "fog":
                    weatherIcon.setImageResource(R.drawable.fognight);
                    break;

                case "volcanic ash":
                    weatherIcon.setImageResource(R.drawable.volc);
                    break;

                case "tornado":
                    weatherIcon.setImageResource(R.drawable.tornado2);
                    break;

                case "Snow":
                case "light snow":
                case "Heavy snow":
                case "Sleet":
                    weatherIcon.setImageResource(R.drawable.nightsnow);
                    break;

                case "Light shower sleet":
                case "Shower sleet":
                case "Light rain and snow":
                case "Rain and snow":
                case "Light shower snow":
                case "Shower snow":
                case "Heavy shower snow":
                    weatherIcon.setImageResource(R.drawable.snowfall);
                    break;
            }

        }
    }
}
















