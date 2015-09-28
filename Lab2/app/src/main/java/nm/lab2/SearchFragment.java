package nm.lab2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

/**
 * Fragment for search functions
 */

public class SearchFragment extends Fragment {
    WebView webView;
    ArrayList<String> images = new ArrayList<>();
    int num_images;
    int current_image;
    FeedReaderDbHelper mDbHelper;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // define parts of interface
        Button searchbutton = (Button) rootView.findViewById(R.id.button);
        searchbutton.setOnClickListener(new SearchListener());
        Button next = (Button) rootView.findViewById(R.id.next_button);
        next.setOnClickListener(new NextListener());
        Button previous = (Button) rootView.findViewById(R.id.previous_button);
        previous.setOnClickListener(new PreviousListener());
        Button database = (Button) rootView.findViewById(R.id.database_button);
        database.setOnClickListener(new DatabaseListener());
        Button add = (Button) rootView.findViewById(R.id.add_button);
        add.setOnClickListener(new addListener());

        //long click listener for adding to feed - IGNORE, for some reason crashes app, use button instead
        /*
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //alert dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getBaseContext());
                alertDialogBuilder.setMessage("Add this to your feed?");

                //alert buttons
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ADD TO DATABASE

                        //code here

                        ///ADD TO DATABASE
                        Toast.makeText(getActivity().getBaseContext(), "Added to feed", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which2) {}//do nothing
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
        */
        return rootView;
    }

    //search button listener
    public class SearchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            View rootView = v.getRootView();
            EditText searchBar = (EditText) rootView.findViewById(R.id.search_bar);
            webView = (WebView) rootView.findViewById(R.id.webView); //where what you searched for will appear!
            String searchQuery = searchBar.getText().toString(); //what you searched for!
            //if something is put into the search bar...
            if (searchQuery.length() != 0) {
                //do HTTP request
                searchGoogle(webView, searchQuery);
            }
        }


        public void searchGoogle(final WebView webView, String searchQuery) {

            HTTPHandler handler = new HTTPHandler(getActivity().getBaseContext());
            handler.searchWithCallback(searchQuery, new SuccessCallback() {
                @Override
                public void callback(boolean success, ArrayList<String> image_list) {
                    if (success) {
                        Log.d("Success", Boolean.toString(success));
                        images = image_list;
                        if (images.isEmpty()){
                            //if no images were found
                            Toast.makeText(getActivity().getBaseContext(), "Search returned no images :-(",
                                    Toast.LENGTH_LONG).show();
                        } else{
                            //otherwise show image
                            webView.loadUrl(images.get(0));
                            num_images = images.size();
                            current_image = 0;
                        }
                    } else {
                        // handle failure
                        Log.d("Failure", Boolean.toString(success));
                    }
                }
            });
        }
    }

    //next button listener
    public class NextListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(current_image < num_images-1){
                //if there are still images
                current_image += 1;
                checkRedirect(images.get(current_image));
                webView.loadUrl(images.get(current_image));
            }else{
                //if looking at last image
                Toast.makeText(getActivity().getBaseContext(), "No more images to view",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //previous button listener
    public class PreviousListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(current_image > 0){
                //if there are still images
                current_image -= 1;
                webView.loadUrl(images.get(current_image));
            }else{
                //if looking at first image
                Toast.makeText(getActivity().getBaseContext(), "This is the first image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //checks to make sure image link doesn't force you to open new window
    public void checkRedirect(String url){
        if (url.contains("?attredirects=0")){
            String new_url = url.replaceAll("\\battredirects=0\\b", "");
            images.set(current_image, new_url);
        }
    }

    //class to change fragment
    public class DatabaseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new DatabaseFragment())
                    .commit();
        }
    }

    //add item to feed
    private class addListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (images.isEmpty()) {
                Toast.makeText(getActivity().getBaseContext(), "Search for images first",
                        Toast.LENGTH_SHORT).show();
            } else {
                mDbHelper = new FeedReaderDbHelper(getActivity().getBaseContext()); //initialize database
                mDbHelper.writeDatabase(images.get(current_image));
            }
        }
    }
}