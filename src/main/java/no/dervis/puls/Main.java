package no.dervis.puls;

import no.dervis.puls.excel.PulsImporter;
import no.dervis.puls.report.MatrixConsolePrinter;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        var surveyMap = new PulsImporter()
                .importExcel(Paths.get("data","data.xlsx")
                        .toAbsolutePath().toString());

        printImportResult(surveyMap);

        MatrixConsolePrinter console = new MatrixConsolePrinter(surveyMap.get("Data"));
        console.pretty(true).printDefault();
    }

    private static void printImportResult(java.util.Map<String, no.dervis.puls.model.survey.PulseSurvey> surveyMap) {
        System.out.printf(
                "Imported %s sheet(s)%n%n", surveyMap.keySet().size()
        );

        surveyMap.forEach((sheetName, pulseSurvey) -> {
            System.out.printf("Sheet: %s %nRows: %s %nColumns: %s %n%n",
                    sheetName,
                    pulseSurvey.getRespondents().size(),
                    pulseSurvey.getQuestions().size());
        });
    }
}
