package com.gogox.codingtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogox.codingtest.data.ContestResultResponseData;
import com.gogox.codingtest.exception.AlreadyJoinedContestException;
import com.gogox.codingtest.model.Contest;
import com.gogox.codingtest.model.Ticket;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log
class ContestServiceTest {
    @InjectMocks
    @Spy
    private ContestService contestService = new ContestService();

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Captor
    private ArgumentCaptor<List<ContestResultResponseData>> captor;

    @BeforeEach
    void setup()
    {
        ReflectionTestUtils.setField(contestService, "drawRate", 10000);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_joinContest() throws AlreadyJoinedContestException {
        final String username = "user1";
        Contest contest = new Contest(1);

        doReturn(contest).when(contestService).getContest();
        contestService.joinContest(username);
        assertFalse(contest.getTicketPool().isEmpty());
    }

    @Test
    void test_joinContest_givenUserHasAlreadyJoined() {
        final String username = "user1";
        Ticket t1 = new Ticket(username);
        Contest contest = new Contest(1);
        contest.setTicketPool(Collections.singletonList(t1));

        doReturn(contest).when(contestService).getContest();
        AlreadyJoinedContestException exception = assertThrows(AlreadyJoinedContestException.class, () -> contestService.joinContest(username));
        assertEquals(t1, exception.getTicket());
    }

    @Test
    void test_startContest() throws JsonProcessingException {
        Ticket t1 = new Ticket("user1");
        Ticket t2 = new Ticket("user2");
        Ticket t3 = new Ticket("user3");
        Ticket t4 = new Ticket("user4");
        Ticket t5 = new Ticket("user5");
        Contest contest = new Contest(1);
        contest.setTicketPool(Arrays.asList(t1, t2, t3, t4, t5));

        doReturn(contest).when(contestService).getContest();

        contestService.startContest();

        verify(contestService, times(1)).sendContestResultByWS(captor.capture());
        log.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(captor.getValue()));
    }
}