package com.initezz.ebookshop.adapter.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.R;
import com.initezz.ebookshop.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>{
    private ArrayList<Item> items;
    private Context context;
    private FirebaseStorage storage;

    public OrderSummaryAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public OrderSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_summary_item_list, parent, false);
        return new OrderSummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item item = items.get(position);

        holder.itemTitleText.setText(item.getTitle());
        holder.itemPriceText.setText("Rs."+item.getPrice()+".00");
        holder.itemQtyText.setText("Quantity : "+item.getQty());

        storage.getReference("item_img/" + item.getImage1Path())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .centerCrop()
                                .into(holder.image);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitleText, itemPriceText,itemQtyText;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleText = itemView.findViewById(R.id.itemTitleTxt);
            itemPriceText = itemView.findViewById(R.id.itemPriceTxt);
            itemQtyText = itemView.findViewById(R.id.itemQtyTxt);
            image = itemView.findViewById(R.id.itemImg);
        }
    }
}
