package pe.edu.lamolina.amauta.controller.consejeria.consejeros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.VerificadorClonacionConsejero;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorEstadoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaCargoDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import pe.edu.lamolina.model.enums.PerfilEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.ColaboradorEstado;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import pe.edu.lamolina.amauta.dao.consejeria.InformeFinalTutoriaDAO;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ConsejerosServiceImp implements ConsejerosService {

    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final AlumnoDAO alumnoDAO;
    private final CarreraDAO carreraDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final ColaboradorEstadoDAO colaboradorEstadoDAO;
    private final ConsejeriaResumenDAO consejeriaResumenDAO;
    private final ConsejeroDAO consejeroDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final DocenteDAO docenteDAO;
    private final InformeFinalTutoriaDAO informeFinalTutoriaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final OficinaDAO oficinaDAO;
    private final PersonaCargoDAO personaCargoDAO;
    private final RolDAO rolDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioRolDAO usuarioRolDAO;
    private final OficinaService oficinaService;

    private VerificadorClonacionConsejero verificadorClonacionConsejero;

    private final List<EstadoMatriculaEnum> estadosMatriculables = Arrays.asList(MAT, NMAT, PMAT, RCI);

    @Override
    public List<Carrera> allCarrerasPregrado() {
        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
    }

    @Override
    public Carrera findbByNombre(Long idcarrera) {
        return carreraDAO.find(idcarrera);
    }

    @Override
    public List<Consejero> allByCarreraDynatable(Carrera carrera, CicloAcademico ciclo, DynatableFilter filter) {

        List<Consejero> consejeros = consejeroDAO.allByCarreraDynatable(carrera, filter);
        List<InformeFinalTutoria> informes = informeFinalTutoriaDAO.allActivosByConsejerosCiclo(consejeros, ciclo);
        Map<Long, InformeFinalTutoria> mapInformes = informes.stream()
                .collect(Collectors.toMap(info -> info.getConsejero().getId(), Function.identity()));

        List<Colaborador> colaboradores = consejeros.stream()
                .map(x -> x.getColaborador())
                .collect(Collectors.toList());

        Map<String, Colaborador> mapColaborador = TypesUtil.convertListToMap("codigo", colaboradores);

        List<Persona> personas = consejeros.stream()
                .map(x -> x.getColaborador().getPersona())
                .collect(Collectors.toList());

        {
            List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allSimpleByCicloConsejeros(consejeros, ciclo);

            Map<Long, List<AlumnoConsejero>> alumnoConsejerosMap = alumnoConsejeros.stream()
                    .collect(groupingBy(x -> x.getConsejero().getId()));

            List<Alumno> alumnos = alumnoConsejeros.stream()
                    .map(x -> x.getAlumno())
                    .collect(toList());

            List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allSimpleByAlumnosCiclo(alumnos, ciclo);

            Map<Long, MatriculaResumen> matriculaResumensMap = matriculaResumens.stream()
                    .collect(toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

            for (Consejero consejero : consejeros) {

                List<AlumnoConsejero> misAlumnoConsejeros = alumnoConsejerosMap.getOrDefault(consejero.getId(), new ArrayList());

                int matriculados = 0;
                int noMatriculados = 0;

                for (AlumnoConsejero alumnoConsejero : misAlumnoConsejeros) {
                    MatriculaResumen matriculaResumen = matriculaResumensMap.getOrDefault(alumnoConsejero.getAlumno().getId(), new MatriculaResumen());
                    if (matriculaResumen.getEstadoEnum() == MAT) {
                        matriculados += 1;
                    }
                    if (matriculaResumen.getEstadoEnum() == NMAT) {
                        noMatriculados += 1;
                    }
                }

                consejero.setAconsejadosMat(matriculados);
                consejero.setAconsejadosNmat(noMatriculados);
                consejero.setInforme(mapInformes.get(consejero.getId()));
            }
        }

        List<Docente> docentes = docenteDAO.allByPersonas(personas);
        for (Persona persona : personas) {
            persona.setDocente(new ArrayList());
        }

        for (Docente docente : docentes) {
            Colaborador colaborador = mapColaborador.get(docente.getCodigo());
            if (colaborador != null) {
                colaborador.getPersona().getDocente().add(docente);
            }
        }

        return consejeros;
    }

    @Override
    public Consejero finByIdPersona(Persona persona) {
        return consejeroDAO.finByIdPersona(persona);
    }

    @Override
    public List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo) {
        List<Facultad> facultades = new ArrayList();
        List<Carrera> carreras = new ArrayList();
        log.debug("***ciclo academico {}", ciclo.getDescripcion());

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(persona);
        log.debug("***cantidad oficina es {}", oficinasMain.size());

        for (Oficina oficina : oficinasMain) {
            log.debug("codigo oficina es {}", oficina.getCodigo());
            log.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                return carreraDAO.allPregradoByCicloMatriculables(ciclo);
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.FAC) {
                facultades.add(new Facultad(oficina.getInstanciaOficina()));
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                carreras.add(new Carrera(oficina.getInstanciaOficina()));
            }
        }

        log.debug("Carreras previas es {} {} {}", carreras.size());
        if (!carreras.isEmpty()) {
            List<Carrera> carrerasCiclo = carreraDAO.allByMatriculablesCicloCarreras(carreras, ciclo);
            carreras.addAll(carrerasCiclo);
        }

        if (!facultades.isEmpty()) {
            List<Carrera> carrerasFac = carreraDAO.allByMatriculablesCicloFacultades(facultades, ciclo);
            carreras.addAll(carrerasFac);
        }

        return carreraDAO.allByCarreras(carreras);
    }

    @Override
    public List<Docente> allDocenteByNombreFacultad(String nombre, Facultad facultad) {
        List<DepartamentoAcademico> departamentos = departamentoAcademicoDAO.allByFacultad(facultad);
        return docenteDAO.allByNombreDepartamentos(nombre, departamentos);
    }

    @Override
    @Transactional
    public void updateEstado(Consejero consejeroForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Consejero consejeroBD = consejeroDAO.find(consejeroForm.getId());

        Assert.isNotNull(consejeroBD, "El consejero no existe en el sistema");
        Assert.isFalse(consejeroBD.getEstadoEnum() == consejeroForm.getEstadoEnum(), "El estado del consejero ya fue modificado");

        if (consejeroForm.getEstadoEnum() == ACT) {
            consejeroBD.setAlumnosActivos(0);
            consejeroBD.setAlumnosInactivos(0);
            consejeroBD.setEstadoEnum(ACT);
            consejeroDAO.update(consejeroBD);
            revisarConsejeria(consejeroBD.getCarrera(), ciclo, true, ds);
            return;
        }

        Consejero consejeroNN = new Consejero(GlobalConstantine.ID_CONSEJERO_NN);
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByConsejeroCiclo(consejeroBD, ciclo);
        for (AlumnoConsejero alumnoConsejero : alumnoConsejeros) {
            Alumno alumno = alumnoConsejero.getAlumno();
            alumno.setConsejero(consejeroNN);
            alumnoDAO.update(alumno);

            alumnoConsejero.setConsejero(consejeroNN);
            alumnoConsejeroDAO.update(alumnoConsejero);
        }

        consejeroBD.setAlumnosActivos(0);
        consejeroBD.setAlumnosInactivos(0);
        consejeroBD.setEstadoEnum(INA);
        consejeroDAO.update(consejeroBD);

        revisarConsejeria(consejeroBD.getCarrera(), ciclo, true, ds);
    }

    @Override
    @Transactional
    public void saveConsejeroByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        Colaborador colaborador = colaboradorDAO.findDocenteActivoByPersonaDptoAcademico(docente.getPersona(), docente.getDepartamentoAcademico());
        Assert.isNotNull(colaborador, "No se pudo encontrar al docente como empleado del departamento académico al que pertenece");

        Carrera carrera = this.findbByNombre(docente.getCarrera().getId());

        Consejero consejero = consejeroDAO.findByColaboradorCarrera(colaborador, carrera);
        Assert.isNull(consejero, "Este docente ya existe como consejero para esta carrera");

        consejero = new Consejero();
        consejero.setEstadoEnum(ACT);
        consejero.setFechaRegistro(new Date());
        consejero.setFechaInicio(new Date());
        consejero.setUserRegistro(ds.getUsuario());
        consejero.setCarrera(carrera);
        consejero.setColaborador(colaborador);
        consejero.setAlumnosInactivos(0);
        consejero.setAlumnosActivos(0);

        consejeroDAO.save(consejero);

        revisarConsejeria(carrera, ciclo, true, ds);

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaborador);
        colaboradorEstado.setEstadoEnum(ColaboradorEstadoEnum.ACT);
        colaboradorEstado.setUserRegistro(ds.getUsuario());
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);

        PersonaCargo personaCargo = new PersonaCargo();
        personaCargo.setCompania(ds.getCompania());
        personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
        personaCargo.setFechaInicio(colaborador.getFechaInicio());
        personaCargo.setFechaRegistro(new Date());
        personaCargo.setOficina(colaborador.getOficina());
        personaCargo.setPerfilCompania(colaborador.getCargo());
        personaCargo.setPersona(docente.getPersona());
        personaCargo.setUserRegistro(ds.getUsuario());
        personaCargoDAO.save(personaCargo);

        Oficina oficinaColaborador = oficinaDAO.find(colaborador.getOficina().getId());
        revisarPerfiles(colaborador, docente.getPersona(), oficinaColaborador, ds);
    }

    private void revisarPerfiles(Colaborador colaborador, Persona persona, Oficina oficinaColaborador, DataSessionPivot ds) {

        Usuario user = usuarioDAO.findActivoByPersona(persona);
        if (user == null) {
            if (colaborador.getPersona().getEmailCompania() != null) {
                user = addUser(persona, ds);
                addUserRoll(oficinaColaborador, user, colaborador, ds);
            }
        } else {
            addUserRoll(oficinaColaborador, user, colaborador, ds);
        }
    }

    private Usuario addUser(Persona personaForm, DataSessionPivot ds) {
        Usuario user = new Usuario();
        user.setEstadoEnum(UserEstadoEnum.ACT);
        user.setGoogle(personaForm.getEmailCompania());
        user.setPersona(personaForm);
        user.setUserRegistro(ds.getUsuario());
        user.setFechaRegistro(new Date());
        usuarioDAO.save(user);

        return user;
    }

    private void addUserRoll(
            Oficina oficinaMain,
            Usuario userColaborador,
            Colaborador colaborador, DataSessionPivot ds) {

        Rol rol = rolDAO.findByCode(RolEnum.TUTO);
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setEstadoEnum(UserEstadoEnum.ACT);
        usuarioRol.setFechaInicio(colaborador.getFechaInicio());
        usuarioRol.setFechaRegistro(new Date());
        usuarioRol.setOficina(oficinaMain);
        usuarioRol.setIdInstancia(oficinaMain.getInstanciaOficina());
        usuarioRol.setTipoOficina(oficinaMain.getTipoOficina().getCodigo());
        usuarioRol.setRol(rol);
        usuarioRol.setUserRegistro(ds.getUsuario());
        usuarioRol.setUsuario(userColaborador);
        usuarioRolDAO.save(usuarioRol);

    }

    @Override
    @Transactional
    public void asignarAlumnosAleatorio(Carrera carrera, CicloAcademico ciclo, DataSessionPivot ds) {

        Consejero consejeroNN = new Consejero(GlobalConstantine.ID_CONSEJERO_NN);
        List<AlumnoConsejero> alumnosConsejeros = alumnoConsejeroDAO.allActivosByConsejeroCarreraCiclo(consejeroNN, carrera, ciclo);
        Assert.isFalse(alumnosConsejeros.isEmpty(), "No existe alumnos a quienes asignar aleatoriamente un tutor de la especialidad");

        List<Alumno> alumnos = alumnosConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);

        List<Consejero> consejeros = consejeroDAO.allActivosByCarrera(carrera);

        int bucle = 0;
        Collections.shuffle(alumnosConsejeros);
        for (AlumnoConsejero alumnoTutor : alumnosConsejeros) {
            Alumno alumno = alumnoTutor.getAlumno();
            MatriculaResumen matriculable = mapMatriculable.get(alumno.getId());
            if (matriculable == null) {
                continue;
            }
            if (!estadosMatriculables.contains(matriculable.getEstadoEnum())) {
                continue;
            }
            Consejero consejero = consejeros.get(bucle);

            alumnoTutor.setConsejero(consejero);
            alumno.setConsejero(consejero);
            if (matriculable.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                consejero.setAlumnosActivos(consejero.getAlumnosActivos() + 1);
            } else {
                consejero.setAlumnosInactivos(consejero.getAlumnosInactivos() + 1);
            }

            alumnoConsejeroDAO.update(alumnoTutor);
            alumnoDAO.update(alumno);

            bucle++;
            bucle = (bucle >= consejeros.size()) ? 0 : bucle;
        }

        for (Consejero consejero : consejeros) {
            consejeroDAO.update(consejero);
        }

        revisarConsejeria(carrera, ciclo, true, ds);
    }

    @Override
    @Transactional
    public void desasignarAlumnos(Carrera carrera, CicloAcademico ciclo, DataSessionPivot ds) {

        List<Consejero> consejeros = new ArrayList();
        Map<Long, Consejero> mapConsejero = new LinkedHashMap();

        List<AlumnoConsejero> alumnosConsejerosTotal = alumnoConsejeroDAO.allActivosByCarreraCiclo(carrera, ciclo);
        Assert.isFalse(alumnosConsejerosTotal.isEmpty(), "No existe alumnos en esta especialidad a quienes retirarle el tutor");

        List<AlumnoConsejero> alumnosConsejeros = new ArrayList();
        for (AlumnoConsejero alumnoTutor : alumnosConsejerosTotal) {
            Consejero consejero = alumnoTutor.getConsejero();
            if (consejero.getId().longValue() != GlobalConstantine.ID_CONSEJERO_NN) {
                alumnosConsejeros.add(alumnoTutor);
            }
        }
        Assert.isFalse(alumnosConsejeros.isEmpty(), "No existe alumnos en esta especialidad a quienes retirarle el tutor");

        List<Alumno> alumnos = alumnosConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);

        for (AlumnoConsejero alumnoTutor : alumnosConsejeros) {
            Consejero consejero = alumnoTutor.getConsejero();
            if (consejero.getId().longValue() == GlobalConstantine.ID_CONSEJERO_NN) {
                continue;
            }
            Consejero consejeroMap = mapConsejero.get(consejero.getId());
            if (consejeroMap != null) {
                alumnoTutor.setConsejero(consejeroMap);
                continue;
            }
            mapConsejero.put(consejero.getId(), consejero);
            consejeros.add(consejero);

        }

        Consejero consejeroNN = new Consejero(GlobalConstantine.ID_CONSEJERO_NN);
        for (AlumnoConsejero alumnoTutor : alumnosConsejeros) {
            alumnoTutor.setConsejero(consejeroNN);
            alumnoConsejeroDAO.update(alumnoTutor);

            Alumno alumno = alumnoTutor.getAlumno();
            MatriculaResumen matriculable = mapMatriculable.get(alumno.getId());
            if (matriculable == null) {
                continue;
            }
            if (!estadosMatriculables.contains(matriculable.getEstadoEnum())) {
                continue;
            }
            alumno.setConsejero(consejeroNN);
            alumnoDAO.update(alumno);
        }

        for (Consejero consejero : consejeros) {
            consejero.setAlumnosActivos(0);
            consejero.setAlumnosInactivos(0);
            consejeroDAO.update(consejero);
        }

        revisarConsejeria(carrera, ciclo, true, ds);
    }

    @Override
    public List<Consejero> allByCarrera(String nombre, Carrera carrera) {
        return consejeroDAO.allByNombreAndCarrera(nombre, carrera);
    }

    @Override
    public List<Alumno> allAlumnosByConsejero(Consejero consejero) {
        return consejeroDAO.allAlumnosByConsejero(consejero);
    }

    @Override
    public List<Alumno> allAlumnosByConsejero(List<Consejero> consejero) {
        return consejeroDAO.allAlumnosByConsejero(consejero);
    }

    @Override
    @Transactional
    public void revisarConsejeria(Carrera carrera, CicloAcademico ciclo, boolean forzar, DataSessionPivot ds) {

        if (verificadorClonacionConsejero == null) {
            verificadorClonacionConsejero = new VerificadorClonacionConsejero();
        }

        if (verificadorClonacionConsejero.isOcupado()) {
            throw new PhobosException("Se están generando los registros de consejero inténtelo en otro momento.");
        }

        DateTime today = new DateTime();

        ConsejeriaResumen resumen = consejeriaResumenDAO.findByCarreraCiclo(carrera, ciclo);

        if (resumen == null) {

            resumen = new ConsejeriaResumen();
            resumen.setCarrera(carrera);
            resumen.setCicloAcademico(ciclo);
            resumen.setFechaActualizacion(today.minusDays(2).toDate());
            consejeriaResumenDAO.save(resumen);

        }

        DateTime ayer = new DateTime(resumen.getFechaActualizacion());

        int dias = Days.daysBetween(ayer.toLocalDate(), today.toLocalDate()).getDays();

        if (dias < 1 && !forzar) {
            return;
        }

        Aconsejado aconsejadoMtbles = alumnoConsejeroDAO.countAconsejadosMatriculables(carrera, ciclo);
        aconsejadoMtbles = (aconsejadoMtbles == null) ? new Aconsejado() : aconsejadoMtbles;

        if (aconsejadoMtbles.getSinRegistro() > 0) {
            List<MatriculaResumen> mtrblesNoregistrados = matriculaResumenDAO.allSinConsejeria(carrera, ciclo);
            for (MatriculaResumen mtble : mtrblesNoregistrados) {
                AlumnoConsejero alumnoTutor = new AlumnoConsejero();
                alumnoTutor.setAlumno(mtble.getAlumno());
                alumnoTutor.setCicloAcademico(ciclo);
                alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
                alumnoTutor.setEstadoEnum(ACT);
                alumnoTutor.setFechaAsigna(today.toDate());
                alumnoTutor.setUserAsigna(ds.getUsuario());
                alumnoConsejeroDAO.save(alumnoTutor);
            }

            aconsejadoMtbles = alumnoConsejeroDAO.countAconsejadosMatriculables(carrera, ciclo);
            aconsejadoMtbles = (aconsejadoMtbles == null) ? new Aconsejado() : aconsejadoMtbles;
        }

        resumen.setAconsejadosActivos(aconsejadoMtbles.getMatriculadosConConsejeros().intValue());
        resumen.setAconsejadosInactivos(aconsejadoMtbles.getNoMatriculadosConConsejeros().intValue());
        resumen.setSinconsejeroActivos(aconsejadoMtbles.getMatriculadosSinConsejeros().intValue());
        resumen.setSinconsejeroInactivos(aconsejadoMtbles.getNoMatriculadosSinConsejeros().intValue());

        Aconsejado aconsejadoNoMtbles = alumnoConsejeroDAO.countAconsejadosNoMatriculables(carrera, ciclo);
        aconsejadoNoMtbles = (aconsejadoNoMtbles == null) ? new Aconsejado() : aconsejadoNoMtbles;
        resumen.setInhabilitados(aconsejadoNoMtbles.getInhabilitados().intValue());

        ConsejeroEstado cont = consejeroDAO.countConsejerosByCarrera(carrera);
        cont = (cont == null) ? new ConsejeroEstado() : cont;
        resumen.setConsejerosActivos(cont.getActivos().intValue());
        resumen.setConsejerosInactivos(cont.getInactivos().intValue());

        consejeriaResumenDAO.update(resumen);

    }

    @Override
    public ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico) {
        ConsejeriaResumen resumen = consejeriaResumenDAO.findByCarreraCiclo(carrera, cicloAcademico);
        resumen = (resumen == null) ? new ConsejeriaResumen() : resumen;
        return resumen;
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico) {
        List<Alumno> alumnos = alumnoDAO.allByName(nombre);

        List<MatriculaResumen> matriculas = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> matriculasMap = TypesUtil.convertListToMap("alumno.id", matriculas);
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, AlumnoConsejero> alumnoConsejeroMap = TypesUtil.convertListToMap("alumno.id", alumnoConsejeros);
        if (alumnoConsejeroMap == null) {
            alumnoConsejeroMap = new HashMap<>();
        }

        for (Alumno alumno : alumnos) {
            MatriculaResumen matriculaResumen = matriculasMap.get(alumno.getId());
            AlumnoConsejero alumnoConsejero = alumnoConsejeroMap.get(alumno.getId());

            alumno.setSituacion("0");

            if (matriculaResumen == null) {
                alumno.setMotivoMatriculable("No cuenta con registro en matricula para el presente ciclo académico");
                continue;
            }
            if (!Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum())) {
                alumno.setMotivoMatriculable("No matriculable");
                continue;
            }
            if (alumnoConsejero != null) {
                alumno.setMotivoMatriculable(String.format("Ya tiene consejero y es; %s", alumnoConsejero.getConsejero().getColaborador().getPersona().getApellidosNombres()));
                continue;
            }
            alumno.setSituacion("1");
        }
        return alumnos;
    }

    @Transactional
    @Override
    public void saveAlumnosConsejero(Consejero consejero, DataSessionPivot ds) {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        for (Alumno alumno : consejero.getAlumno()) {
            AlumnoConsejero alumnoConsejero = new AlumnoConsejero();
            alumnoConsejero.setAlumno(alumno);
            alumnoConsejero.setConsejero(consejero);
            alumnoConsejero.setCicloAcademico(ds.getCicloAcademico());
            alumnoConsejero.setEstadoEnum(ACT);
            alumnoConsejero.setFechaAsigna(ds.getFechaAccionAudit());
            alumnoConsejero.setUserAsigna(ds.getUsuario());
            alumnoConsejeroDAO.save(alumnoConsejero);

            Alumno alumnoUpd = new Alumno(alumno.getId());
            alumnoUpd.setConsejero(consejero);
            alumnoDAO.updateColumns(alumnoUpd, "consejero");
        }
    }

    @Override
    public List<AlumnoConsejero> allAlumnosConsejeros(List<Consejero> consejeros, CicloAcademico cicloAcademico, EstadoEnum... estados) {
        return alumnoConsejeroDAO.allByConsejerosAndCiclo(consejeros, cicloAcademico, estados);
    }

    @Override
    public List<MatriculaResumen> allMatriculadosByCicloAndCarrera(CicloAcademico cicloAcademico, List<Carrera> carreras) {
        return matriculaResumenDAO.allByCicloAndCarrera(cicloAcademico, carreras, EstadoMatriculaEnum.MAT);
    }

    @Override
    public List<AlumnoConsejero> allAlumnosOtraEspecialidad(Carrera carreraConsejero, CicloAcademico ciclo) {
        return alumnoConsejeroDAO.allAlumnosOtraEspecialidad(carreraConsejero, ciclo);
    }

}
