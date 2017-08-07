package com.example.sss.rsssheep;

/**
 * Created by SSS on 23/04/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by SSS on 18/04/2017.
 */

public class PostItemAdapter extends ArrayAdapter<PostData> {
    static class ViewHolder{
        ImageView postThumbView;
        TextView postTitleView, postDateView, postContentView;
        String postThumbViewURL;
    }

    private Activity mContext;
    private ArrayList<PostData> datas;
    private int kindOfView;

    public PostItemAdapter(Context context, int textViewResourceId, ArrayList<PostData> objects, int kindOfView) {
        super(context, textViewResourceId, objects);
        mContext = (Activity)context;
        datas = objects;
        this.kindOfView = kindOfView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            //LayoutInflater inflater = mContext.getLayoutInflater();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(kindOfView == 1) convertView = inflater.inflate(R.layout.positem_simple, null);
            else if(kindOfView == 2) convertView = inflater.inflate(R.layout.positem, null);
            else if(kindOfView == 3) convertView = inflater.inflate(R.layout.positem_cardview, null);
            viewHolder = new ViewHolder();
            if(kindOfView != 1) {
                viewHolder.postThumbView = (ImageView)convertView.findViewById(R.id.postThumb);
                viewHolder.postThumbViewURL = "test";
            }
            viewHolder.postTitleView = (TextView)convertView.findViewById(R.id.postTitleLabel);
            viewHolder.postDateView = (TextView)convertView.findViewById(R.id.postDateLabel);
            if(kindOfView != 3){
                viewHolder.postContentView = (TextView)convertView.findViewById(R.id.postContentLabel);
            }

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        PostData data = datas.get(position);
        if(data.postThumbUrl == null){
            viewHolder.postThumbView.setImageResource(R.drawable.rss_sheep);
        }
        else{
            if(viewHolder.postThumbViewURL != null){
                viewHolder.postThumbViewURL = data.postThumbUrl;
                new DownloadAsyncTask().execute(viewHolder);
            }
        }
        viewHolder.postTitleView.setText(datas.get(position).postTitle);
        viewHolder.postDateView.setText(datas.get(position).postDate);
        if(viewHolder.postContentView != null){
            viewHolder.postContentView.setText(datas.get(position).postContent);
        }

        return convertView;
    }

    private class DownloadAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {
        Bitmap bmp;

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            // TODO Auto-generated method stub
            //load image directly
            ViewHolder viewHolder = params[0];
            try {
                URL imageURL = new URL(viewHolder.postThumbViewURL);
                bmp = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                // TODO: handle exception
                Log.e("error", "Downloading Image Failed");
                bmp = null;
            }

            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            // TODO Auto-generated method stub
            if (bmp == null) {
                result.postThumbView.setImageResource(R.drawable.rss_sheep);
            } else {
                result.postThumbView.setImageBitmap(bmp);
            }
        }
    }
}


