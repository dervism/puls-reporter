package no.dervis.puls.cli;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ApplicationParams {

    File inputFile;

    List<String> columns;

    char columnFrom;
    char columnTo;

    /**
     * Takes in a range of chars from and to, and returns
     * the list of strings containing the letters.
     *
     * @param from The starting column
     * @param to The end column (inclusive)
     * @return A list of letters representing the columns
     */
    public static List<String> calculateColumnList(char from, char to) {
        return IntStream
                .rangeClosed(from, to)
                .mapToObj(value -> Character.toString(value).toUpperCase())
                .collect(Collectors.toList());
    }

}
