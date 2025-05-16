package com.example.lolTest.domain;

import java.util.List;

import lombok.Data;

@Data
public class SimulationSet
{
    private String       simulationId;
    private String       date;
    private int          totalSets;
    private int          setNum;
    private List<String> blueBanList;
    private List<String> redBanList;
    private List<String> bluePickList;
    private List<String> redPickList;

    private String       blueBans;
    private String       redBans;
    private String       bluePicks;
    private String       redPicks;





    public SimulationSet()
    {

    }





    public SimulationSet(final String simulationId, final String date, final int totalSets, final List<String> blueBanList, final List<String> redBanList, final List<String> bluePickList,
            final List<String> redPickList)
    {
        this.simulationId = simulationId;
        this.date = date;
        this.totalSets = totalSets;
        this.blueBanList = blueBanList;
        this.redBanList = redBanList;
        this.bluePickList = bluePickList;
        this.redPickList = redPickList;
    }





    public SimulationSet(final String simulationId, final String date, final int totalSets, final int setNum, final List<String> blueBanList, final List<String> redBanList,
            final List<String> bluePickList, final List<String> redPickList)
    {
        this.simulationId = simulationId;
        this.date = date;
        this.totalSets = totalSets;
        this.setNum = setNum;
        this.blueBanList = blueBanList;
        this.redBanList = redBanList;
        this.bluePickList = bluePickList;
        this.redPickList = redPickList;
    }
}
