package nm.lab2;

import java.util.ArrayList;

public interface SuccessCallback { // creates SuccessCallback class which has a function to be called later
    void callback(boolean success, ArrayList<String> image_list);
}
