package com.example.emanu.diplomskiadmin;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
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
import com.bumptech.glide.Glide;
import com.example.emanu.diplomskiadmin.DB.Content;
import com.example.emanu.diplomskiadmin.DB.Picture;
import com.example.emanu.diplomskiadmin.DB.RelatedPicture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanu on 8/27/2018.
 */

public class EditFragment extends Fragment {
    public static final int REQUEST_CODE = 5;

    private Context mContext;
    private View mView;
    private ImageAdapter mImageAdapter;
    protected List<String> mListRelatedPictures;
    protected Picture mPicture;
    private ExpandableGridView mGridView;
    protected ArrayAdapter<String> mAdapter;
    protected Spinner mSpinner;
    private EditText mArtist;
    private EditText mTitle;
    private EditText mYtlink;
    private EditText mDesc;
    private EditText mHtmlDesc;
    private EditText mRegister;

    protected int mLastPosition = -1;
    protected List<String> mLanguages;
    protected List<Integer> removedRelatedPicturesIds = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
//        if(context instanceof SavingFragment.FragmentUploadListener){
            super.onAttach(context);
            mContext = context;
            //prihvatanje listenera tj activity-a koji implementira interfejs
//            mListener = (SavingFragment.FragmentUploadListener) context;
//        }
//        else {
//            throw new ClassCastException(context.toString() + " must implement SavingFragment.FragmentUploadListener");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_saving, container, false);

        ImageView imageView = mView.findViewById(R.id.imageViewSave);

        Glide.with(mContext)
                .load(mPicture.getPictureBlob()) // Uri/String of the picture
                .into(imageView);

        mArtist = mView.findViewById(R.id.artistEditText);
        mTitle = mView.findViewById(R.id.titleEditText);
        mYtlink = mView.findViewById(R.id.ytlinkEditText);
        if(!mPicture.getVideoId().equals(""))
            mYtlink.setText(SavingFragment.YTLINK_BASE + mPicture.getVideoId());
        mRegister = mView.findViewById(R.id.registerEditText);
        mRegister.setText(mPicture.getRegisterNumber() + "");
        mDesc = mView.findViewById(R.id.descriptionEditText);
        mHtmlDesc = mView.findViewById(R.id.htmlDescEditText);

        //kreiranje image adaptera koji popunjava gridview
        mImageAdapter = new ImageAdapter(mContext, mListRelatedPictures, SavingFragment.getScreenHeight(), SavingFragment.getScreenWidth());
        mGridView = mView.findViewById(R.id.gridViewRelatedSave);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setExpanded();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "Long click to delete a photo", Toast.LENGTH_SHORT).show();
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int idRemovedRelatedPicture = mPicture.removeRelatedPicture(position);
                removedRelatedPicturesIds.add(idRemovedRelatedPicture);
                mListRelatedPictures.remove(position);
                mImageAdapter.notifyDataSetChanged();
                mGridView.invalidateViews();

                return true;
            }
        });

        mLanguages = extractLanguages(mPicture.getContentList());

        mSpinner = mView.findViewById(R.id.languageSpinner);
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mLanguages);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sacuvaj u prethodni content sadrzaj
                if(mLastPosition != -1)
                    saveContent(mLastPosition);

                //ucitaj novi sadrzaj
                loadContent(position);

                mLastPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        enableViews();

        return mView;
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
        try {
            if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){

                //single image is fetched with getData
                if(data.getData() != null){
                    Uri uri = data.getData();
                    Bitmap bitmap = getBitmapFromUri(uri);
                    String encodedString = bitmapToString(bitmap);

                    mPicture.addRelatedPicture(new RelatedPicture(-1, encodedString));
                    mListRelatedPictures.add(uri.toString());
                    mImageAdapter.notifyDataSetChanged();
                    mGridView.invalidateViews();
                }
                else if(data.getClipData() != null){  // multiple images is fetched with getClipData
                    ClipData clipData = data.getClipData();
                    for(int i = 0; i < clipData.getItemCount(); i++){
                        Uri uri = clipData.getItemAt(i).getUri();
                        Bitmap bitmap = getBitmapFromUri(uri);
                        String encodedString = bitmapToString(bitmap);

                        mPicture.addRelatedPicture(new RelatedPicture(-1, encodedString));
                        mListRelatedPictures.add(uri.toString());
                        mImageAdapter.notifyDataSetChanged();
                        mGridView.invalidateViews();
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.NO_WRAP);
//        bitmap.recycle();

        return encodedString;
    }

    public void enableViews(){
        mTitle.setEnabled(true);
        mArtist.setEnabled(true);
        mDesc.setEnabled(true);
        mHtmlDesc.setEnabled(true);
        mYtlink.setEnabled(true);

        Button addPhotosButton = mView.findViewById(R.id.addRelatedButton);
        addPhotosButton.setEnabled(true);
    }

    public void saveContent(int position){
        Content content = mPicture.getContentList().get(position);

        content.setArtist(mArtist.getText().toString());
        content.setTitle(mTitle.getText().toString());
        content.setDescription(mDesc.getText().toString());
        content.setHtmlDescription(mHtmlDesc.getText().toString());

        String ytlinkString = mYtlink.getText().toString();
        mPicture.setVideoId(ytlinkString.substring(SavingFragment.YTLINK_BASE.length(), ytlinkString.length()));
    }

    public void loadContent(int position){
        Content content = mPicture.getContentList().get(position);

        mArtist.setText(content.getArtist());
        mTitle.setText(content.getTitle());
        mDesc.setText(content.getDescription());
        mHtmlDesc.setText(content.getHtmlDescription());
    }

    protected List<String> extractLanguages(List<Content> contentList){
        List<String> languagesList = new ArrayList<>();
        for(Content content : contentList){
            languagesList.add(content.getLanguage());
        }

        return languagesList;
    }

    public void setListRelatedPictures(List<String> mListRelatedPictures) {
        this.mListRelatedPictures = mListRelatedPictures;
    }

    public void setPicture(Picture mPicture) {
        this.mPicture = mPicture;
    }
}
