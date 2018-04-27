package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
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
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.general.PersonaCargoDAO;

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
    PersonaCargoDAO personaCargoDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;
    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    LoadDataMatriculadoService loadDataMatriculadoService;

    @Autowired
    VisorLoadProgramacion visor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static boolean revisar = true;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteHorarioSeccionNoUsados(List<HorarioSeccion> horarios, CicloAcademico cicloAcademico) {
        horarioSeccionDAO.deleteAllByNotInList(horarios);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String extraerEmailCompania(
            Persona perso,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        String email = null;
//        List<Persona> personasVinculadas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personasVinculadas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                main = persona;
                break;
            }
        }
        email = main.getEmailCompania();
        String emails = "";
        for (Persona persona : personasVinculadas) {
            emails += emails.equals("") ? "" : "-.-";
            emails += persona.getId() + "::" + persona.getEmailCompania();
        }
        logger.debug("\tmain de {} es {} y los emails son {}", perso.getKey(), main.getId(), emails);
        for (Persona persona : personasVinculadas) {
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
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        Persona dni = new Persona();
//        List<Persona> personasVinculadas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personasVinculadas) {
            if (persona.getEstado().equals(EstadoEnum.ACT.name())) {
                main = persona;
                break;
            }
        }

        for (Persona persona : personasVinculadas) {
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
            List<Persona> personasVinculadas,
            TipoDocIdentidad tipoDocumento,
            String numeroDocIdentidad,
            String emailCompania,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
//        List<Persona> personasVinculadas = allPersonasByPer(perso, mapKeyPersonas, mapDNIPersonas, ds);
        Persona main = null;
        for (Persona persona : personasVinculadas) {
            if (persona.getEstadoEnum() == PersonaEstadoEnum.ACT) {
                main = persona;
                break;
            }
        }
        logger.debug("\tmain de {} es {}", perso.getKey(), main.getId());
        if (StringUtils.isEmpty(main.getEmailCompania()) && !StringUtils.isEmpty(emailCompania)) {
            main.setEmailCompania(emailCompania);
        }
        if (StringUtils.isEmpty(main.getNumeroDocIdentidad()) && !StringUtils.isEmpty(numeroDocIdentidad)) {
            main.setTipoDocumento(tipoDocumento);
            main.setNumeroDocIdentidad(numeroDocIdentidad);
        }
        personaDAO.update(main);

        for (Persona persona : personasVinculadas) {
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
    public void revisarHorarioGrupos(List<DiaHoraGrupo> horariosGrupo, CicloAcademico ciclo) {
        List<DiaHoraGrupo> antiguos = horariosGrupo.stream().filter(x -> x.getId() != null).collect(Collectors.toList());
        diaHoraGrupoDAO.deleteAllByNotInList(antiguos);

        for (DiaHoraGrupo horario : horariosGrupo) {
            diaHoraGrupoDAO.save(horario);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Persona savePersona(
            Persona persona,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {

        TipoDocIdentidad tipoDoc = persona.getTipoDocumento();
        if (tipoDoc != null && !StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
            Persona tempo = mapDNIPersonas.get(persona.getIdentificacion());
            if (tempo == null) {
                List<Persona> tempos = mapKeyPersonas.get(persona.getKey());
                if (tempos != null && !tempos.isEmpty()) {
                    Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
                    copiarDatosPersonales(perzoma, persona);
                    perzoma.setTipoDocumento(persona.getTipoDocumento());
                    perzoma.setNumeroDocIdentidad(persona.getNumeroDocIdentidad());
                    personaDAO.update(perzoma);
                    return perzoma;
                }
            }

            if (tempo == null) {
                persona.setUserRegistro(ds.getUsuario());
                persona.setFechaRegistro(new Date());
                persona.setEstadoEnum(PersonaEstadoEnum.ACT);
                personaDAO.save(persona);

                mapDNIPersonas.put(persona.getIdentificacion(), persona);
            }

            Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            copiarDatosPersonales(perzoma, persona);
            personaDAO.update(perzoma);
            return perzoma;
        }

        Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
        copiarDatosPersonales(perzoma, persona);
        personaDAO.update(perzoma);
        return perzoma;
    }

    private void copiarDatosPersonales(Persona personaDestino, Persona personaOrigen) {
        if (personaDestino.getPaisNacer() == null) {
            personaDestino.setPaisNacer(personaOrigen.getPaisNacer());
        }
        if (personaDestino.getNacionalidad() == null) {
            personaDestino.setNacionalidad(personaOrigen.getNacionalidad());
        }
        if (personaDestino.getUbicacionNacer() == null) {
            personaDestino.setUbicacionNacer(personaOrigen.getUbicacionNacer());
        }
        if (personaDestino.getUbigeoDomicilio() == null) {
            personaDestino.setUbicacionDomicilio(personaOrigen.getUbicacionDomicilio());
        }
        if (personaDestino.getEstadoCivil() == null) {
            personaDestino.setEstadoCivil(personaOrigen.getEstadoCivil());
        }
        if (personaDestino.getFechaNacer() == null) {
            personaDestino.setFechaNacer(personaOrigen.getFechaNacer());
        }
        if (StringUtils.isEmpty(personaDestino.getEmail())) {
            personaDestino.setEmail(personaOrigen.getEmail());
        }
        if (StringUtils.isEmpty(personaDestino.getTelefono())) {
            personaDestino.setTelefono(personaOrigen.getTelefono());
        }
        if (StringUtils.isEmpty(personaDestino.getCelular())) {
            personaDestino.setCelular(personaOrigen.getCelular());
        }
        if (StringUtils.isEmpty(personaDestino.getDireccion())) {
            personaDestino.setDireccion(personaOrigen.getDireccion());
        }
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

        List<Carrera> carreras = carreraDAO.all();
        Map<String, Carrera> mapCarreras = TypesUtil.convertListToMap("codigo", carreras);
        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
        Map<String, CicloAcademico> mapCiclos = TypesUtil.convertListToMap("codigoAntiguo", ciclos);

        Alumno alu = mapAlumnos.get(alumno.getCodigo());
        String codigoCarrera = StringUtils.isEmpty(alumno.getCodigoEspecialidad()) ? alumno.getCodigoPostgrado() : alumno.getCodigoEspecialidad();
        Carrera carrera = mapCarreras.get(codigoCarrera);
        ModalidadEstudio modalidad = carrera.getModalidadEstudio();
        CicloAcademico cicloInicio = mapCiclos.get(alumno.getCodigoCicloIngreso());
        CicloAcademico cicloActivo = mapCiclos.get(alumno.getCodigoCicloActivo());
        SituacionAcademica situacion = mapSituaciones.get(alumno.getSituacion());
        situacion = (situacion == null) ? mapSituaciones.get("N") : situacion;

        if (alu != null) {
            alu.setPersona(persona);
            alu.setCarrera(carrera);
            alu.setSituacionAcademica(situacion);
            alu.setModalidadEstudio(modalidad);
            alu.setCicloActivo(cicloActivo);
            alu.setCicloIngreso(cicloInicio);
            alumnoDAO.update(alu);

            visor.agregarLog("alu", "saveAlumno", "Alumno " + alumno.getCodigo() + " ya existe, se actualizo", true, "info");

        } else {

            alumno.setCarrera(carrera);
            alumno.setSituacionAcademica(situacion);
            alumno.setCicloActivo(cicloActivo);
            alumno.setCicloIngreso(cicloInicio);
            alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
            alumno.setModalidadEstudio(modalidad);

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
            alumno.setCiclosEstudiados(0);
            alumnoDAO.save(alumno);

            mapAlumnos.put(alumno.getCodigo(), alumno);
            visor.agregarLog("alu", "saveAlumno", "Alumno " + alumno.getCodigo() + " nuevo", true, "info");

            saveUsuario(persona, RolEnum.ALU, ds);
        }

    }

    private void saveUsuario(Persona persona, RolEnum rol, DataSessionPivot ds) {
        Usuario user = usuarioDAO.findByPersona(persona);
        if (user != null) {

            boolean existeAlumno = false;
            boolean existeDocente = false;
            List<UsuarioRol> userRoles = usuarioRolDAO.allByUser(user);
            for (UsuarioRol userRol : userRoles) {
                if (userRol.getRol().getId() == 1 && rol == RolEnum.ALU && userRol.getEstadoEnum() == UserEstadoEnum.ACT) {
                    existeAlumno = true;
                    break;
                }
                if (userRol.getRol().getId() == 2 && rol == RolEnum.DOC && userRol.getEstadoEnum() == UserEstadoEnum.ACT) {
                    existeDocente = true;
                    break;
                }
            }
            if (!existeAlumno && rol == RolEnum.ALU) {
                UsuarioRol userRol = new UsuarioRol();
                userRol.setUsuario(user);
                userRol.setRol(new Rol(1));
                userRol.setEstado(UserEstadoEnum.ACT);
                userRol.setFechaInicio(new Date());
                userRol.setFechaRegistro(new Date());
                userRol.setUsuario(ds.getUsuario());
                usuarioRolDAO.save(userRol);
            }
            if (!existeDocente && rol == RolEnum.DOC) {
                UsuarioRol userRol = new UsuarioRol();
                userRol.setUsuario(user);
                userRol.setRol(new Rol(2));
                userRol.setEstado(UserEstadoEnum.ACT);
                userRol.setFechaInicio(new Date());
                userRol.setFechaRegistro(new Date());
                userRol.setUserRegistro(ds.getUsuario());
                usuarioRolDAO.save(userRol);
            }

            return;
        }

        if (StringUtils.isEmpty(persona.getEmailCompania())) {
            return;
        }

        user = new Usuario();
        user.setPersona(persona);
        user.setGoogle(persona.getEmailCompania().toLowerCase());
        user.setEstadoEnum(UserEstadoEnum.ACT);
        user.setFechaRegistro(new Date());
        user.setUserRegistro(ds.getUsuario());
        usuarioDAO.save(user);

        user.setUserActivo(user);
        usuarioDAO.update(user);

        UsuarioRol userRol = new UsuarioRol();
        userRol.setUsuario(user);
        userRol.setEstado(UserEstadoEnum.ACT);
        userRol.setFechaInicio(new Date());
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
            profeBD.setEstado(DocenteEstadoEnum.ACT);
            profeBD.setDepartamentoAcademico(dpto);
            profeBD.setModalidadEstudio(modalidad);
            profeBD.setPersona(persona);
            profeBD.setFechaRegistro(new Date());
            profeBD.setUserRegistro(ds.getUsuario());
            docenteDAO.save(profeBD);

            visor.agregarLog("doc", "saveDocente", "Profesor " + profeBD.getCodigo() + " ya existe, se actualizo", true, "info");

        } else if (profeBD.getEstadoEnum() != DocenteEstadoEnum.ACT) {
            profeBD.setEstado(DocenteEstadoEnum.ACT);
            profeBD.setFechaModifica(new Date());
            profeBD.setUserModifica(ds.getUsuario());
            docenteDAO.update(profeBD);

            visor.agregarLog("doc", "saveDocente", "Profesor " + profeBD.getCodigo() + " nuevo", true, "info");
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
                docente.setEstado(DocenteEstadoEnum.INA);
                docente.setFechaModifica(new Date());
                docente.setUserModifica(ds.getUsuario());
                docenteDAO.update(docente);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Persona> allPersonasByPer(
            Persona persona,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas,
            DataSessionPivot ds) {

        List<Persona> personas = mapKeyPersonas.get(persona.getKey());
        if (personas == null) {
            personas = new ArrayList();
        }
        logger.debug("\texisten {} personas por key {}", personas.size(), persona.getKey());
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
                    logger.debug("\tagregamos a personas por DNI {}", persona.getIdentificacion());
                    personas.add(per);
                }
            }
        }

        if (personas.isEmpty()) {
            persona.setUserRegistro(ds.getUsuario());
            persona.setFechaRegistro(new Date());
            persona.setEstadoEnum(PersonaEstadoEnum.ACT);
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
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {

        String ids = "";
        for (Persona per : personasVinculadas) {
            ids += ids.equals("") ? "" : "-|-";
            ids += per.getId() + "::" + per.getKey();
        }
        logger.debug("existen {} duplicados son {} {} {}", personasVinculadas.size(), ids);

        if (personasVinculadas.isEmpty()) {
            Persona pp = new Persona(persona);
            pp.setUserRegistro(ds.getUsuario());
            pp.setFechaRegistro(new Date());
            pp.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaDAO.save(pp);
            logger.debug("finalizo revision EMPTY de persona {}", pp.getApellidosNombres());
            personasVinculadas.add(pp);
            return pp;
        }

        if (personasVinculadas.size() == 1) {
            Persona pp = personasVinculadas.get(0);
            pp.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaDAO.update(pp);
            logger.debug("finalizo revision SIZE1 de persona {}", pp.getApellidosNombres());
            //personasVinculadas.add(pp);
            return personasVinculadas.get(0);
        }

        Persona main = findPersonaMain(personasVinculadas);
        logger.debug("\tPersona main de {} es el {} {}", persona.getKey(), main.getId(), main.getKey());
        datoToMain(personasVinculadas, main, ds);
        changePersonasNoMain(personasVinculadas, main, ds);

        for (Persona p : personasVinculadas) {
            personaDAO.update(p);
        }

        logger.debug("finalizo revision RETURN de persona {}", main.getApellidosNombres());
        return main;
    }

    private void datoToMain(List<Persona> personas, Persona main, DataSessionPivot ds) {
        for (Persona persona : personas) {
            if (persona.getId() == main.getId().longValue()) {
                continue;
            }
            copyInfo(main, persona);
        }

        for (Persona persona : personas) {
            if (persona.getId() == main.getId().longValue()) {
                persona.setEstadoEnum(PersonaEstadoEnum.ACT);
                persona.setFechaTraslado(null);
                persona.setUserTraslado(null);
                persona.setPersonaTraslado(null);
                continue;
            }

            persona.setEstadoEnum(PersonaEstadoEnum.INA);
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

            List<PersonaCargo> persoPerfiles = personaCargoDAO.allByPersona(persona);
            //logger.debug("se hallo {} perfiles", persoPerfiles.size());
            for (PersonaCargo pp : persoPerfiles) {
                pp.setPersona(main);
                personaCargoDAO.update(pp);
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
            List<PersonaCargo> persoPerfiles = personaCargoDAO.allByPersona(persona);
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
        List<GrupoSeccion> gpoSeccionesBD = grupoSeccionDAO.allByCiclo(ciclo);
        Map<String, GrupoSeccion> mapGpoSeccionBD = TypesUtil.convertListToMap("codigo", gpoSeccionesBD);

        List<AnexoBoletin> anexosBD = anexoBoletinDAO.all();
        Map<String, GrupoSeccion> mapGpoSecciones = new LinkedHashMap();
        Map<String, AnexoBoletin> mapAnexos = TypesUtil.convertListToMap("codigo", anexosBD);

        List<Curso> cursosBD = cursoDAO.all();
        Map<String, Curso> mapCursoBD = TypesUtil.convertListToMap("codigo", cursosBD);

        for (GrupoSeccion gpoSecc : gruposSecciones) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            GrupoSeccion gpoSeccBD = mapGpoSeccionBD.get(gpoSecc.getCodigo());
            Curso curso = mapCursoBD.get(gpoSecc.getCodigoCurso());
            AnexoBoletin anexo = mapAnexos.get(gpoSecc.getCodigoAnexo());

            logger.debug("\tprocesando el gpoSecc {}", gpoSecc.getCodigo());
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
                gpoSeccBD.setAnexoBoletin(anexo);

                grupoSeccionDAO.save(gpoSeccBD);
                visor.agregarLog("gpoSecc", "saveGpoSecc", "Gpo-Seccion " + gpoSeccBD.getCodigo() + " nuevo", true, "info");

            } else {
                gpoSeccBD.setVersion(gpoSeccBD.getVersion() == null ? "1" : gpoSeccBD.getVersion());
                gpoSeccBD.setEstadoPlanEnum(gpoSeccBD.getEstadoPlan() == null ? EstadoPlanCalificaEnum.PEND : gpoSeccBD.getEstadoPlanEnum());
                gpoSeccBD.setEstadoGrupo(gpoSeccBD.getEstadoGrupo() == null ? EstadoGrupoSeccionEnum.ABI.name() : gpoSeccBD.getEstadoGrupo());
                gpoSeccBD.setEstado(EstadoEnum.ACT.name());
                gpoSeccBD.setAnexoBoletin(anexo);
                grupoSeccionDAO.update(gpoSeccBD);

                visor.agregarLog("gpoSecc", "saveGpoSecc", "Gpo-Seccion " + gpoSeccBD.getCodigo() + " ya existe, se actualizo", true, "info");

                Curso cursoBD = gpoSeccBD.getCurso();
                if (curso.getId() != cursoBD.getId().longValue()) {
                    visor.agregarLog("gpoSecc", "saveGpoSecc", "El curso del gpo-Seccion " + gpoSeccBD.getCodigo()
                            + " está relacionado al curso " + curso.getCodigo() + " pero en la base de datos es " + cursoBD.getCodigo(),
                            false, "error-proceso");
                    String msg = String.format("El curso del grupo-seccion %s está relacionado al curso %s pero en la base de datos es %s",
                            gpoSecc.getCodigo(), curso.getCodigo(), cursoBD.getCodigo());
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
        Map<String, List<Seccion>> mapSeccionesPCUR = new LinkedHashMap();
        Map<String, Seccion> mapSeccionesTCUR = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            seccion.setTipoSeccionEnum(TipoSeccionEnum.valueOf(seccion.getCodigoTipoSeccion()));
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                seccion.setVacantes(0);
                seccion.setMatriculados(0);
                List<Seccion> seccionesPCUR = mapSeccionesPCUR.get(seccion.getCodigoGrupoSeccion());
                if (seccionesPCUR == null) {
                    seccionesPCUR = new ArrayList();
                    mapSeccionesPCUR.put(seccion.getCodigoGrupoSeccion(), seccionesPCUR);
                }
                mapSeccionesTCUR.put(seccion.getCodigoGrupoSeccion(), seccion);

            } else {
                seccion.setVacantes(seccion.getVacantes() == null ? 0 : seccion.getVacantes());
                seccion.setMatriculados(seccion.getMatriculados() == null ? 0 : seccion.getMatriculados());
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {

                List<Seccion> seccionesPCUR = mapSeccionesPCUR.get(seccion.getCodigoGrupoSeccion());
                if (seccionesPCUR == null) {
                    seccionesPCUR = new ArrayList();
                    mapSeccionesPCUR.put(seccion.getCodigoGrupoSeccion(), seccionesPCUR);
                }
                seccionesPCUR.add(seccion);
            }
        }

        for (Map.Entry<String, Seccion> entry : mapSeccionesTCUR.entrySet()) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            String gpoSeccCode = entry.getKey();
            Seccion seccionTCUR = entry.getValue();
            List<Seccion> seccionesPCUR = mapSeccionesPCUR.get(gpoSeccCode);
            for (Seccion seccion : seccionesPCUR) {
                seccionTCUR.setVacantes(seccionTCUR.getVacantes() + seccion.getVacantes());
                seccionTCUR.setMatriculados(seccionTCUR.getMatriculados() + seccion.getMatriculados());
                seccion.setSeccionSuperior(seccionTCUR);
            }
        }

        Map<String, Seccion> mapSecciones = new LinkedHashMap();
        Map<String, List<Seccion>> mapSeccionesPCurBD = new LinkedHashMap();
        Map<String, Seccion> mapSeccionesTCurBD = new LinkedHashMap();

        List<Seccion> seccionesBD = seccionDAO.allByCiclo(ciclo);
        Map<String, Seccion> mapSeccionBD = TypesUtil.convertListToMap("codigo", seccionesBD);
        List<GrupoHoras> gpoHorasBD = grupoHorasDAO.all();
        Map<String, GrupoHoras> mapGpoHoraBD = TypesUtil.convertListToMap("codigo", gpoHorasBD);
        List<Aula> aulasBD = aulaDAO.all();
        Map<String, Aula> mapAulaBD = TypesUtil.convertListToMap("codigo", aulasBD);

        for (Seccion seccion : secciones) {
            GrupoSeccion gpoSecc = mapGpoSecciones.get(seccion.getCodigoGrupoSeccion());
            if (gpoSecc == null) {
                String msg = String.format("La seccion %s no tiene su padre grupo-seccion %s",
                        seccion.getCodigo(), seccion.getCodigoGrupoSeccion());
                throw new PhobosException(msg);
            }

            Curso curso = gpoSecc.getCurso();
            //Seccion seccionBD = seccionDAO.findByCodeCiclo(seccion.getCodigo(), ciclo);
            Seccion seccionBD = mapSeccionBD.get(seccion.getCodigo());
            GrupoHoras gpoHoras = findGrupoHoras(seccion, mapGpoHoraBD);
            Aula aula = findAula(seccion, mapAulaBD);

            System.out.println("SECCION " + seccion.getCodigo() + " :::: vac:" + seccion.getMatriculados() + " mat:" + seccion.getMatriculados());

            if (seccionBD == null) {
                seccionBD = new Seccion();
                seccionBD.setCodigo(seccion.getCodigo());
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setGrupoSeccion(gpoSecc);
                seccionBD.setVacantes(seccion.getVacantes());
                seccionBD.setMatriculados(seccion.getMatriculados());
                seccionBD.setRetirados(0);
                seccionBD.setReservados(0);
                seccionBD.setPrematriculados(0);
                seccionBD.setEsPrincipal(0);
                seccionBD.setTipoSeccionEnum(TipoSeccionEnum.valueOf(seccion.getCodigoTipoSeccion()));
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);

                Integer horasTeoria = curso.getHorasTeoria() == null ? 0 : curso.getHorasTeoria();
                Integer horasPractica = curso.getHorasPractica() == null ? 0 : curso.getHorasPractica();

                if (seccionBD.isTipoSeccionPRA()) {
                    seccionBD.setHorasSemanales(horasPractica);
                    gpoSecc.setHorasPractica(horasPractica);
                } else if (seccionBD.isTipoSeccionTEO()) {
                    seccionBD.setHorasSemanales(horasTeoria);
                    gpoSecc.setHorasTeoria(horasTeoria);
                } else if (seccionBD.isTipoSeccionTCUR()) {
                    seccionBD.setHorasSemanales(horasTeoria);
                    gpoSecc.setHorasTeoria(horasTeoria);
                } else if (seccionBD.isTipoSeccionPCUR()) {
                    seccionBD.setHorasSemanales(horasPractica);
                    gpoSecc.setHorasPractica(horasPractica);
                }

                seccionBD.setEstado(EstadoEnum.ACT.name());
                seccionDAO.save(seccionBD);
                visor.agregarLog("secc", "saveSecc", "Seccion " + seccionBD.getCodigo() + " nuevo", false, "info");

            } else {
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setEstado(EstadoEnum.ACT.name());
                seccionBD.setVacantes(seccion.getVacantes());
                seccionBD.setMatriculados(seccion.getMatriculados());
                seccionBD.setRetirados(0);
                seccionBD.setReservados(0);
                seccionBD.setPrematriculados(0);
                seccionDAO.update(seccionBD);
                visor.agregarLog("secc", "saveSecc", "Seccion " + seccionBD.getCodigo() + " ya existe, se actualizó datos", false, "info");
            }

            if (seccionBD.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                mapSeccionesTCurBD.put(gpoSecc.getCodigo(), seccionBD);
            }
            if (seccionBD.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                List<Seccion> seccionesPCUR = mapSeccionesPCurBD.get(gpoSecc.getCodigo());
                if (seccionesPCUR == null) {
                    seccionesPCUR = new ArrayList();
                    mapSeccionesPCurBD.put(gpoSecc.getCodigo(), seccionesPCUR);
                }
                seccionesPCUR.add(seccionBD);
            }

            gpoSecc.getSecciones().add(seccionBD);
            seccionBD.setDocenteSeccion(new ArrayList());
            seccionBD.setMatriculaSeccion(new ArrayList());
            secciones.set(loop, seccionBD);
            mapSecciones.put(seccionBD.getCodigo(), seccionBD);
            loop++;
            visor.agregarLog("secc", "saveSecc", "Seccion " + seccionBD.getCodigo() + " procesada", true, "info");
            logger.debug("\t\tSeccion {} procesada {} de {}", seccionBD.getCodigo(), loop, secciones.size());

            System.out.println("SECCION_BD " + seccionBD.getCodigo() + " :::: vac:" + seccionBD.getMatriculados() + " mat:" + seccionBD.getMatriculados());
        }

        for (Map.Entry<String, Seccion> entry : mapSeccionesTCurBD.entrySet()) {
            String gpoSeccCode = entry.getKey();
            Seccion seccionTCUR = entry.getValue();
            List<Seccion> seccionesPCUR = mapSeccionesPCurBD.get(gpoSeccCode);
            for (Seccion seccionPCUR : seccionesPCUR) {
                seccionPCUR.setSeccionSuperior(seccionTCUR);
                seccionDAO.update(seccionPCUR);
                visor.agregarLog("secc", "saveSecc", "Seccion PCUR " + seccionPCUR.getCodigo() + " con TCUR " + seccionTCUR.getCodigo(), false, "info");
            }
        }

        return mapSecciones;
    }

    private GrupoHoras findGrupoHoras(Seccion seccion, Map<String, GrupoHoras> mapGpoHoraBD) {
        String codigo = seccion.getCodigoGrupoHorario();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        //GrupoHoras gpoHoras = grupoHorasDAO.findByCode(codigo);
        GrupoHoras gpoHoras = mapGpoHoraBD.get(codigo);
        if (gpoHoras == null) {
            String msg = String.format("El grupo-horas %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return gpoHoras;
    }

    private Aula findAula(Seccion seccion, Map<String, Aula> mapAulaBD) {
        String codigo = seccion.getCodigoAula();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        //Aula aula = aulaDAO.findByCode(codigo);
        Aula aula = mapAulaBD.get(codigo);
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
            Map<String, Docente> mapDocentes,
            CicloAcademico ciclo) {

        List<DocenteSeccion> docentesSeccionesBD = docenteSeccionDAO.allByCiclo(ciclo);
        Map<String, DocenteSeccion> mapDocenteSeccionBD = TypesUtil.convertListToMap("key", docentesSeccionesBD);

        int loop = 0;
        Map<String, DocenteSeccion> mapDocenteSecciones = new LinkedHashMap();
        for (DocenteSeccion profeSecc : docentesSecciones) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

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

            DocenteSeccion profeSeccBD = mapDocenteSeccionBD.get(profeSecc.getCodigoDocente() + "-" + profeSecc.getCodigoSeccion());

            if (profeSeccBD == null) {
                profeSeccBD = new DocenteSeccion();
                profeSeccBD.setDocente(profe);
                profeSeccBD.setSeccion(seccion);
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                docenteSeccionDAO.save(profeSeccBD);
                visor.agregarLog("docSecc", "saveDocSecc", "Docente-Seccion " + profe.getCodigo() + "-" + seccion.getCodigo() + " nuevo", true, "info");

            } else {
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                profeSeccBD.setUserAnulacion(null);
                profeSeccBD.setFechaAnulacion(null);
                docenteSeccionDAO.update(profeSeccBD);
                visor.agregarLog("docSecc", "saveDocSecc", "Docente-Seccion " + profe.getCodigo() + "-" + seccion.getCodigo() + " ya existe y se actualiza", true, "info");
            }

            seccion.getDocenteSeccion().add(profeSeccBD);
            docentesSecciones.set(loop, profeSeccBD);
            mapDocenteSecciones.put(profe.getCodigo() + "-" + seccion.getCodigo(), profeSeccBD);
            loop++;
            logger.debug("\t\tDocente-Seccion {}-{} procesado {} de {}", profe.getCodigo(), seccion.getCodigo(), loop, docentesSecciones.size());
        }

        return mapDocenteSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allByCiclo(ciclo);
        visor.inicializar("docSecc", profeSecciones.size());

        for (DocenteSeccion profeSeccBD : profeSecciones) {
            Seccion secc = profeSeccBD.getSeccion();
            Docente profe = profeSeccBD.getDocente();
            logger.debug("\t\tprocesando revision de profe-seccion {}-{}", profe.getCodigo(), secc.getCodigo());
            visor.agregarLog("docSecc", "revisarDocSecc", "Revisando docente-Seccion " + profe.getCodigo() + "-" + secc.getCodigo(), false, "info");

            DocenteSeccion profeSecc = mapDocenteSecciones.get(profe.getCodigo() + "-" + secc.getCodigo());
            if (profeSecc != null) {
                visor.agregarLog("docSecc", "revisarDocSecc", "Docente-Seccion " + profe.getCodigo() + "-" + secc.getCodigo() + " esta OK", true, "info");
                continue;
            }

            profeSeccBD.setEstado(EstadoEnum.INA.name());
            profeSeccBD.setUserAnulacion(ds.getUsuario());
            profeSeccBD.setFechaAnulacion(new Date());
            docenteSeccionDAO.update(profeSeccBD);
            visor.agregarLog("docSecc", "revisarDocSecc", "Docente-Seccion " + profe.getCodigo() + "-" + secc.getCodigo() + " se vuelve a INA", true, "info");
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

        try {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }
            loadDataMatriculadoService.load(matriSecc, mapResumenes, mapSecciones, ciclo, ds);

            Alumno alumno = alumnoDAO.findFlatByCodigo(matriSecc.getCodigoAlumno());
            alumnoDAO.update(alumno);
            System.out.println("\talumno 222 " + alumno.getCodigo() + " desbloqueado en XYZ-loadDataMatriculados");

        } catch (Exception e) {
            visor.agregarLog("aluSecc", "saveAluSecc", "Alumno-Seccion " + matriSecc.getCodigoAlumno() + "-" + matriSecc.getCodigoSeccion()
                    + " produjo error: " + e.getLocalizedMessage(),
                    false, "error-proceso");
            e.printStackTrace();
        }

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
    public void revisarAlumnoMatriculado(MatriculaResumen resumen) {
        try {

            resumen.setFechaInicioProceso(new Date());
            resumen.setCreditosMatriculados(0);
            resumen.setCursosMatriculados(0);
            resumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            Alumno alumno = resumen.getAlumno();

            System.out.println("bloquearemos alumno " + alumno.getCodigo() + " para revisarAlumnoMatriculado");
            alumnoDAO.findLock(alumno.getId());
            System.out.println("\talumno " + alumno.getCodigo() + " bloqueado en revisarAlumnoMatriculado");

            Map<Long, MatriculaCurso> mapMC = TypesUtil.convertListToMap("id", resumen.getMatriculaCurso());
            Map<Long, MatriculaSeccion> mapMS = TypesUtil.convertListToMap("id", resumen.getMatriculaSeccion());
            List<MatriculaCurso> matriCursos = new ArrayList(mapMC.values());
            List<MatriculaSeccion> matriSecciones = new ArrayList(mapMS.values());

            for (MatriculaCurso mc : matriCursos) {
                if (mc.getCargado() == 1) {
                    resumen.setCreditosMatriculados(resumen.getCreditosMatriculados() + mc.getCreditos());
                    resumen.setCursosMatriculados(resumen.getCursosMatriculados() + 1);
                    mc.setEstadoEnum(EstadoMatriculaEnum.MAT);
                } else {
                    mc.setEstadoEnum(EstadoMatriculaEnum.RET);
                }
                matriculaCursoDAO.update(mc);
            }
            for (MatriculaSeccion ms : matriSecciones) {
                if (ms.getCargado() == 1) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.MAT);
                } else {
                    ms.setEstadoEnum(EstadoMatriculaEnum.RET);
                }
                matriculaSeccionDAO.update(ms);
            }

            if (resumen.getCursosMatriculados() == 0 && !matriCursos.isEmpty()) {
                resumen.setEstadoEnum(EstadoMatriculaEnum.RCI);
                for (MatriculaCurso mc : matriCursos) {
                    mc.setEstadoEnum(EstadoMatriculaEnum.RCI);
                    matriculaCursoDAO.update(mc);
                }
                for (MatriculaSeccion ms : matriSecciones) {
                    ms.setEstadoEnum(EstadoMatriculaEnum.RCI);
                    matriculaSeccionDAO.update(ms);
                }
            }

            if (resumen.getCursosMatriculados() == 0 && matriCursos.isEmpty()) {
                resumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            }
            if (resumen.getCursosMatriculados() > 0) {
                resumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
            }
            matriculaResumenDAO.update(resumen);
            resumen.setProcesado(1);
            resumen.setFechaFinProceso(new Date());
            System.out.println("\talumno " + alumno.getCodigo() + " desbloqueado 3333 en revisarAlumnoMatriculado");
            visor.agregarLog("aluRes", "revisarAluRes", "alumno " + alumno.getCodigo() + " queda como " + resumen.getEstado(), true, "info");

        } catch (Exception e) {
            visor.agregarLog("aluRes", "revisarAluRes", "resumen " + resumen.getId() + " produjo el error: " + e.getLocalizedMessage(), false, "error-proceso");
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarSecciones(List<Seccion> secciones, CicloAcademico ciclo) {
        Map<Long, Seccion> mapSecciones = TypesUtil.convertListToMap("id", secciones);

        List<Seccion> seccionesBD = seccionDAO.allByCiclo(ciclo);
        visor.inicializar("seccBD", seccionesBD.size());
        for (Seccion secc : seccionesBD) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            Seccion seccion = mapSecciones.get(secc.getId());
            logger.debug("\tanalizando anulacion de la sección {}", secc.getCodigo());
            visor.agregarLog("seccBD", "revisarSecc", "revisando seccion " + secc.getCodigo(), false, "info");
            if (seccion == null) {
                secc.setEstado(EstadoEnum.INA.name());
                seccionDAO.update(secc);
                visor.agregarLog("seccBD", "revisarSecc", "Seccion " + secc.getCodigo() + " queda Inactiva", true, "info");
            } else {
                visor.agregarLog("seccBD", "revisarSecc", "Seccion " + secc.getCodigo() + " se queda ACT", true, "info");
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
            List<AlumnoBlocked> bloks = new ArrayList();
            bloks.addAll(mapBloqueados.values());
            System.out.println("tenemos " + bloks.size() + " bloqueados");
            for (AlumnoBlocked blok : bloks) {
                System.out.println("blockkkk :: " + blok.getAlumno().getCodigo());
                mapBloqueados.remove(blok.getAlumno().getCodigo(), blok);
            }
            System.out.println("======================");
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
