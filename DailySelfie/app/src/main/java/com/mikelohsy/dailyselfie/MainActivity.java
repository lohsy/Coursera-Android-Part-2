package com.mikelohsy.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mikelohsy.dailyselfie.fragments.GridViewFragment;
import com.mikelohsy.dailyselfie.fragments.ImageViewFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//Why cant I use Activity and still have an action bar?
public class MainActivity extends ActionBarActivity implements
        GridViewFragment.GridViewFragmentListener{
    final String TAG = "DAILY_SELFIE";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Preferences
    private static final String NUMBER_OF_SELFIES = "com.mikelohsy.dailyselfie.mainactivity.numselfies";
    private static final String SELFIE = "com.mikelohsy.dailyselfie.mainactivity.selfies";
    private static final String DEFAULT_STRING = "com.mikelohsy.dailyselfie.mainactivity.default";

    private int mNumSelfies = 0;
    private ArrayList<String> mImageFilePaths;
    private String mCurrentPhotoPath = null;

    private static final long ALARM_DELAY = 45000;//120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) != null) { //if using this view
            if (savedInstanceState != null) { // not instantiated first time
                return;
            }

            //load preferences
//            Log.i(TAG, "oncreate");
            loadPreferences();

            //init fragment
            GridViewFragment frag = GridViewFragment.newInstance(mImageFilePaths);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, frag, GridViewFragment.FRAGMENT_TAG)
                    .commit();

            //Set the alarm
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + ALARM_DELAY, ALARM_DELAY,
                    pendingIntent);

            //no need to register receiver as it is already declared in AndroidManifest.xml
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
////        Log.i(TAG, "onresume");
//        loadPreferences();
//    }

    private void updateUI () {
        GridViewFragment myFragment = (GridViewFragment)getFragmentManager()
            .findFragmentByTag(GridViewFragment.FRAGMENT_TAG);
        if (myFragment != null && myFragment.isVisible()) {
        myFragment.update();
        }
    }

    @Override
    //Grid View Fragment Call Back Method
    public void onPhotoClicked(String imageFilePath) {
        //Toast.makeText(this, "Clicked " + imageFilePath, Toast.LENGTH_SHORT).show();
        ImageViewFragment frag = ImageViewFragment.newInstance(imageFilePath);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, ImageViewFragment.FRAGMENT_TAG)
                .addToBackStack(null).commit();
    }

    @Override
    //Call back method to delete item from GridView
    public void onPhotoDelete(int index) {
        Log.i(TAG, "DELETE: " + index + " " + mImageFilePaths.get(index));
        mNumSelfies --;
        new File (mImageFilePaths.get(index)).delete();
        mImageFilePaths.remove(index);
        updateUI();
    }

    @Override
    //To enable back transition to previous fragments
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_take_selfie) {
            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    //Handle result of camera
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //update preferences variables
            mNumSelfies++;
            mImageFilePaths.add(mCurrentPhotoPath);

            //Log
            Log.i(TAG, "Image saved at" + mCurrentPhotoPath +
            "\nmNumSelfies: " + mNumSelfies +
            "\nArrayList Size: " + mImageFilePaths.size());

            //update grid view
            updateUI();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            mCurrentPhotoPath = null;
            try {
                mCurrentPhotoPath = generateImageFileName();
                Log.i(TAG, "Save Photo Path: " + mCurrentPhotoPath);
            } catch (IOException e){
                e.printStackTrace();
            }

            if(mCurrentPhotoPath != null){
                //camera app helps me save photo
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        getURIFromImageFile(mCurrentPhotoPath));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Log.e(TAG, "DispatchTakePictureIntent: mCurrentPhotoPath is null");
            }

        }
    }

    private void loadPreferences () {
        Log.i(TAG, "Loading Preferences");
        mImageFilePaths = new ArrayList<>();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mNumSelfies = prefs.getInt(NUMBER_OF_SELFIES, 0);
        Log.i(TAG, "nNumSeflies: " + mNumSelfies);
        if(mNumSelfies != 0){
            for(int i=1; i<=mNumSelfies; i++){
                String filename = prefs.getString(SELFIE+i, DEFAULT_STRING);
                Log.i(TAG, "Key: " + SELFIE+i + "  " + "filename: " + filename);
                if(filename != null && !filename.contentEquals(DEFAULT_STRING)){
                    mImageFilePaths.add(filename);
                } else {
                    Log.e(TAG, "GetPreferences: Expected Key " + SELFIE+i
                            + " but Key is " + DEFAULT_STRING);
                }
            }
        }

        if(mImageFilePaths.size() != mNumSelfies) {
            Log.e(TAG, "mNumSelfies is " + mNumSelfies +
                    " but ArrayList size is " + mImageFilePaths.size());
        }
    }

    private void savePreferences () {
        Log.i(TAG, "Saving Preferences");
        if(mImageFilePaths.size() != mNumSelfies) {
            Log.e(TAG, "mNumSelfies is " + mNumSelfies +
                    " but ArrayList size is " + mImageFilePaths.size());
            return;
        }

        boolean mFileDontExistError = false;
        for(String  f : mImageFilePaths) {
            if (! new File(f).exists()) {
                Log.e(TAG, f + " dont exist!");
                mFileDontExistError = true;
            }
        }


        if(!mFileDontExistError){
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            Log.i(TAG, "mNumselfies" + mNumSelfies);
            Log.i(TAG, "arraylist size: " + mImageFilePaths.size());
            editor.putInt(NUMBER_OF_SELFIES, mNumSelfies);
            for(int i=0; i<mImageFilePaths.size(); i++){
                Log.i(TAG, "Key: " + SELFIE+(i+1) + "  " + "filename: " + mImageFilePaths.get(i));
                editor.putString(SELFIE+(i+1), mImageFilePaths.get(i));
            }
            editor.commit();
        }
    }

    private String generateImageFileName () throws IOException {
        String imageFilename = "selfie_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File imageFile = File.createTempFile(imageFilename, ".jpg", getExternalFilesDir(null));
        return imageFile.getAbsolutePath();
    }

    private Uri getURIFromImageFile (String imageFile) {
        return Uri.fromFile(new File(imageFile));
//        return Uri.fromFile(new File("file: " + imageFile.getAbsolutePath()));
    }
}
