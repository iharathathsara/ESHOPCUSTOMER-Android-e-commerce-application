package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.adapter.cart.OrderSummaryAdapter;
import com.initezz.ebookshop.model.Item;
import com.initezz.ebookshop.model.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Item> items;
    private String userId;
    private Integer total=0;
    private TextView totalLabel;
    private OrderSummaryAdapter cartItemAdapter;
    private int x=0;
    private NotificationManager notificationManager;
    private String channelId = "info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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


        //////////////////////////////Cart Load Item
        String mobile = getIntent().getExtras().getString("mobile");
        String address1 = getIntent().getExtras().getString("address1");
        String address2 = getIntent().getExtras().getString("address2");
        String city = getIntent().getExtras().getString("city");
        String postal = getIntent().getExtras().getString("postal");
        String latitude = getIntent().getExtras().getString("latitude");
        String longitude = getIntent().getExtras().getString("longitude");

        TextView uname = findViewById(R.id.username);
        uname.setText(firebaseAuth.getCurrentUser().getDisplayName());

        TextView uemail = findViewById(R.id.email);
        uemail.setText(firebaseAuth.getCurrentUser().getEmail());

        TextView umobile = findViewById(R.id.mobile);
        umobile.setText(mobile);

        TextView uaddress1 = findViewById(R.id.addressline1);
        uaddress1.setText(address1);

        TextView uaddress2 = findViewById(R.id.addressline2);
        uaddress2.setText(address2);

        TextView ucity = findViewById(R.id.ucity);
        ucity.setText(city);

        TextView upostal = findViewById(R.id.upostal);
        upostal.setText(postal);

        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    items = new ArrayList<>();

                    RecyclerView itemView = findViewById(R.id.cartRecycleView);

                    cartItemAdapter = new OrderSummaryAdapter(items, OrderSummaryActivity.this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderSummaryActivity.this);
                    itemView.setLayoutManager(linearLayoutManager);

                    itemView.setAdapter(cartItemAdapter);

                    firestore.collection("Users")
                            .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        userId = documentSnapshot.getId();

                                        firestore.collection("Users/" + userId + "/cart")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                        if (task.isSuccessful()&&task.getResult().size()>0) {

                                                            CollectionReference collectionReference = firestore.collection("Items");
                                                            HashMap<String, String> pInfo = new HashMap<>();
                                                            int numberOfIds = task.getResult().size();
                                                            int index = 0;

                                                            String[] idsArray = new String[numberOfIds];


                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                String id = snapshot.getData().get("id").toString();
                                                                pInfo.put(id, snapshot.getData().get("qty").toString());
                                                                idsArray[index++] = id;
                                                            }

                                                            List<String> idsToMatch = Arrays.asList(idsArray);

                                                            Query query = collectionReference.whereIn("id", idsToMatch);


                                                            query.get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            items.clear();

                                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                                Item item = snapshot.toObject(Item.class);

                                                                                items.add(new Item(item.getId(), item.getTitle(), item.getPrice(), pInfo.get(item.getId()), item.getImage1Path()));

                                                                            }

                                                                            cartItemAdapter.notifyDataSetChanged();
                                                                            updateTotal();
                                                                        }
                                                                    });

                                                        }else{
                                                            TextView textView=new TextView(OrderSummaryActivity.this);
                                                            textView.setTextSize(24);
                                                            textView.setTextColor(Color.RED);
                                                            textView.setText("Empty Cart");
                                                            textView.setGravity(Gravity.CENTER);

                                                            FrameLayout frameLayout = findViewById(R.id.cartContainer);
                                                            frameLayout.addView(textView);

                                                        }

                                                    }
                                                });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OrderSummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }).start();
        } else {
            startActivity(new Intent(OrderSummaryActivity.this, LoginActivity.class));
            finish();
        }
        //////////////////////////////Cart Load Item

        //////////////////////////////Confirm Btn
        findViewById(R.id.confirmtBtn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String ref = String.valueOf(System.currentTimeMillis());

                LocalDateTime currentDateTime = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);

                Order order = new Order(ref, firebaseAuth.getCurrentUser().getEmail(), total.toString(), mobile, address1,address2,city,postal, latitude, longitude, formattedDateTime, 0);

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


                                                    for (Item cartitem : items) {

                                                        firestore.collection("Items").whereEqualTo("id", cartitem.getId()).get()
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

                                                                                                Integer nowQty = Integer.parseInt(addproduct.getQty()) - Integer.parseInt(cartitem.getQty());

                                                                                                HashMap<String, Object> data = new HashMap<>();
                                                                                                data.put("qty", String.valueOf(nowQty));

                                                                                                firestore.document("Items/" + documentSnapshot.getId())
                                                                                                        .update(data)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {

                                                                                                                addproduct.setQty(cartitem.getQty());

                                                                                                                firestore.collection("Orders/" + orderDocId + "/product")
                                                                                                                        .add(addproduct)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(DocumentReference documentReference) {

                                                                                                                                firestore.collection("Users/" + userId + "/cart/")
                                                                                                                                        .get()
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                                                                                                                        firestore.collection("Users/" + userId + "/cart/").document(document.getId()).delete();
                                                                                                                                                        int count = items.size();
                                                                                                                                                        x++;
                                                                                                                                                        if (count == x) {

                                                                                                                                                            /////////////////////Notification
                                                                                                                                                            Intent intent = new Intent(OrderSummaryActivity.this,PurchasHistoryFragment.class);
                                                                                                                                                            intent.putExtra("name","ABCD");

                                                                                                                                                            PendingIntent pendingIntent = PendingIntent
                                                                                                                                                                    .getActivity(OrderSummaryActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

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
                                                                                                                                                            startActivity(new Intent(OrderSummaryActivity.this,HomeActivity.class));
                                                                                                                                                            finish();                                                                                                                                                                    finish();
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        });

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
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        //////////////////////////////Confirm Btn

    }

    public void updateTotal(){
        total=0;
        for (Item cartitem : items) {
            total = total + Integer.valueOf((Integer.valueOf(cartitem.getPrice()) * Integer.valueOf(cartitem.getQty())));
        }
        totalLabel = findViewById(R.id.cartTotalPriceTxt);
        totalLabel.setText("Total Price : Rs."+total.toString()+".00");
    }
}