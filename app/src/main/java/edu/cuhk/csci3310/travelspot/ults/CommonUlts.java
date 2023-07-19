package edu.cuhk.csci3310.travelspot.ults;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cuhk.csci3310.travelspot.model.Spot;

public class CommonUlts {
    public static final float GEOFENCE_RADIUS = 100;

    public static Map<String, Float> colorMap;
    static {
        colorMap = new HashMap<>();
        colorMap.put("yellow", BitmapDescriptorFactory.HUE_YELLOW);
        colorMap.put("red", BitmapDescriptorFactory.HUE_RED);
        colorMap.put("blue", BitmapDescriptorFactory.HUE_BLUE);
        colorMap.put("cyan", BitmapDescriptorFactory.HUE_CYAN);
    }

    public static BitmapDescriptor getMarkerIcon(String color) {
        float colorCode = BitmapDescriptorFactory.HUE_CYAN;
        if (colorMap.containsKey(color)) {
            colorCode = colorMap.get(color);
        }
        return BitmapDescriptorFactory.defaultMarker(colorCode);
    }
}
