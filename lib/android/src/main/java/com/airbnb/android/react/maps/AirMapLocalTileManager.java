package com.airbnb.android.react.maps;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * Created by zavadpe on 30/11/2017.
 */
public class AirMapLocalTileManager extends ViewGroupManager<AirMapLocalTile> {
    private DisplayMetrics metrics;

    public AirMapLocalTileManager(ReactApplicationContext reactContext) {
        super();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            metrics = new DisplayMetrics();
            ((WindowManager) reactContext.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay()
                    .getRealMetrics(metrics);
        } else {
            metrics = reactContext.getResources().getDisplayMetrics();
        }
    }

    @Override
    public String getName() {
        return "AIRMapLocalTile";
    }

    @Override
    public AirMapLocalTile createViewInstance(ThemedReactContext context) {
        return new AirMapLocalTile(context);
    }

    @ReactProp(name = "fileTemplate")
    public void setFileTemplate(AirMapLocalTile view, String fileTemplate) {
        view.setFileTemplate(fileTemplate);
    }

    @ReactProp(name = "urlTemplate")
    public void setUrlTemplate(AirMapLocalTile view, String urlTemplate) {
        view.setUrlTemplate(urlTemplate);
    }

    @ReactProp(name = "tempRange")
    public void setTempRange(AirMapLocalTile view, ReadableArray tempRange) {
        view.setTempRange(toDoubleArray(tempRange));
    }

    @ReactProp(name = "currentTempRange")
    public void setCurrentTempRange(AirMapLocalTile view, ReadableArray currentTempRange) {
        view.setCurrentTempRange(toDoubleArray(currentTempRange));
    }

    @ReactProp(name = "tileSize", defaultFloat = 256f)
    public void setTileSize(AirMapLocalTile view, float tileSize) {
        view.setTileSize(tileSize);
    }

    @ReactProp(name = "zIndex", defaultFloat = -1.0f)
    public void setZIndex(AirMapLocalTile view, float zIndex) {
        view.setZIndex(zIndex);
    }

    private double[] toDoubleArray(ReadableArray readableArray) {
        if (readableArray == null) return null;
        double[] result = new double[readableArray.size()];
        int counter = 0;
        for (Object d : readableArray.toArrayList()) {
            result[counter++] = (double)d;
        }
        return result;
    }

}