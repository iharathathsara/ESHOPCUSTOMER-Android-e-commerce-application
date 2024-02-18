package com.initezz.ebookshop.adapter.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.initezz.ebookshop.R;
import com.initezz.ebookshop.listner.home.CategerySelectLisner;
import com.initezz.ebookshop.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<Category> categories;
    private Context context;
    private FirebaseStorage storage;
    private CategerySelectLisner categerySelectLisner;

    public CategoryAdapter(ArrayList<Category> categories, Context context,CategerySelectLisner categerySelectLisner) {
        this.categories = categories;
        this.context = context;
        this.categerySelectLisner = categerySelectLisner;
        this.storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_category_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categories.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());

        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categerySelectLisner.categotyItemView(categories.get(position));
            }
        });

        storage.getReference("category_img/"+category.getCategoryImage())
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
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        ImageView image;
        CardView itemCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.homeCategoryName);
            image = itemView.findViewById(R.id.homeCategoryImg);
            itemCard=itemView.findViewById(R.id.itemCard);
        }
    }
}
