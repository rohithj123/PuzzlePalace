package com.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PlayerProgressReportTest {

    @Test
    public void constructor_clampsValuesAndCopiesSnapshots() {
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
    public void emptyFactory_returnsZeroedReport() {
        PlayerProgressReport report = PlayerProgressReport.empty();

        assertEquals(0, report.getCompletionPercent());
        assertEquals(0, report.getSolvedCount());
        assertEquals(0, report.getTotalPuzzles());
        assertTrue(report.getSnapshots().isEmpty());
    }

    @Test
    public void report_calculatesCompletionCorrectly() {
        List<PuzzleProgressSnapshot> snapshots = List.of(
                new PuzzleProgressSnapshot(1, "Solved1"),
                new PuzzleProgressSnapshot(2, "Solved2"),
                new PuzzleProgressSnapshot(3, "Unsolved")
        );

        PlayerProgressReport report = new PlayerProgressReport(66, 2, 3, snapshots);

        assertEquals(66, report.getCompletionPercent());
        assertEquals(2, report.getSolvedCount());
        assertEquals(3, report.getTotalPuzzles());
    }

    @Test
    public void snapshots_areImmutableCopies() {
        List<PuzzleProgressSnapshot> snapshots = new ArrayList<>();
        snapshots.add(new PuzzleProgressSnapshot(10, "Alpha"));

        PlayerProgressReport report = new PlayerProgressReport(50, 1, 2, snapshots);

        List<PuzzleProgressSnapshot> internal = report.getSnapshots();
        assertEquals(1, internal.size());
        snapshots.add(new PuzzleProgressSnapshot(11, "Beta"));

        assertEquals(1, internal.size());
        assertNotSame(snapshots, internal);
    }
}
