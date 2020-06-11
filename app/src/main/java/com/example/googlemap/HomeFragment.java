package com.example.googlemap;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragmentactivity {
    public static Button button;
    TextView addressTV, cityTV, stateTV;
    public static String addressss="" ;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        button=findViewById(R.id.mapBT);
        addressTV= findViewById(R.id.addressTV);
        cityTV = findViewById(R.id.cityTV);
        stateTV = findViewById(R.id.stateTV);
//        Bundle bundle = getArguments();
//        if (bundle!=null){
//            addressTV.setText(bundle.getString("addrerss"));
//            cityTV.setText(bundle.getString("city"));
//            stateTV.setText(bundle.getString("state"));
//            Log.e("bundele",(bundle.getString("addrerss")+bundle.getString("state")));
//            bundle.clear();
//
////            bundle.remove("addrerss");
////            bundle.remove("city");
////            bundle.remove("state");
//
//        }
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeFragment.this,Fragmentactivity.class);
                startActivityForResult(intent,1);
//                bundle.remove("addrerss");
//                bundle.remove("city");
//                bundle.remove("state");

//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                MapView mapView = new MapView();
//                fragmentTransaction.replace(R.id.frame,mapView);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode == 1){
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            addressTV.setText(data.getExtras().getString("addrerss"));
            cityTV.setText(data.getExtras().getString("city"));
            stateTV.setText(data.getExtras().getString("state"));
        }
    }
}
