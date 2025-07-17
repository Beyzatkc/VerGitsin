package com.Beem.vergitsin.Profil;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfilFotoAdapter extends RecyclerView.Adapter<ProfilFotoAdapter.PhotoViewHolder> {

    private final String[] fotokeys;
    private final OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClicked(String photoKey);
    }

    public ProfilFotoAdapter(String[] photoKeys, OnPhotoClickListener listener) {
        this.fotokeys = photoKeys;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        int size = (int) (parent.getResources().getDisplayMetrics().widthPixels / 3.5);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        imageView.setPadding(12, 12, 12, 12);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new PhotoViewHolder(imageView);
    }



    @Override
    public void onBindViewHolder(@NonNull ProfilFotoAdapter.PhotoViewHolder holder, int position) {
        String fotoanahtar = fotokeys[position];
        int resId = holder.imageView.getContext().getResources()
                .getIdentifier(fotoanahtar, "drawable", holder.imageView.getContext().getPackageName());

        holder.imageView.setImageResource(resId);
        holder.imageView.setOnClickListener(v -> listener.onPhotoClicked(fotoanahtar));
    }

    @Override
    public int getItemCount() {
        return fotokeys.length;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}