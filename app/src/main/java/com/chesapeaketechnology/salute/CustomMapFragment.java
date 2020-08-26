package com.chesapeaketechnology.salute;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Custom MapView which prevents its parent view from scrolling while panning the map.
 *
 * @since 0.1.1
 */
public class CustomMapFragment extends MapView
{

    public CustomMapFragment(Context context)
    {
        super(context);
    }

    public CustomMapFragment(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public CustomMapFragment(Context context, AttributeSet attributeSet, int i)
    {
        super(context, attributeSet, i);
    }

    public CustomMapFragment(Context context, GoogleMapOptions googleMapOptions)
    {
        super(context, googleMapOptions);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        // Prevent any outer scroll view from scrolling while interacting with map
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(e);
    }
}