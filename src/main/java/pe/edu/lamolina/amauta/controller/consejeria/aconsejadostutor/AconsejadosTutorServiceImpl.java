package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoCualidadDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.PlanTutorialDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.ID_CONSEJERO_NN;
import pe.edu.lamolina.model.tutoria.AlumnoCualidad;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AconsejadosTutorServiceImpl implements AconsejadosTutorService {

    private final AlumnoCualidadDAO alumnoCualidadDAO;
    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final CitaConsejeroAlumnoDAO citaConsejeroAlumnoDAO;
    private final ConsejeroDAO consejeroDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final PersonaDAO personaDAO;
    private final PlanTutorialDAO planTutorialDAO;

    private final VerificadorService verificadorService;

    @Override
    public Consejero findConsejero(Persona persona, CicloAcademico ciclo) {
        Consejero consejero = consejeroDAO.findByPersonaCiclo(persona, ciclo);
        if (consejero == null) {
            return new Consejero();
        }
        return consejero;
    }

    @Override
    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, Persona tutor) {
        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutor(filter, ciclo, tutor);
        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }
        return alumnoConsejeros;
    }

    @Override
    public List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera, DataSessionPivot ds) {

        List<AlumnoConsejero> alumnoConsejeros = null;
        if (verificadorService.isDeveloperOERA(ds)) {
            alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, tutor, carrera);
        } else {
            alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, tutor, carrera);
        }

        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }

        return alumnoConsejeros;

    }

    @Override
    public List<AlumnoConsejero> allByDynatableByCarreraReporte(DynatableFilter filter, CicloAcademico ciclo, Persona tutor, Carrera carrera) {

        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, tutor, carrera);

        List<Alumno> alumnos = alumnoConsejeros.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaResumen> matriculaResumen = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);

        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("alumno.id", matriculaResumen);

        for (AlumnoConsejero alumnoTutor : alumnoConsejeros) {
            MatriculaResumen matResumen = mapMatriculaResumen.get(alumnoTutor.getAlumno().getId());
            if (matResumen != null) {
                alumnoTutor.setEstadoMatriculableEnum(matResumen.getEstadoEnum());
                alumnoTutor.setEstadoMatriculaAutorizacion(matResumen.getAutorizacionMatricula());
                alumnoTutor.setCursosMatriculados(matResumen.getCursosMatriculados());
                alumnoTutor.setCreditosMatriculados(matResumen.getCreditosMatriculados());
            } else {
                alumnoTutor.setEstadoMatriculableEnum(EstadoMatriculaEnum.INH);
            }
        }

        return alumnoConsejeros;

    }

    @Override
    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico ciclo) {
        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejero(persona, ciclo);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejero(persona, ciclo);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejero(persona, ciclo);
        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        MatriculaResumen matriculaResumenBD = matriculaResumenDAO.findByAlumnoCiclo(matriculaResumen.getAlumno(), ds.getCicloAcademico());
        matriculaResumenBD.setAutorizacionMatricula(matriculaResumen.getAutorizacionMatricula());
        matriculaResumenBD.setFechaAutorizacionMatricula(new Date());
        matriculaResumenBD.setUserConsejero(ds.getUsuario());
        matriculaResumenDAO.update(matriculaResumenBD);
    }

    @Override
    public Persona findPersona(Long idPersona) {
        return personaDAO.find(idPersona);
    }

    @Override
    public AconsejadoEstadoBean allByPersonaCarrera(Persona persona, CicloAcademico ciclo, Carrera carrera, DataSessionPivot ds) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(10000000);

        Long countMatriculable = matriculaResumenDAO.countMatriculablesByConsejeroCarrera(persona, ciclo, carrera);
        Long countNoMatriculados = matriculaResumenDAO.countNoMatriculablesByConsejeroCarrera(persona, ciclo, carrera);
        Long countRetiroCiclo = matriculaResumenDAO.countRetiroCicloByConsejeroCarrera(persona, ciclo, carrera);

        List<AlumnoConsejero> alumnosTutor = null;
        if (verificadorService.isDeveloperOERA(ds)) {
            alumnosTutor = alumnoConsejeroDAO.allByDynatablePersonaTutorCarreraOERA(filter, ciclo, persona, carrera);
        } else {
            alumnosTutor = alumnoConsejeroDAO.allByDynatablePersonaTutorCarrera(filter, ciclo, persona, carrera);
        }

        AconsejadoEstadoBean aconsejadoEstadoBean = new AconsejadoEstadoBean();
        aconsejadoEstadoBean.setMatriculados(countMatriculable);
        aconsejadoEstadoBean.setNoMatriculados(countNoMatriculados);
        aconsejadoEstadoBean.setRetiroCiclo(countRetiroCiclo);
        aconsejadoEstadoBean.setAlumnosConsejeros(alumnosTutor);
        return aconsejadoEstadoBean;
    }

    @Override
    @Transactional
    public void eliminarAlumnoConsejero(Long idAlumnoConsejero) {
        alumnoConsejeroDAO.delete(idAlumnoConsejero);
    }

    @Override
    @Transactional
    public void quitarTutor(Long idAlumnoConsejero) {
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findAll(idAlumnoConsejero);
        alumnoConsejero.setConsejero(new Consejero(ID_CONSEJERO_NN));
        alumnoConsejeroDAO.updateColumns(alumnoConsejero, "consejero");
    }

    @Override
    public Map<Long, List<PlanTutorial>> allPlanes(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<PlanTutorial> planesAll = planTutorialDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, List<PlanTutorial>> mapPlanes = new HashMap();

        for (Alumno alumno : alumnos) {
            List<PlanTutorial> planes = planesAll.stream().filter(plan -> plan.getAlumno().equals(alumno)).collect(Collectors.toList());
            mapPlanes.put(alumno.getId(), planes);
        }

        return mapPlanes;
    }

    @Override
    public Map<Long, List<AlumnoCualidad>> allCualidades(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<AlumnoCualidad> cualidadesAll = alumnoCualidadDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCualidad>> mapCualidades = new HashMap();

        for (Alumno alumno : alumnos) {
            List<AlumnoCualidad> cualidades = cualidadesAll.stream().filter(plan -> plan.getAlumno().equals(alumno)).collect(Collectors.toList());
            mapCualidades.put(alumno.getId(), cualidades);
        }

        return mapCualidades;
    }

    @Override
    public Map<Long, CitaConsejeroAlumno> allCitas(List<Alumno> alumnos, CicloAcademico ciclo) {
        List<CitaConsejeroAlumno> citasAll = citaConsejeroAlumnoDAO.allUltimosByAlumnosCiclo(alumnos, ciclo);
        Map<Long, CitaConsejeroAlumno> mapCitas = new HashMap();

        for (Alumno alumno : alumnos) {
            CitaConsejeroAlumno cita = citasAll.stream().filter(citaConse -> citaConse.getAlumno().equals(alumno)).findFirst().orElse(null);
            if (cita == null) {
                cita = new CitaConsejeroAlumno();
            }
            mapCitas.put(alumno.getId(), cita);
        }

        return mapCitas;
    }

}
