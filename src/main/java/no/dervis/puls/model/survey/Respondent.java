package no.dervis.puls.model.survey;

import java.time.LocalDateTime;
import java.util.Map;

import static no.dervis.puls.model.filters.Filters.byLetter;

public record Respondent(
        int id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Map<Question, Response<?>> answers) {

    public Respondent(Map<Question, Response<?>> answers) {
        this(0, LocalDateTime.now(), LocalDateTime.now(), answers);
    }

    public Response<?> response(Question question) {
        return answers.get(question);
    }

    public int intValue(Question question) {
        return answer(question, Integer.class);
    }

    public int intValue(String column) {
        return intValue(byLetter(answers.keySet(), column));
    }

    public String stringValue(Question question) {
        return answer(question, String.class);
    }

    public String stringValue(String column) {
        return stringValue(byLetter(answers.keySet(), column));
    }

    @SuppressWarnings("unchecked")
    private <T> T answer(Question question, Class<T> clazz) {
        Object response = answers.get(question).response();
        if (clazz.isInstance(response)) {
            return (T) response;
        }
        throw new RuntimeException("Illegal type. Expected " + clazz.getSimpleName()
                + ", was: " + response.getClass().getSimpleName() + " for question: " + question.question());
    }

}
