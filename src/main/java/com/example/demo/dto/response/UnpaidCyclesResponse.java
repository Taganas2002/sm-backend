package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record UnpaidCyclesResponse(
    Long studentId,
    String studentFullName,
    String studentNumber,
    School school,
    List<GroupCycles> groups
) {
    public record School(String name, String logoUrl) {}

    public record GroupCycles(
        Long groupId,
        String groupName,
        List<Cycle> cycles
    ) {}

    public record Cycle(
        String period,
        Integer sessionsPerCycle,
        Integer attended,
        Integer absent,
        BigDecimal sessionPrice,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal balance,
        String status,
        boolean mustPayFirst
    ) {}
}
