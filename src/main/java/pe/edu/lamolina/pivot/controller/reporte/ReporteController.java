package pe.edu.lamolina.pivot.controller.reporte;

import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.controller.programacionhorarios.boletinacademico.BoletinAcademicoExcelView;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.BoletinPDF;
import pe.edu.lamolina.pivot.controller.reporte.view.HorarioAlumnoCicloPDF;
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

    @Autowired
    BoletinPDF boletinPDF;

    @Autowired
    HorarioAlumnoCicloPDF horarioAlumnoCicloPDF;

    @Autowired
    ReporteService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("programacionHorariosQQ")
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

    @RequestMapping("programacionHorarios")
    public ModelAndView generatorpdf(Model model, HttpSession session, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        CicloAcademico ciclo = ds.getCicloAcademico();

        return new ModelAndView(boletinPDF);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/reporteboletinexcel")
    public ModelAndView reporteboletin(Model model, HttpSession session) {
//        RolExamenes rol = service.find(id);
//        service.infoReporteAulas(model, rol);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        return new ModelAndView(boletinAcademicoExcelView);
    }

    @RequestMapping("programacionHorarioAlumno")
    public ModelAndView programacionHorarioAlumno(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        CicloAcademico ciclo = ds.getCicloAcademico();

        List<AlumnoHorario> alumnosHorario = service.allAlumnoHorario(ciclo);

        List<Oficina> oficinas = service.allOficinaConsejero();

        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("instanciaOficina", oficinas);

        Map<String, List<Hora>> mapHorasConHorarios = new HashMap();

        for (AlumnoHorario alumnoHorario : alumnosHorario) {

//            logger.debug(" alumnoHorario   {}", alumnoHorario.getId());
            List<Hora> horasConHorarios = service.allHorario(alumnoHorario);
            if (mapHorasConHorarios.get(alumnoHorario.getId() + "") != null) {
                logger.debug("\nYA EXISTE");
            }

            for (Hora h : horasConHorarios) {
                if (!h.getDias().get(0).getHorarioSeccion().isEmpty()) {

                    String jj = h.getDias().get(0).getHorarioSeccion().get(0).getSeccion().getGrupoSeccion().getCurso().getCodigo();
                    System.out.println("TEXTO = " + jj);
                }
//                for (Dia dia : h.getDias()) {
//                    System.out.println("X " + dia.get);
//                }
//                System.out.println("TEST " + h.getdi);
            }
            System.out.println("+++++++++++++++++++++");
            mapHorasConHorarios.put(alumnoHorario.getId() + "", horasConHorarios);

        }

//        List<Hora> horasMap = new ArrayList(mapHorasConHorarios.values());
        List<Hora> horas = service.allHorasEscuela();
        List<Dia> dias = service.allDiaForPrinter();

        model.addAttribute("cicloAcademico", ciclo);
        model.addAttribute("mapHorasConHorarios", mapHorasConHorarios);
        model.addAttribute("horas", horas);
        model.addAttribute("dias", dias);
        model.addAttribute("alumnosHorario", alumnosHorario);
        model.addAttribute("mapOficinas", mapOficinas);
//        model.addAttribute("horasMap", horasMap);

        return new ModelAndView(horarioAlumnoCicloPDF);

    }

}
