package pe.edu.lamolina.pivot.controller.rolexamen.reportes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("rolexamen/reporte")
public class RolExamenReporteController {

    @Autowired
    RolExamenReporteService service;

    @Autowired
    RolExamenReporteView rolExamenReporteView;

    @Autowired
    RolExamenReporteAulasView rolExamenReporteAulasView;
    
    @RequestMapping("examenes")
    public ModelAndView examenes(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        RolExamenes rol = service.findRolExamenesActivo(ds.getCicloAcademico());
        model.addAttribute("masivos", service.allCursoMasivoExamenByRolExamenes(rol));
        model.addAttribute("regulares", service.allLetrasGrupoRegularByRolExamenes(rol));
        model.addAttribute("especiales", service.allSeccionGrupoEspecialByRolExamenes(rol));

        return new ModelAndView(rolExamenReporteView);
    }

    @RequestMapping("aulas")
    public ModelAndView aulas(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        RolExamenes rol = service.findRolExamenesActivo(ds.getCicloAcademico());
        service.infoReporteAulas(model, rol);

        return new ModelAndView(rolExamenReporteAulasView);
    }

}
