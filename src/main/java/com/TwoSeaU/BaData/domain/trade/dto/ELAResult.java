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

    public boolean isManipulated() {
        return !suspiciousRegions.isEmpty();
    }
}