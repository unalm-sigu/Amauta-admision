package pe.edu.lamolina.amauta.controller.reporte.alumnoCursoMatriculado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("reporte")
public class ReporteAlumnoCursosMatriculadosController {

    @Autowired
    ReporteAlumnoCursosMatService matService;

    @Autowired
    AlumnosMatriculadosPDF alumnoMatriculadosPDF;

    @RequestMapping(value = "cursos/matriculados/{seccion}")
    public ModelAndView AlumnoCursosMatriculados(@PathVariable(value = "seccion") String ciclo, Model model) {
        model.addAttribute("matriculaSeccion", matService.downloadReporte(ciclo));
        return new ModelAndView(alumnoMatriculadosPDF);
    }

}
