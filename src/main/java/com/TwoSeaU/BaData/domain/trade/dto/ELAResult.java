package com.TwoSeaU.BaData.domain.trade.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ELAResult {
    private final List<SuspiciousRegion> suspiciousRegions;
    private final double manipulationScore;
    private final double ratio;

    public ELAResult(List<SuspiciousRegion> suspiciousRegions, double manipulationScore, double ratio) {
        this.suspiciousRegions = List.copyOf(suspiciousRegions);
        this.manipulationScore = manipulationScore;
        this.ratio = ratio;
    }

    //테스트를 위해 임시로 아주 높은 조작 수치가 아니라면 통과
    public boolean isManipulated() {
        return this.ratio > 98;
    }
}