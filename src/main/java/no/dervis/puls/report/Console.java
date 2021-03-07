package no.dervis.puls.report;

import no.dervis.puls.model.survey.Pair;
import no.dervis.puls.model.survey.PulseSurvey;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.lineSeparator;
import static no.dervis.puls.model.filters.Matrix.matrix;

public class Console {

    private static final int DEFAULT_TRUNCATION_LENGTH = 20;
    private static final int DEFAULT_SKIP_HEADERS = 4;

    private final PulseSurvey pulse;

    private int truncation;

    private int skipHeaders;

    private boolean prettyPrint;

    public Console(PulseSurvey pulse) {
        this(pulse, DEFAULT_TRUNCATION_LENGTH, DEFAULT_SKIP_HEADERS, false);
    }

    public Console(PulseSurvey pulse, int truncation) {
        this(pulse, truncation, DEFAULT_SKIP_HEADERS, false);
    }

    public Console(PulseSurvey pulse, int truncation, int skipHeaders, boolean prettyPrint) {
        this.pulse = pulse;
        this.truncation = truncation;
        this.skipHeaders = skipHeaders;
        this.prettyPrint = prettyPrint;
    }

    public Console pretty(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    public void printMatrix() {
        printMatrix(
                List.of(
                        Pair.of(7, 7), // those who rated high, who also rated others high
                        Pair.of(3, 3), // those who rated low, who also rated others low
                        Pair.of(7, 3), // those who rated high, who also rated others low
                        Pair.of(3, 7), // those who rated low, who also rated others high
                        Pair.of(2, 2)  // those who rated very low, who also rated others very low
                ),
                List.of(
                        "Those who rated high (6 or 7), who also rated others high",
                        "Those who rated low (3 or less), who also rated others low",
                        "Those who rated high (6 or 7), who also rated others low",
                        "Those who rated low (3 or less), who also rated others high",
                        "Those who rated very low (2 or less), who also rated others very low"
                )
        );
    }

    public void printMatrix(List<Pair<Integer, Integer>> pairs) {
        printMatrix(pairs, List.of());
    }

    public void printMatrix(List<Pair<Integer, Integer>> pairs, List<String> descriptions) {
        final StringBuilder sb = new StringBuilder();

        if (!descriptions.isEmpty() && pairs.size() != descriptions.size())
            throw new IllegalArgumentException("Pairs and Descriptions have different length.");

        LinkedList<String> list = new LinkedList<>(descriptions);

        pairs.forEach(pair -> {
            if (!list.isEmpty()) {
                sb.append(list.removeFirst());
                sb.append(lineSeparator());
            }

            if (prettyPrint) sb.append(rightPadding(" \t", truncation));
            else sb.append("\t");

            // headers
            pulse.getQuestions().stream()
                    .skip(skipHeaders)
                    .forEach(question -> sb.append(truncate(question.question())).append("\t"));

            sb.append(lineSeparator());

            extracted(sb, matrix(pulse, pair.left(), pair.right()));
        });

        System.out.println(sb.toString());
    }

    private void extracted(StringBuilder sb, String matrix) {

        final String[] lines = matrix.split(lineSeparator());

        Arrays.stream(lines).forEach(line ->  {
            final String[] split = line.split("#");
            final String columnHeader = truncate(split[0]);

            sb.append(columnHeader)
                    .append(ratingsToTabs(split[1]))
                    .append(lineSeparator());
        });

        sb.append(lineSeparator());
        sb.append(lineSeparator());
    }

    private String ratingsToTabs(String ratings) {
        return Arrays
                .stream(ratings.split(","))
                .reduce("", (s, s2) -> s + "\t" + rightPadding(s2, truncation));
    }

    private String rightPadding(String string, int padding) {
        return string.length() < padding ?
                String.format("%1$-" + padding + "s", string) : string;
    }

    private String truncate(String question) {
        return question.length() > truncation ? question.substring(0, truncation) : question;
    }

    public void setTruncation(int truncation) {
        this.truncation = truncation;
    }

    public void setSkipHeaders(int skipHeaders) {
        this.skipHeaders = skipHeaders;
    }
}
