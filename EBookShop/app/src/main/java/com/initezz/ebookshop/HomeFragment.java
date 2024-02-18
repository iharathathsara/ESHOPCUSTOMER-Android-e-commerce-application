package com.initezz.ebookshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.initezz.ebookshop.adapter.home.CategoryAdapter;
import com.initezz.ebookshop.adapter.home.ItemAdapter;
import com.initezz.ebookshop.listner.home.CategerySelectLisner;
import com.initezz.ebookshop.listner.home.ItemSelectListner;
import com.initezz.ebookshop.model.Category;
import com.initezz.ebookshop.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements CategerySelectLisner, ItemSelectListner {
    View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Category> categories;
    private ArrayList<Item> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //////////////////////////////Home Slider
        ImageSlider imageSlider = view.findViewById(R.id.home_image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<SlideModel>(); // Create image list
        slideModels.add(new SlideModel(R.drawable.banner, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        //////////////////////////////Home Slider


        //////////////////////////////Home Load Categories
        categories = new ArrayList<>();
        RecyclerView categoryView = view.findViewById(R.id.category_list_recycle_view);
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, getActivity().getApplicationContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryView.setLayoutManager(linearLayoutManager);
        categoryView.setAdapter(categoryAdapter);
        firestore.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Category category = change.getDocument().toObject(Category.class);
                    switch (change.getType()) {
                        case ADDED:
                            categories.add(category);
                        case MODIFIED:
                            Category old = categories.stream().filter(i -> i.getCategoryName().equals(category.getCategoryName())).findFirst().orElse(null);
                            if (old != null) {
                                old.setCategoryName(category.getCategoryName());
                                old.setCategoryImage(category.getCategoryImage());
                            }
                            break;
                        case REMOVED:
                            categories.remove(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });
        //////////////////////////////Home Load Categories

        //////////////////////////////Home Load Item
        items = new ArrayList<>();
        RecyclerView itemView = view.findViewById(R.id.item_list_recycle_view);
        ItemAdapter itemAdapter = new ItemAdapter(items, getActivity().getApplicationContext(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        itemView.setLayoutManager(layoutManager);
        itemView.setAdapter(itemAdapter);
        firestore.collection("Items")
                .whereEqualTo("status","true")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    Item item = change.getDocument().toObject(Item.class);
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
                itemAdapter.notifyDataSetChanged();
            }

        });
        //////////////////////////////Home Load Item
        return view;
    }

    @Override
    public void categotyItemView(Category category) {
        Intent intent = (new Intent(getActivity().getApplicationContext(), CategoryItemActivity.class));
        intent.putExtra("categoryName", category.getCategoryName());
        startActivity(intent);
    }

    @Override
    public void singleItemView(Item item) {
        Intent intent = (new Intent(getActivity().getApplicationContext(), SingleItemActivity.class));
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
                                                                                                        Toast.makeText(getActivity().getApplicationContext(), "Already product have in your cart and increased qty.", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        // Update failed
                                                                                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });

                                                                                    }else{
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "Not enought stock", Toast.LENGTH_SHORT).show();
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
                                                                            Toast.makeText(getActivity().getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    });

                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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


                } else {
                    startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                }


    }
}