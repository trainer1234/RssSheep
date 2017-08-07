package com.example.sss.rsssheep;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    //private NavigationDrawerAdapter adapter;
    private NavDrawerArrayAdapter adapter;
    private View containerView;
    private static String[][] titles;
    private boolean chkAddContent;

    private ArrayList<Integer> headTitles;
    private List<NavDrawerArray> datas;
    private FragmentDrawerListener drawerListener;
    // array to trace sqlite ids
    String[] sqliteIds;
    ArrayList<HashMap<String, String>> rssFeedList;

    public static String TAG_ID = "id";
    public static String TAG_TITLE = "title";
    public static String TAG_GROUP = "group_site";
    public static String TAG_LINK = "link";

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public ArrayList<Integer> getHeadTitles() {
        return headTitles;
    }

    public void setHeadTitles(ArrayList<Integer> headTitles) {

        this.headTitles = headTitles;
    }

    public ArrayList<Integer> initHeadTitle(){
        ArrayList<Integer> head = new ArrayList<Integer>();
        for(int i=0; i<titles.length; i++){
            head.add(i);
        }
        return head;
    }

    private ArrayList<Integer> initHeadTitleFromDb(){
        ArrayList<Integer> head = new ArrayList<Integer>();
        RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getContext());
        int groupSize = rssDb.getGroupCount();
        for(int i=0; i<groupSize; i++){
            head.add(i);
        }
        return head;
    }

    public ArrayList<String> getHeadPosition(){
        ArrayList<String> arrHeadTitle = new ArrayList<String>();
        for(int i=0; i<titles.length; i++){
            arrHeadTitle.add(titles[i][0]);
        }
        return arrHeadTitle;
    }

    public static List<NavDrawerArray> getData() {
        List<NavDrawerArray> data = new ArrayList<NavDrawerArray>();
        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            List<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
            // quy uoc title dau tien la header cua 1 list
            String headTitle = titles[i][0];
            for (int j=1; j<titles[i].length; j++){
                NavDrawerItem navItem = new NavDrawerItem();
                navItem.setTitle(titles[i][j]);
                navDrawerItems.add(navItem);
            }
            NavDrawerArray navDrawerArray = new NavDrawerArray(titles[i][0], navDrawerItems);
            data.add(navDrawerArray);
        }
        return data;
    }

    public List<NavDrawerArray> getDataFromDb(){
        RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getContext());
        List<NavDrawerArray> data = new ArrayList<NavDrawerArray>();
        List<Website> siteList = rssDb.getAllSites();

        for(int i=0; i<siteList.size(); i++){
            List<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
            boolean chk = true;
            String groupTitle = "";
            for(int j=i; j<siteList.size(); j++){
                Website web = siteList.get(j);
                if(!siteList.get(i).getGroup().equals(siteList.get(j).getGroup())){
                    groupTitle = siteList.get(i).getGroup();
                    i = j - 1;
                    chk = false;
                    NavDrawerArray navDrawerArray = new NavDrawerArray(groupTitle, navDrawerItems);
                    data.add(navDrawerArray);
                    break;
                }
                NavDrawerItem navItem = new NavDrawerItem();
                navItem.setTitle(siteList.get(j).getTitle());
                navDrawerItems.add(navItem);
            }
            if(chk) {
                groupTitle = siteList.get(i).getGroup();
                NavDrawerArray navDrawerArray = new NavDrawerArray(groupTitle, navDrawerItems);
                data.add(navDrawerArray);
                break;
            }
        }
        rssDb.close();
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chkAddContent = false;
        // drawer labels
        TypedArray navDrawerList = getActivity().getResources().obtainTypedArray(R.array.nav_drawer_labels);
        titles = new String[navDrawerList.length()][];
        for(int i = 0; i<navDrawerList.length(); i++){
            int resourceId = navDrawerList.getResourceId(i, 0);
            String[] stringArray = getActivity().getResources().getStringArray(resourceId);
            titles[i] = new String[stringArray.length];
            for(int j = 0; j<stringArray.length; j++){
                titles[i][j] = stringArray[j];
            }
        }
        new loadStoredSites().execute();
        headTitles = initHeadTitleFromDb();
        //headTitles = initHeadTitle();
        //headTitles = getHeadPosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        Button btnAddContent = (Button) layout.findViewById(R.id.btnAddContent);

        //adapter = new NavDrawerArrayAdapter(getActivity(), getData());
        adapter = new NavDrawerArrayAdapter(getActivity(), getDataFromDb());
        //adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                boolean chkCloseDrawer = true;
                for(int i=0; i<headTitles.size(); i++){
                    if(headTitles.get(i) == position){
                        chkCloseDrawer = false;
                        break;
                    }
                }
                if(chkCloseDrawer) mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        btnAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.rss_source);
                alertDialog.setMessage(R.string.rss_source_input);

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputTitle = new EditText(getContext());
                inputTitle.setHint(getString(R.string.web_naming));
                layout.addView(inputTitle);

                final EditText inputGroup = new EditText(getContext());
                inputGroup.setHint(getString(R.string.web_group_naming));
                layout.addView(inputGroup);

                final EditText inputURL = new EditText(getContext());
                inputURL.setHint(getString(R.string.input_url));
                layout.addView(inputURL);

                alertDialog.setView(layout);
                alertDialog.setIcon(R.drawable.rss_sheep);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String customURL = inputURL.getText().toString();
                        final String title = inputTitle.getText().toString();
                        final String group = inputGroup.getText().toString();

                        if(customURL.equals("") || title.equals("") || group.equals("")){
                            Toast.makeText(getContext(), "Phải nhập đầy đủ", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            new UrlChecker().execute(customURL, title, group);
                        }

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.create().show();
            }
        });

        return layout;
    }

    public boolean isRSS(String URL) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(URL);
        return doc.getDocumentElement().getNodeName().equalsIgnoreCase("rss");
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    public class loadStoredSites extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            RSSDatabaseHandler rssDatabaseHandler = new RSSDatabaseHandler(getContext());
            List<Website> siteList = rssDatabaseHandler.getAllSites();
            rssFeedList = new ArrayList<>();
            sqliteIds = new String[siteList.size()];
            for(int i=0; i<siteList.size(); i++){
                Website web = siteList.get(i);
                HashMap<String, String> map = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                map.put(TAG_ID, Integer.toString(web.getId()));
                map.put(TAG_TITLE, web.getTitle());
                map.put(TAG_GROUP, web.getGroup());
                map.put(TAG_LINK, web.getRssLink());

                // adding HashList to ArrayList
                rssFeedList.add(map);

                // add sqlite id to array
                // used when deleting a website from sqlite
                sqliteIds[i] = Integer.toString(web.getId());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class UrlChecker extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        private String inputUrl, title, group;

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.processing));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            inputUrl = params[0];
            title = params[1];
            group = params[2];
            // Check your URL here
            try {
                URL url = new URL(inputUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                String str = Integer.toString(con.getResponseCode());
                Log.i(TAG, "con.getResponseCode() IS : " + con.getResponseCode());
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK && isRSS(inputUrl)){
                    Log.i(TAG,"Success");
                    chkAddContent = true;
                }
                else{
                    Log.i(TAG, "fail");
                    chkAddContent = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "fail");
                chkAddContent = false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            dialog.dismiss();
            // Do something with your result
            if(chkAddContent){
                Toast.makeText(getContext(), getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                RSSDatabaseHandler rssDatabaseHandler = new RSSDatabaseHandler(getContext());
                rssDatabaseHandler.addSite(new Website(title, group, inputUrl));
                //getActivity().recreate();
                Intent it = getActivity().getIntent();
                getActivity().finish();
                startActivity(it);
            }
            else{
                Toast.makeText(getContext(), getString(R.string.add_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }
}