package com.example.emanu.diplomskiadmin;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiadmin.DB.Content;
import com.example.emanu.diplomskiadmin.DB.Picture;
import com.example.emanu.diplomskiadmin.DB.RelatedPicture;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanu on 8/25/2018.
 */

public class SavingFragment extends Fragment {
    public static final int REQUEST_CODE = 5;
    public static final String YTLINK_BASE = "https://www.youtube.com/watch?v=";

    private Context mContext;
    private View mView;
    private Bitmap mImageBitmap;
    private List<Uri> mListBitmaps;
    private ExpandableGridView mGridView;
    private ImageAdapter mImageAdapter;

    private FragmentUploadListener mListener;
    protected List<String> mLanguages;
    protected Spinner mSpinner;
    protected ArrayAdapter<String> mAdapter;
    protected List<Content> mListContent;

    private int mLastPosition;

    private EditText mTitle;
    private EditText mArtist;
    private EditText mDesc;
    private EditText mHtmlDesc;
    private EditText mYtLink;
    private EditText mRegister;

    //interface preko kog fragment komunicira sa activity-em
    public interface FragmentUploadListener {
        void onFinishedUploading(boolean isSucceeded);
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentUploadListener){
            super.onAttach(context);
            mContext = context;
            //prihvatanje listenera tj activity-a koji implementira interfejs
            mListener = (FragmentUploadListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement SavingFragment.FragmentUploadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_saving, container, false);

        //pravljenje liste za related slike
        mListBitmaps = new ArrayList<>();
        //dohvatanje glavne slike iz prethodnog activity-a
        mImageBitmap = ConfirmCaptureActivity.sPhoto;
        final ImageView imageView = mView.findViewById(R.id.imageViewSave);
        imageView.setImageBitmap(mImageBitmap);

        //kreiranje image adaptera koji popunjava gridview
        mImageAdapter = new ImageAdapter(mContext, mListBitmaps, getScreenHeight(), getScreenWidth());
        mGridView = mView.findViewById(R.id.gridViewRelatedSave);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setExpanded();

        //postavljanje listenera za klik na sliku iz gridview-a
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "Long click to delete a photo", Toast.LENGTH_SHORT).show();
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListBitmaps.remove(position);
                mImageAdapter.notifyDataSetChanged();
                mGridView.invalidateViews();
                return true;
            }
        });

        mListContent = new ArrayList<>();

        mLanguages = new ArrayList<>();
        mLanguages.add("Choose a language");
        mSpinner = mView.findViewById(R.id.languageSpinner);
        mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mLanguages);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    disableViews();
                else
                    enableViews();

                //sacuvaj u prethodni content sadrzaj
                if(mLastPosition > 0)
                    saveContent(mLastPosition - 1); //zato sto nulta pozicija nije nijedan jezik

                //ucitaj novi sadrzaj
                if(position > 0)
                    loadContent(position - 1);  //zato sto nulta pozicija nije nijedan jezik

                mLastPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTitle = mView.findViewById(R.id.titleEditText);
        mArtist = mView.findViewById(R.id.artistEditText);
        mDesc = mView.findViewById(R.id.descriptionEditText);
        mHtmlDesc = mView.findViewById(R.id.htmlDescEditText);
        mYtLink = mView.findViewById(R.id.ytlinkEditText);
        mRegister = mView.findViewById(R.id.registerEditText);

        return mView;
    }

    public void loadContent(int position){
        Content content = mListContent.get(position);

        mArtist.setText(content.getArtist());
        mTitle.setText(content.getTitle());
        mDesc.setText(content.getDescription());
        mHtmlDesc.setText(content.getHtmlDescription());
    }

    public void saveContent(int position){
        Content content = mListContent.get(position);

        content.setArtist(mArtist.getText().toString());
        content.setTitle(mTitle.getText().toString());
        content.setDescription(mDesc.getText().toString());
        content.setHtmlDescription(mHtmlDesc.getText().toString());
    }

    public void enableViews(){
        mTitle.setEnabled(true);
        mArtist.setEnabled(true);
        mDesc.setEnabled(true);
        mHtmlDesc.setEnabled(true);
        mYtLink.setEnabled(true);
        mRegister.setEnabled(true);

        Button addPhotosButton = mView.findViewById(R.id.addRelatedButton);
        addPhotosButton.setEnabled(true);
    }

    public void disableViews(){
        mTitle.setEnabled(false);
        mArtist.setEnabled(false);
        mDesc.setEnabled(false);
        mHtmlDesc.setEnabled(false);
        mYtLink.setEnabled(false);
        mRegister.setEnabled(false);

        Button addPhotosButton = mView.findViewById(R.id.addRelatedButton);
        addPhotosButton.setEnabled(false);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            confirmation();
            return null;
        }
    }

    public void onClickConfirm(){
        //todo provera dal su prazna polja

        new MyAsyncTask().execute();
    }

    public void confirmation(){
        EditText artist = mView.findViewById(R.id.artistEditText);
        EditText title = mView.findViewById(R.id.titleEditText);
        EditText description = mView.findViewById(R.id.descriptionEditText);
        EditText ytLinkEditText = mView.findViewById(R.id.ytlinkEditText);


        List<RelatedPicture> relatedPictures = getRelatedPicturesList();
        String encodedString = bitmapToString(mImageBitmap, 100);

        String ytLinkString = ytLinkEditText.getText().toString();
        ytLinkString = ytLinkString.replace(YTLINK_BASE, "");


        //todo obrisi
        Content con = new Content();
        con.setLanguage("Srpski");
        con.setTitle("The Starry Night");
        con.setArtist("Vincent Van Gogh");
        con.setDescription("Painted in June 1889, it depicts the view from the east-facing" +
                " window of his asylum room at Saint-Rémy-de-Provence, just before sunrise, " +
                "with the addition of an idealized village.");
//        con.setHtmlDescription("<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<body>\n" +
//                "\n" +
//                "<h1><center>The Starry Night</center></h1>\n" +
//                "<h2><center>Vincent Van Gogh</center></h1>\n" +
//                "<p>The Starry Night is an oil on canvas by the Dutch post-impressionist\n" +
//                " painter Vincent van Gogh. Painted in June 1889, it depicts the view \n" +
//                " from the east-facing window of his asylum room at Saint-Rémy-de-Provence,\n" +
//                " just before sunrise, with the addition of an idealized village. </p>\n" +
//                " \n" +
//                "<img height=\"200\" width=\"300\" src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/1280px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg\">\n" +
//                "\n" +
//                "<p>It has been in the permanent collection of the Museum of Modern Art in\n" +
//                " New York City since 1941, acquired through the Lillie P. Bliss Bequest. \n" +
//                " Regarded as among Van Gogh's finest works,[4] The Starry Night is one of\n" +
//                " the most recognized paintings in the history of Western culture.</p>\n" +
//                "\n" +
//                "<img height=\"200\" width=\"300\" src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Chevet_de_Saint-Paul_de_Mausole.JPG/1280px-Chevet_de_Saint-Paul_de_Mausole.JPG\" >\n" +
//                " \n" +
//                "</body>\n" +
//                "</html>");
        con.setHtmlDescription("https://en.wikipedia.org/wiki/The_Starry_Night");
        mListContent.add(con);

        Picture picture = new Picture();
//        picture.setVideoId(ytLinkString);
        picture.setVideoId("ZaynGNXAIJ0");
//        picture.setRegisterNumber(Integer.parseInt(mRegister.getText().toString()));
        picture.setRegisterNumber(5);
        picture.setPictureBlob(encodedString);
        picture.setContentList(mListContent);
//        picture.setContentList(listC);
        picture.setRelatedPictures(relatedPictures);

//        Picture picture = new Picture("yqwzQNlOIIU",
//                encodedString,
//                relatedPictures,
//                mListContent);

        //poziv rest web servisa sa servera za cuvanje novih podataka u bazi
        restRequest(picture);
    }

    public List<RelatedPicture> getRelatedPicturesList(){
        //pravljenje RelatedPicture objekta za svaku sliku i stavljanje u listu
        List<RelatedPicture> relatedPictures = new ArrayList<>();

        for (Uri relatedPictureUri : mListBitmaps) {
            try {
                //converting bitmap to bytearray
                Bitmap bitmap = getBitmapFromUri(relatedPictureUri);
                String encodedString = bitmapToString(bitmap, 40);

                relatedPictures.add(new RelatedPicture(encodedString));
            } catch (IOException e) { e.printStackTrace(); }
        }

        return relatedPictures;
    }

    public String bitmapToString(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.NO_WRAP);
//        bitmap.recycle();

        return encodedString;
    }

    public void restRequest(final Picture picture){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "save",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mListener.onFinishedUploading(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mListener.onFinishedUploading(false);
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return new Gson().toJson(picture).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_menu_item:
                onClickConfirm();

                return true;
        }

        return false;
    }

    public void onClickAddPhotos() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){

            //single image is fetched with getData
            if(data.getData() != null){
                Uri uri = data.getData();
                mListBitmaps.add(uri);
                mImageAdapter.notifyDataSetChanged();
                mGridView.invalidateViews();
            }
            else if(data.getClipData() != null){  // multiple images is fetched with getClipData
                ClipData clipData = data.getClipData();
                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri = clipData.getItemAt(i).getUri();
                    mListBitmaps.add(uri);
                    mImageAdapter.notifyDataSetChanged();
                    mGridView.invalidateViews();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
