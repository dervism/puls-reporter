package no.dervis.puls.model.filters;

import no.dervis.puls.model.survey.PulseSurvey;
import no.dervis.puls.model.survey.Question;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Example usage:
 *
 *  var workbook = surveyMap.get("Sheet1")
 *  var s = new LinkedList<Predicate<PulseRatedResponse>>();
 *  s.add(r -> r.score() == 6 || r.score() == 7);
 *  DataRegion dataRegion = DataRegion.create(workbook);
 *  System.out.println(dataRegion.byLetter("F").countAllInts(s));
 *
 */

public record DataRegion(Map<Question, Column> data) {

    public DataRegion(List<Question> questions) {
        this(questions.stream()
                .collect(Collectors.toMap(k -> k, v -> new Column())));
    }

    public Optional<Question> byName(String text) {
        return data.keySet()
                .stream()
                .filter(q -> q.question().equalsIgnoreCase(text))
                .findFirst();
    }

    public Column byId(int id) {
        return data.get(
          id > 1 ? data.keySet().stream().skip(id-1).findFirst().get() :
                  data.keySet().stream().findFirst().get()
        );
    }

    public Column byLetter(String letter) {
        return byId(Filters.LetterMap.valueOf(letter.toUpperCase()).getIndex());
    }

    public static DataRegion create(PulseSurvey survey) {
        final var map = new LinkedHashMap<Question, Column>();

        survey.getQuestions().forEach(question -> {
            final Column column = new Column();
            survey.getRespondents().forEach(respondent -> {
                column.responses().add(respondent.response(question));
            });
            map.put(question, column);
        });

        return new DataRegion(map);
    }
}
