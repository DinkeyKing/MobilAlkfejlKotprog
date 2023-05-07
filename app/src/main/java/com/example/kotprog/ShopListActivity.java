package com.example.kotprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = false;
    private FrameLayout redCircle;

    private FrameLayout cart;
    private TextView contentTextView;

    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null){
            Log.d(LOG_TAG,"User == null");
            finish();
        }
        else {
            Log.d(LOG_TAG,"User ok");
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemList);

        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        typeSpinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemTypeFilter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                queryData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void queryData(){
        mItemList.clear();

        if (!typeSpinner.getSelectedItem().equals("Összes")){
            mItems.whereEqualTo("type", typeSpinner.getSelectedItem()).orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    ShoppingItem item = document.toObject(ShoppingItem.class);
                    item.setId(document.getId());
                    mItemList.add(item);
                }

                mAdapter.notifyDataSetChanged();
            });
        }
        else {
            mItems.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ShoppingItem item = document.toObject(ShoppingItem.class);
                    item.setId(document.getId());
                    mItemList.add(item);
                }

                if (mItemList.size() == 0) {
                    initializeData();
                    queryData();
                }

                mAdapter.notifyDataSetChanged();
            });
        }
    }

    public void deleteItem(ShoppingItem item){
        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(unused -> {
            Log.d(LOG_TAG, "Item deleted: " + item._getId());
        });
        ref.delete().addOnSuccessListener(unused -> {
            Log.d(LOG_TAG, "Failed to delete item: " + item._getId());
        });
        queryData();
        NotificationHelper nh = new NotificationHelper(this);
        nh.send("Szőnyeg törölve : "+ item.getName() + "!");
    }
    private void initializeData() {
        Log.d(LOG_TAG, "Data init");
        String[] itemsName = getResources().getStringArray(R.array.itemNames);
        String[] itemsDesc = getResources().getStringArray(R.array.itemDescs);;
        String[] itemsPrice = getResources().getStringArray(R.array.itemPrices);;
        String[] itemsType = getResources().getStringArray(R.array.itemType);;
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.itemImages);
        TypedArray itemsRating = getResources().obtainTypedArray(R.array.itemRatings);

        for (int i = 0; i < itemsName.length; i++) {
            mItems.add(new ShoppingItem(
                    itemsName[i],
                    itemsDesc[i],
                    itemsPrice[i],
                    itemsRating.getFloat(i, 0),
                    itemsImageResource.getResourceId(i, 0),
                    itemsType[i], 0, null));
        }

        itemsImageResource.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                FirebaseAuth.getInstance().signOut();
                Log.d(LOG_TAG, "Log out button clicked.");
                Intent intent0 = new Intent(this, MainActivity.class);
                startActivity(intent0);
                return true;
            case  R.id.addItemButton:
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivity(intent);
                Log.d(LOG_TAG, "Add item button clicked.");
                return true;
            case  R.id.cart:
                Log.d(LOG_TAG, "Cart button clicked.");
                return true;
            case  R.id.view_selector:
                Log.d(LOG_TAG, "View selector button clicked.");
                if (viewRow){
                    changeSpanCount(item, R.drawable.grid_view_icon, 1);
                } else {
                    changeSpanCount(item, R.drawable.row_view_icon, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        cart = (FrameLayout) rootView.findViewById(R.id.cart);
        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item){
        cartItems++;
        if (cartItems > 0){
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });
        queryData();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim2);
        cart.startAnimation(animation);
    }

    public void goToUpdateItem(ShoppingItem item){
        Intent intent = new Intent(this, UpdateItemActivity.class);
        intent.putExtra("id", item._getId());
        startActivity(intent);
    }
}