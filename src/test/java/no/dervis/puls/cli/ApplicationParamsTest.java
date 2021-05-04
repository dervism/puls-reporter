package no.dervis.puls.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationParamsTest {

    @Test
    void calculateColumnList() {
        assertEquals(
                List.of("A", "B", "C", "D"),
                ApplicationParams.calculateColumnList('A', 'D'));
    }
}