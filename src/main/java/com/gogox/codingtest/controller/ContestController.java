package com.gogox.codingtest.controller;

import com.gogox.codingtest.data.HttpResponseData;
import com.gogox.codingtest.exception.AlreadyJoinedContestException;
import com.gogox.codingtest.model.Ticket;
import com.gogox.codingtest.service.ContestService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/contest")
@Log
public class ContestController {

    private final ContestService contestService;

    @Autowired
    public ContestController(ContestService contestService)
    {
        this.contestService = contestService;
    }

    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponseData> joinContest(HttpServletRequest request) throws AlreadyJoinedContestException {
        String username = request.getUserPrincipal().getName();

        Ticket ticket = contestService.joinContest(username);

        HttpResponseData responseData = HttpResponseData.createResponse(true, "You have joined the contest successfully", ticket);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @ExceptionHandler(AlreadyJoinedContestException.class)
    public ResponseEntity<HttpResponseData> handleException(AlreadyJoinedContestException e) {
        HttpResponseData responseData = HttpResponseData.createResponse(false, "You have already joined the contest", e.getTicket());
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }
}
