package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.ObtencionGrado;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class PromedioSegundoServiceImp implements PromedioSegundoService {

    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final EgresadoDAO egresadoDAO;
    private final ObtencionGradoDAO obtencionGradoDAO;
    private final ReincorporacionDAO reincorporacionDAO;

    private final PromedioReviewService promedioReviewService;

    @Async
    @Override
    public void procesarYear(
            List<Alumno> alumnos,
            CicloAcademico cicloActivo,
            List<CicloAcademico> ciclos,
            DataSessionPivot ds) {

        List<ObtencionGrado> graduados = obtencionGradoDAO.allAceptadosByAlumnos(alumnos);
        List<Egresado> egresados = egresadoDAO.allByAlumnosAceptados(alumnos);
        List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
        List<Reincorporacion> reincorporacionesAntes = reincorporacionDAO.allAceptadosByAlumnosSinCiclo(alumnos, cicloActivo);
        List<Reincorporacion> reincorporacionesActuales = reincorporacionDAO.allAceptadasPendientesByAlumnosCiclo(alumnos, cicloActivo);

        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivo = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);
        Map<Long, List<Reincorporacion>> mapReincorporacionAntes = TypesUtil.convertListToMapList("alumno.id", reincorporacionesAntes);
        Map<Long, List<Reincorporacion>> mapReincorporacionActual = TypesUtil.convertListToMapList("alumno.id", reincorporacionesActuales);
        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);
        Map<Long, ObtencionGrado> mapGraduado = TypesUtil.convertListToMap("alumno.id", graduados);

        for (Alumno alumno : alumnos) {

            ObtencionGrado graduado = mapGraduado.get(alumno.getId());
            Egresado egresado = mapEgresado.get(alumno.getId());
            List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosActivosByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacionAntes.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesActualesByAlumno = TypesUtil.getListNotNull(mapReincorporacionActual.get(alumno.getId()));
            reincorporacionesByAlumno.addAll(reincorporacionesActualesByAlumno);

            promedioReviewService.promediarAllCicloAsync(
                    alumno,
                    cicloActivo,
                    graduado,
                    egresado,
                    ciclos,
                    alumnoCiclos,
                    alumnoCiclosCursosActivosByAlu,
                    alumnoCiclosCursosAllByAlu,
                    reincorporacionesByAlumno, ds);
        }
    }

}
