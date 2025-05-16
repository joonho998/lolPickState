package com.example.lolTest.service.simulation;

import java.util.List;

import com.example.lolTest.domain.SimulationSet;

public interface SimulationService
{
    List<SimulationSet> draftList();





    List<SimulationSet> draftDetail(String simulationId);





    int insertDraft(SimulationSet simulationSet);





    String pickList(String simulationId);





    int updateDraft(String simulationId);
}
