package pe.edu.lamolina.pivot.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
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
    
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpSession session) {
        DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
        
        ds = new DataSession();
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo();
        ds.setCicloAcademico(cicloAcademico);
        ds.setPersona(new Persona(1));
        ds.setUsuario(new Usuario(1));
        ds.setDocente(new Docente(1849));
        ds.setDepartamentoAcademico(new DepartamentoAcademico(1));
        session.setAttribute(Constantine.SESSION_USUARIO, ds);
        
        return "rock'n roll bastard allright!";
    }
    
}
