package pe.edu.lamolina.amauta.zelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;

public class Laboratory {

    public static void main666(String[] args) throws FileNotFoundException, InvalidFormatException, IOException {

        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("One column on top. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
        paragraph = document.createParagraph();
//paragraph with section setting for one column section above
        paragraph = document.createParagraph();
        CTSectPr ctSectPr = paragraph.getCTP().addNewPPr().addNewSectPr();
        CTColumns ctColumns = ctSectPr.addNewCols();
        ctColumns.setNum(BigInteger.valueOf(1));
//left column
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("The left side");
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        paragraph = document.createParagraph();
// right column
//paragraph with column break
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.addBreak(BreakType.COLUMN);
        run = paragraph.createRun();
        run.setText("The right side");
//left border for the paragrapphs on right side
        paragraph.setBorderLeft(Borders.THREE_D_EMBOSS);
        paragraph.getCTP().getPPr().getPBdr().getLeft().setSz(BigInteger.valueOf(20));
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        paragraph.setBorderLeft(Borders.THREE_D_EMBOSS);
        paragraph.getCTP().getPPr().getPBdr().getLeft().setSz(BigInteger.valueOf(20));
        paragraph = document.createParagraph();
        paragraph.setBorderLeft(Borders.THREE_D_EMBOSS);
        paragraph.getCTP().getPPr().getPBdr().getLeft().setSz(BigInteger.valueOf(20));
//paragraph with section break continuous for two column section above
        paragraph = document.createParagraph();
        ctSectPr = paragraph.getCTP().addNewPPr().addNewSectPr();
        ctSectPr.addNewType().setVal(STSectionMark.CONTINUOUS);
        ctColumns = ctSectPr.addNewCols();
        ctColumns.setNum(BigInteger.valueOf(2));
        ctColumns.setEqualWidth(STOnOff.OFF);
        ctColumns.setSep(STOnOff.ON);
        CTColumn ctColumn = ctColumns.addNewCol();
        ctColumn.setW(BigInteger.valueOf(5000));
        ctColumn.setSpace(BigInteger.valueOf(300));
        ctColumn = ctColumns.addNewCol();
        ctColumn.setW(BigInteger.valueOf(5000));
        paragraph.setBorderLeft(Borders.THREE_D_EMBOSS);
        paragraph.getCTP().getPPr().getPBdr().getLeft().setSz(BigInteger.valueOf(20));
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("One column on bottom");
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
//section setting continuous for one column section above
        CTDocument1 ctDocument = document.getDocument();
        CTBody ctBody = ctDocument.getBody();
        ctSectPr = ctBody.addNewSectPr();
        ctSectPr.addNewType().setVal(STSectionMark.CONTINUOUS);
        ctColumns = ctSectPr.addNewCols();
        ctColumns.setNum(BigInteger.valueOf(1));
        FileOutputStream out = new FileOutputStream("C:\\tmp\\cooa.docx");
        document.write(out);

        document.close();
    }

}
