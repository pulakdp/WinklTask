package io.github.pulakdp.winkltask.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.pulakdp.winkltask.BuildConfig;
import io.github.pulakdp.winkltask.R;
import io.github.pulakdp.winkltask.adapter.PhotoAdapter;
import io.github.pulakdp.winkltask.databinding.FragmentBrowserBinding;
import io.github.pulakdp.winkltask.model.PhotoResponse;
import io.github.pulakdp.winkltask.rest.FlickrApiClient;
import io.github.pulakdp.winkltask.rest.FlickrApiInterface;
import io.github.pulakdp.winkltask.util.AppUtil;
import io.github.pulakdp.winkltask.views.SquareImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: PulakDebasish
 */

public class BrowserFragment extends Fragment
        implements SearchView.OnQueryTextListener, PhotoAdapter.ViewHolderListener {

    public static final int ITEM_TO_DISPLAY = 20;

    public ObservableBoolean isEmpty = new ObservableBoolean(true);
    public ObservableBoolean isError = new ObservableBoolean(false);
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableBoolean isDisplay = new ObservableBoolean(false);

    private FragmentBrowserBinding binding;

    private SearchView searchView;
    private GridLayoutManager layoutManager;
    private PhotoAdapter adapter;
    private FlickrApiInterface flickrApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browser, container, false);
        binding.setFrag(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        setUpRecyclerView();
        prepareTransitions();
        postponeEnterTransition();
        scrollToPosition();
    }

    private void setUpRecyclerView() {
        adapter = new PhotoAdapter(getContext(), this);

        layoutManager = new GridLayoutManager(getContext(), 2);
        binding.photoList.setLayoutManager(layoutManager);
        binding.photoList.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        //noinspection deprecation
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                if (!isDisplay.get())
                    isEmpty.set(true);
                return true;
            }
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query))
            return false;
        hideSoftKeyboard(getActivity());
        if (!AppUtil.hasInternetConnection(getContext())) {
            Toast.makeText(getContext(), "No internet connection, can't search", Toast.LENGTH_SHORT).show();
            return false;
        }
        fetchPhotosFromFlickr(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private void fetchPhotosFromFlickr(String query) {
        isEmpty.set(false);
        isLoading.set(true);
        flickrApi = FlickrApiClient.getClient().create(FlickrApiInterface.class);
        compositeDisposable.add(flickrApi.searchPhotos(BuildConfig.FLICKR_API_KEY, query.trim(), ITEM_TO_DISPLAY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addPhotosToAdapter
                        , this::loadFailed));
    }

    private void loadFailed(Throwable throwable) {
        isLoading.set(false);
        isDisplay.set(false);
        isError.set(true);
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void addPhotosToAdapter(PhotoResponse photoResponse) {
        isLoading.set(false);
        isError.set(false);
        MainActivity.photoList = photoResponse.getPhotos().getPhotoList();
        adapter.setData(MainActivity.photoList);
        isDisplay.set(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void scrollToPosition() {
        binding.photoList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                binding.photoList.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = binding.photoList.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);

                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    binding.photoList.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = binding.photoList
                                .findViewHolderForAdapterPosition(MainActivity.currentPosition);
                        if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                            return;
                        }

                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.photo));
                    }
                });
    }

    public void hideSoftKeyboard(@Nullable Activity activity) {
        if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        }
        if (searchView != null)
            searchView.clearFocus();
    }

    @Override
    public void onLoadCompleted(ImageView view, int adapterPosition) {
        AtomicBoolean enterTransitionStarted = new AtomicBoolean();

        if (MainActivity.currentPosition != adapterPosition) {
            return;
        }
        if (enterTransitionStarted.getAndSet(true)) {
            return;
        }
        startPostponedEnterTransition();
    }

    @Override
    public void onItemClicked(View view, int adapterPosition) {
        MainActivity.currentPosition = adapterPosition;

        if (this.getExitTransition() == null)
            return;
        ((TransitionSet) this.getExitTransition()).excludeTarget(view, true);

        SquareImageView transitioningView = view.findViewById(R.id.photo);

        if (getActivity() == null)
            return;

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(transitioningView, transitioningView.getTransitionName())
                .replace(R.id.fragment_container, new FullScreenFragment())
                .addToBackStack(null)
                .commit();
    }
}
