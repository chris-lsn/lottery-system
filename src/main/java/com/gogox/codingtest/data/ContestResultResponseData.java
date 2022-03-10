package com.gogox.codingtest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestResultResponseData {
    private int contestId;
    private boolean isWinner;
    private String toUser;
}
