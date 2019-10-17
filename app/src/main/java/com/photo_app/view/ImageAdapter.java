package com.photo_app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.photo_app.R;
import com.photo_app.model.data.Size;

import java.util.List;


class ImageAdapter extends BaseAdapter {

    private List<Size> mDataSet;

    @Override
    public int getCount() {
        if (mDataSet != null) {
            return mDataSet.size();
        }
        return -1;
    }

    @Override
    public Size getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.photo_tile_item, parent, false);
            imageView = view.findViewById(R.id.photo_item);
        } else {
            imageView = (ImageView) convertView;
        }
        fetchImage(parent.getContext(), getItem(position), imageView);
        return imageView;
    }

    void updateData(List<Size> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    private void fetchImage(@NonNull Context context, Size dataItem, final ImageView imageView) {
        if (!TextUtils.isEmpty(dataItem.getSource())) {
            Glide.with(context)
                    .asBitmap()
                    .load(dataItem.getSource())
                    .apply(new RequestOptions().override(dataItem.getWidth(), dataItem.getHeight()))
                    .centerCrop()
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            setImage(imageView, null);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            setImage(imageView, resource);
                            return true;
                        }
                    }).submit();
        } else {
            setImage(imageView, null);
        }
    }

    private void setImage(@NonNull final ImageView imageView, @NonNull final Bitmap resource) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(resource);
            }
        });
    }
}