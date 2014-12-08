package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Crash;

public class FalseFilter implements Filter {
    @Override
    public boolean include(Crash crash) {
        return false;
    }
}
