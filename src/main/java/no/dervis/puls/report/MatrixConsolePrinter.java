package no.dervis.puls.report;

import no.dervis.puls.model.survey.Pair.IntPair;
import no.dervis.puls.model.survey.PulseSurvey;
import no.dervis.puls.model.survey.PulseTextQuestion;
import no.dervis.puls.model.survey.Question;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static no.dervis.puls.model.filters.Matrix.matrix;

public class MatrixConsolePrinter implements ConsolePrinter {

    private static final int DEFAULT_TRUNCATION_LENGTH = 20;
    private static final int DEFAULT_SKIP_HEADERS = 3;

    private final PulseSurvey pulse;

    private int truncation;
    private int skipHeaders;
    private boolean prettyPrint;

    private List<IntPair> pairs;
    private List<String> descriptions;
    private List<String> aliases;

    public MatrixConsolePrinter(PulseSurvey pulse) {
        this(pulse, DEFAULT_TRUNCATION_LENGTH, DEFAULT_SKIP_HEADERS, false);
    }

    public MatrixConsolePrinter(PulseSurvey pulse, int truncation, int skipHeaders, boolean prettyPrint) {
        this.pulse = pulse;
        this.truncation = truncation;
        this.skipHeaders = skipHeaders;
        this.prettyPrint = prettyPrint;
        this.pairs = List.of();
        this.descriptions = List.of();
        this.aliases = List.of();
    }

    public MatrixConsolePrinter pretty(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    public MatrixConsolePrinter fromColumn(String col) {

        return this;
    }

    public MatrixConsolePrinter toColumn(String col) {

        return this;
    }

    public MatrixConsolePrinter withPairs(List<IntPair> pairs) {
        this.pairs = pairs;
        return this;
    }

    public MatrixConsolePrinter withAlias(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public MatrixConsolePrinter withDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
        return this;
    }

    public MatrixConsolePrinter withTruncation(int truncation) {
        this.truncation = truncation;
        return this;
    }

    public MatrixConsolePrinter skipHeaders(int skipHeaders) {
        this.skipHeaders = skipHeaders;
        return this;
    }

    public void printDefault() {
        this.withPairs(
                List.of(
                        IntPair.of(7, 7), // those who rated high, who also rated others high
                        IntPair.of(3, 3), // those who rated low, who also rated others low
                        IntPair.of(7, 3), // those who rated high, who also rated others low
                        IntPair.of(3, 7), // those who rated low, who also rated others high
                        IntPair.of(2, 2)  // those who rated very low, who also rated others very low
                )
        ).withDescriptions(
                List.of(
                        "Those who rated high (6 or 7), who also rated others high",
                        "Those who rated low (3 or less), who also rated others low",
                        "Those who rated high (6 or 7), who also rated others low",
                        "Those who rated low (3 or less), who also rated others high",
                        "Those who rated very low (2 or less), who also rated others very low"
                )
        ).withAlias(
                List.of(
                        "Spørsmål",
                        "Antall",
                        "Personalleder",
                        "Læring",
                        "Motivasjon",
                        "Tilhørighet",
                        "Arbeidsmengde"
                )
        ).skipHeaders(4)
                .print();
    }

    @Override
    public void print() {
        var sb = new StringBuilder();

        if (!descriptions.isEmpty() && pairs.size() != descriptions.size())
            throw new IllegalArgumentException("Rating pairs and descriptions have different length.");

        var descriptionsList = new LinkedList<>(descriptions);

        pairs.forEach(ratingPair -> {
            var aliasList = new LinkedList<>(aliases);

            if (!descriptionsList.isEmpty()) {
                sb.append(descriptionsList.removeFirst());
                sb.append(lineSeparator());
            }

            // headers
            var questions = new LinkedList<>(pulse.getQuestions());
            questions.add(skipHeaders, new PulseTextQuestion("Questions"));
            questions.add(skipHeaders+1, new PulseTextQuestion("Count"));

            var stream = questions.stream();
            var questionStream = skipHeaders > 0 ? stream.skip(skipHeaders + 1) : stream;

            questionStream
                    .forEach(question -> sb.append(
                            paddingWithTruncation(getAlias(aliasList, question))).append("\t")
                    );

            sb.append(lineSeparator());
            sb.append(
                    build(matrix(pulse, ratingPair), new LinkedList<>(aliases.subList(2, aliases.size())))
            );
        });

        System.out.println(sb.toString());
    }

    private static String getAlias(LinkedList<String> aliasList, Question question) {
        return !aliasList.isEmpty() ? aliasList.removeFirst() : question.question();
    }

    private String build(String matrix, List<String> aliases) {
        var builder = new StringBuilder();
        var lines = matrix.split(lineSeparator());
        var aliasList = new LinkedList<>(aliases);

        Arrays.stream(lines).forEach(line ->  {
            var split = line.split("#");
            var countPrHeader = split[0];
            var columnHeader = paddingWithTruncation(headerName(aliasList, split[1]));
            var ratings = split[2];

            builder.append(columnHeader)
              .append(tabulate(countPrHeader))
              .append(tabulate(ratingsToPercentage(ratings, parseInt(countPrHeader))))
              .append(lineSeparator());
        });

        builder.append(lineSeparator());
        builder.append(lineSeparator());

        return builder.toString();
    }

    private static String headerName(LinkedList<String> aliasList, String header) {
        return !aliasList.isEmpty() ? aliasList.removeFirst() : header;
    }

    private String tabulate(String ratings) {
        return Arrays
                .stream(ratings.split(","))
                .reduce("", (left, right) -> left + "\t" + rightPadding(right, truncation));
    }

    private static String ratingsToPercentage(String ratings, int count) {
        return Arrays
                .stream(ratings.split(","))
                .map(s -> s.isBlank() ? 0 : parseInt(s))
                .peek(s -> { if (s > count) throw new IllegalArgumentException(); })
                .map(s -> percentageOf(s, count))
                .collect(Collectors.joining(","));
    }

    private static String percentageOf(int rating, int count) {
        if (rating == 0) return "" + 0;
        double percentage = ((double)rating/count)*100;
        return rating + " (" + format("%.2f", percentage) + "%)";
    }

    private String paddingWithTruncation(String string) {
        return rightPadding(truncate(string), truncation);
    }

    private static String rightPadding(String string, int padding) {
        return string.length() < padding ?
                format("%1$-" + padding + "s", string) : string;
    }

    private String truncate(String question) {
        return question.length() > truncation ? question.substring(0, truncation) : question;
    }
}
