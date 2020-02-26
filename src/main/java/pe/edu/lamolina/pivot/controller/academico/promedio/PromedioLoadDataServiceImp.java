package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;

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
    ReincorporadosService reincorporadosService;

    @Override
    public BeanPromedios loadDataAlumno(Alumno alumno) {

        alumno = alumnoDAO.findAllInfo(alumno.getId());
        Egresado egresado = egresadoDAO.findPrincipalByAlumno(alumno);
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(alumno.getModalidadEstudio().getOperativeModalidadEnum());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        List<AlumnoCicloCurso> alumnoCicloCursosAll = alumnoCicloCursoDAO.allByAlumno(alumno);
        List<Reincorporacion> reincorporacionesAceptadasByAlumno = reincorporadosService.allReincorporacionesByAlumno(alumno, cicloActivo);

        BeanPromedios bean = new BeanPromedios();
        bean.setAlumno(alumno);
        bean.setEgresado(egresado);
        bean.setCicloActivo(cicloActivo);
        bean.setAlumnoCiclos(alumnoCiclos);
        bean.setAlumnoCicloCursosOperativos(alumnoCicloCursos);
        bean.setAlumnoCicloCursosAll(alumnoCicloCursosAll);
        bean.setReincorporaciones(reincorporacionesAceptadasByAlumno);
        return bean;

    }

    @Override
    public ListBeanPromedios loadDataAlumno(List<Alumno> alumnos) {

        List<CicloAcademico> ciclos = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclosActivos = ciclos.stream().filter(x -> x.getEstadoEnum() == CicloAcademicoEstadoEnum.ACT).collect(Collectors.toList());
        CicloAcademico cicloPregrado = ciclosActivos.stream().filter(x -> x.getModalidadEstudio().getCodigoEnum() == PRE).findAny().orElse(null);
        CicloAcademico cicloPosgrado = ciclosActivos.stream().filter(x -> x.getModalidadEstudio().getCodigoEnum() == EPG).findAny().orElse(null);

        List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
        List<AlumnoCiclo> alumnosCiclosAll = alumnoCicloDAO.allByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
        List<Reincorporacion> reincorporaciones = reincorporadosService.allReincorporacionesByCicloActivo(alumnos, ciclosActivos);

        ListBeanPromedios bean = new ListBeanPromedios();
        bean.setCiclos(ciclos);
        bean.setCiclosActivos(ciclosActivos);
        bean.setCicloPregrado(cicloPregrado);
        bean.setCicloPosgrado(cicloPosgrado);
        bean.setEgresados(egresados);
        bean.setAlumnosCiclosAll(alumnosCiclosAll);
        bean.setAlumnosCiclosCursosActivos(alumnosCiclosCursosActivos);
        bean.setAlumnosCiclosCursosAll(alumnosCiclosCursosAll);
        bean.setReincorporaciones(reincorporaciones);

        return bean;
    }

}
