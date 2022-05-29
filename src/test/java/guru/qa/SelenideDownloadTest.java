package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SelenideDownloadTest {
    public static String csvName = "workers.csv";
    public static String xlsName = "sample-xlsx-file.xls";
    public static String pdfName = "junit-user-guide-5.8.2.pdf";

    ClassLoader cl = SelenideDownloadTest.class.getClassLoader();

    @Test
    void downloadTest () throws Exception {
        Selenide.open ("https://github.com/junit-team/junit5/blob/main/README.md");
        File textFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(textFile)){
            byte[] fileContent = is.readAllBytes();
            String strContent = new String(fileContent, StandardCharsets.UTF_8);
            org.assertj.core.api.Assertions.assertThat(strContent).contains("JUnit 5");
        }
    }

        @Test
        void pdfParsingTest() throws Exception{
            InputStream stream = cl.getResourceAsStream("pdf/junit-user-guide-5.8.2.pdf");
            assert stream != null;
            PDF pdf = new PDF(stream);
            assertEquals(166,pdf.numberOfPages);
            MatcherAssert.assertThat(pdf, new ContainsExactText("Overview"));

    }
    @Test
    void xlsParsingTest() throws Exception{
        InputStream stream = cl.getResourceAsStream("xls/sample-xlsx-file.xlsx");
        assert stream != null;
        XLS xls = new XLS (stream);
        String stringCellValue = xls.excel.getSheetAt(0).getRow(3).getCell(1).getStringCellValue();
        org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Philip");
    }

    @Test
    void csvParsingTest() throws Exception{
        try ( InputStream stream = cl.getResourceAsStream("csv/workers.csv")) {
            assert stream != null;
            try (CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))){

                List<String[]> content = reader.readAll();
                org.assertj.core.api.Assertions.assertThat(content).contains(
                        new String[]{"Name", "Surname"},
                        new String[]{"Marina", "Sidorova"},
                        new String[]{"Helena", "Ivanova"}
                );
            }
        }

    }
    @Test
    void zipParsingTest () throws Exception {
        ZipFile zf = new ZipFile(new File("resources/zip/zip.zip"));
        try (InputStream is = cl.getResourceAsStream("zip/zip.zip")) {
            assert is != null;
            try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(pdfName)) {
                    try (InputStream stream = zf.getInputStream(entry)) {
                        assert stream != null;
                        PDF pdf = new PDF(stream);
                        assertEquals(166, pdf.numberOfPages);
                        MatcherAssert.assertThat(pdf, new ContainsExactText("Overview"));

                    }
                }
                if (entry.getName().equals(csvName)) {
                    try (InputStream stream = zf.getInputStream(entry)) {
                        assert stream != null;
                        try (CSVReader csvReader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                            List<String[]> content = csvReader.readAll();
                            org.assertj.core.api.Assertions.assertThat(content).contains(
                                    new String[]{"Name", "Surname"},
                                    new String[]{"Marina", "Sidorova"},
                                    new String[]{"Helena", "Ivanova"});
                        }
                    }
                }
                if (entry.getName().equals(xlsName)) {
                    try (InputStream stream = zf.getInputStream(entry)) {
                        assert stream != null;
                        XLS xls = new XLS(stream);
                        String stringCellValue = xls.excel.getSheetAt(0).getRow(3).getCell(1).getStringCellValue();
                        org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Philip");
                    }
                }
            }
        }
        }
}
}










