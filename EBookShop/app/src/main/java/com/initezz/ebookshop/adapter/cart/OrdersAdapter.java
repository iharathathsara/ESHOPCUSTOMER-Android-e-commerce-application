package com.initezz.ebookshop.adapter.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.initezz.ebookshop.R;
import com.initezz.ebookshop.listner.cart.OrderSelectListner;
import com.initezz.ebookshop.model.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{
    private ArrayList<Order> orders;
    private Context context;
    private OrderSelectListner orderSelectListner;

    public OrdersAdapter(ArrayList<Order> orders, Context context, OrderSelectListner orderSelectListner) {
        this.orders = orders;
        this.context = context;
        this.orderSelectListner = orderSelectListner;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.orders_list, parent, false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orders.get(position);

        holder.orderIdTxt.setText(order.getId());
        holder.orderDateTxt.setText(order.getDate_time());
        holder.orderPriceTxt.setText(order.getTotal());

        if(order.getDeliver_status()==0){
            holder.orderStatusTxt.setText("Pending");
        }else if(order.getDeliver_status()==1){
            holder.orderStatusTxt.setText("Confirmed");
        }else if(order.getDeliver_status()==2){
            holder.orderStatusTxt.setText("Delivered");
        }

        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.selectOrder(orders.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt,orderPriceTxt,orderStatusTxt;
        View orderCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdText);
            orderDateTxt = itemView.findViewById(R.id.orderDateText);
            orderPriceTxt = itemView.findViewById(R.id.orderPriceText);
            orderStatusTxt = itemView.findViewById(R.id.orderStatusText);
            orderCard = itemView.findViewById(R.id.orderCard);
        }
    }
}
