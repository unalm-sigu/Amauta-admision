package pe.edu.lamolina.amauta.controller.reporte;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.BoletinPDF;
import pe.edu.lamolina.amauta.controller.reporte.view.HorarioAlumnoCicloPDF;
import pe.edu.lamolina.amauta.zelper.pdf.PdfService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("reporte")
public class ReporteController {

    @Autowired
    PdfService pdfService;

    @Autowired
    BoletinPDF boletinPDF;

    @Autowired
    HorarioAlumnoCicloPDF horarioAlumnoCicloPDF;

    @Autowired
    ReporteService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @RequestMapping("programacionHorariosQQ")
//    public void programacionHorarios(HttpServletResponse response,
//            Model model,
//            HttpSession session) throws IOException {
//
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//
//        CicloAcademico ciclo = ds.getCicloAcademico();
//        List<String> lstPdfFiles = pdfService.reporteProgramacion(ciclo);
//        String nom = "programacionHorarios";
//        String fileNameRoot = pdfService.concatPDFs(lstPdfFiles, nom, false);
//
//        if (!fileNameRoot.isEmpty()) {
//            File filex = new File(fileNameRoot);
//            if (!filex.exists()) {
//                response.sendError(HttpServletResponse.SC_NOT_FOUND);
//                return;
//            }
//
//            response.reset();
//            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "inline; filename=\"" + nom + ".pdf\"");
//
//            BufferedInputStream input = null;
//            BufferedOutputStream output = null;
//
//            try {
//                input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
//                output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
//                IOUtils.copy(input, output);
//                response.flushBuffer();
//
//            } finally {
//
//                close(output);
//                close(input);
//
//            }
//        }
//    }

    @RequestMapping("programacionHorarios")
    public ModelAndView generatorpdf(Model model, HttpSession session, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        CicloAcademico ciclo = ds.getCicloAcademico();

        return new ModelAndView(boletinPDF);

    }

    @RequestMapping("programacionHorarioAlumno")
    public ModelAndView programacionHorarioAlumno(Model model, HttpSession session, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("cicloAcademico", ciclo);
        model.addAttribute("alumnosHorario", service.allAlumnoHorario(ciclo));
        model.addAttribute("horariosCachimbo", service.allHorariosCachimbo(ciclo));
        model.addAttribute("consejeros", service.allOficinaByConsejero());
        return new ModelAndView(horarioAlumnoCicloPDF);
    }

}
