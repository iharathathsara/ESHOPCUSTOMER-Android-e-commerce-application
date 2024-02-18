package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.initezz.ebookshop.broadcast.MyBroadCastReciever;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener, SensorEventListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private SensorManager sensorManager;
    private Sensor accelometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        MyBroadCastReciever mbr = new MyBroadCastReciever();
        registerReceiver(mbr,intentFilter);


        ///////////////////////////Screen Shot
        requestPermissions(new String[]{
                android.Manifest.permission.ACTIVITY_RECOGNITION
        }, 100);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (accelometer != null) {
            sensorManager.registerListener(HomeActivity.this, accelometer, SensorManager.SENSOR_DELAY_UI);
        }
        ///////////////////////////Screen Shot

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolBar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        loadFragment(new HomeFragment());


        EditText search = findViewById(R.id.textInputSearch);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    // Handle search action
                    showSearchResult(search.getText().toString());
                    return true;
                }
                return false;
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index for the drawableEnd
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                        showSearchResult(search.getText().toString());

                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navHome) {
            loadFragment(new HomeFragment());
        } else if (item.getItemId() == R.id.navCategory) {
            loadFragment(new CategoryFragment());
        } else if (item.getItemId() == R.id.navProfile) {
            loadFragment(new ProfileFragment());
        } else if (item.getItemId() == R.id.sideNavLogin) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else if (item.getItemId() == R.id.navCart) {
            loadFragment(new CartFragment());
        } else if (item.getItemId() == R.id.sideNavSetting) {
            loadFragment(new SettingFragment());
        } else if (item.getItemId() == R.id.sideNavOrders) {
            loadFragment(new PurchasHistoryFragment());
        } else if (item.getItemId() == R.id.sideNavLogout) {
            firebaseAuth.signOut();
            Toast.makeText(this, "Log Out Success", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double value = Math.floor(x * x + y * y + z * z);

            if (value > 300) {

                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shutter);
                try {
                    // Create a bitmap from the root view
                    View rootView = getWindow().getDecorView().getRootView();
                    Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(screenshotBitmap);
                    rootView.draw(canvas);

                    // Save the bitmap to a file
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String fileName = "screenshot_" + timeStamp + ".png";
                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File screenshotFile = new File(directory,"Screenshots/"+ fileName);

                    FileOutputStream outputStream = new FileOutputStream(screenshotFile);
                    screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    // Notify the user
                    Toast.makeText(HomeActivity.this, "Screenshot saved to " + screenshotFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mediaPlayer.start();


            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void showSearchResult(String text){
        Bundle bundle=new Bundle();
        bundle.putString("searchText",text);

        ShopFragment shopFragment=new ShopFragment();
        shopFragment.setArguments(bundle);

        loadFragment(shopFragment);

    }

}