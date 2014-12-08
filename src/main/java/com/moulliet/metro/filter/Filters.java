package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Crash;

import java.util.ArrayList;
import java.util.List;

public class Filters implements Filter {
    private List<Filter> filters = new ArrayList<>();

    public Filters() {
    }

    public void add(Filter filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    @Override
    public boolean include(Crash crash) {
        for (Filter filter : filters) {
            if (!filter.include(crash)) {
                return false;
            }
        }
        return true;
    }
}
