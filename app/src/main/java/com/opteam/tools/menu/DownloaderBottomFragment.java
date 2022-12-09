package com.opteam.tools.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.opteam.tools.adapter.CrushsAdapter;
import com.opteam.tools.downloadvideo.ClipboardMonitor;
import com.opteam.tools.downloadvideo.downloadurl;
import com.opteam.tools.loc.GPSTracker;
import com.opteam.tools.radar.RandomTextView;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DownloaderBottomFragment extends Fragment {

    public static String STARTFOREGROUND_ACTION = "startforeground";
    public static String STOPFOREGROUND_ACTION = "stopforeground";
    private TwitterSession session;
    View view;
    boolean sw;
    private SharedPreferences prefs;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.downloader_fragment, container, false);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        MobileAds.initialize(view.getContext());
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.i("adbanner", "clicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.i("adbanner", "closed");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i("adbanner", adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.i("adbanner", "impression");
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("adbanner", "Loaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("adbanner", "opened");
            }
        });

        prefs = view.getContext().getSharedPreferences("PREF_CLIP", MODE_PRIVATE);
        sw = prefs.getBoolean("csRunning", false);

//        ProgressDialog dialog = new ProgressDialog(view.getContext());
//        dialog.setMessage("please waite...");
//        dialog.setCancelable(false);
//
//        Button button = view.findViewById(R.id.btn_download);
//        EditText editText = view.findViewById(R.id.text_download);
//        Switch aSwitch = view.findViewById(R.id.switch_download);
//        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//
//        if (sw){
//            aSwitch.setChecked(true);
//        }else {
//            aSwitch.setChecked(false);
//        }
//
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View vie) {
////                TwitterVideoDownloader downloader = new TwitterVideoDownloader(view.getContext(), editText.getText().toString());
////                downloader.DownloadVideo();
//                dialog.show();
//                MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
//                myTwitterApiClient.getCustomTwitterService().getTwit(gettwitid(editText.getText().toString())).enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                            dialog.dismiss();
//                        if (response.body() != null) {
//                            try {
//
//                                JsonObject jsonObject = (JsonObject) response.body();
//                                JsonObject jsonObject1 = jsonObject.getAsJsonObject("extended_entities");
//                                JsonArray elements = jsonObject1.getAsJsonArray("media");
//                                JsonObject jsonObject2 = (JsonObject) elements.get(0);
//                                String type = jsonObject2.get("type").getAsString();
//                                if (jsonObject2.get("type").getAsString().contains("video")) {
//                                    JsonObject jsonObject3 = jsonObject2.getAsJsonObject("video_info");
//                                    JsonArray elements1 = jsonObject3.getAsJsonArray("variants");
//                                    JsonObject jsonObject4 = (JsonObject) elements1.get(2);
//                                    String url = jsonObject4.get("url").getAsString();
//                                    downloadurl.DL(url, gettwitid(editText.getText().toString()), ".mp4", view.getContext());
//                                    Log.i("downloadFileName", "1");
//                                } else if (type.contains("photo")) {
//                                    String url = jsonObject2.get("media_url").getAsString();
//                                    downloadurl.DL(url, gettwitid(editText.getText().toString()), ".jpg", view.getContext());
//                                    Log.i("downloadFileName", "1.5");
//                                }
//
//                                Log.i("downloadFileName", "2");
//                            } catch (Exception e) {
//                                Toast.makeText(view.getContext(), "sorry!! we can't download this", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Log.i("downloadFileName", "3");
//                            Toast.makeText(view.getContext(), "check your connection", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Toast.makeText(view.getContext(), "try again", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                        Log.i("downloadFileName", "error");
//                    }
//                });
//            }
//
//
//        });
//
//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b)
//                    startClipboardMonitor();
//                else
//                    stopClipboardMonitor();
//            }
//        });

////////////////////
        final RandomTextView randomTextView = view.findViewById(
                R.id.random_textview);

         firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        firestore.setFirestoreSettings(settings);



        GPSTracker mGPS = new GPSTracker(view.getContext(),getActivity());


        Locale locale = new Locale("En");

        Geocoder geocoder = new Geocoder(view.getContext(), locale);

        List<Address> list;

        try {

            if (mGPS.canGetLocation) {
                mGPS.getLocation();
                list = geocoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 2);
                if (list.size() > 0) {
                    Address address = list.get(0);
                    Log.i("locop", "CountryCode: " + address.getCountryCode() +
                            " ,AdminArea : " + address.getAdminArea() +
                            " ,CountryName : " + address.getCountryName() +
                            " ,SubLocality : " + address.getSubLocality());

                    Map<String, String> ma = new HashMap<>();
                    ma.put(String.valueOf(session.getUserName()), String.valueOf(session.getUserName()));
                    firestore.collection("location")
                            .document(address.getAdminArea()).set(ma, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("firebase", "check location" + e.getMessage());

                                }
                            });



                    firestore.collection("location").document(address.getAdminArea()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.getData() != null) {
                                Set<String> ma = documentSnapshot.getData().keySet();
                                Log.i("list", ma.toString());
                                if (ma.size() > 0) {
                                    List<String> stringList = new ArrayList<>(ma);

                                    for (int i = 0; i < stringList.size(); i++) {
                                        ////////////////////

                                        randomTextView.setOnRippleViewClickListener(
                                                new RandomTextView.OnRippleViewClickListener() {
                                                    @Override
                                                    public void onRippleViewClicked(View view) {

                                                    }
                                                });

                                        if (!Objects.equals(stringList.get(i), session.getUserName()))
                                            randomTextView.addKeyWord(stringList.get(i));
                                        if (stringList.size() < 2)
                                            randomTextView.addKeyWord("Mzamani1998");


//                                    new Handler().postDelayed(new Runnable()
//                                    {
//                                        @Override
//                                        public void run()
//                                        {
//                                            randomTextView.addKeyWord("ali");
//                                            randomTextView.addKeyWord("ahmad");
//                                            randomTextView.show();
//                                        }
//                                    }, 2 * 1000);


                                    }
                                    randomTextView.show();
                                }
                            } else {
                                Toast.makeText(view.getContext(), "No one found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "No one found", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            } else {

                System.out.println("Unable");
                displayLocationSettingsRequest(view.getContext());
                Toast.makeText(view.getContext(), "Turn on Location", Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("locop", e.getMessage());
        }


        ///////////////////////////

        view.findViewById(R.id.btn_refresh_loc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GPSTracker mGPS = new GPSTracker(view.getContext(), getActivity());


                Locale locale = new Locale("En");

                Geocoder geocoder = new Geocoder(view.getContext(), locale);

                List<Address> list;

                try {

                    if (mGPS.canGetLocation) {
                        mGPS.getLocation();
                        list = geocoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 2);
                        if (list.size() > 0) {
                            Address address = list.get(0);
                            Log.i("locop", "CountryCode: " + address.getCountryCode() +
                                    " ,AdminArea : " + address.getAdminArea() +
                                    " ,CountryName : " + address.getCountryName() +
                                    " ,SubLocality : " + address.getSubLocality());

                            firestore.collection("location").document(address.getAdminArea()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getData() != null) {
                                        Set<String> ma = documentSnapshot.getData().keySet();
                                        Log.i("list", ma.toString());
                                        if (ma.size() > 0) {
                                            List<String> stringList = new ArrayList<>(ma);
                                            for (int i = 0; i < stringList.size(); i++) {
                                                ////////////////////


                                                if (!Objects.equals(stringList.get(i), session.getUserName()))
                                                    randomTextView.addKeyWord(stringList.get(i));
                                                if (stringList.size() < 2)
                                                    randomTextView.addKeyWord("Mzamani1998");


//                                    new Handler().postDelayed(new Runnable()
//                                    {
//                                        @Override
//                                        public void run()
//                                        {
//                                            randomTextView.addKeyWord("ali");
//                                            randomTextView.addKeyWord("ahmad");
//                                            randomTextView.show();
//                                        }
//                                    }, 2 * 1000);


                                            }
                                            randomTextView.show();
                                        }
                                    } else {
                                        Toast.makeText(view.getContext(), "No one found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "No one found", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        } else {

                            System.out.println("Unable");
                            displayLocationSettingsRequest(view.getContext());
                            Toast.makeText(view.getContext(), "Turn on Location", Toast.LENGTH_SHORT).show();
                        }


                    } catch(IOException e){
                        e.printStackTrace();
                        Log.i("locop", e.getMessage());
                    }
                }

        });


        return view;
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("GpsTurnon", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("GpsTurnon", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 102);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("GpsTurnon", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("GpsTurnon", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }



    public long gettwitid(String t) {

        if (t.contains("https://twitter.com")) {
            if (t.contains("?")) {
                int f = t.lastIndexOf("status");
                int e = t.lastIndexOf("?");
                String g = t.substring(f + 7, e);
                Log.i("downloadFileName1", g);
                long r = Long.parseLong(g);
                return r;
            } else {
                int f = t.lastIndexOf("status");
                String g = t.substring(f + 7);
                Log.i("downloadFileName2", g);
                long r = Long.parseLong(g);
                return r;
            }

        } else {
            return 0;
        }
    }


    public void startClipboardMonitor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(
                    new Intent(
                            requireContext(),
                            ClipboardMonitor.class
                    ).setAction(STARTFOREGROUND_ACTION)
            );
        } else {
            requireActivity().startService(
                    new Intent(
                            requireContext(),
                            ClipboardMonitor.class
                    )
            );
        }

    }

    public void stopClipboardMonitor() {


        requireActivity().stopService(
                new Intent(
                        requireContext(),
                        ClipboardMonitor.class
                ).setAction(STOPFOREGROUND_ACTION)
        );


    }

}