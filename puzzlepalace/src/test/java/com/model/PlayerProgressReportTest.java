package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PlayerProgressReportTest {

    @Test
    public void constructorClampsValuesAndCopiesSnapshots() {
        List<PuzzleProgressSnapshot> snapshots = new ArrayList<>();
        snapshots.add(new PuzzleProgressSnapshot(1, "Q1"));

        PlayerProgressReport report = new PlayerProgressReport(150, -1, 5, snapshots);

        assertEquals(100, report.getCompletionPercent());
        assertEquals(0, report.getSolvedCount());
        assertEquals(5, report.getTotalPuzzles());
        assertEquals(1, report.getSnapshots().size());

        snapshots.clear();
        assertEquals(1, report.getSnapshots().size());
    }

    @Test
    public void emptyFactoryReturnsZeroedReport() {
        PlayerProgressReport report = PlayerProgressReport.empty();

        assertEquals(0, report.getCompletionPercent());
        assertEquals(0, report.getSolvedCount());
        assertEquals(0, report.getTotalPuzzles());
        assertTrue(report.getSnapshots().isEmpty());
    }
}
