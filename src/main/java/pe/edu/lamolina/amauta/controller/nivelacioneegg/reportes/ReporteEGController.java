package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpSession;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.*;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("nivelacioneegg/reporte")
public class ReporteEGController {

    private final ReporteEGService service;
    private final ExcelResultadosNotasSeccion excelResultadosNotasSeccion;
    private final ExcelNotasPorSeccion excelNotasPorSeccion;
    private final ExcelAsistenciasPorSeccion excelAsistenciasPorSeccion;
    private final ExcelResultadosIngresantesDesaprobados excelResultadosIngresantesDesaprobados;
    private final ExcelResultadosIngresantesDesaprobadosMoodle excelResultadosIngresantesDesaprobadosMoodle;
    private final ExcelReporteGeneralNivelacion excelReporteGeneralNivelacion;
    private final ExcelResultadosPuntajeAdmision excelResultadosPuntajeAdmision;
    private final ExcelResultadosIngresantesGeneral  excelResultadosIngresantesGeneral;


    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        model.addAttribute("ciclo", ciclo);

        return "nivelacioneegg/reporte/reporte";
    }


    @RequestMapping("generalNotaSeccion")
    public ModelAndView generalNotaSeccion(
            HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.allNotasGeneralByCiclo(ds.getCicloAcademico());

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelResultadosNotasSeccion);
    }

    @RequestMapping("notaSeccion/{codSeccion}")
    public ModelAndView notaSeccion(@PathVariable("codSeccion") String codSeccion,
                                    HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.allNotasBySeccionAndCiclo(ds.getCicloAcademico(), codSeccion);

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelNotasPorSeccion);
    }

    @RequestMapping("asistenciaSeccion/{codSeccion}")
    public ModelAndView asistenciaSeccion(@PathVariable("codSeccion") String codSeccion,
                                          HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.allAsistenciaBySeccionAndCiclo(ds.getCicloAcademico(), codSeccion);

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelAsistenciasPorSeccion);
    }

    @RequestMapping("ingresantesDesaprobados")
    public ModelAndView ingresantesDesaprobados(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.allIngresantesDesaprobadosByCiclo(ds.getCicloAcademico());

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelResultadosIngresantesDesaprobados);
    }

    @RequestMapping("ingresantesDesaprobadosMoodle")
    public ModelAndView ingresantesDesaprobadosMoodle(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.ingresantesDesaprobadosMoodleByCiclo(ds.getCicloAcademico());

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelResultadosIngresantesDesaprobadosMoodle);
    }

    @RequestMapping("informeNivelacionGeneral")
    public ModelAndView informeNivelacionGeneral(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/informeNivelacionGeneral.xlsx");

        ResultadoReporteView resultado = service.allDataProcesada(ds.getCicloAcademico());

        model.addAttribute("formato", formato);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("resultado", resultado);

        return new ModelAndView(excelReporteGeneralNivelacion);
    }

    @RequestMapping("informacionAdmision")
    public ModelAndView informacionAdmision(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.resultadoAdmisionByCiclo(ds.getCicloAcademico());

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelResultadosPuntajeAdmision);
    }

    @RequestMapping("ingresantesGeneral")
    public ModelAndView ingresantesGeneral(HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoReporteView> resultados = service.ingresantesGeneraByCiclol(ds.getCicloAcademico());

        model.addAttribute("resultado", resultados);

        return new ModelAndView(excelResultadosIngresantesGeneral);
    }


}
