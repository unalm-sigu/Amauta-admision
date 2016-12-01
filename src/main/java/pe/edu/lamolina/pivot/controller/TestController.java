package pe.edu.lamolina.pivot.controller;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    UsuarioDAO usuarioDAO;

    //@ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpSession session) {

        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);

        Usuario user = usuarioDAO.findByEmail(ds.getEmail());

        ds = new DataSession();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo();
        ds.setCicloAcademico(cicloAcademico);
        ds.setPersona(user.getPersona());
        ds.setUsuario(user);
        Docente docente = docenteDAO.findPersona(user.getPersona());
        logger.debug("elnombre completo del docente {}", docente.getPersona().getNombreCompleto());
        ds.setDocente(docente);
        ds.setDepartamentoAcademico(docente.getDepartamentoAcademico());
        session.setAttribute(Constantine.SESSION_USUARIO, ds);

        return "redirect:/academico/systemcalifica/sistema";

    }

}
