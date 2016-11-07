package org.telegram.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.ui.Components.BackupImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craterzone on 10/12/14.
 */
public class OsmLocation extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<OverlayItem> overlayItemArray;
    private LocationManager locationManager;
    private Location lastLocation;
    private MapView mapView;
    private static MessageObject message;
    private static LocationActivityDelegate delegate;
    private BackupImageView avatarImageView;
    private TextView nameTextView;
    private TextView distanceTextView;
    private Location userLocation;

    public static interface LocationActivityDelegate {
        public abstract void didSelectLocation(TLRPC.TL_messageMediaGeo location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.location_view_layout);
        if (message != null) {
            findViewById(R.id.share_location).setVisibility(View.GONE);
            findViewById(R.id.location_bottom_view).setVisibility(View.VISIBLE);
            findViewById(R.id.navigation_option).setVisibility(View.GONE);
            avatarImageView = (BackupImageView) findViewById(R.id.location_avatar_view);
            nameTextView = (TextView) findViewById(R.id.location_name_label);
            distanceTextView = (TextView) findViewById(R.id.location_distance_label);
        } else {
            findViewById(R.id.navigation_option).setVisibility(View.VISIBLE);
            findViewById(R.id.share_location).setVisibility(View.VISIBLE);
            findViewById(R.id.location_bottom_view).setVisibility(View.GONE);
        }
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.navigation_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getController().setCenter(new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
        });
        mapView = (MapView) findViewById(R.id.map_view);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        mapView.getController().setZoom(12);

        //--- Create Overlay
        overlayItemArray = new ArrayList<OverlayItem>();

        DefaultResourceProxyImpl defaultResourceProxyImpl
                = new DefaultResourceProxyImpl(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ItemizedIconOverlay mMyLocationOverlay = null;
        if (message != null) {
            mapView.getController().setCenter(new GeoPoint(message.messageOwner.media.geo.lat, message.messageOwner.media.geo._long));
            updateUserData();
            userLocation = new Location("network");
            userLocation.setLatitude(message.messageOwner.media.geo.lat);
            userLocation.setLongitude(message.messageOwner.media.geo._long);
            OverlayItem olItem = new OverlayItem("Here", "SampleDescription", new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude()));
            Drawable newMarker = this.getResources().getDrawable(R.drawable.ic_location);
            olItem.setMarker(newMarker);
            overlayItemArray.add(olItem);
            DefaultResourceProxyImpl mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

            mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlayItemArray, newMarker, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    return false;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            }, mResourceProxy);
        } else {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (lastLocation != null) {
                updateLoc(lastLocation);
            }
            if (lastLocation == null) {
                lastLocation = new Location("network");
                lastLocation.setLatitude(20.659322);
                lastLocation.setLongitude(-11.406250);
            }
            OverlayItem olItem = new OverlayItem("Here", "SampleDescription", new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()));
            Drawable newMarker = this.getResources().getDrawable(R.drawable.ic_location);
            olItem.setMarker(newMarker);
            overlayItemArray.add(olItem);
            DefaultResourceProxyImpl mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

            mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlayItemArray, newMarker, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    return false;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            }, mResourceProxy);
        }

        if (mMyLocationOverlay != null) {
            mapView.getOverlays().add(mMyLocationOverlay);
        }

        findViewById(R.id.share_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
                    location.geo = new TLRPC.TL_geoPoint();
                    location.geo.lat = lastLocation.getLatitude();
                    location.geo._long = lastLocation.getLongitude();


                    delegate.didSelectLocation(location);
                }
                finish();
            }
        });

    }

    private void updateUserData() {
        if (message != null && avatarImageView != null) {
            int fromId = message.messageOwner.from_id;
            if (message.messageOwner instanceof TLRPC.TL_messageForwarded_old) {
                fromId = message.messageOwner.to_id.user_id;
            }
            TLRPC.User user = MessagesController.getInstance().getUser(fromId);
            if (user != null) {
                TLRPC.FileLocation photo = null;
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                }
                avatarImageView.setImage(photo, null, getResources().getDrawable(R.drawable.ic_nav_to), AndroidUtilities.dp(10));
                nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));

            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ApplicationLoader.getInstance().trackScreenView(AnalyticsTrackers.OSMLOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(myLocationListener);
    }

    private void updateLoc(Location loc){
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        mapView.getController().setCenter(locGeoPoint);

        setOverlayLoc(loc);

        mapView.invalidate();
    }
    private void setOverlayLoc(Location overlayloc){
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
        //---
        overlayItemArray.clear();

        OverlayItem newMyLocationItem = new OverlayItem(
                "My Location", "My Location", overlocGeoPoint);
        overlayItemArray.add(newMyLocationItem);
        //---
    }
    private void positionMarker(Location location){
        if(message==null){
            return;
        }
        float distance = location.distanceTo(userLocation);
        if (distance < 1000) {
            distanceTextView.setText(String.format("%d %s", (int) (distance), LocaleController.getString("MetersAway", R.string.MetersAway)));
        } else {
            distanceTextView.setText(String.format("%.2f %s", distance / 1000.0f, LocaleController.getString("KMetersAway", R.string.KMetersAway)));
        }

    }
    private LocationListener myLocationListener
            = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            updateLoc(location);
            positionMarker(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

        public MyItemizedIconOverlay(
                List<OverlayItem> pList,
                OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            // TODO Auto-generated method stub
            super.draw(canvas, mapview, arg2);

            if(!overlayItemArray.isEmpty()){

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                GeoPoint in = overlayItemArray.get(0).getPoint();

                Point out = new Point();
                mapview.getProjection().toPixels(in, out);

                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_location);
                canvas.drawBitmap(bm,
                        out.x - bm.getWidth()/2,  //shift the bitmap center
                        out.y - bm.getHeight()/2,  //shift the bitmap center
                        null);
            }
        }


    }
    public static void setDelegate(LocationActivityDelegate delegateValue) {
        delegate = delegateValue;
    }

    public static void setMessageObject(MessageObject messageObject ){
        message = messageObject;
    }

}
