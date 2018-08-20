package io.github.pulakdp.winkltask.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.github.pulakdp.winkltask.R;
import io.github.pulakdp.winkltask.model.PhotoResponse;

public class MainActivity extends AppCompatActivity {

    public static int currentPosition;
    public static List<PhotoResponse.Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoList = new ArrayList<>();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new BrowserFragment())
                .commit();
    }
}
