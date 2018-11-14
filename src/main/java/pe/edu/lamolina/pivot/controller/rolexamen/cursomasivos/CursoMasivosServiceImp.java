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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;

import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
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

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

//    @Autowired
//    AulaCursoMasivoDAO aulaCursoMasivoDAO;
    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

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
        cursoMasivoExamenDAO.save(cursoMasivosExamen);

        List<Seccion> secciones = seccionDAO.allByCicloAndCurso(cicloAcademico, cursoMasivosExamen.getCurso());

        int alus = 0;
        for (Seccion seccion : secciones) {
            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());
            seccionCursoMasivoDAO.save(seccionCursoMasivo);
            List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
            alus += matriculadosPorSeccion.size();
            for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
                Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();
                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
                alumnoCursoMasivo.setAlumno(alumno);
                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivo.setFechaRegistro(new Date());
                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());                
                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
            }
        }

        cursoMasivosExamen.setAlumnos(alus);
        cursoMasivosExamen.setSecciones(secciones.size());
        cursoMasivoExamenDAO.update(cursoMasivosExamen);

    }

    @Override
    public List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<CursoMasivoExamen> cursosMasivosExamenes = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes);
        for (CursoMasivoExamen cursoMasivoExamen : cursosMasivosExamenes) {

        }

        return cursosMasivosExamenes;
    }

    @Override
    public List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return cursoDAO.allForExamenByCiclo(nombre, rolExamenes, cicloAcademico);
    }

}
