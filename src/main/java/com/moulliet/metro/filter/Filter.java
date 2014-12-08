package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Crash;

public interface Filter {

    boolean include(Crash crash);
}
