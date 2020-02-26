package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class PromedioLoadDataServiceImp implements PromedioLoadDataService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    EgresadoDAO egresadoDAO;
    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Override
    public BeanPromedios loadDataAlumno(Alumno alumno) {

        alumno = alumnoDAO.findAllInfo(alumno.getId());
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio().getOperativeModalidadEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursosAll = alumnoCicloCursoDAO.allByAlumno(alumno);
        List<Reincorporacion> reincorporacionesAceptadasByAlumno = reincorporacionDAO.allAceptadasByAlumnoSinCiclo(alumno, cicloActivo);
        List<Reincorporacion> reincorporacionesByAlumnoCiclo = reincorporacionDAO.allAceptadasPendientesByAlumnoCiclo(alumno, cicloActivo);
        reincorporacionesAceptadasByAlumno.addAll(reincorporacionesByAlumnoCiclo);

        BeanPromedios bean = new BeanPromedios();
        bean.setAlumno(alumno);
        bean.setEgresado(egresado);
        bean.setCicloActivo(cicloActivo);
        bean.setAlumnoCiclos(alumnoCiclos);
        bean.setAlumnoCicloCursosOperativos(alumnoCicloCursos);
        bean.setAlumnoCicloCursosAll(alumnoCicloCursosAll);
        bean.setReincorporaciones(reincorporacionesByAlumnoCiclo);
        return bean;

    }

}
