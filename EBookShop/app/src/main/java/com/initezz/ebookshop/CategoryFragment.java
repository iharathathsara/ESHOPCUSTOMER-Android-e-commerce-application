package com.initezz.ebookshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.adapter.home.CategoryAdapter;
import com.initezz.ebookshop.listner.home.CategerySelectLisner;
import com.initezz.ebookshop.model.Category;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements CategerySelectLisner {

    View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Category> categories;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_category, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        //////////////////////////////Home Load Categories
        categories = new ArrayList<>();
        RecyclerView categoryView = view.findViewById(R.id.categoriesRecycleView);
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, getActivity().getApplicationContext(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        categoryView.setLayoutManager(layoutManager);
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


        return view;
    }

    @Override
    public void categotyItemView(Category category) {
        Intent intent = (new Intent(getActivity().getApplicationContext(), CategoryItemActivity.class));
        intent.putExtra("categoryName", category.getCategoryName());
        startActivity(intent);
    }
}