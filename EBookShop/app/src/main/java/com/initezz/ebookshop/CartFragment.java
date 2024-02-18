package com.initezz.ebookshop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.adapter.cart.CartItemAdapter;
import com.initezz.ebookshop.listner.cart.CartProductSelectListener;
import com.initezz.ebookshop.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements CartProductSelectListener {
View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Item> items;
    private String userId;
    private Integer total=0;
    private TextView totalLabel;
    private CartItemAdapter cartItemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_cart, container, false);

        //////////////////////////////Cart Load Item
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    items = new ArrayList<>();

                    RecyclerView itemView = view.findViewById(R.id.cartRecycleView);

                    cartItemAdapter = new CartItemAdapter(items, getActivity().getApplicationContext(), CartFragment.this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
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
                                                            TextView textView=new TextView(getActivity().getApplicationContext());
                                                            textView.setTextSize(24);
                                                            textView.setTextColor(Color.RED);
                                                            textView.setText("Empty Cart");
                                                            textView.setGravity(Gravity.CENTER);

                                                            FrameLayout frameLayout = view.findViewById(R.id.cartContainer);
                                                            frameLayout.addView(textView);

                                                            view.findViewById(R.id.confirmtBtn).setEnabled(false);

                                                        }

                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }).start();
        } else {
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            getActivity().finish();
        }
        //////////////////////////////Cart Load Item

        view.findViewById(R.id.confirmtBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(),CheckoutActivity.class));
            }
        });


        return view;
    }

    @Override
    public void singleItemView(Item item) {
        Intent intent = (new Intent(getActivity().getApplicationContext(), SingleItemActivity.class));
        intent.putExtra("itemId", item.getId());
        startActivity(intent);
    }

    @Override
    public void increaseQty(Item item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String oldQtyS = item.getQty().toString();
                Integer oldQtyI = Integer.parseInt(oldQtyS);
                Integer newQtyI = oldQtyI + 1;

                firestore.collection("Items").whereEqualTo("id", item.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Item item1 = snapshot.toObject(Item.class);

                            if (item1.getId().equals(item.getId())) {

                                if (newQtyI > Integer.parseInt(item1.getQty())) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Not enough stock", Toast.LENGTH_SHORT).show();
                                } else {
                                    String newQtyS = String.valueOf(newQtyI);
                                    for (Item cartitem : items) {
                                        if (cartitem.getId().equals(item.getId())) {
                                            cartitem.setQty(newQtyS.toString());
                                            break;
                                        }
                                    }

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("id", item.getId());
                                    data.put("qty", newQtyI);

                                    firestore.collection("Users/" + userId + "/cart").whereEqualTo("id", item.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                firestore.document("Users/" + userId + "/cart/" + documentSnapshot.getId()).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Update successful
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Update failed
                                                    }
                                                });
                                                break;
                                            }
                                        }
                                    });

                                    cartItemAdapter.notifyDataSetChanged();
                                    updateTotal();

                                }

                                break;
                            }

                        }
                    }
                });


            }
        }).start();
    }

    @Override
    public void decreaseQty(Item item) {

        String oldQtyS = item.getQty().toString();
        Integer oldQtyI = Integer.parseInt(oldQtyS);
        Integer newQtyI = oldQtyI - 1;

        if (newQtyI >= 1) {
            String newQtyS = String.valueOf(newQtyI);
            for (Item cartitem : items) {
                if (cartitem.getId().equals(item.getId())) {
                    cartitem.setQty(newQtyS.toString());
                    break;
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            data.put("qty", newQtyI);

            firestore.collection("Users/" + userId + "/cart").whereEqualTo("id", item.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        firestore.document("Users/" + userId + "/cart/" + documentSnapshot.getId()).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update successful
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Update failed
                            }
                        });
                        break;
                    }
                }
            });

            cartItemAdapter.notifyDataSetChanged();
            updateTotal();

        }


    }

    @Override
    public void removeFromCart(Item item) {

        firestore.collection("Users/"+userId+"/cart").whereEqualTo("id", item.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Delete the document
                                firestore.collection("Users/"+userId+"/cart").document(document.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                for (Item cartitem : items) {
                                                    if (cartitem.getId().equals(item.getId())) {
                                                        items.remove(cartitem);
                                                        cartItemAdapter.notifyDataSetChanged();
                                                        updateTotal();
                                                        break;
                                                    }
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        } else {
                        }
                    }
                });

    }

    public void updateTotal(){
        total=0;
        for (Item cartitem : items) {
            total = total + Integer.valueOf((Integer.valueOf(cartitem.getPrice()) * Integer.valueOf(cartitem.getQty())));
        }
        totalLabel = view.findViewById(R.id.cartTotalPriceTxt);
        totalLabel.setText("Total Price : Rs."+total.toString()+".00");
    }

}