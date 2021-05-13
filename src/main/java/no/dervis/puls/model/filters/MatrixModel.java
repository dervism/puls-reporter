package no.dervis.puls.model.filters;

import java.util.LinkedList;
import java.util.List;

public class MatrixModel {

    private final List<MatrixModelLine> modelLines;

    public MatrixModel() {
        this.modelLines = new LinkedList<>();
    }

    public void addModelLine(MatrixModelLine line) {
        modelLines.add(line);
    }

    public List<MatrixModelLine> getModelLines() {
        return modelLines;
    }
}
