package com.example.sss.rsssheep;

/**
 * Created by SSS on 23/04/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostFragment extends Fragment {
    private ArrayList<PostData> listData = new ArrayList<PostData>();
    private SwipeRefreshLayout swipeContainer;

    private enum RSSXMLTag {
        TITLE, DATE, LINK, CONTENT, GUID, IGNORETAG, DESCRIPTION;
    }

    private PostItemAdapter postAdapter;
    private RssDataController rssDataController;
    private ListView listView;
    private View rootView;
    private String url = "";
    private int kindOfView;
    private boolean openDirectly, chkBookmark;
    private String actionBarTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        String menuItemTitle = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int listPos = info.position;
        // check for selected option
        if(menuItemTitle.equals(getString(R.string.add_favourite_post))){
            // user selected favourite
            // update item into favourite post table

            PostDatabaseHandler postDb = new PostDatabaseHandler(getContext());
            PostData tmp = listData.get(listPos);
            PostData post = postDb.getPost(tmp.getPostLink());
            post.setBookmark(true);
            postDb.updatePost(post);
            Toast.makeText(getContext(), R.string.add_success, Toast.LENGTH_SHORT).show();

            postDb.close();
            //reloading same activity again
            //getActivity().recreate();
        }
        else if(menuItemTitle.equals(getString(R.string.remove_favourite_post))){
            PostDatabaseHandler postDb = new PostDatabaseHandler(getContext());
            PostData postInList = listData.get(listPos);
            PostData postInDb = postDb.getPost(postInList.getPostLink());
            postInDb.setBookmark(false);
            postInDb.setPostLife(postDb.getDateTime());
            postDb.updatePost(postInDb);

            listData.remove(listPos);
            postAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
            postDb.close();
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.postListView){
            menu.setHeaderTitle(getString(R.string.favourite));
            if(chkBookmark){
                menu.add(Menu.NONE, 0, 0, R.string.remove_favourite_post);
            }
            else{
                menu.add(Menu.NONE, 0, 0, R.string.add_favourite_post);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        url = getArguments().getString("URL");
        kindOfView = getArguments().getInt("kind_of_view");
        openDirectly = getArguments().getBoolean("open_webpage_directly");
        chkBookmark = getArguments().getBoolean("bookmark");
        actionBarTitle = getArguments().getString("action_bar_title");

        rootView = inflater.inflate(R.layout.fragment_post, container, false);

        swipeContainer = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPost(0);
            }
        });

        //generateDummyData();
        new RssDataController().execute(url);

        listView = (ListView)rootView.findViewById(R.id.postListView);

        if(!actionBarTitle.equals(getString(R.string.app_name))){
            listView.setBackgroundColor(Color.WHITE);
        }

        if(kindOfView == 1){
            postAdapter = new PostItemAdapter(getActivity(), R.layout.positem_simple, listData, kindOfView);
        }
        else if(kindOfView == 2){
            postAdapter = new PostItemAdapter(getActivity(), R.layout.positem, listData, kindOfView);
        }
        else if(kindOfView == 3){
            postAdapter = new PostItemAdapter(getActivity(), R.layout.positem_cardview, listData, kindOfView);
        }
        listView.setAdapter(postAdapter);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PostData data = listData.get(i);
                if(openDirectly){
                    Intent openWebpage = new Intent(Intent.ACTION_VIEW, Uri.parse(data.postLink));
                    startActivity(openWebpage);
                }
                else{
                    Bundle postInfo = new Bundle();
                    postInfo.putString("link", data.postLink);
                    Intent postViewIntent = new Intent(getActivity(), PostViewActivity.class);
                    postViewIntent.putExtras(postInfo);
                    startActivity(postViewIntent);
                }
            }
        });
        registerForContextMenu(listView);

        if(chkBookmark){
            loadBookmarkPost();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private void loadBookmarkPost(){
        postAdapter.clear();
        PostDatabaseHandler postDb = new PostDatabaseHandler(getContext());
        List<PostData> postDatas = postDb.getAllPost();
        for(int i=0; i<postDatas.size(); i++){
            if(postDatas.get(i).isBookmark()){
                listData.add(postDatas.get(i));
            }
        }
        postAdapter.notifyDataSetChanged();
        postDb.close();
    }

    public void refreshPost(int page){
        if(!actionBarTitle.equals(getString(R.string.bookmark))){
            postAdapter.clear();
            new RssDataController().execute(url);
            if(kindOfView == 1){
                postAdapter = new PostItemAdapter(getActivity(), R.layout.positem_simple, listData, kindOfView);
            }
            else if(kindOfView == 2){
                postAdapter = new PostItemAdapter(getActivity(), R.layout.positem, listData, kindOfView);
            }
            else if(kindOfView == 3){
                postAdapter = new PostItemAdapter(getActivity(), R.layout.positem_cardview, listData, kindOfView);
            }
            listView.setAdapter(postAdapter);
        }
        swipeContainer.setRefreshing(false);
    }

    private void generateDummyData(){
        PostData data = null;
        //listData = new PostData[10];
        for(int i = 0; i < 10; i++){
            data = new PostData();
            data.postDate = "May 20, 2013";
            data.postTitle = "Post " + (i + 1) + " Title: This is the Post Title from RSS Feed";
            //data.postThumbUrl = "http://farm4.staticflickr.com/3777/9049174610_bf51be8a07_s.jpg";
            data.postThumbUrl = null;
            listData.add(data);
        }
    }

    private class RssDataController extends AsyncTask<String, Void, ArrayList<PostData>> {

        private RSSXMLTag currentTag;

        @Override
        protected ArrayList<PostData> doInBackground(String... params) {
            String urlStr = params[0];
            InputStream is = null;
            ArrayList<PostData> postDataList = new ArrayList<PostData>();
            try{
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10*1000);
                connection.setConnectTimeout(10*1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.d("debug", "The response is " + response);
                is = connection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData pdData = null;
                //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy HH:mm:ss", Locale.getDefault());

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_DOCUMENT){

                    }
                    else if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equals("item")){
                            pdData = new PostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                        else if(xpp.getName().equals("title")){
                            currentTag = RSSXMLTag.TITLE;
                        }
                        else if(xpp.getName().equals("link")){
                            currentTag = RSSXMLTag.LINK;
                        }
                        else if(xpp.getName().equals("pubDate")){
                            currentTag = RSSXMLTag.DATE;
                        }
                        else if(xpp.getName().equals("content")){
                            currentTag = RSSXMLTag.CONTENT;
                        }
                        else if(xpp.getName().equals("description")){
                            currentTag = RSSXMLTag.DESCRIPTION;
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG){
                        if(xpp.getName().equals("item")){
                            // format the data here, otherwise format data in Adapter
                            if(pdData.getPostDate() == null || pdData.getPostLink() == null
                                    || pdData.getPostTitle() == null){
                                eventType = xpp.next();
                                continue;
                            }
                            /*try{
                                Date postDate = dateFormat.parse(pdData.getPostDate());
                                pdData.setPostDate(dateFormat.format(postDate));
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }*/
                            postDataList.add(pdData);
                        }
                        else{
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    }
                    else if(eventType == XmlPullParser.TEXT){
                        String content = xpp.getText();
                        content = content.trim();
                        Log.d("debug", content);
                        if(pdData != null){
                            switch(currentTag){
                                case TITLE:
                                    if(content.length() != 0){
                                        pdData.setPostTitle(content);
                                    }
                                    break;
                                case LINK:
                                    if(content.length() != 0){
                                        pdData.setPostLink(content);
                                    }
                                    break;
                                case DATE:
                                    if(content.length() != 0){
                                        /*try{
                                            Date postDate = dateFormat.parse(content);
                                            pdData.setPostDate(dateFormat.format(postDate));
                                        }
                                        catch (Exception ex){
                                            ex.printStackTrace();
                                        }*/
                                        pdData.setPostDate(content);
                                    }
                                    break;
                                case DESCRIPTION:
                                    if(content.length() != 0){
                                        org.jsoup.nodes.Document docHtml = Jsoup.parse(content);
                                        Elements imgEle = docHtml.select("img");
                                        String imgSrc = imgEle.attr("src");
                                        if(imgSrc != "") pdData.setPostThumbUrl(imgSrc);
                                        try{
                                            Element img = imgEle.get(0);
                                            String postContent = img.previousSibling()
                                                    .previousSibling().toString();
                                            pdData.setPostContent(postContent);
                                        }
                                        catch (Exception ex){
                                            ex.printStackTrace();
                                        }

                                        Elements contentEles = docHtml.select("a");
                                        if(contentEles.size() != 0) {
                                            Element contentEle = contentEles.get(0);
                                            try{
                                                String postContent = contentEle.nextSibling().nextSibling().toString();
                                                pdData.setPostContent(postContent);
                                            }
                                            catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    eventType = xpp.next();
                }
                Log.v("tst", String.valueOf((postDataList.size())));
                // read string
                /*final int bufferSize = 1024;
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                while(true){
                    int count = is.read(buffer, 0, bufferSize);
                    if(count == -1) break;
                    os.write(buffer);
                }
                os.close();

                String result = new String(os.toByteArray(), "UTF-8");
                Log.d("debug", result);*/
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } /*catch (ParseException e) {
                e.printStackTrace();
            }*/

            return postDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostData> result) {
            PostDatabaseHandler postDb = new PostDatabaseHandler(getContext());
            HashMap<String, Boolean> map = new HashMap<String, Boolean>();
            for(int i=0; i<result.size(); i++){
                listData.add(result.get(i));
                PostData post = result.get(i);
                map.put(post.getPostLink(), true);
                PostData tmp = null;
                try{
                    tmp = postDb.getPost(post.getPostLink());
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                if(tmp != null) { // post exist in database
                    // do nothing
                }
                else{
                    post.setBookmark(false);
                    post.setPostRSSGroup(url);
                    postDb.addPost(post);
                }
            }
            ArrayList<PostData> postDatas = (ArrayList<PostData>)postDb.getAllPost();
            if(postDatas != null){
                String currentGroup = url;
                for(int i=postDatas.size() - 1; i >= 0; i--){
                    if(postDatas.get(i).getPostRSSGroup().equals(currentGroup)){
                        if(map.get(postDatas.get(i).getPostLink()) != null
                                && map.get(postDatas.get(i).getPostLink()) == true) continue;
                        listData.add(postDatas.get(i));
                    }
                }
                postDb.close();
                postAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
