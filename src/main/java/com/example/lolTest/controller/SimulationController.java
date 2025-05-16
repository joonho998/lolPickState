package com.example.lolTest.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.lolTest.domain.SimulationSet;
import com.example.lolTest.handler.DraftHandler;
import com.example.lolTest.service.simulation.SimulationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SimulationController
{
    private final DraftHandler      draftHandler;
    private final SimulationService simulationService;





    @GetMapping("/simulations")
    public String showSimulationList(final Model model)
    {
        model.addAttribute("simulationSets", this.simulationService.draftList());
        return "layout/simulation/simulationList";
    }





    @PostMapping("/simulations/detail")
    public String simulationDetail(@RequestParam final String simulationId, final Model model)
    {
        model.addAttribute("simulationSets", this.simulationService.draftDetail(simulationId));
        return "layout/simulation/simulationDetail";
    }





    @PostMapping("/simulations/creat")
    public String creatDraft(@RequestParam final String roomId, @RequestParam(required = false) final String simulationId, final Model model)
    {
        this.draftHandler.createRoom(roomId);
        List<SimulationSet> simulationSetList = this.simulationService.draftDetail(simulationId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("simulationId", simulationId);
        //        model.addAttribute("totalSets", simulationSetList.get);
        return "layout/index";
    }





    @PostMapping("/simulations/save")
    @ResponseBody
    public int saveDraft(@RequestBody final SimulationSet simulationSet)
    {
        int success = this.simulationService.insertDraft(simulationSet);
        if ( 0 < success )
        {
            this.simulationService.updateDraft(simulationSet.getSimulationId());
        }
        return success;
    }





    @PostMapping("/simulations/exists")
    @ResponseBody
    public boolean checkRoomExists(@RequestParam final String roomId)
    {
        return this.draftHandler.roomExists(roomId);
    }





    @PostMapping("/simulations/pickList")
    @ResponseBody
    public String pickList(@RequestParam final String simulationId)
    {
        return this.simulationService.pickList(simulationId);
    }

}
