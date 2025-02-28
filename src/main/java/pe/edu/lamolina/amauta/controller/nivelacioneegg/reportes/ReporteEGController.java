package pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes;

import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoNotaSeccion;
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

    @RequestMapping("reporteNotaSeccion")
    public ModelAndView reporteRecargasComedor(
            HttpSession session, Model model) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<ResultadoNotaSeccion> resultado = service.allResultadoNotaSeccionByCiclo(ds.getCicloAcademico());

        model.addAttribute("resultado", resultado);

        return new ModelAndView(excelResultadosNotasSeccion);
    }

}
