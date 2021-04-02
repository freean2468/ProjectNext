package com.mrhi.projectnext.object;

import android.util.Log;

import com.mrhi.projectnext.model.ModelTicker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
 * @author 송훈일(freean2468 @ gmail.com)
 */
public class ObjectAlgorithm {
    public static final int VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY = 0;
    public static final int VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY = VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY + 1;
    public static final int SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS = VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY + 1;
    public static final int SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME = SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS + 1;
    public static final int HIGH_LOW = SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME + 1;
    public static final int CLOSE_OPEN = HIGH_LOW + 1;
    public static final int BUY_LOW = CLOSE_OPEN + 1;
    public static final int BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS = BUY_LOW + 1;
    public static final int RECOVERING = BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS + 1;
    public static final int BUY_OPEN_SELL_CLOSE = RECOVERING + 1;
    public static final int PROBABILITY_CONTINUITY_2DAYS_LOSE = BUY_OPEN_SELL_CLOSE + 1;
    public static final int FLUCTUATION_RATE_ONE_DAY = PROBABILITY_CONTINUITY_2DAYS_LOSE + 1;
    public static final int FLUCTUATION_RATE_SEVERAL_DAYS = FLUCTUATION_RATE_ONE_DAY + 1;
    public static final int NASDAQ_CORRELATION = FLUCTUATION_RATE_SEVERAL_DAYS + 1;
    public static final int CLOSE_LIKE_OR_SAME_DAY = NASDAQ_CORRELATION + 1;

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

    public void add(ModelTicker modelTicker) {
        tickerSet.add(modelTicker);
    }

    public ModelTicker getTicker(String name) {
        Iterator<ModelTicker> iterator = tickerSet.iterator();

        while (iterator.hasNext()) {
            ModelTicker modelTicker = iterator.next();
            Log.d("debug", "ticker : " + modelTicker.getName());
            if (modelTicker.getName().equals(name)) {
                return modelTicker;
            }
        }
        return null;
    }

