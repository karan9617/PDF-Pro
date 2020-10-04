package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.Main10Activity;
import com.example.myapplication.Main4Activity;
import com.example.myapplication.Main5Activity;
import com.example.myapplication.Main7Activity;
import com.example.myapplication.Main8Activity;
import com.example.myapplication.R;
public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    CardView cardView1,cardView2,cardView3,cardView4,cardView5;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                cardView1 = root.findViewById(R.id.card_view1);
                cardView2 = root.findViewById(R.id.card_view2);
                cardView3 = root.findViewById(R.id.card_view3);
                cardView4 = root.findViewById(R.id.card_view4);
                cardView5 = root.findViewById(R.id.card_view5);

                cardView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Main4Activity.class);
                        startActivity(intent);
                    }
                });
                cardView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Main5Activity.class);
                        startActivity(intent);
                    }
                });
                cardView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Main7Activity.class);
                        startActivity(intent);
                    }
                });

                cardView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Main8Activity.class);
                        startActivity(intent);
                    }
                });
                cardView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Main10Activity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        return root;
    }
}
