package com.sep.assignment1.model;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sep.assignment1.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private ArrayList<Menu> mMenuList;
    private Context mContext;
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMenuNameTv;
        private ImageView mMenuImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mMenuNameTv = (TextView) itemView.findViewById(R.id.menu_item_name_tv);
            mMenuImageView = (ImageView) itemView.findViewById(R.id.menu_item_iv);
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
        holder.mMenuNameTv.setText(menu.getMenuName());
        if(!menu.getImageURL().equals("")) Picasso.with(mContext).load(menu.getImageURL()).into(holder.mMenuImageView);
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }
}
