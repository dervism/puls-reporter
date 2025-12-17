package no.dervis.puls.model.survey;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PulseRatedQuestionTest {

    @Test
    void has_correct_min() {
        assertEquals(1, new PulseRatedQuestion("", List.of(1, 2, 3, 5, 7)).minRating());
    }

    @Test
    void has_correct_max() {
        assertEquals(7, new PulseRatedQuestion("", List.of(1, 2, 3, 5, 7)).maxRating());
    }
}