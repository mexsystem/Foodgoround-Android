package soo.jesse.com.foodgoround;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else
        {
            mMap.setMyLocationEnabled(true);
        }


        String mode="vendorShopList";
        String URL = "https://foodgoround.000webhostapp.com/"+mode;
        new Background1().execute(URL,null,"");

    }

    public class Background1 extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog1;

        @Override
        protected String doInBackground(String... params) {
            // Here you are in the worker thread and you are not allowed to access UI thread from here.
            // Here you can perform network operations or any heavy operations you want.
            String result = "";
            result = new JSONReader().readURL(params[0].toString());
            return result;
        }

        @Override
        protected void onPreExecute() {
            // Here you can show progress bar or something on the similar lines.
            // Since you are in a UI thread here.
            dialog1 = new ProgressDialog(MapsActivity.this);
            dialog1.setMessage("Loading...");
            dialog1.setIndeterminate(false);
            dialog1.setCancelable(false);
            dialog1.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            super.onProgressUpdate(params);
            // You can track you progress update here
        }

        @Override
        protected void onPostExecute(String params) {
            super.onPostExecute(params);
            // After completing execution of given task, control will return here.
            // Hence if you want to populate UI elements with fetched data, do it here.

            dialog1.dismiss();

            try
            {
                String nameData = "";
                String latData = "";
                String longData = "";
                JSONArray jArray1 = new JSONArray(params);

                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.truck);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                for(int y=0; y<jArray1.length(); y++){


                    JSONObject jObject0 = jArray1.getJSONObject(y);
                    nameData=jObject0.getString("vendorName");
                    latData=jObject0.getString("vendorLat");
                    longData=jObject0.getString("vendorLong");
                    String name1 = nameData;
                    LatLng latlng1 = new LatLng(Float.parseFloat(latData), Float.parseFloat(longData));
                    mMap.addMarker(new MarkerOptions().position(latlng1).title(name1).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor((float)0.5,(float)0.5));


                    if(y==0){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 15));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                }





            }
            catch(Exception err)
            {
                //server disconnect
                Log.d("errlog","MapsActivity : "+err.toString());

                new AlertDialog.Builder(MapsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("Error")
                        .setMessage("Server is under maintenance, please try again later.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //hide for testing purpose
                                //finish();
                            }
                        })
                        .show();
            }
        }

    }

}
