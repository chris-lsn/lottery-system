package com.gogox.codingtest.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Contest {
    private int contestId;
    private List<Ticket> ticketPool;

    public Contest(int contestId)
    {
        this.contestId = contestId;
        this.ticketPool = new ArrayList<>();
    }
}
