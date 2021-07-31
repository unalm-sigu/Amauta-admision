package pe.edu.lamolina.amauta.zelper.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import java.io.IOException;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

/**
 *
 * @author Albatross
 */
public class PdfImageProvider extends AbstractImageProvider {

    public final static String PATH = "/public/pdf/img/";
    public final static String ONLY_CHART_ENCUESTA = "only_chart_encuesta_";

    @Override
    public Image retrieve(String src) {
        try {
            
            if (src.contains(ONLY_CHART_ENCUESTA)) {
                return Image.getInstance(GlobalConstantine.TMP_DIR + src);
            }

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
