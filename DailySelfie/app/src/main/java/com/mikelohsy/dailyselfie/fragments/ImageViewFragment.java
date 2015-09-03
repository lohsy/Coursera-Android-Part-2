package com.mikelohsy.dailyselfie.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikelohsy.dailyselfie.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageViewFragment extends Fragment {
    final String TAG = "DAILY_SELFIE";
    public static final String FRAGMENT_TAG = "com.mikelohsy.dailyselfie.imageviewfragment";

    private static final String FILEPATH = "com.mikelohsy.dailyselfie.imageviewfragment.filepath";

    private String mFilePath;
    private ImageView mImageView;

    public static ImageViewFragment newInstance(String filePath) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(FILEPATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilePath = getArguments().getString(FILEPATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageview);
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
        mImageView.setImageBitmap(bitmap);

        return view;
    }


}
