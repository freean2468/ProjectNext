package com.mrhi.projectnext.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.mrhi.projectnext.R;
import com.mrhi.projectnext.fragment.FragmentAlgorithmResult;
import com.mrhi.projectnext.fragment.FragmentMain;

public class ActivityMain extends FragmentActivity {
    public static final int PAGES = 2;
    public static final int PAGE_MAIN = 0;
    public static final int PAGE_ALGORITHM_RESULT = 1;

    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager2 = findViewById(R.id.viewPager2);
        pagerAdapter = new FragmentAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        viewPager2.setCurrentItem(PAGE_ALGORITHM_RESULT);
    }

    /**
     * A simple pager adapter
     */
    private class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case PAGE_MAIN: return FragmentMain.getInstance();
                case PAGE_ALGORITHM_RESULT: return FragmentAlgorithmResult.getInstance();
                default: return null;
            }
        }

        @Override
        public int getItemCount() {
            return PAGES;
        }
    }

    public ViewPager2 getViewPager2() { return viewPager2; }
}

