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
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        ds = new DataSession();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo();
        ds.setCicloAcademico(cicloAcademico);
        ds.setPersona(new Persona(1));
        ds.setUsuario(new Usuario(1));
        Docente docente = docenteDAO.find(1849L);
        logger.debug("elnombre completo del docente {}", docente.getPersona().getNombreCompleto());
        ds.setDocente(docente);
        ds.setDepartamentoAcademico(new DepartamentoAcademico(1));
        session.setAttribute(Constantine.SESSION_USUARIO, ds);
        
        return "rock'n roll bastard allright!";
    public String index() {

        return "redirect:/academico/systemcalifica/sistema";
    }
    
}
