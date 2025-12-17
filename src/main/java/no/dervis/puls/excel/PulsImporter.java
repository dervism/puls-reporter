package no.dervis.puls.excel;

import no.dervis.puls.model.survey.*;
import no.dervis.puls.model.survey.Responses.PulseGenericResponse;
import no.dervis.puls.model.survey.Responses.PulseRatedResponse;
import no.dervis.puls.model.survey.Responses.PulseTextResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Imports any Excel workbook. The first row is
 * assumed to be the headers.
 */

public class PulsImporter {

    public Map<String, PulseSurvey> importExcel(String filename) {
        try {
            FileInputStream file = new FileInputStream(filename);
            Workbook workbook = new XSSFWorkbook(file);

            Map<String, PulseSurvey> surveyMap = new LinkedHashMap<>();

            final AtomicReference<Sheet> s = new AtomicReference<>();
            final AtomicReference<Row> r = new AtomicReference<>();
            final AtomicReference<Cell> c = new AtomicReference<>();

            for (Sheet sheet : workbook) {
                s.set(sheet);

                PulseSurvey pulseSurvey = new PulseSurvey();
                Iterator<Row> rowIterator = sheet.rowIterator();

                List<Question> questions = getHeaders(workbook, rowIterator);

                pulseSurvey.setQuestions(questions);
                List<Respondent> respondents = new LinkedList<>();

                try {
                    rowIterator.forEachRemaining(row -> {
                        r.set(row);
                        var questionResponseMap = new LinkedHashMap<Question, Response<?>>();

                        row.cellIterator().forEachRemaining(cell -> {
                            c.set(cell);
                            switch (cell.getCellType()) {
                                case STRING -> questionResponseMap.put(questions.get(cell.getColumnIndex()),
                                        new PulseTextResponse(cell.getStringCellValue()));
                                case NUMERIC -> questionResponseMap.put(questions.get(cell.getColumnIndex()),
                                        DateUtil.isCellDateFormatted(cell) ?
                                        new PulseGenericResponse<>(cell.getLocalDateTimeCellValue()) :
                                        new PulseRatedResponse((int)cell.getNumericCellValue())
                                );
                                case BLANK  -> questionResponseMap.put(questions.get(cell.getColumnIndex()),
                                        new PulseTextResponse(""));
                                default -> System.out.println("Uknown value type for cell " + cell.getAddress());
                            }
                        });

                        respondents.add(new Respondent(questionResponseMap));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Sheet: " + s.get().getSheetName());
                    System.err.println("Row: " + r.get().getRowNum());
                    System.err.println("Cell: " + c.get().getAddress());
                }

                pulseSurvey.setRespondents(respondents);
                surveyMap.put(sheet.getSheetName(), pulseSurvey);
            }

            return surveyMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Question> getHeaders(Workbook workbook, Iterator<Row> rowIterator) {
        DataFormatter formatter = new DataFormatter(Locale.ROOT);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Row firstRow = rowIterator.next();
        List<Question> questions = new LinkedList<>();

        for (Cell cell : firstRow) {
            String header = formatter.formatCellValue(cell, evaluator).trim();
            questions.add(new PulseTextQuestion(header));
        }
        return questions;
    }

}
