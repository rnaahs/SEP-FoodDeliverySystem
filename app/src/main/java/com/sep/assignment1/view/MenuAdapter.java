package com.sep.assignment1.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sep.assignment1.R;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.Food;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private ArrayList<Menu> mMenuList;
    private Context mContext;
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTv;
        private TextView mPriceTv;
        private TextView mDescriptionTv;
        private Button mEditBtn;
        private Button mDeleteBtn;
        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTv = (TextView) itemView.findViewById(R.id.menu_item_name_tv);
            mPriceTv = (TextView) itemView.findViewById(R.id.menu_item_price_tv);
            mDescriptionTv = (TextView) itemView.findViewById(R.id.menu_item_description_tv);
            mEditBtn = (Button) itemView.findViewById(R.id.menu_item_edit_btn);
            mImageView = (ImageView) itemView.findViewById(R.id.menu_item_iv);
            mEditBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
            mDeleteBtn = (Button) itemView.findViewById(R.id.menu_item_delete_btn);
            mDeleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public MenuAdapter(ArrayList<Menu> menus, Context context) {
        mMenuList = menus;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Menu menu = mMenuList.get(position);
        Food food = menu.getFoodById(menu.getFoodId());
        holder.mImageView.setImageResource(mContext.getResources().getIdentifier("/drawable"+menu.getMenuImgURL(),null , mContext.getPackageName()));
        holder.mNameTv.setText(food.getFoodName());
        holder.mPriceTv.setText("$"+food.getFoodPrice());
        holder.mDescriptionTv.setText(menu.getDescription());
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }
}
