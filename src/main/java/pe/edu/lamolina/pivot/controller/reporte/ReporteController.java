package pe.edu.lamolina.pivot.controller.reporte;

import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.controller.programacionhorarios.boletinacademico.BoletinAcademicoExcelView;
import pe.edu.lamolina.pivot.zelper.pdf.PdfService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Controller
@RequestMapping("reporte")
public class ReporteController {

    @Autowired
    PdfService pdfService;

    @Autowired
    BoletinAcademicoExcelView boletinAcademicoExcelView;

    @RequestMapping("programacionHorarios")
    public void programacionHorarios(HttpServletResponse response,
            Model model,
            HttpSession session) throws IOException {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        CicloAcademico ciclo = ds.getCicloAcademico();
        List<String> lstPdfFiles = pdfService.reporteProgramacion(ciclo);
        String nom = "programacionHorarios";
        String fileNameRoot = pdfService.concatPDFs(lstPdfFiles, nom, false);

        if (!fileNameRoot.isEmpty()) {
            File filex = new File(fileNameRoot);
            if (!filex.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.reset();
            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + nom + ".pdf\"");

            BufferedInputStream input = null;
            BufferedOutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
                IOUtils.copy(input, output);
                response.flushBuffer();

            } finally {

                close(output);
                close(input);

            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reporteboletinexcel")
    public ModelAndView reporteboletin(Model model, HttpSession session) {
//        RolExamenes rol = service.find(id);
//        service.infoReporteAulas(model, rol);

        return new ModelAndView(boletinAcademicoExcelView);
    }

}
