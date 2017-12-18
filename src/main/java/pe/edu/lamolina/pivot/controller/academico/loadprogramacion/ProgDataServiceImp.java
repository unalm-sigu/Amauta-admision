package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.academico.SituacionAcademica;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.inscripcion.Postulante;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.UserEstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoMatriculaCursoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.RolEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ProgDataServiceImp implements ProgDataService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    AulaDAO aulaDAO;
    @Autowired
    GrupoHorasDAO grupoHorasDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    PersonaPerfilDAO personaPerfilDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static boolean revisar = true;

    private Integer random;

    private synchronized Integer getRandom() {
        if (random == null) {
            random = 0;
        }
        random++;
        return random;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String extraerEmailCompania(
            Persona perso,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        String email = null;
        List<Persona> personas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                main = persona;
                break;
            }
        }

        for (Persona persona : personas) {
            if (persona == main) {
                continue;
            }
            if (StringUtils.isEmpty(main.getEmailCompania()) && !StringUtils.isEmpty(persona.getEmailCompania())) {
                email = persona.getEmailCompania();
                persona.setEmailCompania(null);
                personaDAO.update(persona);
                break;
            }
        }

        return email;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Persona extraerDocumentoIdentidad(
            Persona perso,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        Persona dni = new Persona();
        List<Persona> personas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                main = persona;
                break;
            }
        }

        for (Persona persona : personas) {
            if (persona == main) {
                continue;
            }
            if (StringUtils.isEmpty(main.getNumeroDocIdentidad()) && !StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
                dni.setNumeroDocIdentidad(persona.getNumeroDocIdentidad());
                dni.setTipoDocumento(persona.getTipoDocumento());
                persona.setNumeroDocIdentidad(null);
                personaDAO.update(persona);
                break;
            }
        }

        return dni;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeDocumentoIdentidad(
            Persona perso,
            TipoDocIdentidad tipoDocumento,
            String numeroDocIdentidad,
            String emailCompania,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        List<Persona> personas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                main = persona;
                break;
            }
        }
        if (StringUtils.isEmpty(main.getEmailCompania()) && !StringUtils.isEmpty(emailCompania)) {
            main.setEmailCompania(emailCompania);
        }
        if (StringUtils.isEmpty(main.getNumeroDocIdentidad()) && !StringUtils.isEmpty(numeroDocIdentidad)) {
            main.setTipoDocumento(tipoDocumento);
            main.setNumeroDocIdentidad(numeroDocIdentidad);
        }
        personaDAO.update(main);

        for (Persona persona : personas) {
            Usuario user = usuarioDAO.findByPersona(persona);
            if (user == null) {
                continue;
            }
            if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                user.setPersona(main);
                usuarioDAO.update(user);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Persona savePersona(
            Persona persona,
            Map<String, TipoDocIdentidad> mapTiposDoc,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {

        TipoDocIdentidad tipoDoc = mapTiposDoc.get(persona.getCodigoTipoDocumento());
        if (tipoDoc == null) {
            persona.setCodigoTipoDocumento("DNI");
            tipoDoc = mapTiposDoc.get(persona.getCodigoTipoDocumento());
        }

        persona.setTipoDocumento(tipoDoc);
        logger.debug("buscando {} {} con tipoDoc {}", persona.getCodigoTipoDocumento(), persona.getNumeroDocIdentidad(), tipoDoc);
        if (tipoDoc != null && !StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
            Persona tempo = mapDNIPersonas.get(persona.getIdentificacion());
            if (tempo == null) {
                persona.setUserRegistro(ds.getUsuario());
                persona.setFechaRegistro(new Date());
                persona.setEstado(EstadoEnum.ACT.name());
                personaDAO.save(persona);

                mapDNIPersonas.put(persona.getIdentificacion(), persona);
            }

            return revisarPersona(persona, mapKeyPersonas, mapDNIPersonas, ds);
        }
        return revisarPersona(persona, mapKeyPersonas, mapDNIPersonas, ds);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAlumno(
            Alumno alumno,
            Map<Long, Persona> mapIdPersonas,
            Map<String, Alumno> mapAlumnos,
            Map<String, SituacionAcademica> mapSituaciones, DataSessionPivot ds) {
        Persona persona = mapIdPersonas.get(alumno.getPersona().getId());
        if (StringUtils.isEmpty(persona.getEmailCompania())) {
            persona.setEmailCompania(alumno.getEmail());
            personaDAO.update(persona);
        }

        Alumno alu = mapAlumnos.get(alumno.getCodigo());
        if (alu != null) {
            alu.setPersona(persona);
            alumnoDAO.update(alu);

        } else {
            String cod = StringUtils.isEmpty(alumno.getCodigoEspecialidad()) ? alumno.getCodigoPostgrado() : alumno.getCodigoEspecialidad();
            Carrera carrera = carreraDAO.findByCodigo(cod);
            alumno.setCarrera(carrera);
            SituacionAcademica situacion = mapSituaciones.get(alumno.getSituacion());
            alumno.setSituacionAcademica(situacion);
            alumno.setCicloActivo(ds.getCicloAcademico());
            alumno.setCicloIngreso(ds.getCicloAcademico());
            alumno.setRetirosCursos(0);
            alumno.setRetirosCiclos(0);
            alumno.setRetirosExtemporaneos(0);
            alumno.setCreditosCursados(0);
            alumno.setCreditosAprobados(0);
            alumno.setCreditosCarreraCursados(0);
            alumno.setCreditosCarreraAprobados(0);
            alumno.setCursosInscritos(0);
            alumno.setCursosAprobados(0);
            alumno.setCursosCarreraInscritos(0);
            alumno.setCursosCarreraAprobados(0);
            alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);
            alumno.setPromedioAcumulado(BigDecimal.ZERO);
            alumnoDAO.save(alumno);

            mapAlumnos.put(alumno.getCodigo(), alumno);

            saveUsuario(persona, RolEnum.ALU, ds);
        }

    }

    private void saveUsuario(Persona persona, RolEnum rol, DataSessionPivot ds) {
        Usuario user = usuarioDAO.allByPersona(persona);
        if (user != null) {

            boolean existeAlumno = false;
            boolean existeDocente = false;
            List<UsuarioRol> userRoles = usuarioRolDAO.allByUser(user);
            for (UsuarioRol userRol : userRoles) {
                if (userRol.getId() == 1 && rol == RolEnum.ALU) {
                    existeAlumno = true;
                    break;
                }
                if (userRol.getId() == 2 && rol == RolEnum.DOC) {
                    existeDocente = true;
                    break;
                }
            }
            if (!existeAlumno && rol == RolEnum.ALU) {
                UsuarioRol userRol = new UsuarioRol();
                userRol.setUsuario(user);
                userRol.setRol(new Rol(1));
                usuarioRolDAO.save(userRol);
            }
            if (!existeDocente && rol == RolEnum.DOC) {
                UsuarioRol userRol = new UsuarioRol();
                userRol.setUsuario(user);
                userRol.setRol(new Rol(2));
                usuarioRolDAO.save(userRol);
            }

            return;
        }

        if (StringUtils.isEmpty(persona.getEmailCompania())) {
            return;
        }

        user = new Usuario();
        user.setPersona(persona);
        user.setUsuario(persona.getEmailCompania().toLowerCase());
        user.setEstadoEnum(UserEstadoEnum.ACT);
        user.setFechaRegistro(new Date());
        user.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(user);

        user.setUserActivo(user);
        usuarioDAO.update(user);

        UsuarioRol userRol = new UsuarioRol();
        userRol.setUsuario(user);
        if (rol == RolEnum.ALU) {
            userRol.setRol(new Rol(1));
        }
        if (rol == RolEnum.DOC) {
            userRol.setRol(new Rol(2));
        }
        usuarioRolDAO.save(userRol);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Docente saveDocente(Docente docente, ModalidadEstudio modalidad, Map<String, DepartamentoAcademico> mapDptos, DataSessionPivot ds) {
        Persona persona = personaDAO.find(docente.getPersona().getId());

        Docente profeBD = docenteDAO.findByCode(docente.getCodigo());
        if (profeBD == null) {
            DepartamentoAcademico dpto = mapDptos.get(docente.getCodigoDepartamento());
            profeBD = new Docente();
            profeBD.setCodigo(docente.getCodigo());
            profeBD.setEstadoEnum(EstadoEnum.ACT);
            profeBD.setDepartamentoAcademico(dpto);
            profeBD.setModalidadEstudio(modalidad);
            profeBD.setPersona(persona);
            profeBD.setFechaRegistro(new Date());
            profeBD.setUserRegistro(ds.getUsuario());
            docenteDAO.save(profeBD);

        } else if (profeBD.getEstadoEnum() != EstadoEnum.ACT) {
            profeBD.setEstadoEnum(EstadoEnum.ACT);
            profeBD.setFechaModifica(new Date());
            profeBD.setUserModifica(ds.getUsuario());
            docenteDAO.update(profeBD);
        }
        saveUsuario(persona, RolEnum.DOC, ds);
        return profeBD;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void anularDocentes(Map<String, Docente> mapDocentes, ModalidadEstudio modalidad, DataSessionPivot ds) {
        List<Docente> docentesActivos = docenteDAO.allActivos(modalidad);
        for (Docente docente : docentesActivos) {
            Docente profe = mapDocentes.get(docente.getCodigo());
            if (profe == null) {
                docente.setEstadoEnum(EstadoEnum.INA);
                docente.setFechaModifica(new Date());
                docente.setUserModifica(ds.getUsuario());
                docenteDAO.update(docente);
            }
        }
    }

    private List<Persona> allPersonasByPer(
            Persona persona,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas,
            DataSessionPivot ds) {

        List<Persona> personas = mapKeyPersonas.get(persona.getKey());
        if (personas == null) {
            personas = new ArrayList();
        }
        if (!StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
            Persona per = mapDNIPersonas.get(persona.getIdentificacion());
            if (per != null) {
                boolean ok = false;
                for (Persona pp : personas) {
                    if (pp.getId() == per.getId().longValue()) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    personas.add(per);
                }
            }
        }

        if (personas.isEmpty()) {
            persona.setUserRegistro(ds.getUsuario());
            persona.setFechaRegistro(new Date());
            persona.setEstado(EstadoEnum.ACT.name());
            persona.setNumeroDocIdentidad(null);
            personaDAO.save(persona);

            personas.add(persona);
            List<Persona> personax = mapKeyPersonas.get(persona.getKey());
            if (personax == null) {
                personax = new ArrayList();
                mapKeyPersonas.put(persona.getKey(), personax);
            }
            personax.add(persona);
        }
        return personas;

    }

    @Override
    @Transactional
    public Persona revisarPersona(
            Persona persona,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {

        List<Persona> personas = allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
        logger.debug("existen {} duplicados parar {} {} {}", personas.size(), persona.getPaterno(), persona.getMaterno(), persona.getNombres());

        if (personas.isEmpty()) {
            Persona pp = new Persona(persona);
            pp.setUserRegistro(ds.getUsuario());
            pp.setFechaRegistro(new Date());
            pp.setEstado(EstadoEnum.ACT.name());
            logger.debug("finalizo revision de persona {}", pp.getApellidosNombres());
            return pp;
        }

        if (personas.size() == 1) {
            Persona pp = personas.get(0);
            pp.setEstado(EstadoEnum.ACT.name());
            personaDAO.update(pp);
            logger.debug("finalizo revision de persona {}", pp.getApellidosNombres());
            return personas.get(0);
        }

        Persona main = findPersonaMain(personas);
        datoToMain(personas, main, ds);
        changePersonasNoMain(personas, main, ds);

        for (Persona p : personas) {
            personaDAO.update(p);
        }

        logger.debug("finalizo revision de persona {}", main.getApellidosNombres());
        return main;
    }

    private void datoToMain(List<Persona> personas, Persona main, DataSessionPivot ds) {
        for (Persona persona : personas) {
            if (persona.getId() == main.getId().longValue()) {
                continue;
            }
            //logger.debug("pasando datos de {} hacia {}", persona.getId(), main.getId());
            copyInfo(main, persona);
        }

        for (Persona persona : personas) {
            if (persona.getId() == main.getId().longValue()) {
                //logger.debug("Activando persona {}", persona.getId());
                persona.setEstado(EstadoEnum.ACT.name());
                persona.setFechaTraslado(null);
                persona.setUserTraslado(null);
                persona.setPersonaTraslado(null);
                continue;
            }

            //logger.debug("desactivando persona {}", persona.getId());
            persona.setEstado(EstadoEnum.INA.name());
            persona.setFechaTraslado(new Date());
            persona.setUserTraslado(ds.getUsuario());
            persona.setPersonaTraslado(main);
        }
    }

    private void copyInfo(Persona main, Persona persona) {
        if (StringUtils.isEmpty(main.getSexo()) && !StringUtils.isEmpty(persona.getSexo())) {
            main.setSexo(persona.getSexo());
        }
        if (StringUtils.isEmpty(main.getEmail()) && !StringUtils.isEmpty(persona.getEmail())) {
            main.setEmail(persona.getEmail());
            persona.setEmail(null);
        }
        if (StringUtils.isEmpty(main.getCelular()) && !StringUtils.isEmpty(persona.getCelular())) {
            main.setCelular(persona.getCelular());
        }
        if (StringUtils.isEmpty(main.getTelefono()) && !StringUtils.isEmpty(persona.getTelefono())) {
            main.setTelefono(persona.getTelefono());
        }
        if (StringUtils.isEmpty(main.getDireccion()) && !StringUtils.isEmpty(persona.getDireccion())) {
            main.setDireccion(persona.getDireccion());
        }
        if (StringUtils.isEmpty(main.getTituloAcademico()) && !StringUtils.isEmpty(persona.getTituloAcademico())) {
            main.setTituloAcademico(persona.getTituloAcademico());
        }
        if (StringUtils.isEmpty(main.getFoto()) && !StringUtils.isEmpty(persona.getFoto())) {
            main.setFoto(persona.getFoto());
        }
        if (main.getFechaNacer() == null && persona.getFechaNacer() != null) {
            main.setFechaNacer(persona.getFechaNacer());
        }
        if (main.getFechaRegistro() == null && persona.getFechaRegistro() != null) {
            main.setFechaRegistro(persona.getFechaRegistro());
        }
        if (main.getFechaValidacion() == null && persona.getFechaValidacion() != null) {
            main.setFechaValidacion(persona.getFechaValidacion());
        }
        if (main.getUbicacionNacer() == null && persona.getUbicacionNacer() != null) {
            main.setUbicacionNacer(persona.getUbicacionNacer());
        }
        if (main.getUbicacionDomicilio() == null && persona.getUbicacionDomicilio() != null) {
            main.setUbicacionDomicilio(persona.getUbicacionDomicilio());
        }
        if (main.getPaisNacer() == null && persona.getPaisNacer() != null) {
            main.setPaisNacer(persona.getPaisNacer());
        }
        if (main.getNacionalidad() == null && persona.getNacionalidad() != null) {
            main.setNacionalidad(persona.getNacionalidad());
        }
        if (main.getUserValidacion() == null && persona.getUserValidacion() != null) {
            main.setUserValidacion(persona.getUserValidacion());
        }
        if (main.getUserRegistro() == null && persona.getUserRegistro() != null) {
            main.setUserRegistro(persona.getUserRegistro());
        }
    }

    private void changePersonasNoMain(List<Persona> personas, Persona main, DataSessionPivot ds) {

        //logger.debug("procedemos a cambiar el ID de las personas por el principal ", main.getId());
        for (Persona persona : personas) {
            if (persona.getId() == main.getId().longValue()) {
                continue;
            }
            //logger.debug("cambiando el ID de {} a {}", persona.getId(), main.getId());

            List<Postulante> postulantes = postulanteDAO.allByPersona(persona);
            //logger.debug("se hallo {} postulantes", postulantes.size());
            for (Postulante postulante : postulantes) {
                postulante.setPersona(main);
                postulanteDAO.update(postulante);
            }

            List<Alumno> alumnos = alumnoDAO.allByPersona(persona);
            //logger.debug("se hallo {} alumnos", alumnos.size());
            for (Alumno alumno : alumnos) {
                alumno.setPersona(main);
                alumnoDAO.update(alumno);
            }

            List<Docente> docentes = docenteDAO.allByPersona(persona);
            //logger.debug("se hallo {} docentes", docentes.size());
            for (Docente docente : docentes) {
                docente.setPersona(main);
                docenteDAO.update(docente);
            }

            List<PersonaPerfil> persoPerfiles = personaPerfilDAO.allByPersona(persona);
            //logger.debug("se hallo {} perfiles", persoPerfiles.size());
            for (PersonaPerfil pp : persoPerfiles) {
                pp.setPersona(main);
                personaPerfilDAO.update(pp);
            }

        }

        List<Usuario> usuarios = usuarioDAO.allByPersonas(personas);
        if (usuarios.isEmpty()) {
            return;
        }

        Usuario userMain = null;
        Usuario usuario = usuarioDAO.findByPersona(main);

        if (usuario == null) {
            for (Usuario user : usuarios) {
                if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                    userMain = user;
                    break;
                }
            }
            if (userMain == null) {
                userMain = usuarios.get(0);
            }

            userMain.setPersona(main);

            for (Usuario user : usuarios) {
                if (user == userMain) {
                    continue;
                }
                if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                    user.setEstadoEnum(UserEstadoEnum.INA);
                    user.setFechaModifica(new Date());
                    user.setUserModifica(ds.getUsuario());
                }
                user.setUserActivo(userMain);
                usuarioDAO.update(user);
            }
            return;
        }

        if (usuario.getEstadoEnum() == UserEstadoEnum.INA) {
            for (Usuario user : usuarios) {
                if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                    userMain = user;
                    break;
                }
            }
            if (userMain != null) {
                //userMain.setPersona(main);
                usuario.setPersona(null);
                usuarioDAO.update(usuario);
                //usuarioDAO.update(userMain);
            } else {
                userMain = usuario;
            }

            for (Usuario user : usuarios) {
                if (user == userMain) {
                    continue;
                }
                if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                    user.setEstadoEnum(UserEstadoEnum.INA);
                    user.setFechaModifica(new Date());
                    user.setUserModifica(ds.getUsuario());
                }
                user.setUserActivo(userMain);
                usuarioDAO.update(user);
            }
            return;
        }

        if (usuario.getEstadoEnum() == UserEstadoEnum.ACT) {
            for (Usuario user : usuarios) {
                if (user.getId() == usuario.getId().longValue()) {
                    continue;
                }
                if (user.getEstadoEnum() == UserEstadoEnum.ACT) {
                    user.setEstadoEnum(UserEstadoEnum.INA);
                    user.setFechaModifica(new Date());
                    user.setUserModifica(ds.getUsuario());
                }
                user.setUserActivo(usuario);
                usuarioDAO.update(user);
            }
        }

    }

    private Persona findPersonaMain(List<Persona> personas) {
        for (Persona persona : personas) {
            if (persona.getFechaValidacion() != null) {
                //logger.debug("se escoge por fecha de validacion");
                return persona;
            }
        }
        for (Persona persona : personas) {
            List<Postulante> postulantes = postulanteDAO.allByPersona(persona);
            if (!postulantes.isEmpty()) {
                //logger.debug("se escoge por postulantes");
                return persona;
            }
        }
        for (Persona persona : personas) {
            List<Docente> docentes = docenteDAO.allByPersona(persona);
            if (!docentes.isEmpty()) {
                //logger.debug("se escoge por docentes");
                return persona;
            }
        }
        for (Persona persona : personas) {
            List<PersonaPerfil> persoPerfiles = personaPerfilDAO.allByPersona(persona);
            if (!persoPerfiles.isEmpty()) {
                //logger.debug("se escoge por perfiles");
                return persona;
            }
        }
        for (Persona persona : personas) {
            List<Alumno> alumnos = alumnoDAO.allByPersona(persona);
            if (!alumnos.isEmpty()) {
                //logger.debug("se escoge por alumnos");
                return persona;
            }
        }
        for (Persona persona : personas) {
            Usuario user = usuarioDAO.findByPersona(persona);
            if (user != null && user.getEstadoEnum() == UserEstadoEnum.ACT) {
                //logger.debug("se escoge por fecha de user");
                return persona;
            }
        }
        for (Persona persona : personas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                //logger.debug("se escoge por persona activa");
                return persona;
            }
        }
        return personas.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo) {
        int loop = 0;
        Map<String, GrupoSeccion> mapGpoSecciones = new LinkedHashMap();
        for (GrupoSeccion gpoSecc : gruposSecciones) {

            logger.debug("\tprocesando el gpoSecc {}", gpoSecc.getCodigo());
            GrupoSeccion gpoSeccBD = grupoSeccionDAO.findByCodeCiclo(gpoSecc.getCodigo(), ciclo);
            Curso curso = cursoDAO.findByCode(gpoSecc.getCodigoCurso());
            logger.debug("\tbuscando curso {} resultado es {}", gpoSecc.getCodigoCurso(), curso);
            logger.debug("\ttiene {} creditos - {} creditosVariables", curso.getCreditos(), curso.getCreditosVariables());
            if (gpoSeccBD == null) {

                gpoSeccBD = new GrupoSeccion();
                gpoSeccBD.setCicloAcademico(ciclo);
                gpoSeccBD.setCodigo(gpoSecc.getCodigo());
                gpoSeccBD.setCurso(curso);
                gpoSeccBD.setVersion("1");
                gpoSeccBD.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
                gpoSeccBD.setEstadoGrupo(EstadoGrupoSeccionEnum.ABI.name());
                gpoSeccBD.setEstado(EstadoEnum.ACT.name());

                grupoSeccionDAO.save(gpoSeccBD);

            } else {
                gpoSeccBD.setVersion(gpoSeccBD.getVersion() == null ? "1" : gpoSeccBD.getVersion());
                gpoSeccBD.setEstadoPlanEnum(gpoSeccBD.getEstadoPlan() == null ? EstadoPlanCalificaEnum.PEND : gpoSeccBD.getEstadoPlanEnum());
                gpoSeccBD.setEstadoGrupo(gpoSeccBD.getEstadoGrupo() == null ? EstadoGrupoSeccionEnum.ABI.name() : gpoSeccBD.getEstadoGrupo());
                gpoSeccBD.setEstado(EstadoEnum.ACT.name());
                grupoSeccionDAO.update(gpoSeccBD);

                Curso cursoBD = gpoSeccBD.getCurso();
                if (curso.getId() != cursoBD.getId().longValue()) {
                    String msg = String.format("El curso del grupo-seccion %s está relacionado al curso %s pero en la base de datos es %s",
                            gpoSecc.getCodigo(), cursoBD.getCodigo(), curso.getCodigo());
                    throw new PhobosException(msg);
                }
            }

            gpoSeccBD.setSecciones(new ArrayList());
            gruposSecciones.set(loop, gpoSeccBD);
            mapGpoSecciones.put(gpoSeccBD.getCodigo(), gpoSeccBD);
            loop++;
        }

        return mapGpoSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones) {
        int loop = 0;
        Map<String, Seccion> mapSecciones = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            GrupoSeccion gpoSecc = mapGpoSecciones.get(seccion.getCodigoGrupoSeccion());
            if (gpoSecc == null) {
                String msg = String.format("La seccion %s no tiene su padre grupo-seccion %s",
                        seccion.getCodigo(), seccion.getCodigoGrupoSeccion());
                throw new PhobosException(msg);
            }

            Curso curso = gpoSecc.getCurso();
            Seccion seccionBD = seccionDAO.findByCodeCiclo(seccion.getCodigo(), ciclo);
            GrupoHoras gpoHoras = findGrupoHoras(seccion);
            Aula aula = findAula(seccion);

            if (seccionBD == null) {
                seccionBD = new Seccion();
                seccionBD.setCodigo(seccion.getCodigo());
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setGrupoSeccion(gpoSecc);
                seccionBD.setMatriculados(0);
                seccionBD.setRetirados(0);
                seccionBD.setVacantes(0);
                seccionBD.setEsPrincipal(0);
                seccionBD.setTipoSeccionEnum(TipoSeccionEnum.valueOf(seccion.getCodigoTipoSeccion()));
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);

                Integer horasTeoria = curso.getHorasTeoria() == null ? 0 : curso.getHorasTeoria();
                Integer horasPractica = curso.getHorasPractica() == null ? 0 : curso.getHorasPractica();
                seccionBD.setHorasTeoria(horasTeoria);
                seccionBD.setHorasPractica(horasPractica);
                seccionBD.setHorasSemanales(horasTeoria + horasPractica);
                seccionBD.setEstado(EstadoEnum.ACT.name());
                //seccionBD.setSeccionSuperior(seccionBD);

                seccionDAO.save(seccionBD);
            } else {
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setEstado(EstadoEnum.ACT.name());
                seccionDAO.update(seccionBD);
            }

            gpoSecc.getSecciones().add(seccionBD);
            seccionBD.setDocenteSeccion(new ArrayList());
            seccionBD.setMatriculaSeccion(new ArrayList());
            secciones.set(loop, seccionBD);
            mapSecciones.put(seccionBD.getCodigo(), seccionBD);
            loop++;
        }

        return mapSecciones;
    }

    private GrupoHoras findGrupoHoras(Seccion seccion) {
        String codigo = seccion.getCodigoGrupoHorario();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        GrupoHoras gpoHoras = grupoHorasDAO.findByCode(codigo);
        if (gpoHoras == null) {
            String msg = String.format("El grupo-horas %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return gpoHoras;
    }

    private Aula findAula(Seccion seccion) {
        String codigo = seccion.getCodigoAula();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        Aula aula = aulaDAO.findByCode(codigo);
        if (aula == null) {
            String msg = String.format("El aula %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return aula;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, DocenteSeccion> loadDataDocentesSecciones(
            List<DocenteSeccion> docentesSecciones,
            Map<String, Seccion> mapSecciones,
            Map<String, Docente> mapDocentes) {

        int loop = 0;
        Map<String, DocenteSeccion> mapDocenteSecciones = new LinkedHashMap();
        for (DocenteSeccion profeSecc : docentesSecciones) {
            //logger.debug("\tprocesando el profe-seccion {}-{}", profeSecc.getCodigoDocente(), profeSecc.getCodigoSeccion());
            Seccion seccion = mapSecciones.get(profeSecc.getCodigoSeccion());
            Docente profe = mapDocentes.get(profeSecc.getCodigoDocente());
            if (seccion == null) {
                String msg = String.format("La seccion %s no existe para se incluida en docente-seccion",
                        profeSecc.getCodigoSeccion());
                throw new PhobosException(msg);
            }
            if (profe == null) {
                String msg = String.format("El docente %s no existe para se incluida en docente-seccion",
                        profeSecc.getCodigoDocente());
                throw new PhobosException(msg);
            }

            DocenteSeccion profeSeccBD = docenteSeccionDAO.findByDocenteSeccion(profe, seccion);

            if (profeSeccBD == null) {
                profeSeccBD = new DocenteSeccion();
                profeSeccBD.setDocente(profe);
                profeSeccBD.setSeccion(seccion);
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                docenteSeccionDAO.save(profeSeccBD);

            } else {
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                profeSeccBD.setUserAnulacion(null);
                profeSeccBD.setFechaAnulacion(null);
                docenteSeccionDAO.update(profeSeccBD);
            }

            seccion.getDocenteSeccion().add(profeSeccBD);
            docentesSecciones.set(loop, profeSeccBD);
            mapDocenteSecciones.put(profe.getCodigo() + "-" + seccion.getCodigo(), profeSeccBD);
            loop++;
        }

        return mapDocenteSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allByCiclo(ciclo);
        for (DocenteSeccion profeSeccBD : profeSecciones) {
            Seccion secc = profeSeccBD.getSeccion();
            Docente profe = profeSeccBD.getDocente();
            //logger.debug("\tprocesando el profe-seccion {}-{}", profe.getCodigo(), secc.getCodigo());

            DocenteSeccion profeSecc = mapDocenteSecciones.get(profe.getCodigo() + "-" + secc.getCodigo());
            if (profeSecc != null) {
                continue;
            }

            profeSeccBD.setEstado(EstadoEnum.INA.name());
            profeSeccBD.setUserAnulacion(ds.getUsuario());
            profeSeccBD.setFechaAnulacion(new Date());
            docenteSeccionDAO.update(profeSeccBD);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loadDataMatriculados(
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            CicloAcademico ciclo, DataSessionPivot ds) {

        int rr = getRandom();

        Seccion seccion = mapSecciones.get(matriSecc.getCodigoSeccion());
        if (seccion == null) {
            String msg = String.format("La seccion %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoSeccion());
            throw new PhobosException(msg);
        }

        Alumno alumno = alumnoDAO.findByCodigo(matriSecc.getCodigoAlumno());
        if (alumno == null) {
            String msg = String.format("El alumno %s no existe para se incluida en matricula-seccion", matriSecc.getCodigoAlumno());
            throw new PhobosException(msg);
        }

        System.out.println(rr + " vamos a bloquear alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") para loadDataMatriculados");
        alumnoDAO.findLock(alumno.getId());
        System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + "(" + alumno.getId() + ") bloqueado para loadDataMatriculados");

        MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());
        if (resumen == null) {
            resumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
            if (resumen != null) {
                resumen.setMatriculaSeccion(new ArrayList());
                resumen.setMatriculaCurso(new ArrayList());
                mapResumenes.put(alumno.getCodigo(), resumen);
            }
        }

        if (resumen == null) {
            System.out.println("\t" + rr + " creando mat-resumen del alumno " + alumno.getCodigo() + " :::: ");
            resumen = new MatriculaResumen();
            resumen.setAlumno(alumno);
            resumen.setCicloAcademico(ciclo);
            resumen.setCreditosMatriculados(0);
            resumen.setCreditosRetirados(0);
            resumen.setCursosMatriculados(0);
            resumen.setCursosRetirados(0);
            resumen.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            resumen.setNotaAcumulada("0");
            resumen.setNotaAvance("0");
            resumen.setNotaFinal("0");
            resumen.setPorcentajeAvance(0);
            matriculaResumenDAO.save(resumen);
            System.out.println("\t" + rr + " mat-resumen es " + resumen.getId());

            resumen.setMatriculaSeccion(new ArrayList());
            resumen.setMatriculaCurso(new ArrayList());
            mapResumenes.put(alumno.getCodigo(), resumen);
        }

        if (resumen.getEstadoEnum() != EstadoMatriculaCursoEnum.MAT) {
            System.out.println("\t" + rr + " guardando mat-resumen " + resumen.getId() + " del alumno " + alumno.getCodigo());
            resumen.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriculaResumenDAO.update(resumen);
        }

        MatriculaSeccion matriSeccBD = findMatriculaSeccion(resumen.getMatriculaSeccion(), seccion);
        if (matriSeccBD == null) {
            matriSeccBD = matriculaSeccionDAO.findByAlumnoSeccion(alumno, seccion);
        }
        if (matriSeccBD == null) {
            System.out.println("\t" + rr + " creando mat-seccion del alumno " + alumno.getCodigo());
            matriSeccBD = new MatriculaSeccion();
            matriSeccBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriSeccBD.setFechaRegistro(new Date());
            matriSeccBD.setUserRegistro(ds.getUsuario());
            matriSeccBD.setSeccion(seccion);
            matriSeccBD.setMatriculaResumen(resumen);
            matriculaSeccionDAO.save(matriSeccBD);

            System.out.println("\t" + rr + " mat-seccion es " + matriSeccBD.getId());
        }

        if (!existeSeccion(resumen.getMatriculaSeccion(), seccion)) {
            System.out.println("\t" + rr + " mat-seccion " + matriSeccBD.getId() + " se agrega al alumno " + alumno.getCodigo());
            resumen.getMatriculaSeccion().add(matriSeccBD);
        }

        if (matriSeccBD.getEstadoEnum() != EstadoMatriculaCursoEnum.MAT) {
            System.out.println("\t" + rr + " guardando mat-seccion " + matriSeccBD.getId() + " del alumno " + alumno.getCodigo());
            matriSeccBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriculaSeccionDAO.update(matriSeccBD);
        }

        Curso curso = seccion.getGrupoSeccion().getCurso();
        MatriculaCurso matriCursoBD = findMatriculaCurso(resumen.getMatriculaCurso(), curso, rr);
        if (matriCursoBD == null) {
            matriCursoBD = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);
        }
        if (matriCursoBD == null) {
            System.out.print("\t" + rr + " creando mat-curso del alumno " + alumno.getCodigo() + " :::: ");
            matriCursoBD = new MatriculaCurso();

            matriCursoBD.setCurso(curso);
            matriCursoBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriCursoBD.setMatriculaResumen(resumen);
            matriCursoBD.setNotaAcumulada("0");
            matriCursoBD.setNotaAvance("0");
            matriCursoBD.setNotaFinal("0");
            matriCursoBD.setPorcentajeAvanceNota(0);
            matriculaCursoDAO.save(matriCursoBD);

            System.out.println("\t" + rr + " mat-curso es " + matriCursoBD.getId());
        }

        matriCursoBD.setCreditos(curso.getCreditosVariables() != null ? matriSecc.getCreditos() : curso.getCreditos());
        matriculaCursoDAO.update(matriCursoBD);

        if (!existeCurso(resumen.getMatriculaCurso(), curso)) {
            System.out.println("\t" + rr + " mat-curso " + matriCursoBD.getId() + " agregado al mat-resumen " + resumen.getId() + " del alumno " + alumno.getCodigo());
            resumen.getMatriculaCurso().add(matriCursoBD);
            resumen.setCursosMatriculados(resumen.getCursosMatriculados() + 1);
            resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() + curso.getCreditos());
            matriculaResumenDAO.update(resumen);

            System.out.println("\t" + rr + " finalizo actualizacion mat-resumen " + resumen.getId() + " para el mat-curso " + matriCursoBD.getId() + " del alumno " + alumno.getCodigo());
        }

        if (matriCursoBD.getEstadoEnum() != EstadoMatriculaCursoEnum.MAT) {
            matriCursoBD.setEstadoEnum(EstadoMatriculaCursoEnum.MAT);
            matriculaCursoDAO.update(matriCursoBD);
        }
        matriSecc.setProcesado(1);

        System.out.println("\t" + rr + " alumno " + alumno.getCodigo() + " desbloqueado en loadDataMatriculados");
    }

    private MatriculaCurso findMatriculaCurso(List<MatriculaCurso> alumnoCursos, Curso curso, int rr) {
        for (MatriculaCurso alumnoCurso : alumnoCursos) {
            Curso cur = alumnoCurso.getCurso();
            if (cur.getId().longValue() == curso.getId()) {
                System.out.println("\t" + rr + " entregando " + alumnoCurso.getId() + " mat-curso");
                return alumnoCurso;
            }
        }
        return null;
    }

    private boolean existeCurso(List<MatriculaCurso> alumnoCursos, Curso curso) {
        for (MatriculaCurso alumnoCurso : alumnoCursos) {
            Curso cur = alumnoCurso.getCurso();
            if (cur.getId().longValue() == curso.getId()) {
                return true;
            }
        }
        return false;
    }

    private MatriculaSeccion findMatriculaSeccion(List<MatriculaSeccion> alumnoSecciones, Seccion seccion) {
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            if (secc.getId().longValue() == seccion.getId()) {
                return alumnoSeccion;
            }
        }
        return null;
    }

    private boolean existeSeccion(List<MatriculaSeccion> alumnoSecciones, Seccion seccion) {
        for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
            Seccion secc = alumnoSeccion.getSeccion();
            if (secc.getId().longValue() == seccion.getId()) {
                return true;
            }
        }
        return false;
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarAlumnoMatriculado(MatriculaResumen aluResumen, Map<String, MatriculaResumen> mapResumenes, Map<String, AlumnoBlocked> mapBloqueadox) {
        try {

            Alumno alumno = aluResumen.getAlumno();

            System.out.println("bloquearemos alumno " + alumno.getCodigo() + " para revisarAlumnoMatriculado");
            alumnoDAO.findLock(alumno.getId());
            //AlumnoBlocked aluBlock = new AlumnoBlocked(alumno, System.currentTimeMillis(), "revisarAlumnoMatriculado");
            //mapBloqueados.put(alumno.getCodigo(), aluBlock);
            //System.out.println("\talumno " + alumno.getCodigo() + " ingresa a bloqueados revisarAlumnoMatriculado");

            MatriculaResumen resumen = mapResumenes.get(alumno.getCodigo());

            if (resumen == null) {
                aluResumen.setEstadoEnum(EstadoMatriculaCursoEnum.RCI);
                aluResumen.setCreditosRetirados(aluResumen.getCreditosRetirados() + aluResumen.getCreditosMatriculados());
                aluResumen.setCreditosMatriculados(0);
                aluResumen.setCursosRetirados(aluResumen.getCursosRetirados() + aluResumen.getCursosMatriculados());
                aluResumen.setCursosMatriculados(0);
                matriculaResumenDAO.update(aluResumen);

                List<MatriculaCurso> alumnoCursos = matriculaCursoDAO.allByMatriculaResumen(aluResumen);
                for (MatriculaCurso alumnoCurso : alumnoCursos) {
                    alumnoCurso.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                    matriculaCursoDAO.update(alumnoCurso);
                }

                List<MatriculaSeccion> alumnoSecciones = matriculaSeccionDAO.allByMatriculaSeccion(aluResumen);
                for (MatriculaSeccion alumnoSeccion : alumnoSecciones) {
                    alumnoSeccion.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                    matriculaSeccionDAO.update(alumnoSeccion);
                }
                //mapBloqueados.remove(alumno.getCodigo());
                //AlumnoBlocked aluBlu = mapBloqueados.get(alumno.getCodigo());
                //System.out.println("\tcomprobamos retiro del map " + aluBlu);
                System.out.println("\talumno " + alumno.getCodigo() + " desbloqueado 2222 en revisarAlumnoMatriculado");
                aluResumen.setProcesado(1);
                return;
            }

            List<MatriculaCurso> alumnoCursos = matriculaCursoDAO.allByMatriculaResumen(resumen);
            for (MatriculaCurso aluCurso : alumnoCursos) {
                Curso curso = aluCurso.getCurso();

                if (!existeCurso(resumen.getMatriculaCurso(), curso)) {
                    resumen.setCursosRetirados(resumen.getCursosRetirados() + 1);
                    resumen.setCursosMatriculados(resumen.getCursosMatriculados() - 1);
                    resumen.setCreditosRetirados(resumen.getCreditosRetirados() + curso.getCreditos());
                    resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() - curso.getCreditos());

                    aluCurso.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                    matriculaCursoDAO.update(aluCurso);
                }
            }

            List<MatriculaSeccion> alumnoSecciones = matriculaSeccionDAO.allByMatriculaSeccion(resumen);
            for (MatriculaSeccion aluSeccion : alumnoSecciones) {
                Seccion secc = aluSeccion.getSeccion();
                if (!existeSeccion(resumen.getMatriculaSeccion(), secc)) {
                    aluSeccion.setEstadoEnum(EstadoMatriculaCursoEnum.RET);
                    matriculaSeccionDAO.update(aluSeccion);
                }
            }

            matriculaResumenDAO.update(resumen);
            //System.out.println(mapBloqueados);
            //mapBloqueados.remove(alumno.getCodigo());
            //AlumnoBlocked aluBlu = mapBloqueados.get(alumno.getCodigo());
            //System.out.println("\tcomprobamos retiro del map del alumno " + alumno.getCodigo() + " --> " + aluBlu);
            //System.out.println(mapBloqueados);
            aluResumen.setProcesado(1);
            System.out.println("\talumno " + alumno.getCodigo() + " desbloqueado 3333 en revisarAlumnoMatriculado");

//            int loop = 1;
//            Iterator entries = mapBloqueados.entrySet().iterator();
//            while (entries.hasNext()) {
//                long ahora = System.currentTimeMillis();
//                Entry entry = (Entry) entries.next();
//                String alumno1 = (String) entry.getKey();
//                AlumnoBlocked aluBlock22 = (AlumnoBlocked) entry.getValue();
//                long hora = aluBlock22.getInicio();
//                String zona = aluBlock22.getZona();
//
//                if (alumno1.equals(alumno.getCodigo())) {
//                    System.out.println("\t" + alumno1 + " sigue en el map, no se elimino");
//                    entries.remove();
//                }
//
//                System.out.println(loop + "====> alumno " + alumno1 + " bloqueado por " + (ahora - hora) + " mseg en " + zona);
//                loop++;
//            }
//
//            loop = 1;
//            entries = mapBloqueados.entrySet().iterator();
//            while (entries.hasNext()) {
//                long ahora = System.currentTimeMillis();
//                Entry entry = (Entry) entries.next();
//                String alumno1 = (String) entry.getKey();
//                AlumnoBlocked aluBlock22 = (AlumnoBlocked) entry.getValue();
//                long hora = aluBlock22.getInicio();
//                String zona = aluBlock22.getZona();
//
//                System.out.println(loop + "=//==//=> alumno " + alumno1 + " bloqueado por " + (ahora - hora) + " mseg en " + zona);
//                loop++;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarSecciones(List<Seccion> secciones, CicloAcademico ciclo) {
        Map<Long, Seccion> mapSecciones = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            //logger.debug("\tprocesando la seccion {}", seccion.getCodigo());
            seccion.setMatriculados(seccion.getMatriculaSeccion().size());
            seccionDAO.update(seccion);
            mapSecciones.put(seccion.getId(), seccion);
        }

        List<Seccion> seccionesBD = seccionDAO.allByCiclo(ciclo);
        for (Seccion secc : seccionesBD) {
            Seccion seccion = mapSecciones.get(secc.getId());
            logger.debug("\tanalizando anulacion de la sección {}", secc.getCodigo());
            if (seccion == null) {
                logger.debug("\tanulando sección {}", secc.getCodigo());
                secc.setEstado(EstadoEnum.INA.name());
                seccionDAO.update(secc);
            }
        }
        logger.debug("\tRevision de secciones finalizada");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo) {
        Map<Long, GrupoSeccion> mapGrupoSecciones = new LinkedHashMap();
        for (GrupoSeccion gpoSecc : gruposSecciones) {
            mapGrupoSecciones.put(gpoSecc.getId(), gpoSecc);
            //logger.debug("\tprocesando el gpo-seccion {}", gpoSecc.getCodigo());
            Seccion seccSuperior = null;
            List<Seccion> secciones = gpoSecc.getSecciones();
            for (Seccion secc : secciones) {
                if (secc.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    continue;
                }
                seccSuperior = secc;
                break;
            }
            for (Seccion secc : secciones) {
                if (secc == seccSuperior) {
                    continue;
                }
                secc.setSeccionSuperior(seccSuperior);
                seccionDAO.update(secc);
            }
        }
        List<GrupoSeccion> grupoSeccionesDB = grupoSeccionDAO.allByCiclo(ciclo);
        for (GrupoSeccion gpoSecc : grupoSeccionesDB) {
            GrupoSeccion grupoSeccion = mapGrupoSecciones.get(gpoSecc.getId());
            if (grupoSeccion == null) {
                gpoSecc.setEstado(EstadoEnum.INA.name());
                gpoSecc.setEstadoPlanEnum(EstadoPlanCalificaEnum.CER);
                gpoSecc.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.CER);
                gpoSecc.setVersion("0");
                grupoSeccionDAO.update(gpoSecc);
            }
        }

    }

    @Async
    @Override
    public void revisarBloqueados(Map<String, AlumnoBlocked> mapBloqueados) {
        if (1 == 1) {
            return;
        }
        for (;;) {
            //System.out.println(mapBloqueados);
            //Iterator entries = mapBloqueados.entrySet().iterator();
            List<AlumnoBlocked> bloks = new ArrayList();
            bloks.addAll(mapBloqueados.values());
            System.out.println("tenemos " + bloks.size() + " bloqueados");
            for (AlumnoBlocked blok : bloks) {
                System.out.println("blockkkk :: " + blok.getAlumno().getCodigo());
                mapBloqueados.remove(blok.getAlumno().getCodigo(), blok);
            }
            System.out.println("======================");

//            while (entries.hasNext()) {
//                long ahora = System.currentTimeMillis();
//                Entry entry = (Entry) entries.next();
//                String alumno = (String) entry.getKey();
//                AlumnoBlocked aluBlock = (AlumnoBlocked) entry.getValue();
//                long hora = aluBlock.getInicio();
//                String zona = aluBlock.getZona();
//
//                System.out.println("alumno " + alumno + " bloqueado por " + (ahora - hora) + " mseg en " + zona);
//
//                if ((ahora - hora) > 5000) {
//                    System.out.println("tiene " + mapBloqueados.size() + " elementos");
//                    mapBloqueados.remove(alumno);
//                    System.out.println("se queda con " + mapBloqueados.size() + " elementos");
//                }
//            }
            if (!revisar) {
                break;
            }

            long t1 = System.currentTimeMillis();
            for (;;) {
                long t2 = System.currentTimeMillis();
                if ((t2 - t1) > 5000) {
                    break;
                }
            }
        }

    }

    @Override
    public void detenerRevisionBloqueado() {
        revisar = false;
    }

}
