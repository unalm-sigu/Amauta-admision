package pe.edu.lamolina.amauta.controller.rolexamen.reportes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("rolexamen/reporte")
public class RolExamenReporteController {

    @Autowired
    RolExamenReporteService service;

    @Autowired
    RolExamenReporteView rolExamenReporteView;

    @Autowired
    RolExamenReporteAulasView rolExamenReporteAulasView;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/examenes")
    public ModelAndView examenes(Model model, HttpSession session, @PathVariable Long id) {
        RolExamenes rol = service.find(id);
        model.addAttribute("masivos", service.allCursoMasivoExamenByRolExamenes(rol));
        model.addAttribute("regulares", service.allLetrasGrupoRegularByRolExamenes(rol));
        model.addAttribute("especiales", service.allSeccionGrupoEspecialByRolExamenes(rol));

        return new ModelAndView(rolExamenReporteView);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/aulas")
    public ModelAndView aulas(Model model, HttpSession session, @PathVariable Long id) {
        RolExamenes rol = service.find(id);
        service.infoReporteAulas(model, rol);

        return new ModelAndView(rolExamenReporteAulasView);
    }

}
