package com.gogox.codingtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogox.codingtest.data.ContestResultResponseData;
import com.gogox.codingtest.exception.AlreadyJoinedContestException;
import com.gogox.codingtest.model.Contest;
import com.gogox.codingtest.model.Ticket;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log
public class ContestService {
    @Value("${contest.draw.rate}")
    private int drawRate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Contest contest = new Contest(1);

    /**
     * Check if the user has already joined the lottery.
     * if yes, throw new AlreadyJoinedContestException
     * if no, create a ticket and add it in the ticketPool of next lottery
     *
     * @param username the username of user
     * @return the created Ticket
     * @throws AlreadyJoinedContestException
     */
    public Ticket joinContest(String username) throws AlreadyJoinedContestException {
        Contest c = getContest();
        Ticket ticket = c.getTicketPool().stream().filter(t -> t.getUsername().equals(username))
                .findFirst().orElse(null);
        if (ticket != null) {
            throw new AlreadyJoinedContestException(ticket);
        }
        Ticket newTicket = new Ticket(username);
        c.getTicketPool().add(newTicket);
        log.info(c.getContestId() + " draw - User " + username + " joined");
        return newTicket;
    }

    @Scheduled(fixedRateString = "${contest.draw.rate}")
    public void startContest() {
        Contest c = this.getContest();
        if (CollectionUtils.isEmpty(c.getTicketPool())) {
            log.info(c.getContestId() + " draw - Ticket pool is empty, no winner");
        } else {
            Random rand = new Random();
            Ticket winningTicket = c.getTicketPool().get(rand.nextInt(c.getTicketPool().size()));
            log.info(c.getContestId() + " draw - Winner is " + winningTicket.getUsername());

            List<ContestResultResponseData> contestResultResponseDataList = c.getTicketPool().stream()
                    .map(t -> new ContestResultResponseData(c.getContestId(), t.getUsername().equals(winningTicket.getUsername()), t.getUsername()))
                    .collect(Collectors.toList());

            sendContestResultByWS(contestResultResponseDataList);
        }
        contest = new Contest(c.getContestId() + 1);
        log.info(c.getContestId() + " draw - Will be started after " + drawRate / 1000 + "s");
    }

    public Contest getContest() {
        return contest;
    }

    /**
     * The frontend receives the result by subscribing /user/queue/contest-result using SockJS
     * @param contestResultResponseDataList
     */
    public void sendContestResultByWS(List<ContestResultResponseData> contestResultResponseDataList) {
        for (ContestResultResponseData contestResultResponseData : contestResultResponseDataList)
        {
            try {
                log.info("Sending contest result by WebSocket; data: "
                        + new ObjectMapper().writeValueAsString(contestResultResponseDataList));
                messagingTemplate.convertAndSendToUser(contestResultResponseData.getToUser(), "/queue/contest-result",
                        contestResultResponseData);
            } catch (MessagingException e) {
                log.warning("Fail to send contest result");
            } catch (JsonProcessingException e) {
                log.warning("Fail to print the list of contestResultResponseData");
            }
        }
    }
}
