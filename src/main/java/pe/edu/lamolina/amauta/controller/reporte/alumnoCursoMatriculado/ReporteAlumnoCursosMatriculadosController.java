package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Controller
@RequestMapping("reporte")
public class ReporteAlumnoCursosMatriculadosController {

    @Autowired
    ReporteAlumnoCursosMatService matService;

    @Autowired
    AlumnosMatriculadosPDF alumnoMatriculadosPDF;

    @RequestMapping(value = "cursos/matriculados/{seccion}")
    public ModelAndView AlumnoCursosMatriculados(@PathVariable(value = "seccion") String seccion, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("matriculaSeccion", matService.downloadReporte(seccion, ds));
        return new ModelAndView(alumnoMatriculadosPDF);
    }

}
