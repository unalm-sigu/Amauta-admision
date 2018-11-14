package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.AulaRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CursoMasivosServiceImp implements CursoMasivosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    RolExamenesDAO rolExamenesDAO;
    
    
    @Autowired
    CursoDAO cursoDAO;

//    @Autowired
//    SeccionCursoMasivoDAO seccionCursoMasivoDAO;
//
//    @Autowired
//    AulaCursoMasivoDAO aulaCursoMasivoDAO;
//
//    @Autowired
//    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;
    

    @Override
    public List<CursoMasivoExamen> allCursoMasivoExamenes(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public List<Curso> allCursosByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allCursosByCicloActivo(cicloAcademico);
    }

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }

    @Override
    @Transactional
    public void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        logger.debug("Contenido de cursoMasivoExamen= {}", cursoMasivosExamen.getCurso());
        cursoMasivosExamen.setUserRegistro(ds.getUsuario());
        cursoMasivosExamen.setFechaRegistro(new Date());
        cursoMasivosExamen.setAlumnos(15);
        cursoMasivosExamen.setSecciones(2);
        cursoMasivosExamen.setCapacidadAulas(3);
        cursoMasivoExamenDAO.save(cursoMasivosExamen);
    }

    @Override
    public List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<CursoMasivoExamen> cursosMasivosExamenes = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes);
//        for (CursoMasivoExamen cursoMasivoExamen : cursosMasivosExamenes) {
//
//            List<SeccionCursoMasivo> secciones = seccionCursoMasivoDAO.
//                    allByCursoMasivoExamenAndEstados(cursoMasivoExamen, Arrays.asList(SeccionRolExamenEstadoEnum.ACT, SeccionRolExamenEstadoEnum.EXC));
//            List<AulaCursoMasivo> capacidadAulas = aulaCursoMasivoDAO.
//                    allByCursoMasivoExamenAndEstados(cursoMasivoExamen, Arrays.asList(AulaRolExamenEstadoEnum.ACT, AulaRolExamenEstadoEnum.EXC));
//            List<AlumnoCursoMasivo> alumnos = alumnoCursoMasivoDAO.
//                    allByLetraGrupoRegularAndEstados(cursoMasivoExamen, Arrays.asList(AlumnoRolExamenEstadoEnum.ACT, AlumnoRolExamenEstadoEnum.EXC));
//
//            cursoMasivoExamen.setSeccionesCursosMasivos(secciones);
//            List<SeccionCursoMasivo> seccionesCursosMasivosActivos = secciones.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());
//            cursoMasivoExamen.setSeccionesCursosMasivosCount(seccionesCursosMasivosActivos.size());
//
//            cursoMasivoExamen.setAulasCursosMasivos(capacidadAulas);
//            List<AulaCursoMasivo> seccionesGruposRegularesActivos = capacidadAulas.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());
//            cursoMasivoExamen.setAulasCursosMasivosCount(seccionesGruposRegularesActivos.size());
//
//            cursoMasivoExamen.setAlumnosCursosMasivos(alumnos);
//            List<AlumnoCursoMasivo> alumnosGruposRegularesActivos = alumnos.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());
//            cursoMasivoExamen.setAlumnosCursosMasivosCount(alumnosGruposRegularesActivos.size());
//        }

        return cursosMasivosExamenes;
    }

    @Override
    public List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%"; 
        return cursoDAO.allForExamenByCiclo(nombre, rolExamenes, cicloAcademico);
    }

    
}
