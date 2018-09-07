package pe.edu.lamolina.pivot.controller.academico.profesor.informacionprofesor;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.DedicacionDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.rrhh.CategoriaDocenteDAO;
import pe.edu.lamolina.pivot.dao.rrhh.DedicacionDocenteDAO;
import pe.edu.lamolina.pivot.dao.rrhh.SituacionDocenteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;

@Service
@Transactional(readOnly = true)
public class InformacionProfesorServiceImp implements InformacionProfesorService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    PaisDAO paisDAO;
    
    @Autowired
    SituacionDocenteDAO situacionDocenteDAO;
    
    @Autowired
    CategoriaDocenteDAO categoriaDocenteDAO;
    
    @Autowired
    DedicacionDocenteDAO dedicacionDocenteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Docente findDocente(Docente docente) {
        return docenteDAO.findByDocente(docente);
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudio(Compania compania) {
        return modalidadEstudioDAO.allActivoByCompania(compania);
    }

    @Override
    public String validarEmailByPersona(String email, Persona persona) {
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmail(email);

        } else {
            persona.setEmail(email);
            personas = personaDAO.allByEmailWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public String validarEmailEmpresaByPersona(String email, Persona persona) {
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmailEmpresa(email);

        } else {
            persona.setEmailCompania(email);
            personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public List<SituacionDocente> allSituaciones() {
        return situacionDocenteDAO.all();
    }

    @Override
    public List<CategoriaDocente> allCategorias() {
        return categoriaDocenteDAO.all();
    }

    @Override
    public List<DedicacionDocente> allDedicaciones() {
        return dedicacionDocenteDAO.all();
    }

}
