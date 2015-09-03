package com.mikelohsy.dailyselfie.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikelohsy.dailyselfie.R;

import java.io.File;
import java.util.ArrayList;

public class GridViewFragment extends Fragment {
    final String TAG = "DAILY_SELFIE";

    public static final String FRAGMENT_TAG = "com.mikelohsy.dailyselfie.gridviewfragment";
    private static final String FILEPATHS = "com.mikelohsy.dailyselfie.gridviewfragment.filepaths";

    private GridViewAdapter mAdapter;
    private ArrayList<String> mImageFilePaths;
    private GridViewFragmentListener mListener;

    public interface GridViewFragmentListener {
        public void onPhotoClicked (String imageFilePath);
        public void onPhotoDelete (int index);
    }

    public static GridViewFragment newInstance(ArrayList<String> imageFilePaths) {
        GridViewFragment fragment = new GridViewFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(FILEPATHS, imageFilePaths);
        fragment.setArguments(args);
        return fragment;
    }

    public GridViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mImageFilePaths = getArguments().getStringArrayList(FILEPATHS);
        }
        if(mImageFilePaths == null) {
            Log.e(TAG, "GridView Fragment: mImageFilePaths is null");
        } else {
            for(String s : mImageFilePaths){
                Log.i(TAG, s);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid_view, container, false);
        GridView mGridView = (GridView) view.findViewById(R.id.gridview);
        mAdapter = new GridViewAdapter(mImageFilePaths);

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onPhotoClicked(mImageFilePaths.get(position));
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //Toast.makeText(getActivity(), "long clicked", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPhotoDelete(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GridViewFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GridViewFragmentListener");
        }
    }

    public void update() {
        mAdapter.notifyDataSetChanged();
    }

    public void update(String newPhotoPath) {
        mAdapter.addNewImagePath(newPhotoPath);
    }

    static class Holder {
        ImageView mImageView;
        TextView mTextView;
    }

    class GridViewAdapter extends BaseAdapter {

        ArrayList<String> mImgFilePaths;

        GridViewAdapter (ArrayList<String> mImageFilePaths) {
            mImgFilePaths = mImageFilePaths;
        }

        public void addNewImagePath(String newImageFilePath){
            mImgFilePaths.add(newImageFilePath);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImgFilePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder;

            if (convertView == null) { // have to inflate layout
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.grid_view_item, parent, false);

                holder = new Holder();
                holder.mImageView = (ImageView) convertView.findViewById(R.id.gridview_imageview);
                holder.mTextView = (TextView) convertView.findViewById(R.id.gridview_textview);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            String mPathToImage = mImgFilePaths.get(position);

            if(mPathToImage != null) {
                setPictureAndCaption(holder, mPathToImage);
            } else {
                Log.e(TAG, "GridView Adapter - getView: mPathToImage is null");
            }

            return convertView;
        }

        private void setPictureAndCaption(Holder holder, final String mPathToImage) {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = 8; // scale factor
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mPathToImage, bmOptions);
            holder.mImageView.setImageBitmap(bitmap);

            holder.mTextView.setText(new File(mPathToImage).getName());
        }
    }

}
