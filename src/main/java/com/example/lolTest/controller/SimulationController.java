package com.example.lolTest.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lolTest.domain.SimulationSet;

@Controller
public class SimulationController
{
    @GetMapping("/simulations")
    public String showSimulationList(final Model model)
    {
        // 샘플 데이터 (실제 데이터는 DB에서 가져오거나 다른 방법으로 처리)
        List<SimulationSet> simulationSets = Arrays.asList(new SimulationSet("1", "2025-05-09", 3, Arrays.asList("Aatrox", "Ahri", "Akali", "Ashe", "Anivia"),  // 블루팀 벤
                Arrays.asList("Zed", "Yasuo", "Teemo", "Thresh", "Samira"),              // 레드팀 벤
                Arrays.asList("Jinx", "Lux", "Ezreal", "Sona", "Yasuo"),       // 블루팀 픽
                Arrays.asList("Kaisa", "Nami", "Leona", "LeeSin", "Riven")    // 레드팀 픽
        ), new SimulationSet("2", "2025-05-08", 2, Arrays.asList("Zed", "Riven", "Akali", "Aatrox", "Sivir"), Arrays.asList("Ezreal", "Jhin", "Lucian", "Ashe", "Samira"),
                Arrays.asList("Yasuo", "Kaisa", "Thresh", "LeeSin", "Lux"), Arrays.asList("Nami", "Jhin", "Teemo", "Riven", "Ahri")));

        model.addAttribute("simulationSets", simulationSets);
        return "layout/simulation/simulationList";
    }





    @PostMapping("/simulations/detail")
    public String simulationDetail(@RequestParam final String simulationId, final Model model)
    {
        // 임시 4세트 데이터
        List<SimulationSet> sets = new ArrayList<>();

        for ( int i = 1; i <= 4; i++ )
        {
            SimulationSet set = new SimulationSet();
            set.setSimulationId("1");
            set.setSet(i);
            set.setTotalSets(5);
            set.setDate("2025-05-09");
            set.setBlueBans(Arrays.asList("Aatrox", "Ahri", "Akali", "Alistar", "Amumu"));
            set.setRedBans(Arrays.asList("Anivia", "Annie", "Ashe", "AurelionSol"));
            set.setBluePicks(Arrays.asList("Azir", "Bard", "Blitzcrank", "Brand", "Braum"));
            set.setRedPicks(Arrays.asList("Caitlyn", "Camille", "Cassiopeia", "ChoGath", "Corki"));
            sets.add(set);
        }

        model.addAttribute("simulationSets", sets);
        return "layout/simulation/simulationDetail";
    }

}
