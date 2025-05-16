package com.example.lolTest.service.simulation.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lolTest.domain.SimulationSet;
import com.example.lolTest.mapper.simulation.SimulationMapper;
import com.example.lolTest.service.simulation.SimulationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SimulationServiceImpl implements SimulationService
{
    private final SimulationMapper simulationMapper;





    @Override
    public int insertDraft(final SimulationSet simulationSet)
    {

        return this.simulationMapper.insertDraft(simulationSet);
    }





    @Override
    public int updateDraft(final String simulationId)
    {

        return this.simulationMapper.updateDraft(simulationId);
    }





    @Override
    public List<SimulationSet> draftList()
    {
        List<SimulationSet> simulationSetList = this.simulationMapper.draftList();
        for ( SimulationSet set : simulationSetList )
        {
            set.setBlueBanList(Arrays.stream(set.getBlueBans().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setRedBanList(Arrays.stream(set.getRedBans().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setBluePickList(Arrays.stream(set.getBluePicks().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setRedPickList(Arrays.stream(set.getRedPicks().split(",")).map(String::trim).collect(Collectors.toList()));
        }
        return simulationSetList;
    }





    @Override
    public List<SimulationSet> draftDetail(final String simulationId)
    {
        List<SimulationSet> simulationSetList = this.simulationMapper.draftDetail(simulationId);
        for ( SimulationSet set : simulationSetList )
        {
            set.setBlueBanList(Arrays.stream(set.getBlueBans().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setRedBanList(Arrays.stream(set.getRedBans().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setBluePickList(Arrays.stream(set.getBluePicks().split(",")).map(String::trim).collect(Collectors.toList()));
            set.setRedPickList(Arrays.stream(set.getRedPicks().split(",")).map(String::trim).collect(Collectors.toList()));
        }
        return simulationSetList;
    }





    @Override
    public String pickList(final String simulationId)
    {
        List<SimulationSet> SimulationSetList = this.simulationMapper.pickList(simulationId);
        StringBuilder picks = new StringBuilder();
        for ( SimulationSet simulationSet : SimulationSetList )
        {
            picks.append(simulationSet.getBluePicks());
            picks.append(",");
            picks.append(simulationSet.getRedPicks());
        }
        return picks.toString();
    }

}
