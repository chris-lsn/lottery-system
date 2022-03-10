package com.gogox.codingtest.exception;

import com.gogox.codingtest.model.Ticket;
import lombok.Getter;

@Getter
public class AlreadyJoinedContestException extends Exception{

    private final Ticket ticket;

    public AlreadyJoinedContestException(Ticket ticket)
    {
        super("User " + ticket.getUsername() + " has already joined the contest");
        this.ticket = ticket;
    }
}
