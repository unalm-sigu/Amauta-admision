package pe.edu.lamolina.amauta.zelper.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import java.io.IOException;

/**
 *
 * @author Albatross
 */
public class PdfImageProvider extends AbstractImageProvider {

    public final static String PATH = "/public/pdf/img/";

    @Override
    public Image retrieve(String src) {
        try {

            return Image.getInstance(this.getClass().getResource(PATH + src));
        } catch (BadElementException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String getImageRootPath() {
        return null;
    }
}
