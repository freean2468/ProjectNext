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
 * @author 송훈일(freean2468 @ gmail.com)
 */
public class ObjectAlgorithm {
    public static final int ALGORITHM_VOLUME_100PER_INCREASED_CASE = 0;
    public static final int ALGORITHM_FOURTEEN_DAYS_CASE = 1;
    public static final int ALGORITHM_SURGE_DAYS_CASE = 2;
    public static final int ALGORITHM_BUY_OPEN_CASE = 3;
    public static final int ALGORITHM_FOURTEEN_DAYS_VOLUME_CASE = 4;
    public static final int ALGORITHM_BUY_LOW_CASE = 5;
    public static final int ALGORITHM_PRICE_DECREASE_3DAYS_CASE = 6;
    public static final int ALGORITHM_OHLC_CASE = 7;
    public static final int ALGORITHM_CLOSE_FLUCTUATION_CASE = 8;

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
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmVolume100PerIncreatedCase(String name, int day1, int day2, int day3, int day4) {
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

    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmTest2(String name, int day1, int day2, int day3, int day4) {
        /**
         * 현재 선택된 종목으로 알고리즘 실행
         */
        ModelTicker ticker = getTicker(name);

        /**
         * 시나리오 2
         * 전날 대비 volume이 100%이상 하락한 날 Low에 매수 시
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
    public ArrayList<ModelTicker.Daily> algorithmBuyOpenCase(String name) {
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
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmFourteenDays(String name) {
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
    }//end of algorithmFourteenDays

    /**
     * 떨어지는 주가가 다시 상한가로 전환되는데까지 걸리는 평균 일수
     *
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmSwitchPrice(String name) {
        //name: spinner에서 선택한 종목명
        //현재 스피너에서 선택한 종목명으로 알고리즘을 실행
        ModelTicker ticker = getTicker(name);

        //떨어지기 시작하는 날 = today
        ArrayList<ModelTicker.Daily> slidingDate = new ArrayList() {
        };
        //회복하는 날
        ArrayList<ModelTicker.Daily> recoveredDate = new ArrayList() {
        };

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
                yesterday = today;
            }//end of if not null yesterday
        }//end of for OneYear

        resultList.add(slidingDate);
        resultList.add(recoveredDate);

        return resultList;
    }//end of algorithmSwitchPrice

    /**
     * 14일 동안 종가의 가격과 거래량 상승이 이루어진 날이 8일 이상일 때,
     * 기준일에 매수하여 14일 후에 매도할 때의 수익률
     *
     * @param name
     * @return
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmFourteenDaysVolume(String name) {
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

    }//end of algorithmFourteenDaysVolume

    /**
     * 시나리오 3
     * 최고가와 최저가의 평균을 내서 막대 그래프에 보여준다.
     * 최고가 평균과 최저가의 평균값의 차이를 평균을 내어서 보여주어 투자 여부를 결정하게 한다.
     *
     */
    public LinkedList<Double> maxAndMinAverage(String name)
    {
        double maxValueAvg = 0.0;
        double minValueAvg = 0.0;
        double avgGap = 0.0;

        ModelTicker ticker = getTicker(name);

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

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

        valueList.add(maxValueAvg);
        valueList.add(minValueAvg);
        valueList.add(avgGap);

        return valueList;
    }
    //

    public LinkedList<Double> yesterdayCloseGapValue(String name)
    {
        //Pooh Algorithm
        double yesterdayCloseValue = 0.0;
        double openValue = 0.0;
        double gapValue = 0.0;
        ModelTicker ticker = getTicker(name);

        Set<ModelTicker.Daily> dailySet = ticker.getCopy();
        List<ModelTicker.Daily> dailyList = new ArrayList<>();
        dailyList.addAll(dailySet);

        ModelTicker.Daily yesterday = null;
        ModelTicker.Daily today = null;

        LinkedList<Double> gapList = new LinkedList<Double>();

        for(int i = 0 ; i < dailyList.size(); ++i)
        {
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
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmBuyLow(String name, int day0, int day1, int day2, int day3, int day4) {
        /**
         * rapunzel algorithm
         * 저가에 매수 시 그날 종가, 다음날, 일주일 후, 한달 후, 6개월 후의 종가에 대한 수익률
         */

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
    }//end of algorithmBuyLow

    /**
     * close가 decreaseDay(현재는 3일)일 연속으로 하락 후 매수 시,
     * 다음날, 일주일 후, 한달 후, 6개월 후의 close에 대한 수익률
     *
     * @param name
     * @return
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmVolumeAndPriceDecrease5daysCase(String name, int day1, int day2, int day3, int day4, int decreaseDay) {
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
    public List<ModelTicker.Daily> algorithmOHLCcase(String name)
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
     * 기준일의 종가 대비 다음날, 일주일, 2주, 한달 후 종가의 변동률
     *
     * @author 허선영
     */
    public LinkedList<ArrayList<ModelTicker.Daily>> algorithmCloseFluctuationRateCase(String name, int day1, int day2, int day3, int day4) {
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
    }//end of algorithmCloseFluctuationRateCase

}

