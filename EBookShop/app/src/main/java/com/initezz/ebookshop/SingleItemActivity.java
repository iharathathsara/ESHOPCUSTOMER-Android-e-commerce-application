package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.adapter.home.ItemAdapter;
import com.initezz.ebookshop.listner.home.ItemSelectListner;
import com.initezz.ebookshop.model.Item;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SingleItemActivity extends AppCompatActivity implements ItemSelectListner, SensorEventListener {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private String category;
    private ArrayList<Item> items;
    private FirebaseAuth firebaseAuth;
    private Integer availableQty;
    private Item item;

    private String itemPrice;

    private SensorManager sensorManager;
    private Sensor accelometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

///////////////////////////Screen Shot
        requestPermissions(new String[]{
                android.Manifest.permission.ACTIVITY_RECOGNITION
        }, 100);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelometer != null) {
            sensorManager.registerListener(SingleItemActivity.this, accelometer, SensorManager.SENSOR_DELAY_UI);
        }
        ///////////////////////////Screen Shot

        String id = getIntent().getExtras().getString("itemId");

        EditText selectQty = findViewById(R.id.itemSelectQty);
//        inputQty = Integer.parseInt(selectQty.getText().toString());
        selectQty.setText("1");

        TextView titleText = findViewById(R.id.itemTitle);
        TextView descText = findViewById(R.id.itemDesc);
        TextView priceText = findViewById(R.id.itemPrice);
        TextView qtyText = findViewById(R.id.itemQty);

        ImageView itemImg1 = findViewById(R.id.itemImg1);
        ImageView itemImg2 = findViewById(R.id.itemImg2);
        ImageView itemImg3 = findViewById(R.id.itemImg3);

        firestore.collection("Items")
                .whereEqualTo("status","true")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            item = snapshot.toObject(Item.class);

                            if (item.getId().equals(id)) {
                                titleText.setText(item.getTitle());
                                descText.setText(item.getDescription());
                                priceText.setText("Rs." + item.getPrice() + ".00");
                                qtyText.setText("Quantity : " + item.getQty());

                                itemPrice = item.getPrice();

                                availableQty = Integer.parseInt(item.getQty());

                                category = item.getCategory();

                                if(item.getQty().equals("0")){
                                    Button buyNowBtn=findViewById(R.id.buyNowBtn);
                                    buyNowBtn.setEnabled(false);
                                    ImageButton addToCartBtn=findViewById(R.id.addToCartBtn);
                                    addToCartBtn.setVisibility(View.GONE);

                                }

                                //////////////////////////////Home Slider
                                ImageSlider imageSlider = findViewById(R.id.singleItemImageSlider);
                                ArrayList<SlideModel> slideModels = new ArrayList<SlideModel>(); // Create image list
                                slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/ebooks-17a8c.appspot.com/o/item_img%2F" + item.getImage1Path() + "?alt=media", ScaleTypes.CENTER_INSIDE));
                                slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/ebooks-17a8c.appspot.com/o/item_img%2F" + item.getImage2Path() + "?alt=media", ScaleTypes.CENTER_INSIDE));
                                slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/ebooks-17a8c.appspot.com/o/item_img%2F" + item.getImage3Path() + "?alt=media", ScaleTypes.CENTER_INSIDE));
                                imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                                //////////////////////////////Home Slider

                                storage.getReference("item_img/" + item.getImage1Path())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(itemImg1);
                                            }
                                        });
                                storage.getReference("item_img/" + item.getImage2Path())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(itemImg2);
                                            }
                                        });
                                storage.getReference("item_img/" + item.getImage3Path())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .centerCrop()
                                                        .into(itemImg3);
                                            }
                                        });
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                findViewById(R.id.loader).setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }).start();
                                break;
                            }

                        }
                    }
                });

