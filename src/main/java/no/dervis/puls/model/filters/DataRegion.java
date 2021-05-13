package no.dervis.puls.model.filters;

import no.dervis.puls.model.survey.PulseSurvey;
import no.dervis.puls.model.survey.Question;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a mapping of all the columns in an Excel sheet.
 * The DataRegion record makes it possible to access all the
 * data in a single column based on a columns name, id or letter.
 *
 * Example usage to filter and count data in a column:
 *
 * <pre>
 *
 *  var sheet = surveyMap.get("Sheet1")
 *  var s = new LinkedList<Predicate<PulseRatedResponse>>();
 *  s.add(r -> r.score() == 6 || r.score() == 7);
 *  DataRegion dataRegion = DataRegion.create(sheet);
 *  System.out.println(dataRegion.byLetter("F").countAllInts(s));
 *
 * </pre>
 *
 * @author dervis
 */

public record DataRegion(Map<Question, Column> data) {

    public DataRegion(List<Question> questions) {
        this(questions.stream()
                .collect(Collectors.toMap(k -> k, v -> new Column())));
    }

    public Optional<Question> byName(String text) {
        Objects.requireNonNull(text);
        return data.keySet()
                .stream()
                .filter(q -> q.question().equalsIgnoreCase(text))
                .findFirst();
    }

    /**
     * Get a column by it's id. The method counts the columns
     * based on an index that start at 1. So column A is 1, B
     * is 2, etc.
     *
     * @param id the id of the column
     * @return the column and all data in that column
     */
    public Optional<Column> byId(int id) {

        Objects.requireNonNull(data);

        if (data.keySet().isEmpty())
            return Optional.empty();

        if (id < 0 || id > data.keySet().size())
            return Optional.empty();

        Optional<Question> question = data.keySet()
                .stream()
                .skip(id - 1)
                .findFirst();

        return question.map(data::get);
    }

    /**
     * Find a column by its column letter. The letters available
     *
     * @param letter a letter identifing a column in Excel
     * @return the column
     */
    public Optional<Column> byLetter(String letter) {
        Objects.requireNonNull(letter);
        return byId(Filters.LetterMap.valueOf(letter.toUpperCase()).getIndex());
    }

    /**
     * Takes in a PulseSurvey, and returns a mapping of all
     * the columns and their data.
     *
     * @param survey a sheet in an Excel workbook
     * @return a mapping of question -> column with all data
     */
    public static DataRegion create(PulseSurvey survey) {
        final var map = new LinkedHashMap<Question, Column>();

        survey.getQuestions().forEach(question -> {
            Column column = new Column();
            survey.getRespondents().forEach(respondent ->
                    // for each row, add only the data in the column
                    // identified by the parameter 'question'
                    column.responses().add(respondent.response(question)));
            map.put(question, column);
        });

        return new DataRegion(map);
    }
}
