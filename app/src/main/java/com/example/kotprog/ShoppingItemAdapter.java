package com.example.kotprog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder>
implements Filterable {
    private ArrayList<ShoppingItem> mShoppingItemData;
    private ArrayList<ShoppingItem> mShoppingItemDataAll;
    private Context mContext;
    private int lastPosition = -1;

    private FirebaseStorage mStorage;
    private StorageReference storageRef;

    public ShoppingItemAdapter(Context mContext, ArrayList<ShoppingItem> mShoppingItemDataAll) {
        this.mShoppingItemDataAll = mShoppingItemDataAll;
        this.mShoppingItemData = mShoppingItemDataAll;
        this.mContext = mContext;
        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemData.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mShoppingItemDataAll.size();
                results.values = mShoppingItemDataAll;
            } else  {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ShoppingItem item : mShoppingItemDataAll){
                    if (item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingItemData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mDescText;
        private TextView mPriceText;
        private TextView mTypeText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemNameTextView);
            mDescText = itemView.findViewById(R.id.itemDescTextView);
            mPriceText = itemView.findViewById(R.id.itemPriceTextView);
            mTypeText = itemView.findViewById(R.id.itemTypeTextView);
            mItemImage = itemView.findViewById(R.id.itemImageView);
            mRatingBar = itemView.findViewById(R.id.itemRatingBar);
        }

        public void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mDescText.setText(currentItem.getDesc());
            mPriceText.setText(currentItem.getPrice());
            mTypeText.setText(currentItem.getType());
            mRatingBar.setRating(currentItem.getRating());

            mItemImage.invalidate();

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            if (currentItem.getImagePath() != null) {
                StorageReference ref = storageRef.child(currentItem.getImagePath());
                GlideApp.with(mContext).load(ref).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mItemImage);
            }

            itemView.findViewById(R.id.toCartButton).setOnClickListener(view -> {
                Log.d("Activity", "Add to cart clicked.");
                ((ShopListActivity)mContext).updateAlertIcon(currentItem);
            });
            itemView.findViewById(R.id.deleteButton).setOnClickListener(view -> {
                Log.d("Activity", "Delete item clicked.");
                ((ShopListActivity)mContext).deleteItem(currentItem);
            });
            itemView.findViewById(R.id.updateButton).setOnClickListener(view -> {
                Log.d("Activity", "Update item clicked.");
                ((ShopListActivity)mContext).goToUpdateItem(currentItem);
            });
        }
    }
}
