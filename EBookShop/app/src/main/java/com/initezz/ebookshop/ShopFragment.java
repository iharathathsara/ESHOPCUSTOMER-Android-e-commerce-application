package com.initezz.ebookshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.adapter.home.SearchResultAdapter;
import com.initezz.ebookshop.listner.home.SearchSelectListener;
import com.initezz.ebookshop.model.Item;

import java.util.ArrayList;

public class ShopFragment extends Fragment implements SearchSelectListener {

    View view;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private ArrayList<Item> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Bundle bundle = this.getArguments();
        String searchTxt = bundle.getString("searchText");
//        Toast.makeText(getActivity().getApplicationContext(), searchTxt, Toast.LENGTH_SHORT).show();

        showSearchResult(searchTxt);



        EditText search = getActivity().findViewById(R.id.textInputSearch);



//        if (!searchTxt.isEmpty()) {
//            search.setText(searchTxt);
//            showSearchResult(searchTxt);
//        } else {
//            search.setText("");
//            showSearchResult("");
//        }

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
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
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                        showSearchResult(search.getText().toString());

                        return true;
                    }
                }
                return false;
            }
        });

        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    showSearchResult(search.getText().toString());
                }
                return false;
            }
        });


        return view;
    }

    public void showSearchResult(String text) {

        items = new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.shopRecycleView);

        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(items, getActivity().getApplicationContext(), ShopFragment.this);
        GridLayoutManager layoutManager1 = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        itemView.setLayoutManager(layoutManager1);

        itemView.setAdapter(searchResultAdapter);

        firestore.collection("Items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Item item = snapshot.toObject(Item.class);
                                if (item.getTitle().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getCategory().toLowerCase().contains(text.toLowerCase())) {
                                    items.add(item);
                                }
                            }

                            if (items.size() <= 0) {

                                view.findViewById(R.id.noResult).setVisibility(View.VISIBLE);

                            } else {

                                view.findViewById(R.id.noResult).setVisibility(View.GONE);

                                searchResultAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }

    @Override
    public void viewProduct(Item item) {

    }
}