    /**
     * 전날 대비 volume이 100%이상 상승한 날 high에 매수 시
     * day1일 후 수익률, day2일 후 수익률, day3일 후 수익률, day4일 후 수익률
     *
     * @param name
     * @param day1
     * @param day2
     * @param day3
     * @param day4
     * @return
     * @auth 송훈일
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY(String name, int day1, int day2, int day3, int day4) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        ArrayList<ModelTicker.Daily> buyPositions = new ArrayList<>();
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

    /**
     * 전날 대비 volume이 100%이상 하락한 날 Low에 매수 시
     * 다음 날 수익률, 7일 후 수익률, 30일 후 수익률, 180일 후 수익률
     * @author 김택민
     */

    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY(String name, int day1, int day2, int day3, int day4) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);


        /**
         * 사는 시점과 다음날 수익률, 7일후 수익률, 30일후 수익률, 180일후 수익률을
         * 구하고 각각 담기위해 5개의 ArrayList를 선언하였다.
         */

        ArrayList<ModelTicker.Daily> buyPositions = new ArrayList(){};
        ArrayList<ModelTicker.Daily> positions1 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions2 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions3 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions4 = new ArrayList<>();
        /**
         * 전체 날짜를 정렬하기 위한 작업이다.
         * 내부에서 정렬해주는 Set을 이용하여 값을 담은뒤
         * 정렬된 그대로를 ArrayList에 담아준다.
         */
        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);


        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        /**
         * 전체 날짜중 어제 거래량이 100% 떨어졌고 오늘 거래량보다 적으면 조건을 실행한다.
         * try catch가 있는 이유는 실제 날짜 31일이 넘어갈경우 인덱스를 벗어날수 있기에 작성된 구문이다.
         * 모든 조건을 수행한뒤 다음날,7일후,30일후, 180일후의 값을 ArrayList에 담는다.
         */

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                if (yesterday.getVolume() / 2 > today.getVolume()) {
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
        /**
         * 결과 값을 담는 LinkedList에 각각의 ArrayList를 담아준다.
         */
        resultList.add(buyPositions);
        resultList.add(positions1);
        resultList.add(positions2);
        resultList.add(positions3);
        resultList.add(positions4);

        return resultList;
    }

    /**
     * open에 매수하면 close와 비교할 때 수익을 낼 확률
     *
     * @authour 송훈일
     */
    public ArrayList<ModelTicker.Daily> algorithm_BUY_OPEN_SELL_CLOSE(String name) {
        ModelTicker ticker = getTicker(name);
        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        ArrayList<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        return dailyList;
    }

    /**
     * 14일 동안 종가의 가격상승이 이루어진 날이 8일 이상일 때,
     * 기준일에 매수하여 14일 후에 매도할 때의 수익률
     * @authour 허선영
     *
     * @param name
     * @return
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS(String name) {
        /*
            rapunzel algorithm
            14일의 기간 중, 8일 동안 가격상승이 이루어졌을 때 첫날 매수하여 14일 째 되는 날 매도했을 때의 수익률
            매수가와 매도가는 그 날의 고가
         */
        //name: spinner에서 선택한 종목명

        //현재 스피너에서 선택한 종목명으로 알고리즘을 실행
        ModelTicker ticker = getTicker(name);

        //매수한 날 = today
        ArrayList<ModelTicker.Daily> buyDate = new ArrayList() {
        };
        //매도한 날 = today + 14
        ArrayList<ModelTicker.Daily> sellDate = new ArrayList() {
        };

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                int count = 0;

                //14일 거르는 for문
                for (int j = 0; j < 14; j++) {
                    //전일보다 종가의 가격상승이 이루어지는 날이 8일 이상이면
                    if (yesterday.getClose() < today.getClose()) {
                        count++;

                        if (count >= 8) {
                            //알고리즘에 걸리는 날이 마지막 날이면 마지막날 + 14일은 없으니 오류
                            try {
                                ModelTicker.Daily fourteenDaysLater = dailyList.get(i + 14);
                                sellDate.add(fourteenDaysLater);
                                buyDate.add(today);
                            } catch (IndexOutOfBoundsException ioobe) {
                                break;
                            }
                            break;
                        }
                    }//end of if
                }//end of for FourteenDays
            }//end of if not null yesterday
            yesterday = today;
        }//end of for OneYear

        resultList.add(buyDate);
        resultList.add(sellDate);

        return resultList;
    }//end of algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS

    /**
     * 떨어지는 주가가 다시 상한가로 전환되는데까지 걸리는 평균 일수
     *
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_RECOVERING(String name) {
        //name: spinner에서 선택한 종목명
        //현재 스피너에서 선택한 종목명으로 알고리즘을 실행
        ModelTicker ticker = getTicker(name);

        //떨어지기 시작하는 날 = today
        ArrayList<ModelTicker.Daily> slidingDate = new ArrayList() {};
        //회복하는 날
        ArrayList<ModelTicker.Daily> recoveredDate = new ArrayList() {};

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {

                //전일보다 오늘의 종가가 떨어지면 = slidingDate
                if (yesterday.getClose() > today.getClose()) {
                    boolean flag = false;

                    //회복하는 날 = recoverdDate
                    for (int j = i + 1; j < dailyList.size(); j++) {
                        if (today.getClose() <= dailyList.get(j).getClose()) {
                            recoveredDate.add(dailyList.get(j));
                            slidingDate.add(today);
                            flag = true;
                            break;
                        }
                    }
                }//end of if
            }//end of if not null yesterday
            yesterday = today;
        }//end of for OneYear

        resultList.add(slidingDate);
        resultList.add(recoveredDate);

        return resultList;
    }//end of algorithm_RECOVERING

    /**
     * 14일 동안 종가의 가격과 거래량 상승이 이루어진 날이 8일 이상일 때,
     * 기준일에 매수하여 14일 후에 매도할 때의 수익률
     *
     * @param name
     * @return
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME(String name) {
        //현재 스피너에서 선택한 종목명으로 알고리즘을 실행
        ModelTicker ticker = getTicker(name);

        //매수한 날 = today
        ArrayList<ModelTicker.Daily> buyDate = new ArrayList() {};
        //매도한 날 = today + 14
        ArrayList<ModelTicker.Daily> sellDate = new ArrayList() {};

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                int count = 0;

                //14일 거르는 for문
                for (int j = 0; j < 14; j++) {
                    //전일보다 종가의 가격상승이 이루어지는 날이 8일 이상이면
                    if (yesterday.getClose() < today.getClose() && yesterday.getVolume() < today.getVolume()) {
                        count++;

                        if (count >= 8) {
                            //알고리즘에 걸리는 날이 마지막 날이면 마지막날 + 14일은 없으니 오류
                            try {
                                ModelTicker.Daily fourteenDaysLater = dailyList.get(i + 14);
                                sellDate.add(fourteenDaysLater);
                                buyDate.add(today);
                            } catch (IndexOutOfBoundsException ioobe) {
                                break;
                            }
                            break;
                        }
                    }//end of if
                }//end of for FourteenDays
            }//end of if not null yesterday
            yesterday = today;
        }//end of for OneYear

        resultList.add(buyDate);
        resultList.add(sellDate);

        return resultList;

    }//end of algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME

    /**
     * 최고가와 최저가의 평균을 내서 막대 그래프에 보여준다.
     * 최고가 평균과 최저가의 평균값의 차이를 평균을 내어서 보여주어 투자 여부를 결정하게 한다.
     * @author 김택민
     */
    public LinkedList<Double> algorithm_HIGH_LOW(String name)
    {
        double maxValueAvg = 0.0;
        double minValueAvg = 0.0;
        double avgGap = 0.0;

        ModelTicker ticker = getTicker(name);

        /**
         * 전체 날짜를 정렬하기 위한 작업이다.
         * 내부에서 정렬해주는 Set을 이용하여 값을 담은뒤
         * 정렬된 그대로를 ArrayList에 담아준다.
         */
        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        /**
         * 최고값과 최저가의 평균을 낸뒤 그 차이값을 avgGap 변수에 넣어준다.
         */

        for(int i = 0 ; i < dailyList.size(); i++)
        {
            maxValueAvg += dailyList.get(i).getHigh();
            minValueAvg += dailyList.get(i).getLow();
            avgGap += maxValueAvg - minValueAvg;
        }
        maxValueAvg /= dailyList.size();
        minValueAvg /= dailyList.size();
        avgGap /= dailyList.size();

        LinkedList<Double> valueList = new LinkedList<Double>();

        /**
         * 링크드리스트에 최고값 평균,최저가 평균, 평균값의 차이를 담는다.
         */
        valueList.add(maxValueAvg);
        valueList.add(minValueAvg);
        valueList.add(avgGap);

        return valueList;
    }

    /**
     * 장이 닫힐때와 그 다음날에 장이 열렸을때의 가격 차이를 모두 얻어서 평균을 내주는 알고리즘
     * @author 김택민
    */
    public LinkedList<Double> algorithm_CLOSE_OPEN(String name)
    {
        //Pooh Algorithm
        double yesterdayCloseValue = 0.0;
        double openValue = 0.0;
        double gapValue = 0.0;
        ModelTicker ticker = getTicker(name);

        /**
         * 전체 날짜를 정렬하기 위한 작업이다.
         * 내부에서 정렬해주는 Set을 이용하여 값을 담은뒤
         * 정렬된 그대로를 ArrayList에 담아준다.
         */
        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        /**
         * 비교를 위해 yesterday와 today를 설정하였다.
         */
        ModelTicker.Daily yesterday = null;
        ModelTicker.Daily today = null;

        LinkedList<Double> gapList = new LinkedList<Double>();

        for(int i = 0 ; i < dailyList.size(); ++i)
        {
            /**
             * 처음 들어왔을때 yesterday의 값이 변동되는 곳이 없었으니 그대로 null 이다.
             * today에 담긴값을 yesterday에게 준다.
             * 어제의 장이 닫는 시간과 장이 열린 값의 차이를 구하여
             * 별도로 선언한 링크드리스트 gapList에 담아주고 모두 담았으면 gapList를 리턴한다.
             */
            today = dailyList.get(i);
            if(yesterday!=null)
            {
                yesterdayCloseValue = yesterday.getClose();
                openValue = today.getOpen();

                gapValue = yesterdayCloseValue - openValue;
                gapList.add(gapValue);
            }
            yesterday = today;
        }
        return gapList;
    }

    /**
     * 저가로 매수 했을 시 그날, 다음날, 일주일, 한달, 6개월 후의 종가에 매도했을 때의 수익률
     *
     * @param name
     * @param day0
     * @param day1
     * @param day2
     * @param day3
     * @param day4
     * @return
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_BUY_LOW(String name, int day0, int day1, int day2, int day3, int day4) {
        ModelTicker ticker = getTicker(name);

        //매수한 날 = today
        ArrayList<ModelTicker.Daily> buyDate = new ArrayList() {};
        //매도한 날 = today + X
        ArrayList<ModelTicker.Daily> positions1 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions2 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions3 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions4 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions5 = new ArrayList<>();

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                if (yesterday.getVolume() * 2 < today.getVolume()) {
                    buyDate.add(today);

                    ModelTicker.Daily daily1;
                    ModelTicker.Daily daily2;
                    ModelTicker.Daily daily3;
                    ModelTicker.Daily daily4;
                    ModelTicker.Daily daily5;

                    try {
                        daily1 = dailyList.get(i + day0);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily1 = null;
                    }
                    try {
                        daily2 = dailyList.get(i + day1);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily2 = null;
                    }
                    try {
                        daily3 = dailyList.get(i + day2);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily3 = null;
                    }
                    try {
                        daily4 = dailyList.get(i + day3);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily4 = null;
                    }
                    try {
                        daily5 = dailyList.get(i + day4);
                    } catch (IndexOutOfBoundsException ioobe) {
                        daily5 = null;
                    }

                    positions1.add(daily1);
                    positions2.add(daily2);
                    positions3.add(daily3);
                    positions4.add(daily4);
                    positions5.add(daily5);
                }
            }
            yesterday = today;
        }

        resultList.add(buyDate);
        resultList.add(positions1);
        resultList.add(positions2);
        resultList.add(positions3);
        resultList.add(positions4);
        resultList.add(positions5);

        return resultList;
    }//end of algorithm_BUY_LOW

    /**
     * close가 decreaseDay(현재는 3일)일 연속으로 하락 후 매수 시,
     * 다음날, 일주일 후, 한달 후, 6개월 후의 close에 대한 수익률
     *
     * @param name
     * @return
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS(String name, int day1, int day2, int day3, int day4, int decreaseDay) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        ArrayList<ModelTicker.Daily> buyPositions = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions1 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions2 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions3 = new ArrayList<>();
        ArrayList<ModelTicker.Daily> positions4 = new ArrayList<>();

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        int count = 0;
        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {
                if (yesterday.getClose() > today.getClose()) {
                    count++;

                    if (count >= decreaseDay) {
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

                        count = 0;
                    }
                } else {
                    count = 0;
                }
            }//end of if yesterDay null

            yesterday = today;
        }

        resultList.add(buyPositions);
        resultList.add(positions1);
        resultList.add(positions2);
        resultList.add(positions3);
        resultList.add(positions4);

        return resultList;

    }//end of algorithmVolumeAndPriceDecreaseCase

    /**
     * open, high, low, close의 하루 변동률의 평균
     *
     * @param name
     * @return
     * @author 허선영
     */
    public List<ModelTicker.Daily> algorithm_FLUCTUATION_RATE_ONE_DAY(String name)
    {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        return dailyList;
    }//end of algorithmOLCDCase

    /**
     * 나스닥과 선택한 종목과의 하루 데이터 결합성 percentage
     *
     * @author 허선영
     */
    public List<ModelTicker.Daily> algorithm_NASDAQ_CORRELATION(String name){
        /**
         * 현재 선택한 종목
         */
        ModelTicker ticker = getTicker(name);

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        return dailyList;

    }//end of NASDAQ_JOIN

    /**
     * 기준일의 종가 대비 다음날, 일주일, 2주, 한달 후 종가의 변동률
     *
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_FLUCTUATION_RATE_SEVERAL_DAYS(String name, int day1, int day2, int day3, int day4) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        //기준 날 = today
        ArrayList<ModelTicker.Daily> standardDate = new ArrayList() {
        };
        //비교하는 날 = today + X
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
                ModelTicker.Daily daily1;
                ModelTicker.Daily daily2;
                ModelTicker.Daily daily3;
                ModelTicker.Daily daily4;

                try {
                    daily4 = dailyList.get(i + day4);
                } catch (IndexOutOfBoundsException ioobe) {
                    break;
                }

                daily1 = dailyList.get(i + day1);
                daily2 = dailyList.get(i + day2);
                daily3 = dailyList.get(i + day3);

                standardDate.add(today);
                positions1.add(daily1);
                positions2.add(daily2);
                positions3.add(daily3);
                positions4.add(daily4);
            }
            yesterday = today;
        }

        resultList.add(standardDate);
        resultList.add(positions1);
        resultList.add(positions2);
        resultList.add(positions3);
        resultList.add(positions4);

        return resultList;
    }//end of algorithm_FLUCTUATION_RATE_SEVERAL_DAYS

    /**
     *  전날 대비 종가가 이틀 연속 하락했을 때, 3일째에 상승할 확률
     *
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithm_PROBABILITY_CONTINUITY_2DAYS_LOSE(String name, int CELL_DATE) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        //이틀 연속 하락
        ArrayList<ModelTicker.Daily> sliding2Days = new ArrayList() {};

        //3일 이상으로 연속 하락
        ArrayList<ModelTicker.Daily> sliding3DaysOrMore = new ArrayList() {};

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        LinkedList<ArrayList<ModelTicker.Daily>> resultList = new LinkedList<>();

        ModelTicker.Daily yesterday = null;

        int count = 0;
        for (int i = 0; i < dailyList.size(); ++i) {
            ModelTicker.Daily today = dailyList.get(i);
            if (yesterday != null) {

                if (yesterday.getClose() > today.getClose()) {
                    count ++;

                    if(count > CELL_DATE) {
                        sliding3DaysOrMore.add(today);
                    }else if(count == CELL_DATE){
                        sliding2Days.add(today);
                    }

                }else{
                    count = 0;
                }
            }//end of if not null yesterday
            yesterday = today;
        }//end of for OneYear

        resultList.add(sliding2Days);
        resultList.add(sliding3DaysOrMore);

        return resultList;

    }//end of algorithm_PROBABILITY_CONTINUITY_2DAYS_LOSE

    /**
     * 종료가 금액이 최저가와 비슷하거나 일치 할경우가 언제인지 구하는 알고리즘
     * @param name
     * @return
     * author 김택민
     */

    public ArrayList<Date> algorithm_CLOSE_VALUE_SAME_OR_LIKE_LOW(String name)
    {
        /**
         * 골라낸 날짜를 보관할 Arraylist를 선언한다.
        */
        double near = 0.0;
        Date date = new Date();
        ArrayList<Date> dateList = new ArrayList<Date>();

        /**
         * 선택된 종목으로 알고리즘을 실행한다.
         *
        */
        ModelTicker ticker = getTicker(name);
        /**
         * 전체 날짜를 정렬하기 위한 작업이다.
         * 내부에서 정렬해주는 Set을 이용하여 값을 담은뒤
         * 정렬된 그대로를 ArrayList에 담아준다.
        */
        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);


        for(int i = 0 ; i < dailyList.size(); i++)
        {
            /**
             * 근사 값을 구하기 위한 알고리즘이다.
             * 시장이 닫는 시간을 기준으로 -3% +3% 값을 이용하여 비교한뒤
             * 골라진 날짜만을 dateList에 담는다.
             */
            near = dailyList.get(i).getClose();
            double smallValue = near - (near * 0.03);
            double bigValue = near + (near * 0.03);

            if(smallValue <= near && near <= bigValue)
            {
                dateList.add(dailyList.get(i).getDate());
            }
        }
        return dateList;
    }

}

