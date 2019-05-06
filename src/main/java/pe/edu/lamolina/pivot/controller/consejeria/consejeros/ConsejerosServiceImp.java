package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ConsejerosServiceImp implements ConsejerosService {

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    ConsejeriaResumenDAO consejeriaResumenDAO;
    @Autowired
    ConsejeroDAO consejeroDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    ColaboradorDAO colaboradorDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    OficinaService oficinaService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<EstadoMatriculaEnum> estadosMatriculables = Arrays.asList(MAT, NMAT, PMAT, RCI);

    @Override
    public Carrera findbByNombre(Long idcarrera) {
        return carreraDAO.find(idcarrera);
    }

    @Override
    public List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter) {

        List<Consejero> consejeros = consejeroDAO.allByCarreraDynatable(carrera, filter);
        List<Colaborador> colaboradores = consejeros.stream().map(x -> x.getColaborador()).collect(Collectors.toList());
        Map<String, Colaborador> mapColaborador = TypesUtil.convertListToMap("codigo", colaboradores);
        List<Persona> personas = consejeros.stream().map(x -> x.getColaborador().getPersona()).collect(Collectors.toList());

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
        logger.debug("***ciclo academico {}", ciclo.getDescripcion());

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(persona);
        logger.debug("***cantidad oficina es {}", oficinasMain.size());

        for (Oficina oficina : oficinasMain) {
            logger.debug("codigo oficina es {}", oficina.getCodigo());
            logger.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

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

        logger.debug("Carreras previas es {} {} {}", carreras.size());
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

        Consejero consejeroNN = new Consejero(Constantine.ID_CONSEJERO_NN);
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
    }

    @Override
    @Transactional
    public void asignarAlumnosAleatorio(Carrera carrera, CicloAcademico ciclo, DataSessionPivot ds) {

        Consejero consejeroNN = new Consejero(Constantine.ID_CONSEJERO_NN);
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
            if (consejero.getId().longValue() != Constantine.ID_CONSEJERO_NN) {
                alumnosConsejeros.add(alumnoTutor);
            }
        }
        Assert.isFalse(alumnosConsejeros.isEmpty(), "No existe alumnos en esta especialidad a quienes retirarle el tutor");

        List<Alumno> alumnos = alumnosConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);

        for (AlumnoConsejero alumnoTutor : alumnosConsejeros) {
            Consejero consejero = alumnoTutor.getConsejero();
            if (consejero.getId().longValue() == Constantine.ID_CONSEJERO_NN) {
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

        Consejero consejeroNN = new Consejero(Constantine.ID_CONSEJERO_NN);
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
    @Transactional
    public void revisarConsejeria(Carrera carrera, CicloAcademico ciclo, boolean forzar, DataSessionPivot ds) {
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
                alumnoTutor.setConsejero(new Consejero(Constantine.ID_CONSEJERO_NN));
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

}
