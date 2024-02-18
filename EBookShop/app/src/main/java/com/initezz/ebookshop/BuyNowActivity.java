package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.initezz.ebookshop.model.Item;
import com.initezz.ebookshop.model.Order;
import com.initezz.ebookshop.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class BuyNowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker_current, marker_pin;
    private com.google.android.gms.location.LocationRequest locationRequest;

    private LatLng dLocation;

    private NotificationManager notificationManager;
    private String channelId = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ///////////////////////Notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "INFO", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("This is Information");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0, 1000, 1000, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        ///////////////////////Notification

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText addressLine1EditText = findViewById(R.id.addressline1);
        EditText addressLine2EditText = findViewById(R.id.address2);
        EditText addressCityEditText = findViewById(R.id.city);
        EditText addressPostalEditText = findViewById(R.id.postalcode);
        EditText mobileEditText = findViewById(R.id.mobile);

        TextView totlePrice1 = findViewById(R.id.totlePrice);

        String itemPrice = getIntent().getExtras().getString("price");
        String itemQty = getIntent().getExtras().getString("qty");
        String pid = getIntent().getExtras().getString("pid");
        String availableQty = getIntent().getExtras().getString("availableQty");

        Integer total = Integer.valueOf(Integer.valueOf(itemPrice) * Integer.valueOf(itemQty));

        totlePrice1.setText("Total : Rs."+total.toString()+".00");

        //////////////////////////////Load Data
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            firestore.collection("Users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                String userId = documentSnapshot.getId();
                                User user = documentSnapshot.toObject(User.class);

                                if (user.getLine1() != null) {
                                    addressLine1EditText.setText(user.getLine1());
                                }
                                if (user.getLine2() != null) {
                                    addressLine2EditText.setText(user.getLine2());
                                }
                                if (user.getCity() != null) {
                                    addressCityEditText.setText(user.getCity());
                                }
                                if (user.getPostal() != null) {
                                    addressPostalEditText.setText(user.getPostal());
                                }
                                if (user.getMobile() != null) {
                                    mobileEditText.setText(user.getMobile());
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BuyNowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(BuyNowActivity.this, "Please Login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BuyNowActivity.this, LoginActivity.class));
        }
        //////////////////////////////Load Data

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //////////////////////////////Checkout btn
        findViewById(R.id.countinueButton).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                EditText mobileView = findViewById(R.id.mobile);
                EditText address1View = findViewById(R.id.addressline1);
                EditText address2View = findViewById(R.id.address2);
                EditText cityView = findViewById(R.id.city);
                EditText postalcodeView = findViewById(R.id.postalcode);

                String address1 = address1View.getText().toString().replace(',', ' ');
                String address2 = address2View.getText().toString().replace(',', ' ');
                String city = cityView.getText().toString().replace(',', ' ');
                String postalcode = postalcodeView.getText().toString().replace(',', ' ');
                String mobile = mobileView.getText().toString();

                if (address1.isEmpty()) {
                    Toast.makeText(BuyNowActivity.this, "Please enter Address line1", Toast.LENGTH_SHORT).show();
                } else if (address2.isEmpty()) {
                    Toast.makeText(BuyNowActivity.this, "Please enter Address line2", Toast.LENGTH_SHORT).show();
                } else if (city.isEmpty()) {
                    Toast.makeText(BuyNowActivity.this, "Please enter City", Toast.LENGTH_SHORT).show();
                } else if (postalcode.isEmpty()) {
                    Toast.makeText(BuyNowActivity.this, "Please enter Postal code", Toast.LENGTH_SHORT).show();
                } else if (mobile.isEmpty()) {
                    Toast.makeText(BuyNowActivity.this, "Please enter Mobile", Toast.LENGTH_SHORT).show();
                } else {

                    if (dLocation != null && marker_current != null) {

                        String ref = String.valueOf(System.currentTimeMillis());

                        LocalDateTime currentDateTime = LocalDateTime.now();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = currentDateTime.format(formatter);

                        Order order = new Order(ref, firebaseAuth.getCurrentUser().getEmail(), total.toString(), mobile, address1, address2, city, postalcode, String.valueOf(dLocation.latitude), String.valueOf(dLocation.longitude), formattedDateTime, 0);

                        firestore.collection("Users")
                                .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String userId = documentSnapshot.getId();

                                            firestore.collection("Orders")
                                                    .add(order)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            String orderDocId = documentReference.getId();

//                                                            for (Item cartitem : items) {

                                                            firestore.collection("Items").whereEqualTo("id", pid).get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                                Item addproduct = snapshot.toObject(Item.class);

                                                                                firestore.collection("Items/")
                                                                                        .whereEqualTo("id", addproduct.getId())
                                                                                        .get()
                                                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                                                                                    Integer nowQty = Integer.parseInt(addproduct.getQty()) - Integer.parseInt(itemQty);

                                                                                                    HashMap<String, Object> data = new HashMap<>();
                                                                                                    data.put("qty", String.valueOf(nowQty));

                                                                                                    firestore.document("Items/" + documentSnapshot.getId())
                                                                                                            .update(data)
                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {

                                                                                                                    addproduct.setQty(itemQty);

                                                                                                                    firestore.collection("Orders/" + orderDocId + "/product")
                                                                                                                            .add(addproduct)
                                                                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(DocumentReference documentReference) {

                                                                                                                                    /////////////////////Notification
                                                                                                                                    Intent intent = new Intent(BuyNowActivity.this,PurchasHistoryFragment.class);
                                                                                                                                    intent.putExtra("name","ABCD");

                                                                                                                                    PendingIntent pendingIntent = PendingIntent
                                                                                                                                            .getActivity(BuyNowActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

                                                                                                                                    Notification notification = new NotificationCompat.Builder(getApplicationContext(),channelId)
                                                                                                                                            .setSmallIcon(R.drawable.ic_stat_name)
                                                                                                                                            .setContentTitle("Order Confirmation")
                                                                                                                                            .setContentText("Your Order Confirmed Soon")
                                                                                                                                            .setColor(Color.RED)
                                                                                                                                            .setContentIntent(pendingIntent)
                                                                                                                                            .build();

                                                                                                                                    notificationManager.notify(1,notification);
                                                                                                                                    /////////////////////Notification

                                                                                                                                    Toast.makeText(getApplicationContext(), "Confirmed Your Order.", Toast.LENGTH_SHORT).show();
                                                                                                                                    startActivity(new Intent(BuyNowActivity.this, HomeActivity.class));
                                                                                                                                    finish();

                                                                                                                                }
                                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                                                @Override
                                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            })
                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                                @Override
                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            }
                                                                                        });


                                                                            }
                                                                        }
                                                                    });

//                                                            }


                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {
                        Toast.makeText(BuyNowActivity.this, "Please select your deliver location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //////////////////////////////Checkout btn

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                if (latLng != null && marker_current != null) {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    dLocation = latLng;
                    marker_current.setPosition(latLng);
                }
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if (checkPermissions()) {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }

                return false;
            }
        });

        if (checkPermissions()) {
            map.setMyLocationEnabled(true);
            getLastLocation();
        } else {
            requestPermissions(
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }

    }

    public void moveCamera(LatLng latLng) {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15f)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        dLocation = latLng;
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
            });

            //Current location live update
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setWaitForAccurateLocation(true)
                    .setMinUpdateIntervalMillis(500)
                    .setMaxUpdateDelayMillis(1000)
                    .build();

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            currentLocation = locationResult.getLastLocation();
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            dLocation = latLng;

            if (marker_current == null) {
                MarkerOptions options = new MarkerOptions()
                        .title("My Location")
                        .position(latLng);
                marker_current = map.addMarker(options);
            } else {
                marker_current.setPosition(latLng);
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Snackbar.make(findViewById(R.id.container), "Location permission denied", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    private boolean checkPermissions() {
        boolean permission = false;

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            permission = true;
        }
        return permission;
    }
}