package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_EPG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_PRE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_VER;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
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
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;

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
    RestriccionCarreraDAO restriccionCarreraDAO;
    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;
    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;
    @Autowired
    HorarioAulaDAO horarioAulaDAO;
    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;
    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;
    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    @Autowired
    LoadDataMatriculadoService loadDataMatriculadoService;
    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

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

        String kk = "miranda/ubaldo/alfides-teodoro";

        TipoDocIdentidad tipoDoc = persona.getTipoDocumento();
        if (tipoDoc != null && !StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
            if (persona.getKey().equals(kk)) {
                System.out.println("  " + persona.getIdentificacion());
            }
            Persona tempo = mapDNIPersonas.get(persona.getIdentificacion());

            if (tempo == null) {
                List<Persona> tempos = mapKeyPersonas.get(persona.getKey());
                if (tempos != null && !tempos.isEmpty()) {
                    Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
                    copiarDatosPersonales(perzoma, persona);
                    perzoma.setTipoDocumento(persona.getTipoDocumento());
                    perzoma.setNumeroDocIdentidad(persona.getNumeroDocIdentidad());
                    personaDAO.update(perzoma);
                    System.out.println("return perzoma 111 " + perzoma.getId());
                    return perzoma;
                }
            }

            if (tempo == null) {
                persona.setUserRegistro(ds.getUsuario());
                persona.setFechaRegistro(new Date());
                persona.setEstadoEnum(PersonaEstadoEnum.ACT);
                personaDAO.save(persona);
                System.out.println("save perzoma 444 " + persona.getId());

                mapDNIPersonas.put(persona.getIdentificacion(), persona);
            }

            Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            if (persona.getKey().equals(kk)) {
                ObjectUtil.printAttr(persona);
            }
            copiarDatosPersonales(perzoma, persona);
            if (persona.getKey().equals(kk)) {
                ObjectUtil.printAttr(persona);
            }
            personaDAO.update(perzoma);
            System.out.println("return perzoma 222 " + perzoma.getId());
            return perzoma;
        }

        Persona perzoma = revisarPersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
        copiarDatosPersonales(perzoma, persona);
        personaDAO.update(perzoma);
        System.out.println("return perzoma 333 " + perzoma.getId());
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
            Map<String, SituacionAcademica> mapSituaciones,
            Map<String, Carrera> mapCarreras,
            Map<String, CicloAcademico> mapCiclos, DataSessionPivot ds) {

        Persona persona = mapIdPersonas.get(alumno.getPersona().getId());
        if (StringUtils.isEmpty(persona.getEmailCompania())) {
            persona.setEmailCompania(alumno.getEmail());
            personaDAO.update(persona);
        }

//        List<Carrera> carreras = carreraDAO.all();
//        Map<String, Carrera> mapCarreras = TypesUtil.convertListToMap("codigo", carreras);
//        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
//        Map<String, CicloAcademico> mapCiclos = TypesUtil.convertListToMap("codigoAntiguo", ciclos);
        Alumno alu = mapAlumnos.get(alumno.getCodigo());
        String codigoCarrera = StringUtils.isEmpty(alumno.getCodigoPostgrado()) ? alumno.getCodigoEspecialidad() : alumno.getCodigoPostgrado();
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
            if (cicloInicio != null) {
                alu.setCicloIngreso(cicloInicio);
            }
            alumnoDAO.update(alu);

            logger.debug("\tActualizando alumno {}", alumno.getCodigo());
            visor.agregarLog("alu", "saveAlumno", "Alumno " + alumno.getCodigo() + " ya existe, se actualizo", true, "info");

        } else {

            if (cicloInicio == null) {
                int year = Integer.valueOf(alumno.getCodigo().substring(0, 4));
                int nroMatricula = Integer.valueOf(alumno.getCodigo().substring(4, 8));
                if (year >= 1990) {
                    CicloAcademico ciclo1 = mapCiclos.get(year + "1");
                    CicloAcademico ciclo2 = mapCiclos.get(year + "2");
                    if (ciclo2 == null) {
                        cicloInicio = ciclo1;
                    } else if (ciclo2.getMatriculaSiguiente() == null) {
                        cicloInicio = ciclo1;
                    } else if (nroMatricula < ciclo2.getMatriculaInicio()) {
                        cicloInicio = ciclo1;
                    } else {
                        cicloInicio = ciclo2;
                    }
                    logger.debug("\tCalculando ciclo-ingreso del alumno {} en {}", alumno.getCodigo(), cicloInicio.getDescripcion());
                } else {
                    logger.debug("\tNo se calcula el ciclo-ingreso del alumno {}", alumno.getCodigo());
                }
            }

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
            logger.debug("\tCreando nuevo alumno {}", alumno.getCodigo());
            visor.agregarLog("alu", "saveAlumno", "Alumno " + alumno.getCodigo() + " nuevo", true, "info");

            saveUsuario(persona, RolEnum.ALU, ds);

            if (cicloInicio != null) {
                int nroMatricula = Integer.valueOf(alumno.getCodigo().substring(4, 8));
                if (cicloInicio.getMatriculaSiguiente() <= nroMatricula) {
                    cicloInicio.setMatriculaSiguiente(nroMatricula + 1);
                }
            }
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

            visor.agregarLog("doc", "saveDocente", "Profesor " + profeBD.getCodigo() + " es nuevo", true, "info");

        } else if (profeBD.getEstadoEnum() != DocenteEstadoEnum.ACT) {
            profeBD.setEstado(DocenteEstadoEnum.ACT);
            profeBD.setFechaModifica(new Date());
            profeBD.setUserModifica(ds.getUsuario());
            docenteDAO.update(profeBD);

            visor.agregarLog("doc", "saveDocente", "Profesor " + profeBD.getCodigo() + " ya existe, se actualizo", true, "info");
        } else {
            visor.agregarLog("doc", "saveDocente", "Profesor " + profeBD.getCodigo() + " no necesita ser actualizacion", true, "info");
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

            List<Postulante> postulantes = postulanteDAO.allByPersona(persona);
            for (Postulante postulante : postulantes) {
                postulante.setPersona(main);
                postulanteDAO.update(postulante);
            }

            List<Alumno> alumnos = alumnoDAO.allByPersona(persona);
            for (Alumno alumno : alumnos) {
                alumno.setPersona(main);
                alumnoDAO.update(alumno);
            }

            List<Docente> docentes = docenteDAO.allByPersona(persona);
            for (Docente docente : docentes) {
                docente.setPersona(main);
                docenteDAO.update(docente);
            }

            List<PersonaCargo> persoPerfiles = personaCargoDAO.allByPersona(persona);
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
                usuario.setPersona(null);
                usuarioDAO.update(usuario);
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
    public void revisionPreviaGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo) {
        List<GrupoSeccion> gpoSeccionesBD = grupoSeccionDAO.allByCiclo(ciclo);
        Map<String, GrupoSeccion> mapGpoSeccionBD = TypesUtil.convertListToMap("codigo", gpoSeccionesBD);

        List<Curso> cursosBD = cursoDAO.all();
        Map<String, Curso> mapCursoBD = TypesUtil.convertListToMap("codigo", cursosBD);

        List<GrupoSeccion> gposSeccionesUnused = grupoSeccionDAO.allUnusedByCiclo(ciclo);

        for (GrupoSeccion gpoSecc : gruposSecciones) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            GrupoSeccion gpoSeccBD = mapGpoSeccionBD.get(gpoSecc.getCodigo());
            if (gpoSeccBD == null) {
                visor.agregarLog("gpoSecc", "revisionGpoSecc", "No es necesario revisar " + gpoSecc.getCodigo() + " porque es es nuevo", true, "info");
                continue;
            }

            Curso curso = mapCursoBD.get(gpoSecc.getCodigoCurso());
            Curso cursoBD = gpoSeccBD.getCurso();
            if (curso.getId() == cursoBD.getId().longValue()) {
                visor.agregarLog("gpoSecc", "revisionGpoSecc", "No es necesario revisar " + gpoSecc.getCodigo() + " porque datos son iguales", true, "info");
                continue;
            }

            List<Seccion> seccionesBD = seccionDAO.allByGposSeccion(gpoSeccBD);
            for (Seccion seccion : seccionesBD) {
                List<MatriculaSeccion> alumnosSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
                if (alumnosSeccion.size() > 0) {
                    visor.agregarLog("gpoSecc", "revisionGpoSecc", "El curso del gpo-Seccion " + gpoSeccBD.getCodigo()
                            + " está relacionado al curso " + curso.getCodigo() + " pero en la base de datos es " + cursoBD.getCodigo(),
                            false, "error-proceso");
                    String msg = String.format("El curso del grupo-seccion %s está relacionado al curso %s pero en la base de datos es %s",
                            gpoSecc.getCodigo(), curso.getCodigo(), cursoBD.getCodigo());
                    throw new PhobosException(msg);
                }
            }

            String codGpoSecc = getCodeGpoSeccFree(gposSeccionesUnused);
            String codGpoAntes = gpoSeccBD.getCodigo();
            gpoSeccBD.setCodigo(codGpoSecc);
            grupoSeccionDAO.update(gpoSeccBD);

            int loopSecc = 0;
            for (Seccion seccion : seccionesBD) {
                seccion.setCodigo(codGpoSecc + loopSecc);
                seccion.setCodigo2(codGpoSecc + loopSecc);
                seccionDAO.update(seccion);
                loopSecc++;
            }

            gposSeccionesUnused.add(gpoSeccBD);

            visor.agregarLog("gpoSecc", "revisionGpoSecc", "El codigo del gpo-Seccion " + codGpoAntes
                    + " fue cambiado al UNUSED " + codGpoSecc + " porque el archivo tiene al curso " + curso.getCodigo()
                    + " pero en la base de datos es " + cursoBD.getCodigo(),
                    true, "info");

        }
    }

    private String getCodeGpoSeccFree(List<GrupoSeccion> gposSeccionesUnused) {
        Map<String, GrupoSeccion> mapGpoSecc = TypesUtil.convertListToMap("codigo", gposSeccionesUnused);
        for (int i = 0; i < 100; i++) {
            String cod = "Y" + NumberFormat.codigo(i, 2);
            GrupoSeccion gpoSecc = mapGpoSecc.get(cod);
            if (gpoSecc == null) {
                return cod;
            }
        }
        return "Y00";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo, DataSessionPivot ds) {

        List<GrupoSeccion> gpoSeccionesBD = grupoSeccionDAO.allByCiclo(ciclo);
        Map<String, GrupoSeccion> mapGpoSeccionBD = TypesUtil.convertListToMap("codigo", gpoSeccionesBD);

        List<AnexoBoletin> anexosBD = anexoBoletinDAO.all();
        Map<String, GrupoSeccion> mapGpoSecciones = new LinkedHashMap();
        Map<String, AnexoBoletin> mapAnexos = TypesUtil.convertListToMap("codigo", anexosBD);

        List<Curso> cursosBD = cursoDAO.all();
        Map<String, Curso> mapCursoBD = TypesUtil.convertListToMap("codigo", cursosBD);

//        List<Seccion> secciones = seccionDAO.allByGposSeccion(gpoSeccionesBD);
//        logger.debug("Size secciones {}", secciones.size());
        Map<String, String> mapCurso1SinModal = new LinkedHashMap();
        Map<String, String> mapCurso2SinModal = new LinkedHashMap();
        for (GrupoSeccion gpoSecc : gruposSecciones) {
            Curso curso1 = mapCursoBD.get(gpoSecc.getCodigoCurso());
            if (mapCurso1SinModal.get(curso1.getCodigo()) == null) {
                ModalidadEstudio modalidad1 = curso1.getModalidadEstudio();
                if (modalidad1 == null) {
                    logger.debug("El curso1 " + curso1.getCodigo() + " no tiene modalidad-estudio");
                    visor.agregarLog("gpoSecc", "saveGpoSecc", "El curso1 " + curso1.getCodigo() + " no tiene modalidad-estudio", true, "error");
                }
                mapCurso1SinModal.put(curso1.getCodigo(), curso1.getCodigo());
            }

            GrupoSeccion gpoSeccBD = mapGpoSeccionBD.get(gpoSecc.getCodigo());
            if (gpoSeccBD != null) {
                Curso curso2 = gpoSeccBD.getCurso();
                if (mapCurso2SinModal.get(curso2.getCodigo()) == null) {
                    ModalidadEstudio modalidad2 = curso2.getModalidadEstudio();
                    if (modalidad2 == null) {
                        logger.debug("El curso2 " + curso2.getCodigo() + " no tiene modalidad-estudio");
                        visor.agregarLog("gpoSecc", "saveGpoSecc", "El curso2 " + curso2.getCodigo() + " no tiene modalidad-estudio", true, "error");
                    }
                    mapCurso2SinModal.put(curso2.getCodigo(), curso2.getCodigo());
                }
            }

        }

        for (GrupoSeccion gpoSecc : gruposSecciones) {
            Curso curso1 = mapCursoBD.get(gpoSecc.getCodigoCurso());
            ModalidadEstudio modalidad1 = curso1.getModalidadEstudio();
            if (modalidad1 == null) {
                throw new PhobosException("El curso10 " + curso1.getCodigo() + " no tiene modalidad-estudio");
            }

            GrupoSeccion gpoSeccBD = mapGpoSeccionBD.get(gpoSecc.getCodigo());
            if (gpoSeccBD != null) {
                Curso curso2 = gpoSeccBD.getCurso();
                ModalidadEstudio modalidad2 = curso2.getModalidadEstudio();
                if (modalidad2 == null) {
                    throw new PhobosException("El curso20 " + curso2.getCodigo() + " no tiene modalidad-estudio");
                }
            }
        }

        int loop = 0;
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
                gpoSeccBD.setEstadoEnum(SeccionEstadoEnum.ACT);
                gpoSeccBD.setCursoDirigido(gpoSecc.getCursoDirigido());
                gpoSeccBD.setAnexoBoletin(anexo);
                gpoSeccBD.setTipoDictadoEnum(TipoDictadoGrupoSeccionEnum.SEM);

                grupoSeccionDAO.save(gpoSeccBD);
                mapGpoSeccionBD.put(gpoSeccBD.getCodigo(), gpoSeccBD);
                visor.agregarLog("gpoSecc", "saveGpoSecc", "Gpo-Seccion " + gpoSeccBD.getCodigo() + " nuevo", true, "info");

            } else {
                gpoSeccBD.setVersion(gpoSeccBD.getVersion() == null ? "1" : gpoSeccBD.getVersion());
                gpoSeccBD.setEstadoPlanEnum(gpoSeccBD.getEstadoPlan() == null ? EstadoPlanCalificaEnum.PEND : gpoSeccBD.getEstadoPlanEnum());
                gpoSeccBD.setEstadoGrupo(gpoSeccBD.getEstadoGrupo() == null ? EstadoGrupoSeccionEnum.ABI.name() : gpoSeccBD.getEstadoGrupo());
                gpoSeccBD.setEstadoEnum(SeccionEstadoEnum.ACT);
                gpoSeccBD.setCursoDirigido(gpoSecc.getCursoDirigido());
                gpoSeccBD.setAnexoBoletin(anexo);
                if (gpoSeccBD.getEstadoGrupoEnum() == EstadoGrupoSeccionEnum.CER && gpoSeccBD.getUsuarioCierraActa() == null) {
                    gpoSeccBD.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
                    gpoSeccBD.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
                    gpoSeccBD.setFechaCierreActa(null);
                }
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

            if (ciclo.getTipoEnum() == TipoCicloEnum.NIV) {
                List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByTipoCursoCurriculaEnum(TipoCursoCurriculaEnum.GEN);
                TipoCursoCurricula tipocursogeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
                TipoCursoCurricula tipocursoobligatorio = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.OBL);
                Map<Long, CursoCurricula> curCurriculaMap = TypesUtil.convertListToMap("curso.id", cursosCurricula);
                List<PrecioCursoEstructura> precioCursoEstructura = precioCursoEstructuraDAO.allByCiclo(ciclo);
                List<CursoCicloAcademico> cursoCicloAcademico = cursoCicloAcademicoDAO.allByCiclo(ciclo);
                Set<String> tpcs = precioCursoEstructura.stream().map(PrecioCursoEstructura::getTpc).collect(Collectors.toSet());
                if (curso.getTpc() != null && !tpcs.contains(curso.getTpc())) {
                    tpcs.add(curso.getTpc());

                    PrecioCursoEstructura pce = new PrecioCursoEstructura();
                    pce.setCicloAcademico(ciclo);
                    pce.setFechaPrecio(new Date());
                    pce.setPrecio(BigDecimal.ZERO);
                    pce.setTpc(curso.getTpc());
                    pce.setCreditos(curso.getCreditos());
                    pce.setUserPrecio(ds.getUsuario());
                    pce.setEstado(EstadoEnum.ACT.name());

                    precioCursoEstructuraDAO.save(pce);
                }
                Set<Curso> cursos = cursoCicloAcademico.stream().map(CursoCicloAcademico::getCurso).collect(Collectors.toSet());
                int factorHoras = 3;
                int horasTeoria = curso.getHorasTeoria() * factorHoras;
                int horasPractica = curso.getHorasPractica() * factorHoras;

                if (!cursos.contains(curso)) {
                    cursos.add(curso);

                    CursoCicloAcademico cca = new CursoCicloAcademico();
                    cca.setCicloAcademico(ciclo);
                    cca.setPrecio(BigDecimal.ZERO);
                    cca.setPrecioAdicional(BigDecimal.ZERO);
                    cca.setEstado(EstadoEnum.ACT.name());

                    cca.setHorasSemanalesTeoria(horasTeoria);
                    cca.setHorasSemanalesPractica(horasPractica);
                    cca.setCurso(curso);
                    cca.setMinimoAlumnos(BigDecimal.ZERO);

                    cca.setTipoCursoCurricula(tipocursoobligatorio);
                    if (curCurriculaMap.get(curso.getId()) != null) {
                        cca.setTipoCursoCurricula(tipocursogeneral);
                    }
                    cursoCicloAcademicoDAO.save(cca);
                }
            }
        }

        return mapGpoSecciones;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones, DataSessionPivot ds) {

        Map<String, List<Seccion>> mapSeccionesPCUR = new LinkedHashMap();
        Map<String, Seccion> mapSeccionesTCUR = new LinkedHashMap();

        List<Aula> aulasBD = aulaDAO.all();
        Map<String, Aula> mapAulaBD = TypesUtil.convertListToMap("codigo", aulasBD);
        Map<String, String> mapAulaNoExiste = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            String codigo = seccion.getCodigoAula();
            if (StringUtils.isEmpty(codigo)) {
                continue;
            }
            Aula aula = mapAulaBD.get(codigo);
            if (aula == null) {
                if (mapAulaNoExiste.get(codigo) == null) {
                    logger.debug("No existe el aula [" + codigo + "] en la base de datos");
                    visor.agregarLog("secc", "saveSecc", "No existe el aula [" + codigo + "] en la base de datos", false, "error");
                    mapAulaNoExiste.put(codigo, codigo);
                }
            }
        }

        List<GrupoHoras> gpoHorasBD = grupoHorasDAO.all();
        Map<String, GrupoHoras> mapGpoHoraBD = TypesUtil.convertListToMap("codigo", gpoHorasBD);
        Map<String, String> mapGpoNoExiste = new LinkedHashMap();
        for (Seccion seccion : secciones) {
            String codigo = seccion.getCodigoGrupoHorario();
            if (StringUtils.isEmpty(codigo)) {
                continue;
            }

            GrupoHoras gpoHoras = mapGpoHoraBD.get(codigo);
            if (gpoHoras == null) {
                if (mapGpoNoExiste.get(codigo) == null) {
                    logger.debug("No existe el grupo-horas [" + codigo + "] en la base de datos");
                    visor.agregarLog("secc", "saveSecc", "No existe el grupo-horas [" + codigo + "] en la base de datos", false, "error");
                    mapGpoNoExiste.put(codigo, codigo);
                }
            }
        }

        for (Seccion seccion : secciones) {
            Aula aula = findAula(seccion, mapAulaBD, ciclo);
            GrupoHoras gpoHoras = findGrupoHoras(seccion, mapGpoHoraBD);
        }

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

        int loop = 0;
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
            Aula aula = findAula(seccion, mapAulaBD, ciclo);

            Integer horasTeoria = curso.getHorasTeoria() == null ? 0
                    : (ciclo.getTipoEnum() == TipoCicloEnum.REG ? curso.getHorasTeoria() : curso.getHorasTeoriaVerano());
            Integer horasPractica = curso.getHorasPractica() == null ? 0
                    : (ciclo.getTipoEnum() == TipoCicloEnum.REG ? curso.getHorasPractica() : curso.getHorasPracticaVerano());

            if (seccionBD == null) {
                seccionBD = new Seccion();
                seccionBD.setCodigo(seccion.getCodigo());
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setGrupoSeccion(gpoSecc);
                seccionBD.setVacantes(seccion.getVacantes());
                seccionBD.setMatriculados(seccion.getMatriculados());
                seccionBD.setRestriccionCapa(seccion.getRestriccionCapa());
                seccionBD.setAmpliacionVacante(0);
                seccionBD.setRetirados(0);
                seccionBD.setReservados(0);
                seccionBD.setPrematriculados(0);
                seccionBD.setEsPrincipal(0);
                seccionBD.setTipoSeccionEnum(TipoSeccionEnum.valueOf(seccion.getCodigoTipoSeccion()));
                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);

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

                seccionBD.setGrupoHoras(gpoHoras);
                seccionBD.setAula(aula);
                seccionBD.setCodigo2(seccion.getCodigo2());
                seccionBD.setEstado(EstadoEnum.ACT.name());
                seccionBD.setVacantes(seccion.getVacantes());
                seccionBD.setMatriculados(seccion.getMatriculados());
                seccionBD.setRestriccionCapa(seccion.getRestriccionCapa());
                seccionBD.setRetirados(0);
                seccionBD.setReservados(0);
                seccionBD.setPrematriculados(0);
                seccionDAO.update(seccionBD);
                visor.agregarLog("secc", "saveSecc", "Seccion " + seccionBD.getCodigo() + " ya existe, se actualizó datos", false, "info");
            }

            List<RestriccionCarrera> restriccionCarr = seccion.getRestriccionesCarrera();
            List<RestriccionCarrera> restriccionCarrBD = seccionBD.getRestriccionesCarrera();
            ListsInspector inspector = TypesUtil.analizeLists(restriccionCarrBD, restriccionCarr, "carrera.codigo");

            List<RestriccionCarrera> resCarrDead = inspector.getDeadList();
            List<RestriccionCarrera> resCarrNew = inspector.getNewList();
            for (RestriccionCarrera restriccionCarrera : resCarrDead) {
                restriccionCarreraDAO.delete(restriccionCarrera);
            }
            for (RestriccionCarrera restriccionCarrera : resCarrNew) {
                ObjectUtil.printAttr(restriccionCarrera);
                restriccionCarrera.setEstadoEnum(EstadoEnum.ACT);
                restriccionCarrera.setFechaRegistro(new Date());
                restriccionCarrera.setUsuarioRegistro(ds.getUsuario());
                restriccionCarrera.setSeccion(seccionBD);
                restriccionCarreraDAO.save(restriccionCarrera);
            }

            List<RestriccionFacultad> restriccionFac = seccion.getRestriccionesFacultad();
            List<RestriccionFacultad> restriccionFacBD = seccionBD.getRestriccionesFacultad();
            ListsInspector inspectorFacultad = TypesUtil.analizeLists(restriccionFacBD, restriccionFac, "facultad.codigo");

            List<RestriccionFacultad> resFacDead = inspectorFacultad.getDeadList();
            List<RestriccionFacultad> resFacNew = inspectorFacultad.getNewList();
            for (RestriccionFacultad restriccionFacultad : resFacDead) {
                restriccionFacultadDAO.delete(restriccionFacultad);
            }
            for (RestriccionFacultad restriccionFacultad : resFacNew) {
                restriccionFacultad.setEstadoEnum(EstadoEnum.ACT);
                restriccionFacultad.setFechaRegistro(new Date());
                restriccionFacultad.setUsuarioRegistro(ds.getUsuario());
                restriccionFacultad.setSeccion(seccionBD);
                restriccionFacultadDAO.save(restriccionFacultad);
            }

            List<RestriccionRepitencia> restriccionRep = seccion.getRestriccionesRepitencia();
            List<RestriccionRepitencia> restriccionRepBD = seccionBD.getRestriccionesRepitencia();
            ListsInspector inspectorRepitencia = TypesUtil.analizeLists(restriccionRepBD, restriccionRep, "tipoRepitencia.codigo");

            List<RestriccionRepitencia> resRepDead = inspectorRepitencia.getDeadList();
            List<RestriccionRepitencia> resRepNew = inspectorRepitencia.getNewList();
            for (RestriccionRepitencia restriccionRepitencia : resRepDead) {
                restriccionRepitenciaDAO.delete(restriccionRepitencia);
            }
            for (RestriccionRepitencia restriccionRepitencia : resRepNew) {
                restriccionRepitencia.setEstadoEnum(EstadoEnum.ACT);
                restriccionRepitencia.setFechaRegistro(new Date());
                restriccionRepitencia.setUsuarioRegistro(ds.getUsuario());
                restriccionRepitencia.setSeccion(seccionBD);
                restriccionRepitenciaDAO.save(restriccionRepitencia);
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
            gpoSecc.setCodigo2(seccion.getCodigo2().substring(0, 3));
            grupoSeccionDAO.update(gpoSecc);

            seccionBD.setDocenteSeccion(new ArrayList());
            seccionBD.setMatriculaSeccion(new ArrayList());
            secciones.set(loop, seccionBD);
            mapSecciones.put(seccionBD.getCodigo(), seccionBD);
            loop++;
            visor.agregarLog("secc", "saveSecc", "Seccion " + seccionBD.getCodigo() + " procesada", true, "info");
            logger.debug("\t\tSeccion {} procesada {} de {}", seccionBD.getCodigo(), loop, secciones.size());

        }

        for (Map.Entry<String, Seccion> entry : mapSeccionesTCurBD.entrySet()) {
            String gpoSeccCode = entry.getKey();
            Seccion seccionTCUR = entry.getValue();
            List<Seccion> seccionesPCUR = mapSeccionesPCurBD.get(gpoSeccCode);
            if (seccionesPCUR == null) {
                System.out.println("No hay secciones PCUR para el TCUR " + seccionTCUR.getCodigo());
                System.out.println("No hay secciones PCUR para el TCUR " + seccionTCUR.getCodigo());
                System.out.println("No hay secciones PCUR para el TCUR " + seccionTCUR.getCodigo());
                System.out.println("No hay secciones PCUR para el TCUR " + seccionTCUR.getCodigo());
            }
            seccionesPCUR = (seccionesPCUR == null) ? new ArrayList() : seccionesPCUR;
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

        GrupoHoras gpoHoras = mapGpoHoraBD.get(codigo);
        if (gpoHoras == null) {
            String msg = String.format("El grupo-horas %s de la seccion %s no existe en la base de datos",
                    codigo, seccion.getCodigo());
            throw new PhobosException(msg);
        }
        return gpoHoras;
    }

    private Aula findAula(Seccion seccion, Map<String, Aula> mapAulaBD, CicloAcademico ciclo) {
        String codigo = seccion.getCodigoAula();
        if (StringUtils.isEmpty(codigo)) {
            return null;
        }

        Aula aula = mapAulaBD.get(codigo);
        if (aula == null && ciclo.getCodigo().compareTo("201710") >= 0) {
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

        Map<Long, EventoCicloAcademico> mapEvento = new HashMap();
        List<ModalidadEstudio> modalidadesEtudio = modalidadEstudioDAO.allByCodigos(Arrays.asList(ModalidadEstudioEnum.PRE.name(), ModalidadEstudioEnum.EPG.name()));
        for (ModalidadEstudio modalidad : modalidadesEtudio) {
            EventoCicloAcademico eventoDictadoClases = this.getEventoClases(ciclo, modalidad);
            if (eventoDictadoClases == null) {
                visor.agregarLog("horSecc", "saveHorSecc", "No está configurado el Dictado de Clases para la modalidad " + modalidad.getNombre(), false, "error");
                throw new PhobosException("No está configurado el Dictado de Clases para la modalidad " + modalidad.getNombre());
            }
            mapEvento.put(modalidad.getId(), eventoDictadoClases);
        }

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
            System.out.println(seccion.getId() + ":::" + seccion.getCodigo() + ":::" + seccion.getCodigo2());

            GrupoSeccion gpoSeccion = seccion.getGrupoSeccion();
            ModalidadEstudio modalidadCurso = gpoSeccion.getCurso().getModalidadEstudio();
            EventoCicloAcademico eventoClases = mapEvento.get(modalidadCurso.getId());

            DocenteSeccion profeSeccBD = mapDocenteSeccionBD.get(profeSecc.getCodigoDocente() + "-" + profeSecc.getCodigoSeccion());

            if (profeSeccBD == null) {
                profeSeccBD = new DocenteSeccion();
                profeSeccBD.setDocente(profe);
                profeSeccBD.setSeccion(seccion);
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                profeSeccBD.setPorcentajeCarga(profeSecc.getPorcentajeCarga());

                if (gpoSeccion.getFechaInicioModular() != null) {
                    profeSeccBD.setFechaInicio(gpoSeccion.getFechaInicioModular());
                    profeSeccBD.setFechaFin(gpoSeccion.getFechaFinModular());
                } else {
                    profeSeccBD.setFechaInicio(eventoClases.getFechaInicio());
                    profeSeccBD.setFechaFin(eventoClases.getFechaFin());
                }

                docenteSeccionDAO.save(profeSeccBD);
                visor.agregarLog("docSecc", "saveDocSecc", "Docente-Seccion " + profe.getCodigo() + "-" + seccion.getCodigo() + " nuevo", true, "info");

            } else {
                profeSeccBD.setPrincipal(profeSecc.getPrincipal() == null ? 0 : profeSecc.getPrincipal());
                profeSeccBD.setEstado(EstadoEnum.ACT.name());
                profeSeccBD.setUserAnulacion(null);
                profeSeccBD.setFechaAnulacion(null);
                profeSeccBD.setPorcentajeCarga(profeSecc.getPorcentajeCarga());

                if (profeSeccBD.getFechaInicio() == null) {
                    if (gpoSeccion.getFechaInicioModular() != null) {
                        profeSeccBD.setFechaInicio(gpoSeccion.getFechaInicioModular());
                        profeSeccBD.setFechaFin(gpoSeccion.getFechaFinModular());
                    } else {
                        profeSeccBD.setFechaInicio(eventoClases.getFechaInicio());
                        profeSeccBD.setFechaFin(eventoClases.getFechaFin());
                    }
                }

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
            profeSeccBD.setPorcentajeCarga(null);
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
            //alumnoDAO.updateInactivar(alumno);
            System.out.println("\talumno 222 " + alumno.getCodigo() + " desbloqueado en XYZ-loadDataMatriculados");
            visor.removeAlumno(alumno, matriSecc.getCodigoSeccion());

        } catch (Exception e) {
            visor.agregarLog("aluSecc", "saveAluSecc", "Alumno-Seccion " + matriSecc.getCodigoAlumno() + "-" + matriSecc.getCodigoSeccion()
                    + " produjo error: " + e.getLocalizedMessage(),
                    false, "error-proceso");
            e.printStackTrace();
        }

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
                secc.setEstadoEnum(SeccionEstadoEnum.INA);
                secc.setCodigo2(secc.getCodigo());
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
        Map<Long, GrupoSeccion> mapGrupoSecciones = TypesUtil.convertListToMap("id", gruposSecciones);
        List<GrupoSeccion> grupoSeccionesDB = grupoSeccionDAO.allByCiclo(ciclo);
        List<Seccion> seccionesDB = seccionDAO.allByGposSeccion(grupoSeccionesDB);
        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", seccionesDB);
        visor.inicializar("gpoSeccBD", grupoSeccionesDB.size());

        for (GrupoSeccion gpoSecc : grupoSeccionesDB) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            GrupoSeccion grupoSeccion = mapGrupoSecciones.get(gpoSecc.getId());
            logger.debug("\tanalizando anulacion de la sección {}", gpoSecc.getCodigo());
            visor.agregarLog("gpoSeccBD", "revisarGpoSecc", "revisando gpoSeccion " + gpoSecc.getCodigo(), false, "info");

            if (grupoSeccion == null) {
                String code2 = null;
                List<Seccion> seccionesGpoSecc = mapSecciones.get(gpoSecc.getId());
                if (seccionesGpoSecc != null) {
                    code2 = seccionesGpoSecc.get(0).getCodigo2().substring(0, 3);
                }

                gpoSecc.setCodigo2(code2);
                gpoSecc.setEstadoEnum(SeccionEstadoEnum.INA);
                gpoSecc.setEstadoPlanEnum(EstadoPlanCalificaEnum.CER);
                gpoSecc.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.CER);
                gpoSecc.setFechaCierreActa(new Date());
                gpoSecc.setVersion("0");
                grupoSeccionDAO.update(gpoSecc);
                visor.agregarLog("gpoSeccBD", "revisarGpoSecc", "GpoSeccion " + gpoSecc.getCodigo() + " queda Inactiva", true, "info");

            } else {
                visor.agregarLog("gpoSeccBD", "revisarGpoSecc", "GpoSeccion " + gpoSecc.getCodigo() + " se queda ACT", true, "info");
            }
        }
        logger.debug("\tRevision de gpoSecciones finalizada");

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void codigo2NullGpoSeccion(CicloAcademico ciclo) {
        grupoSeccionDAO.setCodigo2Null(ciclo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void codigo2NullSeccion(CicloAcademico ciclo) {
        seccionDAO.setCodigo2Null(ciclo);
    }

    @Override
    public void detenerRevisionBloqueado() {
        revisar = false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearCursos(
            String rutaFileCursos,
            Map<String, Curso> mapCursos,
            Map<String, DepartamentoAcademico> mapDepartamentosAcademicos,
            DataSessionPivot ds) {

        try {
            FileInputStream fis = new FileInputStream(rutaFileCursos);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            ModalidadEstudio pregrado = null;
            ModalidadEstudio postgrado = null;
            List<ModalidadEstudio> modalidades = modalidadEstudioDAO.all();
            for (ModalidadEstudio modalidad : modalidades) {
                if (modalidad.isPregrado()) {
                    pregrado = modalidad;
                }
                if (modalidad.isPostgrado()) {
                    postgrado = modalidad;
                }
            }

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String curCodigo = getCellStringValue(1, row);
                if (StringUtils.isEmpty(curCodigo)) {
                    break;
                }
            }
            visor.inicializar("cur", loop);

            loop = 0;
            rowIterator = mySheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                String curCodigo = getCellStringValue(1, row);
                String curNuevo = getCellStringValue(2, row);
                String nombre = getCellStringValue(3, row);
                String depCodigo = getCellStringValue(4, row);
                String curCredit = getCellStringValue(5, row);
                String curCrevar = getCellStringValue(6, row);
                String curTeoria = getCellStringValue(7, row);
                String curPracti = getCellStringValue(8, row);
                String tCurso = getCellStringValue(9, row);
                String tipo = getCellStringValue(10, row);

                Integer curCreditTeo = getCellIntegerValue(12, row);
                Integer curCreditPra = getCellIntegerValue(13, row);

                if (mapCursos.get(curNuevo) != null) {
                    visor.agregarLog("cur", "saveCursos", "Curso " + curNuevo + " ya existe", true, "info");
                    Curso cursoDb = mapCursos.get(curNuevo);
                    cursoDb.setCreditosTeoria(curCreditTeo);
                    cursoDb.setCreditosPractica(curCreditPra);
                    cursoDb.setDepartamentoAcademico(mapDepartamentosAcademicos.get(depCodigo));
                    cursoDAO.update(cursoDb);
                    continue;
                }

                Curso curso = new Curso();
                curso.setEstadoEnum(EstadoEnum.ACT);
                curso.setCodigo(curNuevo);
                curso.setNombre(nombre);
                if (!StringUtils.isEmpty(curCredit)) {
                    curso.setCreditos(Integer.parseInt(curCredit));
                    if (curso.getCreditos() > 0) {
                        curso.setTipoCreditoEnum(TipoCreditoEnum.FIJO);
                    }
                }
                curso.setDepartamentoAcademico(mapDepartamentosAcademicos.get(depCodigo));
                curso.setCodigoAnterior1(curCodigo);
                if (!StringUtils.isEmpty(curCrevar) && curCrevar.length() > 3) {
                    curso.setCreditosVariables(Integer.parseInt(curCrevar.substring(4)));
                    if (curso.getCreditosVariables() > 0) {
                        curso.setTipoCreditoEnum(TipoCreditoEnum.VAR);
                    }
                }
                if (!StringUtils.isEmpty(curTeoria)) {
                    curso.setHorasTeoria(Integer.parseInt(curTeoria));
                }
                if (!StringUtils.isEmpty(curPracti)) {
                    curso.setHorasPractica(Integer.parseInt(curPracti));
                }

                if (tCurso.compareTo("TT") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.TEO.name());
                } else if (tCurso.compareTo("TP") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.TEOPRA.name());
                } else if (tCurso.compareTo("PP") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.PRA.name());
                }

                int nivel = Integer.valueOf(curso.getCodigo().substring(2, 3));
                nivel = nivel == 0 ? 1 : nivel;
                curso.setNivel(nivel);
                if (nivel < 7) {
                    curso.setModalidadEstudio(pregrado);
                } else {
                    curso.setModalidadEstudio(postgrado);
                }

                curso.setUserRegsitro(ds.getUsuario());
                curso.setFechaRegistro(new Date());
                cursoDAO.save(curso);
                mapCursos.put(curso.getCodigo(), curso);
                visor.agregarLog("cur", "saveCursos", "Curso " + curNuevo + " nuevo guardado", true, "info");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<HorarioSeccion> crearHorarioSecciones(String rutaFile,
            Map<String, Seccion> mapSecciones,
            Map<Integer, Dia> mapDias,
            Map<Integer, Hora> mapHoras,
            Map<String, Aula> mapAulas,
            CicloAcademico cicloAcademico) {

        List<HorarioSeccion> horarios = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Map<String, List<HorarioSeccion>> mapSeccHorarios = new LinkedHashMap();
            Map<String, List<HorarioAula>> mapAulaHorarios = new LinkedHashMap();

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String clave = getCellStringValue(2, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);
                String aulaCod = getCellStringValue(6, row);

                if (StringUtils.isEmpty(clave)) {
                    break;
                }

                if (StringUtils.isEmpty(diaNum)) {
                    continue;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                Seccion seccion = mapSecciones.get(clave);
                Dia dia = mapDias.get(Integer.parseInt(diaNum));
                Hora hora = mapHoras.get(Integer.parseInt(horaNum));
                Aula aula = mapAulas.get(aulaCod);
                if (aula == null && !StringUtils.isEmpty(aulaCod) && cicloAcademico.getCodigo().compareTo("201710") >= 0) {
                    throw new PhobosException("Aula " + aulaCod + " no se halló en la base de datos");
                }

                List<HorarioSeccion> horarioSecc = mapSeccHorarios.get(clave);
                if (horarioSecc == null) {
                    horarioSecc = new ArrayList();
                    mapSeccHorarios.put(clave, horarioSecc);
                }

                HorarioSeccion horario = new HorarioSeccion(seccion, dia, hora, aula);
                horarioSecc.add(horario);

                if (aula != null) {
                    List<HorarioAula> horariosAula = mapAulaHorarios.get(clave);
                    if (horariosAula == null) {
                        horariosAula = new ArrayList();
                        mapAulaHorarios.put(clave, horariosAula);
                    }

                    HorarioAula horarioAula = new HorarioAula(seccion, dia, hora, aula);
                    horariosAula.add(horarioAula);
                }

            }

            visor.inicializar("horSecc", mapSeccHorarios.size());
            visor.inicializar("horAula", mapAulaHorarios.size());

            logger.debug("{} horarioSeccion leídos", mapSeccHorarios.size());
            logger.debug("{} horarioAula leídos", mapAulaHorarios.size());

            List<HorarioSeccion> horarioSeccCicloBD = horarioSeccionDAO.allByCiclo(cicloAcademico);
            List<HorarioAula> horarioAulaCicloBD = horarioAulaDAO.allByCiclo(cicloAcademico);

            Map<Long, List<HorarioSeccion>> mapHorariosSecciones = TypesUtil.convertListToMapList("seccion.id", horarioSeccCicloBD);
            Map<Long, List<HorarioAula>> mapHorariosAulas = TypesUtil.convertListToMapList("seccion.id", horarioAulaCicloBD);

            Map<Long, EventoCicloAcademico> mapEvento = new HashMap();
            List<ModalidadEstudio> modalidadesEtudio = modalidadEstudioDAO.allByCodigos(Arrays.asList(ModalidadEstudioEnum.PRE.name(), ModalidadEstudioEnum.EPG.name()));
            for (ModalidadEstudio modalidad : modalidadesEtudio) {
                EventoCicloAcademico eventoDictadoClases = this.getEventoClases(cicloAcademico, modalidad);
                if (eventoDictadoClases == null) {
                    visor.agregarLog("horSecc", "saveHorSecc", "No está configurado el Dictado de Clases para la modalidad " + modalidad.getNombre(), false, "error");
                    throw new PhobosException("No está configurado el Dictado de Clases para la modalidad " + modalidad.getNombre());
                }
                mapEvento.put(modalidad.getId(), eventoDictadoClases);
            }

            for (Map.Entry<String, List<HorarioSeccion>> entry : mapSeccHorarios.entrySet()) {
                String clave = entry.getKey();
                logger.debug("clave {} de horarioSeccion", clave);
                Seccion seccion = mapSecciones.get(clave);
                if (seccion == null) {
                    visor.agregarLog("horSecc", "saveHorSecc", "Horario-seccion no se puede grabar para seccion " + clave + " no existente", false, "error");
                    logger.debug("\tNo existe PTM!!!!");
                }

                ModalidadEstudio modalidadCurso = seccion.getGrupoSeccion().getCurso().getModalidadEstudio();
                EventoCicloAcademico eventoDictadoClases = mapEvento.get(modalidadCurso.getId());

                List<HorarioSeccion> horarioSecc = entry.getValue();
                List<HorarioSeccion> horarioSeccBD = mapHorariosSecciones.get(seccion.getId());
                horarioSeccBD = (horarioSeccBD == null) ? new ArrayList() : horarioSeccBD;
                ListsInspector inspector = TypesUtil.analizeLists(horarioSeccBD, horarioSecc, "key");

                List<HorarioSeccion> nuevos = inspector.getNewList();
                List<HorarioSeccion> muertos = inspector.getDeadList();

                int contador = 0;
                for (HorarioSeccion nuevo : nuevos) {
                    logger.debug("\t ( {} / {} ) Agregando horario-seccion {} {} {}", contador++, nuevos.size(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero(), nuevo.getSeccion().getCodigo());
                    if (eventoDictadoClases != null) {
                        nuevo.setFechaInicio(eventoDictadoClases.getFechaInicio());
                        nuevo.setFechaFin(eventoDictadoClases.getFechaFin());
                    }
                    nuevo.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                    horarioSeccionDAO.save(nuevo);
                    horarios.add(nuevo);
                }

                horarioSeccionDAO.deleteAllInList(muertos);

                List<HorarioSeccion> existentesBD = inspector.getOldListDB();
                List<HorarioSeccion> existentesForm = inspector.getOldListForm();
                Map<String, HorarioSeccion> mapHorarioSeccBD = existentesBD.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));
                Map<String, HorarioSeccion> mapHorarioSeccForm = existentesForm.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));

                for (Map.Entry<String, HorarioSeccion> entry2 : mapHorarioSeccBD.entrySet()) {
                    HorarioSeccion hsBD = entry2.getValue();
                    HorarioSeccion hsForm = mapHorarioSeccForm.get(entry2.getKey());
                    hsBD.setAula(hsForm.getAula());
                    logger.debug("\tActualizando horario-seccion {} {} {}", hsBD.getDia().getNumeroDia(), hsBD.getHora().getNumero(), hsBD.getSeccion().getCodigo());
                    horarioSeccionDAO.update(hsBD);
                    horarios.add(hsBD);
                }
                visor.agregarLog("horSecc", "saveHorSecc", "horarios-seccion actualizados para " + seccion.getCodigo(), true, "info");
            }

            for (Map.Entry<String, List<HorarioAula>> entry : mapAulaHorarios.entrySet()) {
                String clave = entry.getKey();
                logger.debug("clave {} de horarioSeccion", clave);
                Seccion seccion = mapSecciones.get(clave);
                if (seccion == null) {
                    visor.agregarLog("horSecc", "saveHorSecc", "Horario-aula no se puede grabar para seccion " + clave + " no existente", false, "error");
                    logger.debug("\tNo existe PTM!!!!");
                }
                ModalidadEstudio modalidadCurso = seccion.getGrupoSeccion().getCurso().getModalidadEstudio();
                EventoCicloAcademico eventoDictadoClases = this.getEventoClases(cicloAcademico, modalidadCurso);

                List<HorarioAula> horarioSecc = entry.getValue();
                List<HorarioAula> horarioAulaBD = mapHorariosAulas.get(seccion.getId());
                horarioAulaBD = (horarioAulaBD == null) ? new ArrayList() : horarioAulaBD;
                ListsInspector inspector = TypesUtil.analizeLists(horarioAulaBD, horarioSecc, "key");

                List<HorarioAula> nuevos = inspector.getNewList();
                List<HorarioAula> muertos = inspector.getDeadList();

                int contador = 0;
                for (HorarioAula nuevo : nuevos) {
                    logger.debug("\t ( {} / {} ) Agregando horario-aula {} {} {}", contador++, nuevos.size(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero(), nuevo.getSeccion().getCodigo());
                    if (eventoDictadoClases != null) {
                        nuevo.setFechaInicio(eventoDictadoClases.getFechaInicio());
                        nuevo.setFechaFin(eventoDictadoClases.getFechaFin());
                        nuevo.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                        nuevo.setTipoEnum(TipoHorarioAulaEnum.DICT);
                        horarioAulaDAO.save(nuevo);
                    }

                }

                horarioAulaDAO.deleteAllInList(muertos);

                List<HorarioAula> existentesBD = inspector.getOldListDB();
                List<HorarioAula> existentesForm = inspector.getOldListForm();
                Map<String, HorarioAula> mapHorarioAulaBD = existentesBD.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));
                Map<String, HorarioAula> mapHorarioAulaForm = existentesForm.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));

                for (Map.Entry<String, HorarioAula> entry2 : mapHorarioAulaBD.entrySet()) {
                    HorarioAula hsBD = entry2.getValue();
                    HorarioAula hsForm = mapHorarioAulaForm.get(entry2.getKey());
                    hsBD.setSeccion(hsForm.getSeccion());
                    logger.debug("\tActualizando horario-aula {} {} {}", hsBD.getDia().getNumeroDia(), hsBD.getHora().getNumero(), hsBD.getAula().getCodigo());
                    horarioAulaDAO.update(hsBD);
                }
                visor.agregarLog("horAula", "saveHorAula", "horarios-aula actualizados para " + seccion.getCodigo(), true, "info");
            }

            return horarios;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DiaHoraGrupo> crearHorarioGrupos(
            String rutaFile,
            Map<Integer, Dia> mapDias,
            Map<Integer, Hora> mapHoras,
            Map<String, GrupoHoras> mapGrupos,
            CicloAcademico ciclo) {

        List<DiaHoraGrupo> horarios = new ArrayList<>();

        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Map<String, List<DiaHoraGrupo>> mapGpoHorarios = new LinkedHashMap();

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String cicloCod = getCellStringValue(1, row);
                String gpo = getCellStringValue(2, row);
                String hdia = getCellStringValue(3, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);

                if (StringUtils.isEmpty(cicloCod)) {
                    break;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                Dia dia = mapDias.get(Integer.parseInt(diaNum));
                Hora hora = mapHoras.get(Integer.parseInt(horaNum));
                GrupoHoras grupo = mapGrupos.get(gpo);
                DiaHoraGrupo hdiaGpo = new DiaHoraGrupo(ciclo, grupo, dia, hora);

                List<DiaHoraGrupo> diasHorasGpo = mapGpoHorarios.get(gpo);
                if (diasHorasGpo == null) {
                    diasHorasGpo = new ArrayList();
                    mapGpoHorarios.put(gpo, diasHorasGpo);
                }
                diasHorasGpo.add(hdiaGpo);

            }

            visor.inicializar("horGpo", mapGpoHorarios.size());

            List<DiaHoraGrupo> hdiaGpoTodosBD = diaHoraGrupoDAO.allByCiclo(ciclo);
            Map<Long, List<DiaHoraGrupo>> mapHorarioGpos = TypesUtil.convertListToMapList("grupoHorario.id", hdiaGpoTodosBD);
            for (Map.Entry<String, List<DiaHoraGrupo>> entry : mapGpoHorarios.entrySet()) {
                String gpo = entry.getKey();
                GrupoHoras grupo = mapGrupos.get(gpo);
                List<DiaHoraGrupo> hdiaGpo = entry.getValue();
                List<DiaHoraGrupo> hdiaGpoBD = mapHorarioGpos.get(grupo.getId());
                hdiaGpoBD = (hdiaGpoBD == null) ? new ArrayList() : hdiaGpoBD;
                ListsInspector inspector = TypesUtil.analizeLists(hdiaGpoBD, hdiaGpo, "key");

                List<DiaHoraGrupo> nuevos = inspector.getNewList();
                List<DiaHoraGrupo> muertos = inspector.getDeadList();

                logger.debug("\tNuevos grupos por agregar {}", nuevos.size());
                int cont = 0;
                for (DiaHoraGrupo nuevo : nuevos) {
                    logger.debug("\t({}, {}) {} {} {}", cont++, nuevos.size(), nuevo.getGrupoHorario().getCodigo(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero());
                    diaHoraGrupoDAO.save(nuevo);
                    horarios.add(nuevo);
                }

                diaHoraGrupoDAO.deleteAllInList(muertos);
                visor.agregarLog("horGpo", "saveHorGpo", "Guardando horario de " + gpo, true, "info");

            }

            return horarios;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    private String getCellStringValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato == null) {
            return null;
        }

        dato = StringUtils.replaceChars(dato, '\t', ' ');
        dato = StringUtils.replaceChars(dato, '\r', ' ');
        dato = StringUtils.replaceChars(dato, '\n', ' ');
        dato = StringUtils.replaceChars(dato, ',', ' ');
        dato = StringUtils.replaceChars(dato, '|', ' ');
        dato = StringUtils.replaceChars(dato, '´', '\'');
        dato = dato.replaceAll("\\s{2,}", " ").trim();

        if (dato.equals(".")) {
            return "";
        }
        if (dato.equals("-")) {
            return "";
        }
        if (dato.equals(",")) {
            return "";
        }

        return dato;
    }

    private Integer getCellIntegerValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue()).intValue();
        }

        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato == null) {
            return null;
        }

        dato = StringUtils.replaceChars(dato, '\t', ' ');
        dato = StringUtils.replaceChars(dato, '\r', ' ');
        dato = StringUtils.replaceChars(dato, '\n', ' ');
        dato = StringUtils.replaceChars(dato, ',', ' ');
        dato = StringUtils.replaceChars(dato, '|', ' ');
        dato = StringUtils.replaceChars(dato, '´', '\'');
        dato = dato.replaceAll("\\s{2,}", " ").trim();

        if (dato.equals(".")) {
            return 0;
        }
        if (dato.equals("-")) {
            throw new PhobosException("Valor de integer desconocido");
        }
        if (dato.equals(",")) {
            return 0;
        }
        if (StringUtils.isEmpty(dato)) {
            return null;
        }

        return Integer.valueOf(dato);
    }

    private EventoCicloAcademico getEventoClases(CicloAcademico cicloAcademico, ModalidadEstudio modalidad) {
        EventoAcademicoEnum eventoEnum = cicloAcademico.getTipoEnum() == TipoCicloEnum.NIV
                ? CLASES_VER
                : (modalidad.isPregrado() ? CLASES_PRE : (modalidad.isPostgrado() ? CLASES_EPG : null));
        if (eventoEnum == null) {
            throw new PhobosException("No esta considerado el Dictado de clases para la modalidad " + modalidad.getNombre());
        }

        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoEnum);
        return eventoCiclo;
    }

    @Override
    @Transactional
    public void actualizarCiclo(List<CicloAcademico> ciclos) {
        for (CicloAcademico ciclo : ciclos) {
            cicloAcademicoDAO.update(ciclo);
        }
    }

}
