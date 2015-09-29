package nm.lab2;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nmohamed on 9/26/2015.
 */

public class HTTPHandler {
    public RequestQueue queue;

    public HTTPHandler(Context context) {
        queue = Volley.newRequestQueue(context); // queue must be initialized with context, so create initializer which does this
    }

    public void searchWithCallback(String searchQuery, final SuccessCallback callback) { // other classes call this function with a callback, which will be called when this is done
        String URL = "https://www.googleapis.com/customsearch/v1"
                + "?key=AIzaSyBoYCcrPHjcoJdKL60M_C3HRny3UotXfXw"
                + "&cx=007746169189621556577:jbnze9nyyia"
                + "&searchType=image"
                + "&q=" + searchQuery;

        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Do something to JSON response to get image out
                        ArrayList<String> image_list = new ArrayList<>();
                        try{
                            JSONArray items = response.getJSONArray("items");
                            for(int i = 0; i < response.length(); i++){
                                JSONObject image = (JSONObject) items.get(i);
                                String image_link = image.getString("link");
                                image_list.add(image_link);
                            }
                        }catch (JSONException e) {
                           e.printStackTrace();
                           Log.e("JSON try/catch error", "There was an error with your JSONObject request :-(");
                        }

                        callback.callback(true, image_list);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // we had an error, failure!
                        Log.e("JSON error", "There was an error with your error listener :-(");
                        callback.callback(false, null);
                    }
                }
        );

        queue.add(getRequest);
    }
}

//https://www.googleapis.com/customsearch/v1?key=AIzaSyBoYCcrPHjcoJdKL60M_C3HRny3UotXfXw&cx=007746169189621556577:jbnze9nyyia&searchType=image&q=turtle
