package com.example.lolTest.mapper.simulation;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.lolTest.domain.SimulationSet;

@Mapper
public interface SimulationMapper
{
    int insertDraft(SimulationSet simulationSet);





    List<SimulationSet> draftList();





    List<SimulationSet> draftDetail(String simulationId);





    List<SimulationSet> pickList(String simulationId);





    int updateDraft(String simulationId);
}
