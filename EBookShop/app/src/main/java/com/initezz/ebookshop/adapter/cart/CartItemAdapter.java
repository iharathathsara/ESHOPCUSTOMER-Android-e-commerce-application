package com.initezz.ebookshop.adapter.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.R;
import com.initezz.ebookshop.listner.cart.CartProductSelectListener;
import com.initezz.ebookshop.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{
    private ArrayList<Item> items;
    private Context context;
    private FirebaseStorage storage;
    private CartProductSelectListener cartProductSelectListener;

    public CartItemAdapter(ArrayList<Item> items, Context context, CartProductSelectListener cartProductSelectListener) {
        this.items = items;
        this.context = context;
        this.cartProductSelectListener = cartProductSelectListener;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_item_list, parent, false);
        return new CartItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item item = items.get(position);

        holder.itemTitleText.setText(item.getTitle());
        holder.itemPriceText.setText(item.getPrice());
        holder.itemQtyText.setText(item.getQty());

        holder.increseQtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProductSelectListener.increaseQty(items.get(position));
            }
        });
        holder.decreaseQtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProductSelectListener.decreaseQty(items.get(position));
            }
        });
        holder.removeCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProductSelectListener.removeFromCart(items.get(position));
            }
        });
        holder.cart_Item_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProductSelectListener.singleItemView(items.get(position));
            }
        });

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
        TextView itemTitleText, itemPriceText;
        EditText itemQtyText;
        ImageView image;
        View cart_Item_View;
        ImageButton removeCartButton;
        ImageButton increseQtyBtn;
        ImageButton decreaseQtyBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleText = itemView.findViewById(R.id.itemTitleTxt);
            itemPriceText = itemView.findViewById(R.id.itemPriceTxt);
            itemQtyText = itemView.findViewById(R.id.cartItemQty);
            image = itemView.findViewById(R.id.itemImg);
            cart_Item_View = itemView.findViewById(R.id.itemCardView);
            removeCartButton = itemView.findViewById(R.id.removeCartBtn);
            increseQtyBtn = itemView.findViewById(R.id.increaseQtyBtn);
            decreaseQtyBtn = itemView.findViewById(R.id.decreaseQtyBtn);
        }
    }
}
