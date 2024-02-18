package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.initezz.ebookshop.model.Category;
import com.initezz.ebookshop.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryItemActivity extends AppCompatActivity implements ItemSelectListner {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Category> categories;
    private ArrayList<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String categoryName = getIntent().getExtras().getString("categoryName");

        TextView catNameTxt=findViewById(R.id.categoryNameTitle);
        catNameTxt.setText(categoryName);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //////////////////////////////Home Load Item
        items = new ArrayList<>();
        RecyclerView itemView = findViewById(R.id.categoryRecycleView);
        ItemAdapter itemAdapter = new ItemAdapter(items, CategoryItemActivity.this, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        itemView.setLayoutManager(layoutManager);
        itemView.setAdapter(itemAdapter);
        firestore.collection("Items")
                .whereEqualTo("status","true")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Item item = change.getDocument().toObject(Item.class);
                    if(item.getCategory().equals(categoryName)){
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
            }
        });
        //////////////////////////////Home Load Item
    }

    @Override
    public void singleItemView(Item item) {
        Intent intent = (new Intent(CategoryItemActivity.this, SingleItemActivity.class));
        intent.putExtra("itemId", item.getId());
        startActivity(intent);
    }

    @Override
    public void addToCart(Item item) {

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

                                                                                    if((Integer.parseInt(qty)+1) <= Integer.parseInt(item.getQty())){


                                                                                        Map<String, Object> data = new HashMap<>();
                                                                                        data.put("id", item.getId());
                                                                                        data.put("qty", Integer.parseInt(qty)+1);

                                                                                        firestore.document("Users/" + userId + "/cart/"+productDoc)
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

                                                                                    }else{
                                                                                        Toast.makeText(getApplicationContext(), "Not enought stock", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        }else{

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
}