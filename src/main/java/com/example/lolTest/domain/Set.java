package com.example.lolTest.domain;

import java.util.List;

import lombok.Data;

@Data
public class Set
{
    private List<String> bans;
    private List<String> picks;





    public Set(final List<String> bans, final List<String> picks)
    {
        this.bans = bans;
        this.picks = picks;
    }
}
