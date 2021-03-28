package com.mrhi.projectnext.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mrhi.projectnext.R;
import com.mrhi.projectnext.activity.ActivityMain;

public class FragmentAlgorithmResult extends Fragment {
    private static FragmentAlgorithmResult instance = new FragmentAlgorithmResult();
    private FragmentAlgorithmResult(){

    }
    public static FragmentAlgorithmResult getInstance() { return instance; }
    private LinearLayout viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_algorithm_result, container, false);

        viewGroup = view.findViewById(R.id.viewGroup);

        ((ActivityMain)getActivity()).getViewPager2().setCurrentItem(ActivityMain.PAGE_MAIN);

        return view;
    }

    public LinearLayout getViewGroup() { return viewGroup; }
}
