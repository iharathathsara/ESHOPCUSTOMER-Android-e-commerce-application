package com.initezz.ebookshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.initezz.ebookshop.adapter.cart.OrderSummaryAdapter;
import com.initezz.ebookshop.adapter.cart.OrdersAdapter;
import com.initezz.ebookshop.model.Item;
import com.initezz.ebookshop.model.Order;

import java.util.ArrayList;

public class OrderItemsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Item> items;
    private String userId;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String orderid = getIntent().getExtras().getString("orderId");

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {

            items = new ArrayList<>();
            RecyclerView categoryView = findViewById(R.id.orderItemsView);
            OrderSummaryAdapter ordersAdapter = new OrderSummaryAdapter(items, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            categoryView.setLayoutManager(linearLayoutManager);
            categoryView.setAdapter(ordersAdapter);


            firestore.collection("Orders")
                    .whereEqualTo("id",orderid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                    Order order = snapshot1.toObject(Order.class);

                                    if (order.getId().equals(orderid)) {

                                        orderId = snapshot1.getId();

                                        firestore.collection("Orders/" + orderId + "/product").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot snapshot2 : task.getResult()) {

                                                                Item item = snapshot2.toObject(Item.class);
                                                                items.add(item);

                                                            }
                                                            ordersAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                        break;
                                    }

                                }


                            } else {

                            }
                        }
                    });

        } else {
            startActivity(new Intent(OrderItemsActivity.this, LoginActivity.class));
            finish();
        }

    }
}