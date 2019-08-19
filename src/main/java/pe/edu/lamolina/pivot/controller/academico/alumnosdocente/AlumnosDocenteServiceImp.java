package pe.edu.lamolina.pivot.controller.academico.alumnosdocente;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;

@Service
@Transactional(readOnly = true)
public class AlumnosDocenteServiceImp implements AlumnosDocenteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public Seccion findSeccion(Long idSeccion) {
        return seccionDAO.find(idSeccion);
    }

    @Override
    public List<MatriculaSeccion> allMatriculadosBySeccion(Seccion seccion, CicloAcademico ciclo) {
        CicloAcademico cicloSecc = seccion.getGrupoSeccion().getCicloAcademico();
        if (cicloSecc.getId().compareTo(ciclo.getId()) != 0) {
            return new ArrayList();
        }

        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        return matriculados;
    }

    @Override
    public List<AlumnoConsejero> allAconsejadosByMatriculados(List<MatriculaSeccion> matriculados, CicloAcademico ciclo) {
        List<Alumno> alumnos = new ArrayList();
        for (MatriculaSeccion matriculado : matriculados) {
            alumnos.add(matriculado.getMatriculaResumen().getAlumno());
        }

        return alumnoConsejeroDAO.allByAlumnosCiclo(alumnos, ciclo);
    }

    @Override
    public List<Oficina> allConsejerias() {
        return oficinaDAO.allOficinaConsejero();
    }

}
