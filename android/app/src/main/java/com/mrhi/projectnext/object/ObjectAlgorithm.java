package com.mrhi.projectnext.object;

import com.mrhi.projectnext.model.ModelTicker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 각종 Algorithm이 구현될 클래스
 * 싱글톤 구현
 * 여기서 알고리즘에 필요한 데이터 셋을 추리고 ObjectAnyChart에서 이 데이터셋을 이용해
 * 계산 및 도식화
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ObjectAlgorithm {
    private static ObjectAlgorithm instance = new ObjectAlgorithm();

    private ObjectAlgorithm() {

    }

    public static ObjectAlgorithm getInstance() {
        return instance;
    }

    private SortedSet<ModelTicker> tickerSet = new TreeSet<ModelTicker>(new Comparator<ModelTicker>() {
        @Override
        public int compare(ModelTicker o1, ModelTicker o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });

    public void add(ModelTicker modelTicker) { tickerSet.add(modelTicker); }

    public ModelTicker getTicker(String name) {
        Iterator<ModelTicker> iterator = tickerSet.iterator();

        while(iterator.hasNext()) {
            ModelTicker modelTicker = iterator.next();
            if (modelTicker.getName().equals(name)) {
                return modelTicker;
            }
        }

        return null;
    }

    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmTest(String name, int day1, int day2, int day3, int day4) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        /**
         * 시나리오 1
         * 전날 대비 volume이 100%이상 상승한 날 high에 매수 시
         * 다음 날 수익률, 7일 후 수익률, 30일 후 수익률, 180일 후 수익률
         */
        ArrayList<ModelTicker.Daily> buyPositions = new ArrayList(){};
        ArrayList<ModelTicker.Daily> positions1 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions2 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions3 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions4 = new ArrayList<>();

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                if (yesterday.getVolume() * 2 < today.getVolume()) {
                    buyPositions.add(today);

                    ModelTicker.Daily daily1;
                    ModelTicker.Daily daily2;
                    ModelTicker.Daily daily3;
                    ModelTicker.Daily daily4;

                    try {
                        daily1 = dailyList.get(i + day1);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily1 = null;
                    }
                    try {
                        daily2 = dailyList.get(i + day2);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily2 = null;
                    }
                    try {
                        daily3 = dailyList.get(i + day3);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily3 = null;
                    }
                    try {
                        daily4 = dailyList.get(i + day4);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily4 = null;
                    }

                    positions1.add(daily1);
                    positions2.add(daily2);
                    positions3.add(daily3);
                    positions4.add(daily4);
                }
            }

            yesterday = today;
        }

        resultList.add(buyPositions);
        resultList.add(positions1);
        resultList.add(positions2);
        resultList.add(positions3);
        resultList.add(positions4);

        return resultList;
    }
}