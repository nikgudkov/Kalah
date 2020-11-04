package org.task.kalah.dto.response;

import lombok.Value;

import java.util.Map;

@Value
public class GameSowStonesResponse {

    Long id;
    String uri;
    Map<Integer, Integer> status;

}
