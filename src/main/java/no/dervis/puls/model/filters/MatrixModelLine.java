package no.dervis.puls.model.filters;

import java.util.List;

public class MatrixModelLine {

    private final int columnFilterCount;

    private final String question;

    private final List<String> ratingsList;

    public MatrixModelLine(int columnFilterCount, String question, List<String> ratingsList) {
        this.columnFilterCount = columnFilterCount;
        this.question = question;
        this.ratingsList = ratingsList;
    }

    public int getColumnFilterCount() {
        return columnFilterCount;
    }

    public String getColumnFilterCountAsString() {
        return columnFilterCount + "";
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getRatingsList() {
        return ratingsList;
    }
}
