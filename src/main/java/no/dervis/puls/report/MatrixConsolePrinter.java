package no.dervis.puls.report;

import no.dervis.puls.model.survey.Pair;
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
    private static final int DEFAULT_SKIP_HEADERS = 4;

    private final PulseSurvey pulse;

    private int truncation;

    private int skipHeaders;

    private boolean prettyPrint;

    private List<Pair<Integer, Integer>> pairs;
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

    public MatrixConsolePrinter withPairs(List<Pair<Integer, Integer>> pairs) {
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
                        Pair.of(7, 7), // those who rated high, who also rated others high
                        Pair.of(3, 3), // those who rated low, who also rated others low
                        Pair.of(7, 3), // those who rated high, who also rated others low
                        Pair.of(3, 7), // those who rated low, who also rated others high
                        Pair.of(2, 2)  // those who rated very low, who also rated others very low
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
        ).print();
    }

    @Override
    public void print() {
        final StringBuilder sb = new StringBuilder();

        if (!descriptions.isEmpty() && pairs.size() != descriptions.size())
            throw new IllegalArgumentException("Pairs and Descriptions have different length.");

        LinkedList<String> descriptionsList = new LinkedList<>(descriptions);

        pairs.forEach(pair -> {
            LinkedList<String> aliasList = new LinkedList<>(aliases);

            if (!descriptionsList.isEmpty()) {
                sb.append(descriptionsList.removeFirst());
                sb.append(lineSeparator());
            }

            // headers
            final LinkedList<Question> questions = new LinkedList<>(pulse.getQuestions());
            questions.add(skipHeaders, new PulseTextQuestion("Questions"));
            questions.add(skipHeaders+1, new PulseTextQuestion("Count"));

            questions.stream()
                    .skip(skipHeaders+1)
                    .forEach(question -> {
                                String truncate = truncate(
                                        !aliasList.isEmpty() ? aliasList.removeFirst() : question.question()
                                );
                                sb.append(prettyPrint ?
                                        rightPadding(truncate, truncation) : truncate
                                ).append("\t");
                    }
                    );

            sb.append(lineSeparator());
            sb.append(
                    extracted(matrix(pulse, pair.left(), pair.right()),
                            new LinkedList<>(aliases.subList(2, aliases.size())))
            );
        });

        System.out.println(sb.toString());
    }

    private String extracted(String matrix, List<String> aliases) {

        StringBuilder sb = new StringBuilder();
        final String[] lines = matrix.split(lineSeparator());

        LinkedList<String> aliasList = new LinkedList<>(aliases);

        Arrays.stream(lines).forEach(line ->  {
            final String[] split = line.split("#");
            final String countPrHeader = split[0];
            final String columnHeader = truncate(split[1]);
            final String ratings = split[2];

            sb.append(!aliasList.isEmpty() ? truncateOrPadding(aliasList.removeFirst()) : columnHeader)
                    .append(ratingsToTabs(countPrHeader))
                    .append(ratingsToTabs(ratingsToPercentage(ratings, parseInt(countPrHeader))))
                    .append(lineSeparator());
        });

        sb.append(lineSeparator());
        sb.append(lineSeparator());

        return sb.toString();
    }

    private String ratingsToTabs(String ratings) {
        return Arrays
                .stream(ratings.split(","))
                .reduce("", (s, s2) -> s + "\t" + rightPadding(s2, truncation));
    }

    private String ratingsToPercentage(String ratings, int count) {
        return Arrays
                .stream(ratings.split(","))
                .peek(s -> {
                    if (parseInt(s) > count) throw new IllegalArgumentException();
                })
                .map(s -> percentageOf(s, count))
                .collect(Collectors.joining(","));

    }

    private String percentageOf(String rating, int count) {
        if (rating.isBlank()) return rating;

        int score = parseInt(rating);

        if (score == 0) return rating;

        double percentage = ((double)score/count)*100;
        return rating + " (" + format("%.2f", percentage) + "%)";
    }

    private String rightPadding(String string, int padding) {
        return string.length() < padding ?
                format("%1$-" + padding + "s", string) : string;
    }

    private String truncateOrPadding(String string) {
        if (string.length() > truncation) return truncate(string);
        else return rightPadding(string, truncation);
    }

    private String truncate(String question) {
        return question.length() > truncation ? question.substring(0, truncation) : question;
    }
}
