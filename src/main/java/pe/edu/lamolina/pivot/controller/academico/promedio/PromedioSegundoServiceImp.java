package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PromedioSegundoServiceImp implements PromedioSegundoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    EgresadoDAO egresadoDAO;
    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    PromedioReviewService promedioReviewService;

    @Async
    @Override
    public void procesarYear(
            List<Alumno> alumnos,
            CicloAcademico cicloActivo,
            List<CicloAcademico> ciclos,
            DataSessionPivot ds) {

        List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
        List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnos, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));

        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivo = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);
        Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);
        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);

        for (Alumno alumno : alumnos) {
            Egresado egresado = mapEgresado.get(alumno.getId());
            List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosActivosByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

            promedioReviewService.promediarAllCicloAsync(
                    alumno,
                    cicloActivo,
                    egresado,
                    ciclos,
                    alumnoCiclos,
                    alumnoCiclosCursosActivosByAlu,
                    alumnoCiclosCursosAllByAlu,
                    reincorporacionesByAlumno, ds);
        }
    }

}
