package no.dervis.puls.model.survey;

import no.dervis.puls.model.filters.DataRegion;
import no.dervis.puls.model.survey.PulseRatedQuestion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PulseSurveyTest {

    @Test
    void should_initialize_questions_with_empty_responses() {
        final DataRegion pulseSurvey = new DataRegion(List.of(
                new PulseRatedQuestion("Q1"),
                new PulseRatedQuestion("Q2"),
                new PulseRatedQuestion("Q3"),
                new PulseRatedQuestion("Q4")
        ));

        assertEquals(pulseSurvey.data().keySet().size(), 4);

        pulseSurvey.data().keySet().forEach(question -> {
            var responses = pulseSurvey.data().get(question);
            assertNotNull(responses);
            assertTrue(responses.responses().isEmpty());
        });

    }
}