package com.initezz.ebookshop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.initezz.ebookshop.adapter.cart.CartItemAdapter;
import com.initezz.ebookshop.adapter.cart.OrdersAdapter;
import com.initezz.ebookshop.adapter.home.CategoryAdapter;
import com.initezz.ebookshop.listner.cart.OrderSelectListner;
import com.initezz.ebookshop.model.Category;
import com.initezz.ebookshop.model.Item;
import com.initezz.ebookshop.model.Order;
import com.initezz.ebookshop.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PurchasHistoryFragment extends Fragment implements OrderSelectListner {
    View view;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Order> orders;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchas_history, container, false);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().isEmailVerified()) {

            orders = new ArrayList<>();
            RecyclerView categoryView = view.findViewById(R.id.ordersList);
            OrdersAdapter ordersAdapter = new OrdersAdapter(orders, getActivity().getApplicationContext(), this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            categoryView.setLayoutManager(linearLayoutManager);
            categoryView.setAdapter(ordersAdapter);

            firestore.collection("Orders")
                    .whereEqualTo("email",firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                    Order order = snapshot1.toObject(Order.class);
                                    if (order.getEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                                        orders.add(order);
                                    }
                                }

                                ordersAdapter.notifyDataSetChanged();
                            } else {

                            }
                        }
                    });

        }else{
                Toast.makeText(getActivity().getApplicationContext(), "Please Verify Your Email.", Toast.LENGTH_SHORT).show();
                firebaseAuth.getCurrentUser().sendEmailVerification();
            }
        } else {
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void selectOrder(Order order) {
        startActivity(new Intent(getActivity().getApplicationContext(),OrderItemsActivity.class)
                .putExtra("orderId",order.getId()));
    }
}