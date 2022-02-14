package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MatriculaBloqueoAlumnoServiceImp implements MatriculaBloqueoAlumnoService {

    @Autowired
    MatriculaBloqueoAlumnoDAO matriculaBloqueoAlumnoDAO;
    
    @Override
    public List<MatriculaBloqueoAlumno> all(DynatableFilter filter) {
       return matriculaBloqueoAlumnoDAO.allDynatable(filter);
    }

    @Override
    @Transactional
    public void save(MatriculaBloqueoAlumno matriculaBloqueoAlumno, DataSessionPivot ds) {
        matriculaBloqueoAlumno.setUserRegistro(ds.getUsuario());
        matriculaBloqueoAlumno.setFechaRegistro(new Date());
        matriculaBloqueoAlumnoDAO.save(matriculaBloqueoAlumno);
    }

    @Override
    @Transactional
    public void update(MatriculaBloqueoAlumno matriculaBloqueoAlumno) {
        matriculaBloqueoAlumnoDAO.update(matriculaBloqueoAlumno);
    }

    @Override
    @Transactional
    public void eliminar(Long idMatriculaBloqueoAlumno) {
        matriculaBloqueoAlumnoDAO.delete(idMatriculaBloqueoAlumno);
    }

    @Override
    public MatriculaBloqueoAlumno find(Long idMatriculaBloqueoAlumno) {
        return matriculaBloqueoAlumnoDAO.find(idMatriculaBloqueoAlumno);
    }


}
