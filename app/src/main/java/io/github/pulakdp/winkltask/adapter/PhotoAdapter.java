package io.github.pulakdp.winkltask.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.github.pulakdp.winkltask.R;
import io.github.pulakdp.winkltask.databinding.GridItemPhotoBinding;
import io.github.pulakdp.winkltask.model.PhotoResponse.Photo;
import io.github.pulakdp.winkltask.ui.MainActivity;
import io.github.pulakdp.winkltask.util.AppUtil;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<Photo> data;
    private ViewHolderListener listener;

    public PhotoAdapter(Context context, ViewHolderListener listener) {
        data = MainActivity.photoList;
        this.context = context;
        this.listener = listener;
    }

    public interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridItemPhotoBinding itemBinding =
                GridItemPhotoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PhotoViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = getPhotoItemForPosition(position);
        holder.getBinding().photoTitle.setText(photo.getTitle());
        holder.getBinding().photo.setTransitionName(photo.getTitle());
        Glide.with(context)
                .load(photo.getPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        listener.onLoadCompleted(holder.getBinding().photo, position);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onLoadCompleted(holder.getBinding().photo, position);
                        Bitmap photo = ((BitmapDrawable) resource).getBitmap();
                        Palette.from(photo).generate(palette -> {
                            if (palette == null)
                                return;

                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                int vibrantColor = swatch.getRgb();
                                int textColor = AppUtil.getBlackOrWhiteColor(vibrantColor);
                                holder.getBinding().photoFooter.setBackgroundColor(vibrantColor);
                                holder.getBinding().photoTitle.setTextColor(textColor);
                            } else {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                if (mutedSwatch != null) {
                                    int mutedColor = mutedSwatch.getRgb();
                                    int textColor = AppUtil.getBlackOrWhiteColor(mutedColor);
                                    holder.getBinding().photoFooter.setBackgroundColor(mutedColor);
                                    holder.getBinding().photoTitle.setTextColor(textColor);
                                }
                            }
                        });
                        return false;
                    }
                })
                .into(holder.getBinding().photo);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<Photo> photos) {
        data = photos;
        notifyDataSetChanged();
    }

    private Photo getPhotoItemForPosition(int position) {
        return data.get(position);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private GridItemPhotoBinding binding;

        public PhotoViewHolder(GridItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClicked(view, getAdapterPosition());
        }

        public GridItemPhotoBinding getBinding() {
            return binding;
        }
    }
}
