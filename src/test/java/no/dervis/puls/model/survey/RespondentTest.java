package no.dervis.puls.model.survey;

import no.dervis.puls.model.survey.Responses.PulseRatedResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RespondentTest {

    @Test
    void test_returns_correct_answer_type() {
        final PulseRatedQuestion ratedQuestion = new PulseRatedQuestion("");

        var respondent = new Respondent(Map.of(ratedQuestion, new PulseRatedResponse(3)));

        assertDoesNotThrow(() -> respondent.intValue(ratedQuestion));
        assertThrows(RuntimeException.class, () -> respondent.stringValue(ratedQuestion));
    }
}