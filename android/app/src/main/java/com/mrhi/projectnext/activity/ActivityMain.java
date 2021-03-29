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

/**
 * 메인 Activity이고, 이 Activity는  viewpager2를 가지고
 * fragment 2개를 넘나든다.
 *
 * @athor 송훈일(freean2468@gmail.com)
 */
public class ActivityMain extends FragmentActivity {
    /**
     * 총 페이지 수는 2개
     * 메인 화면은 0
     * 알고리즘 결과 화면은 1
     */
    public static final int PAGES = 2;
    public static final int PAGE_MAIN = 0;
    public static final int PAGE_ALGORITHM_RESULT = 1;

    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewPager2);
        pagerAdapter = new FragmentAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        /**
         * 최초 화면 생성 시 PAGE_ALGORITHM_RESULT 화면으로 이동해
         * onCreate() 함수를 호출 시켜 초기화 시킨다.
         */
        viewPager2.setCurrentItem(PAGE_ALGORITHM_RESULT);
    }

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

