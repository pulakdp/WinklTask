package io.github.pulakdp.winkltask.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import io.github.pulakdp.winkltask.R;
import io.github.pulakdp.winkltask.adapter.PhotoPagerAdapter;
import io.github.pulakdp.winkltask.databinding.FragmentFullScreenBinding;

public class FullScreenFragment extends Fragment {

    private FragmentFullScreenBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_full_screen, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.photoPager.setAdapter(new PhotoPagerAdapter(this, MainActivity.photoList));
        binding.photoPager.setCurrentItem(MainActivity.currentPosition);
        binding.photoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MainActivity.currentPosition = position;
            }
        });
        prepareSharedElementTransition();
        postponeEnterTransition();
    }

    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.photo_shared_element_transition);
        setSharedElementEnterTransition(transition);

        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                        Fragment currentFragment = (Fragment) binding.photoPager.getAdapter()
                                .instantiateItem(binding.photoPager, MainActivity.currentPosition);
                        View view = currentFragment.getView();
                        if (view == null) {
                            return;
                        }

                        sharedElements.put(names.get(0), view.findViewById(R.id.photo));
                    }
                });
    }
}
