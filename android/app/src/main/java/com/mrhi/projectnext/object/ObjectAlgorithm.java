package com.mrhi.projectnext.object;

import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.charts.Stock;
import com.mrhi.projectnext.model.ModelTicker;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

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
            Log.d("debug", modelTicker.getName() + " vs " + name);
            if (modelTicker.getName().equals(name)) {
                return modelTicker;
            }
        }

        return null;
    }
}