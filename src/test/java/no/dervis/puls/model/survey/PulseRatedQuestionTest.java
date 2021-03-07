package no.dervis.puls.model.survey;

import no.dervis.puls.model.survey.PulseRatedQuestion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PulseRatedQuestionTest {

    @Test
    void has_correct_min() {
        assertEquals(new PulseRatedQuestion("", List.of(1, 2, 3, 5, 7)).minRating(), 1);
    }

    @Test
    void has_correct_max() {
        assertEquals(new PulseRatedQuestion("", List.of(1, 2, 3, 5, 7)).maxRating(), 7);
    }
}