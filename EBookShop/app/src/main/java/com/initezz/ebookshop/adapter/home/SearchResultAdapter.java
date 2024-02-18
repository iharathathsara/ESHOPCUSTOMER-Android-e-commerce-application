package com.initezz.ebookshop.adapter.home;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.R;
import com.initezz.ebookshop.listner.home.SearchSelectListener;
import com.initezz.ebookshop.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private Context context;
    private FirebaseStorage storage;
    private SearchSelectListener selectListener;

    public SearchResultAdapter(ArrayList<Item> items, Context context, SearchSelectListener selectListener) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_item_list_view, parent, false);
        return new SearchResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemTitleTextView.setText(item.getTitle());
        holder.itemPriceTextView.setText("Rs."+item.getPrice()+".00");

        if(item.getQty().equals("0")){
            holder.itemQtyTextView.setText("Out Of Stock");
            holder.itemQtyTextView.setTextColor(Color.RED);
            holder.addToCartButton.setVisibility(View.GONE);
        }else{
            holder.itemQtyTextView.setText("Quantity:"+item.getQty());
        }


//        holder.single_Item_View.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                itemSelectListner.singleItemView(items.get(position));
//            }
//        });
//        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                itemSelectListner.addToCart(items.get(position));
//            }
//        });

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
        TextView itemTitleTextView, itemPriceTextView, itemQtyTextView;
        ImageView image;
        View single_Item_View;

        ImageButton addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleTextView = itemView.findViewById(R.id.homeItemTitleText);
            itemPriceTextView = itemView.findViewById(R.id.homeItemPriceText);
            itemQtyTextView = itemView.findViewById(R.id.homeItemQtyText);
            image = itemView.findViewById(R.id.homeItemImageView);
            single_Item_View = itemView.findViewById(R.id.singleItemViewCard);
            addToCartButton = itemView.findViewById(R.id.homeItemAddCartBtn);
        }
    }
}
