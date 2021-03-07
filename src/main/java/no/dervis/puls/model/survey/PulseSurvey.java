package no.dervis.puls.model.survey;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PulseSurvey {
    private List<Question> questions;
    private List<Respondent> respondents;

    public PulseSurvey() { }

    public PulseSurvey(List<Question> questions, List<Respondent> respondents) {
        this.questions = questions;
        this.respondents = respondents;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Respondent> getRespondents() {
        return respondents;
    }

    public void setRespondents(List<Respondent> respondents) {
        this.respondents = respondents;
    }

    public PulseSurvey filter(Predicate<Respondent> predicate) {
        return new PulseSurvey(questions,
                respondents.stream()
                        .filter(predicate)
                        .collect(Collectors.toList()));
    }

    public PulseSurvey filter(List<Predicate<Respondent>> predicates) {
        return filter(predicates, respondents);
    }

    private PulseSurvey filter(List<Predicate<Respondent>> predicates, List<Respondent> data) {
        if (predicates.isEmpty()) {
            return new PulseSurvey(questions, data);
        }
        return filter(predicates, data.stream()
                .filter(predicates.remove(0))
                .collect(Collectors.toList()));
    }

    public int size() {
        return respondents.size();
    }
}
