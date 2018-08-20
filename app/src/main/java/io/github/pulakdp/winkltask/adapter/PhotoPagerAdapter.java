package io.github.pulakdp.winkltask.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.github.pulakdp.winkltask.R;
import io.github.pulakdp.winkltask.databinding.FullscreenPhotoBinding;
import io.github.pulakdp.winkltask.model.PhotoResponse;

public class PhotoPagerAdapter extends FragmentStatePagerAdapter {

    private List<PhotoResponse.Photo> photos;

    public PhotoPagerAdapter(Fragment fragment, List<PhotoResponse.Photo> photos) {
        super(fragment.getChildFragmentManager());
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int i) {
        return PhotoFragment.newInstance(photos.get(i));
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    public static class PhotoFragment extends Fragment {

        public static final String PHOTO = "photo";

        private PhotoResponse.Photo photo;
        private FullscreenPhotoBinding binding;

        public static PhotoFragment newInstance(final PhotoResponse.Photo photo) {
            Bundle args = new Bundle();
            args.putSerializable(PHOTO, photo);
            PhotoFragment fragment = new PhotoFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            photo = (PhotoResponse.Photo) getArguments().getSerializable(PHOTO);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fullscreen_photo, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            binding.photo.setTransitionName(photo.getTitle());
            loadPhoto();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }

        private void loadPhoto() {
            Glide.with(this)
                    .load(photo.getPhotoUrl())
                    .apply(new RequestOptions().error(R.drawable.placeholder))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            getParentFragment().startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            getParentFragment().startPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(binding.photo);
        }
    }
}
