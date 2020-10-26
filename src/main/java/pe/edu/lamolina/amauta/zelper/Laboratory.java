package pe.edu.lamolina.amauta.zelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class Laboratory {

    public static void main666(String[] args) throws FileNotFoundException, InvalidFormatException, IOException {

        XWPFDocument doc = new XWPFDocument(OPCPackage.open(new FileInputStream("C:\\tmp\\CORRELATIVO.docx")));
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        for (XWPFParagraph para : paragraphList) {

            for (XWPFRun run : para.getRuns()) {
                String text = run.text();
                text = text.replace("@(CORRELATIVO-DOCUMENTO)", "HOLA");
                run.setText(text, 0);
            }
        }
        doc.write(new FileOutputStream("C:\\tmp\\coo.docx"));
    }

}
