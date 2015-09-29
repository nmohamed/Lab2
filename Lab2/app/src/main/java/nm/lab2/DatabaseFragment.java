package nm.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment for image feed
 */

public class DatabaseFragment extends Fragment {
    int current_image = 0;
    int num_images = 0;
    WebView webView;
    ArrayList<String> images = new ArrayList<>();
    FeedReaderDbHelper mDbHelper;

    public DatabaseFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_database, container, false);

        // define parts of interface
        Button searchFragmentButton = (Button) rootView.findViewById(R.id.search_fragment_button);
        searchFragmentButton.setOnClickListener(new ChangeBackFragmentListener());
        Button next = (Button) rootView.findViewById(R.id.next_button_database);
        next.setOnClickListener(new NextListener());
        Button previous = (Button) rootView.findViewById(R.id.previous_button_database);
        previous.setOnClickListener(new PreviousListener());
        Button delete = (Button) rootView.findViewById(R.id.delete_button);
        delete.setOnClickListener(new DeleteListener());
        webView = (WebView) rootView.findViewById(R.id.database_web_view);

        //retrieve from database
        getSavedImages();
        //put first image in webView if it exists
        if (num_images != 0){
            webView.loadUrl(images.get(0));
        } else{
            Toast.makeText(getActivity().getBaseContext(), "No images have been saved",
                    Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    //get images from database and save them into arraylist "images"
    public void getSavedImages() {
        mDbHelper = new FeedReaderDbHelper(getActivity()); //initialize database
        images = mDbHelper.readDatabase();
        num_images = images.size();
    }

    //change back to search fragment
    public class ChangeBackFragmentListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new SearchFragment())
                    .commit();
        }
    }

    //next button listener
    public class NextListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(current_image < num_images-1){
                //if there are still images
                current_image += 1;
                webView.loadUrl(images.get(current_image));
            }else{
                //if looking at last image
                Toast.makeText(getActivity().getBaseContext(), "No more images to view",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //previous button listener
    public class PreviousListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(current_image > 0){
                //if you aren't on the first image
                current_image -= 1;
                webView.loadUrl(images.get(current_image));
            }else{
                //if looking at first image
                Toast.makeText(getActivity().getBaseContext(), "This is the first image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //listener to delete images from feed
    private class DeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (num_images == 0) { //if there are no images in feed
                Toast.makeText(getActivity().getBaseContext(), "No images to delete",
                        Toast.LENGTH_SHORT).show();
            }else{
                mDbHelper.deleteDatabase(images.get(current_image));

                //update webview by making sure you aren't viewing the deleted image
                getSavedImages();
                if (current_image != 0) {
                    //change image view to previous image
                    current_image -= 1;
                    webView.loadUrl(images.get(current_image));
                }else{
                    //if there are no images left in feed, show nothing
                    webView.loadUrl("about:blank");
                }

            }
        }
    }
}