//////////////////////////////Home Load Item
        items = new ArrayList<>();
        RecyclerView itemView = findViewById(R.id.recItems);
        ItemAdapter itemAdapter = new ItemAdapter(items, this, this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL,false);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        itemView.setLayoutManager(layoutManager);
        itemView.setAdapter(itemAdapter);
        firestore.collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Item item = change.getDocument().toObject(Item.class);
                    if (item.getCategory().equals(category)) {
                        switch (change.getType()) {
                            case ADDED:
                                items.add(item);
                            case MODIFIED:
                                Item old = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().orElse(null);
                                if (old != null) {
                                    old.setTitle(item.getTitle());
                                    old.setPrice(item.getPrice());
                                    old.setQty(item.getQty());
                                }
                                break;
                            case REMOVED:
                                items.remove(item);
                        }
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }
        });
        //////////////////////////////Home Load Item

        findViewById(R.id.qtyAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldQtyS = selectQty.getText().toString();
                Integer oldQtyI = Integer.parseInt(oldQtyS);
                Integer newQtyI = oldQtyI + 1;
                if (newQtyI > availableQty) {
                    Toast.makeText(SingleItemActivity.this, "Not availble quantities", Toast.LENGTH_SHORT).show();
                } else {
                    String newQtyS = String.valueOf(newQtyI);
                    selectQty.setText(newQtyS);
                }
            }
        });

        findViewById(R.id.qtyMinusBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldQtyS = selectQty.getText().toString();
                Integer oldQtyI = Integer.parseInt(oldQtyS);
                Integer newQtyI = oldQtyI - 1;
                if (newQtyI == 0) {
                    Toast.makeText(SingleItemActivity.this, "Quantity should be greater than 1", Toast.LENGTH_SHORT).show();
                } else {
                    String newQtyS = String.valueOf(newQtyI);
                    selectQty.setText(newQtyS);
                }
            }
        });

        //////////////////////////////Add To Cart
        findViewById(R.id.addToCartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {

                    firestore.collection("Users")
                            .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String userId = documentSnapshot.getId();

                                        firestore.collection("Users/" + userId + "/cart")
                                                .whereEqualTo("id", item.getId())
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if (queryDocumentSnapshots.size() > 0) {
                                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                                String productDoc = documentSnapshot.getId();

                                                                firestore.collection("Users/" + userId + "/cart")
                                                                        .whereEqualTo("id", item.getId())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                                                                    String qty = snapshot.getData().get("qty").toString();

                                                                                    if ((Integer.parseInt(qty) + 1) <= Integer.parseInt(item.getQty())) {


                                                                                        Map<String, Object> data = new HashMap<>();
                                                                                        data.put("id", item.getId());
                                                                                        data.put("qty", Integer.parseInt(qty) + 1);

                                                                                        firestore.document("Users/" + userId + "/cart/" + productDoc)
                                                                                                .update(data)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        // Update successful
                                                                                                        Toast.makeText(getApplicationContext(), "Already product have in your cart and increased qty.", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        // Update failed
                                                                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });

                                                                                    } else {
                                                                                        Toast.makeText(getApplicationContext(), "Not enought stock", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        } else {

                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("id", item.getId());
                                                            data.put("qty", 1);

                                                            firestore.collection("Users")
                                                                    .document(userId)
                                                                    .collection("cart")
                                                                    .add(data)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
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


                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
        //////////////////////////////Add To Cart

        findViewById(R.id.buyNowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SingleItemActivity.this, BuyNowActivity.class)
                        .putExtra("price", itemPrice)
                        .putExtra("qty", selectQty.getText().toString())
                        .putExtra("pid", id)
                        .putExtra("availableQty", availableQty)
                );

            }
        });

    }

    @Override
    public void singleItemView(Item item) {
        Intent intent = (new Intent(SingleItemActivity.this, SingleItemActivity.class));
        intent.putExtra("itemId", item.getId());
        startActivity(intent);
    }

    @Override
    public void addToCart(Item item1) {
//        findViewById(R.id.addToCartBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {

            firestore.collection("Users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String userId = documentSnapshot.getId();

                                firestore.collection("Users/" + userId + "/cart")
                                        .whereEqualTo("id", item1.getId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.size() > 0) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        String productDoc = documentSnapshot.getId();

                                                        firestore.collection("Users/" + userId + "/cart")
                                                                .whereEqualTo("id", item1.getId())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                                                            String qty = snapshot.getData().get("qty").toString();

                                                                            if ((Integer.parseInt(qty) + 1) <= Integer.parseInt(item1.getQty())) {


                                                                                Map<String, Object> data = new HashMap<>();
                                                                                data.put("id", item1.getId());
                                                                                data.put("qty", Integer.parseInt(qty) + 1);

                                                                                firestore.document("Users/" + userId + "/cart/" + productDoc)
                                                                                        .update(data)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                // Update successful
                                                                                                Toast.makeText(getApplicationContext(), "Already product have in your cart and increased qty.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                // Update failed
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });

                                                                            } else {
                                                                                Toast.makeText(getApplicationContext(), "Not enought stock", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    }
                                                                });

                                                    }
                                                } else {

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("id", item.getId());
                                                    data.put("qty", 1);

                                                    firestore.collection("Users")
                                                            .document(userId)
                                                            .collection("cart")
                                                            .add(data)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
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


                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
//            }
//        });

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
                    Toast.makeText(SingleItemActivity.this, "Screenshot saved to " + screenshotFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
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
}