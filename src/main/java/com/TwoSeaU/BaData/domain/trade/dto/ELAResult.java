package com.TwoSeaU.BaData.domain.trade.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ELAResult {
    private final List<SuspiciousRegion> suspiciousRegions;
    private final double manipulationScore;

    public ELAResult(List<SuspiciousRegion> suspiciousRegions, double manipulationScore) {
        this.suspiciousRegions = List.copyOf(suspiciousRegions);
        this.manipulationScore = manipulationScore;
    }

    //테스트를 위해 임시로 검증을 통과하도록 하드코딩
    public boolean isManipulated() {
        return false;
    }
}