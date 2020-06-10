package com.example.googlemap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static Button button;
    TextView addressTV, cityTV, stateTV;
    public static String addressss="" ;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        button=view.findViewById(R.id.mapBT);
        addressTV= view.findViewById(R.id.addressTV);
        cityTV = view.findViewById(R.id.cityTV);
        stateTV = view.findViewById(R.id.stateTV);
        Bundle bundle = getArguments();
        if (bundle!=null){
            addressTV.setText(bundle.getString("addrerss"));
            cityTV.setText(bundle.getString("city"));
            stateTV.setText(bundle.getString("state"));
            Log.e("bundele",(bundle.getString("addrerss")+bundle.getString("state")));
            bundle.clear();
//            bundle.remove("addrerss");
//            bundle.remove("city");
//            bundle.remove("state");

        }
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bundle.remove("addrerss");
//                bundle.remove("city");
//                bundle.remove("state");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MapView mapView = new MapView();
                fragmentTransaction.replace(R.id.frame,mapView);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        return view;
    }
}
