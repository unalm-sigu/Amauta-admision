package pe.edu.lamolina.pivot.zelper.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import java.io.IOException;

/**
 *
 * @author Albatross
 */
class PdfImageProvider extends AbstractImageProvider {

    private String PATH="/document/img/";
    
    @Override
    public Image retrieve(String src) {
        try {
            this.getClass().getResourceAsStream(PATH+src);
            return Image.getInstance(this.getClass().getResource(PATH+src));
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
