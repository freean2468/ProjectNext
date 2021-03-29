package com.mrhi.projectnext.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mrhi.projectnext.R;
import com.mrhi.projectnext.activity.ActivityMain;

/**
 * 알고리즘 결과 화면을 담당할 fragment 싱글톤 적용을 통해 한 번 생성 후 재활용한다.
 * layout 안에는 LinearLayout밖에 없는데 이 안에
 * anychar 그래프 view와 기다 통계 수치 도식에 필요한 view는 programmatic 방식으로
 * 추가한다.
 *
 * @author 송훈일(freean2468@gmail.com)
 */
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

        /**
         * 최초 초기화가 완료되면 다시 fragment main으로 이동
         */
        ((ActivityMain)getActivity()).getViewPager2().setCurrentItem(ActivityMain.PAGE_MAIN);

        return view;
    }

    public LinearLayout getViewGroup() { return viewGroup; }
}
