package com.lielamar.toed.modules;

public enum Direction {

    N("north"),
    NE("north_east"),
    E("east"),
    SE("south_east"),
    S("south"),
    SW("south_west"),
    W("west"),
    NW("north_west"),
    X("location");


    private final String rawName;

    Direction(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return rawName;
    }
}
