package com.example.lolTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickState
{
    public int                 index             = 0;
    public List<String>        order             = Arrays.asList("blue-ban-1", "red-ban-1", "blue-ban-2", "red-ban-2", "blue-ban-3", "red-ban-3", "blue-pick-1", "red-pick-1", "red-pick-2",
            "blue-pick-2", "blue-pick-3", "red-pick-3", "red-ban-4", "blue-ban-4", "red-ban-5", "blue-ban-5", "red-pick-4", "blue-pick-4", "blue-pick-5", "red-pick-5");
    public List<String>        selected          = new ArrayList<>();

    private String             phase             = "BAN";
    private final int          timeLeft          = 30;
    private final List<String> selectedChampions = new ArrayList<>();





    public void selectChampion(final String championId)
    {
        if ( this.selectedChampions.size() < 10 )
        {
            this.selectedChampions.add(championId);
            // 여기서 상태가 PICK이면, phase 변경 로직을 추가할 수 있음
            if ( this.selectedChampions.size() >= 10 )
            {
                this.phase = "PICK";
            }
        }
    }

}
