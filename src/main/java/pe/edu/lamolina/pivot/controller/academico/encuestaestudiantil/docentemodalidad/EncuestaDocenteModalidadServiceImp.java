package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docentemodalidad;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.model.enums.DocumentoPdfEnum;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteModalidadServiceImp implements EncuestaDocenteModalidadService {

    @Autowired
    EncuestaDocenteModalidadDAO encuestaDocenteModalidadDAO;

    @Autowired
    PuntajeEncuestaDocenteModalidadDAO puntajeEncuestaDocenteModalidadDAO;

    @Autowired
    PdfGenerator pdfGenerator;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo) {
        return encuestaDocenteModalidadDAO.allByDynatableCicloAcademico(filter, ciclo);
    }

    @Override
    public String reporteTodos() {
        System.out.println("PASA1");
        Context ctx = new Context();
        System.out.println("PASA2");

        PdfContent pdfContent = new PdfContent();
        System.out.println("PASA3");
        pdfContent.setContext(ctx);
        System.out.println("PASA4");
        pdfContent.setDocumentPdfEnum(DocumentoPdfEnum.RESULTADO_ENCUESTA);
        
        String filePdf = pdfGenerator.generateDocument(pdfContent, "tmp");
        
        System.out.println(filePdf);
        return filePdf;
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        return puntajeEncuestaDocenteModalidadDAO.allByEncuestaDocenteModalidad(encuestaDocenteModalidad);
    }

}
