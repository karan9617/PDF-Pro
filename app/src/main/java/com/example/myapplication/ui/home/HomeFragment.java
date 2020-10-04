package com.example.myapplication.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.BaseClassForPageInsert;
import com.example.myapplication.ContactsAllImg;
import com.example.myapplication.CustomAdapter;
import com.example.myapplication.CustomAdapterGrid;
import com.example.myapplication.Database.DBHandler;
import com.example.myapplication.FragmentRefreshListener;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.R;
import com.example.myapplication.SaveSharedPreferenceForMainList2;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ListView listview;
    ImageView searchimage;
    TextView norecent;
    GridView gridView;
    DBHandler  dbHandler;
    List<ContactsAllImg> array = new ArrayList<>();
    ArrayList<ContactsAllImg> finalarray = new ArrayList<>();
        //database for main list

    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        listview = root.findViewById(R.id.listview);
        gridView = root.findViewById(R.id.gridview);
        BaseClassForPageInsert.arrayList.clear();
        searchimage = root.findViewById(R.id.searchimage);
        norecent = root.findViewById(R.id.norecent);

        norecent.setVisibility(View.INVISIBLE);
        searchimage.setVisibility(View.INVISIBLE);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onChanged(@Nullable String s) {
                ((Main2Activity)getActivity()).setFragmentRefreshListener(new Main2Activity.FragmentRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(SaveSharedPreferenceForMainList2.getIntegerCount(getActivity().getApplicationContext()) == 1) {
                            gridView.setVisibility(View.INVISIBLE);
                            listview.setVisibility(View.VISIBLE);

                                CustomAdapter customAdapter = new CustomAdapter(getActivity(), finalarray, getActivity().getApplicationContext());
                                customAdapter.notifyDataSetChanged();
                                listview.setAdapter(customAdapter);


                        }
                        else{
                            gridView.setVisibility(View.VISIBLE);
                            listview.setVisibility(View.INVISIBLE);
                                CustomAdapterGrid customAdapter = new CustomAdapterGrid(getActivity(), finalarray);
                                customAdapter.notifyDataSetChanged();
                                gridView.setAdapter(customAdapter);
                        }
                    }
                });
                if(SaveSharedPreferenceForMainList2.getIntegerCount(getActivity().getApplicationContext()) == 1) {
                    gridView.setVisibility(View.INVISIBLE);
                    listview.setVisibility(View.VISIBLE);

                    dbHandler = new DBHandler(getActivity().getApplicationContext());
                    array = dbHandler.getAllContacts();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    for (int i = 0; i < array.size(); i++) {
                        finalarray.add(array.get(i));
                    }
                    if(finalarray.size() > 0){
                        norecent.setVisibility(View.INVISIBLE);
                        searchimage.setVisibility(View.INVISIBLE);
                    }
                    else{
                        norecent.setVisibility(View.VISIBLE);
                        searchimage.setVisibility(View.VISIBLE);
                    }
                    CustomAdapter customAdapter = new CustomAdapter(getActivity(), finalarray, getActivity().getApplicationContext());
                    customAdapter.notifyDataSetChanged();
                    listview.setAdapter(customAdapter);
                }
                else{
                    gridView.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);

                    dbHandler = new DBHandler(getActivity().getApplicationContext());
                    array = dbHandler.getAllContacts();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    for (int i = 0; i < array.size(); i++) {
                        finalarray.add(array.get(i));
                    }
                    if(finalarray.size() > 0){
                        norecent.setVisibility(View.INVISIBLE);
                        searchimage.setVisibility(View.INVISIBLE);
                    }
                    else{
                        norecent.setVisibility(View.VISIBLE);
                        searchimage.setVisibility(View.VISIBLE);
                    }
                    CustomAdapterGrid customAdapter = new CustomAdapterGrid(getActivity(), finalarray);
                    customAdapter.notifyDataSetChanged();
                    gridView.setAdapter(customAdapter);
                }

            }
        });
        return root;
    }

}




