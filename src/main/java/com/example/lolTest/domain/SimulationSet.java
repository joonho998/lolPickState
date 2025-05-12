package com.example.lolTest.domain;

import java.util.List;

import lombok.Data;

@Data
public class SimulationSet
{
    private String       simulationId;
    private String       date;
    private int          totalSets;
    private int          set;
    private List<String> blueBans;
    private List<String> redBans;
    private List<String> bluePicks;
    private List<String> redPicks;





    public SimulationSet()
    {

    }





    public SimulationSet(final String simulationId, final String date, final int totalSets, final List<String> blueBans, final List<String> redBans, final List<String> bluePicks,
            final List<String> redPicks)
    {
        this.simulationId = simulationId;
        this.date = date;
        this.totalSets = totalSets;
        this.blueBans = blueBans;
        this.redBans = redBans;
        this.bluePicks = bluePicks;
        this.redPicks = redPicks;
    }





    public SimulationSet(final String simulationId, final String date, final int totalSets, final int set, final List<String> blueBans, final List<String> redBans, final List<String> bluePicks,
            final List<String> redPicks)
    {
        this.simulationId = simulationId;
        this.date = date;
        this.totalSets = totalSets;
        this.set = set;
        this.blueBans = blueBans;
        this.redBans = redBans;
        this.bluePicks = bluePicks;
        this.redPicks = redPicks;
    }
}
