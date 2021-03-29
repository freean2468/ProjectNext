package com.mrhi.projectnext.model;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 서버에서 제공받는 ticker의 일일 시고저종 + volume 데이터를 담을 모델.
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ModelTicker {
    private String name;
    private SortedSet<Daily> dailyList;

    public class Daily {
        private Date date;
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;

        public Daily(Date date, Double open, Double high, Double low, Double close, long volume) {
            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        public Daily(Double high, Double low)
        {
            this.high = high;
            this.low = low;
        }

        public Date getDate() { return date; }
        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }
        public long getVolume() { return volume; }
    }

    public ModelTicker(String name) {
        this.name = name;
        dailyList = new TreeSet<>(new Comparator<Daily>() {
            @Override
            public int compare(Daily o1, Daily o2) {
                return o1.date.compareTo(o2.date);
            }
        });
    }

    public void add(Date date, Double open, Double high, Double low, Double close, long volume) {
        dailyList.add(new Daily(date, open, high, low, close, volume));
    }

    public Set<Daily> getCopy() { return new HashSet<>(dailyList); }
    public String getName() { return name; }
}
