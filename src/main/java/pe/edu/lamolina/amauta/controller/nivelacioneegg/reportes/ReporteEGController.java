package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoReporteView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.ExcelAsistenciasPorSeccion;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.ExcelNotasPorSeccion;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.ExcelResultadosNotasSeccion;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
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

}
