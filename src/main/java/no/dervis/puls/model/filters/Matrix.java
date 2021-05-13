package no.dervis.puls.model.filters;

import no.dervis.puls.model.survey.Pair;
import no.dervis.puls.model.survey.Pair.IntPair;
import no.dervis.puls.model.survey.Pair.StringPair;
import no.dervis.puls.model.survey.PulseSurvey;
import no.dervis.puls.model.survey.Respondent;
import no.dervis.puls.model.survey.Responses.PulseRatedResponse;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static no.dervis.puls.model.filters.Filters.byLetter;

public class Matrix {

    private final static String[] letters = {"E", "F", "G", "H", "I"};

    public static PulseSurvey multiColumnFilter(PulseSurvey pulseSurvey, List<Pair<String, Integer>> pairs) {
        return pulseSurvey.filter(new LinkedList<>(
                pairs.stream()
                        .map(pair -> selectPredicate(pair.left(), pair.right()))
                        .collect(Collectors.toList())
        ));
    }

    public static MatrixModel matrix(PulseSurvey pulseSurvey, IntPair ratings) {
        var map = new LinkedHashMap<String, List<StringPair>>();
        for (String left : letters) {
            for (String right : letters)
                map.merge(left, List.of(StringPair.of(left, right)),
                        (k, v) -> of(k, v).flatMap(Collection::stream).collect(toList()));
        }

        return makeMatrix(pulseSurvey, ratings, map);
    }

    private static MatrixModel makeMatrix(PulseSurvey pulseSurvey, IntPair ratings, LinkedHashMap<String, List<StringPair>> map) {
        var dataRegion = DataRegion.create(pulseSurvey);

        MatrixModel model = new MatrixModel();

        map.forEach((column, pairs) -> {
            List<String> ratingsList = pairs.stream()
                    .map(pair -> column.equalsIgnoreCase(pair.right()) ? "0" : rowFilter(pulseSurvey, ratings, pair) + "")
                    .collect(toList());

            MatrixModelLine line = new MatrixModelLine(
                    columnFilter(dataRegion, ratings.left(), column),
                    byLetter(pulseSurvey.getQuestions(), column).question(),
                    ratingsList
            );

            model.addModelLine(line);
        });

        return model;
    }

    private static int columnFilter(DataRegion dataRegion, int rating, String column) {
        return dataRegion
                .byLetter(column)
                .orElseThrow(() -> new IllegalArgumentException("Could not find column " + column))
                .countInt(selectPredicate(rating));
    }

    private static int rowFilter(PulseSurvey pulseSurvey, IntPair ratings, StringPair columns) {
        return pulseSurvey.filter(new LinkedList<>(
                List.of(
                        selectPredicate(columns.left(), ratings.left()),
                        selectPredicate(columns.right(), ratings.right()))
        )).size();
    }

    /**
     * Given a column 'n' and a value 'v', makes a predicate that checks
     * all cells that match:
     *
     * column(n) = k OR column(n) = k - 1 => for values 2 or 7, or
     * column(n) = k OR column(n) = k - 1 OR column(n) = k - 2 => for values 3 or 5.
     *
     * The predicates can then be used to group the pulse results into
     * all ratings that are "1 or 2", "1 to 3", "3 to 5" or "6 or 7".
     *
     * @param column the column in the excel sheet
     * @param value the max rating to filter
     * @return a predicate
     * @see Matrix#makePredicate
     * @see Matrix#makePredicate3
     */
    private static Predicate<Respondent> selectPredicate(String column, int value) {
        return switch (value) {
            case 2, 7 -> makePredicate(column, value);
            case 3, 5 -> makePredicate3(column, value);
            default -> throw new IllegalArgumentException();
        };
    }

    private static Predicate<PulseRatedResponse> selectPredicate(int value) {
        return switch (value) {
            case 2, 7 -> makePredicate(value);
            case 3, 5 -> makePredicate3(value);
            default -> throw new IllegalArgumentException();
        };
    }

    private static Function<Pair<String, Integer>, Predicate<Respondent>> makeFPredicate() {
        return g -> r -> r.intValue(g.left()) == g.right()-1;
    }

    private static Predicate<Respondent> makePredicate(final String n, final int k) {
        return r -> r.intValue(n) == k-1 || r.intValue(n) == k;
    }

    private static Predicate<PulseRatedResponse> makePredicate(final int k) {
        return r -> r.response() == k-1 || r.response() == k;
    }

    private static Predicate<Respondent> makePredicate3(final String n, final int k) {
        return r -> r.intValue(n) == k-2 || r.intValue(n) == k-1 || r.intValue(n) == k;
    }

    private static Predicate<PulseRatedResponse> makePredicate3(final int k) {
        return r -> r.response() == k-2 || r.response() == k-1 || r.response() == k;
    }
}
