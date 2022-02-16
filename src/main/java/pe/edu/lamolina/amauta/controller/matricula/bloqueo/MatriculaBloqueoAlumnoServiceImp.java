package pe.edu.lamolina.amauta.controller.matricula.bloqueo;

import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoAlumno;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MatriculaBloqueoAlumnoServiceImp implements MatriculaBloqueoAlumnoService {

    @Autowired
    MatriculaBloqueoAlumnoDAO matriculaBloqueoAlumnoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<MatriculaBloqueoAlumno> all(DynatableFilter filter) {
        return matriculaBloqueoAlumnoDAO.allDynatable(filter);
    }

    @Override
    @Transactional
    public void save(MatriculaBloqueoAlumnoDTO matriculaBloqueoAlumno, DataSessionPivot ds) {
        for (SituacionAcademica situacionAcademica : matriculaBloqueoAlumno.getSituacionAcademicas()) {
            MatriculaBloqueoAlumno matriculaBloqueoAlumnoNew = new MatriculaBloqueoAlumno();
            matriculaBloqueoAlumnoNew.setUserRegistro(ds.getUsuario());
            matriculaBloqueoAlumnoNew.setFechaRegistro(new Date());
            matriculaBloqueoAlumnoNew.setSituacionAcademica(situacionAcademica);
            matriculaBloqueoAlumnoNew.setCicloAplica(matriculaBloqueoAlumno.getCicloAplica());
            matriculaBloqueoAlumnoNew.setCarrera(matriculaBloqueoAlumno.getCarrera());
            
            MatriculaBloqueoAlumno matriculaBloqueoAlumnoDB = matriculaBloqueoAlumnoDAO.findByCicloCarreraSituacion(
                    matriculaBloqueoAlumno.getCicloAplica(), matriculaBloqueoAlumno.getCarrera(), situacionAcademica);
            
            if (matriculaBloqueoAlumnoDB != null) {
                continue;
            }
            
            matriculaBloqueoAlumnoDAO.save(matriculaBloqueoAlumnoNew);
        }
    }

    @Override
    @Transactional
    public void update(MatriculaBloqueoAlumno matriculaBloqueoAlumno) {
        matriculaBloqueoAlumnoDAO.updateColumns(matriculaBloqueoAlumno, "cicloAplica", "situacionAcademica", "carrera");
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

    @Override
    public List<Carrera> allCarrera() {
        return carreraDAO.allCarrerasActivaByModalidad(PRE.name());
    }

    @Override
    public List<SituacionAcademica> allSituacionAcademica() {
        return situacionAcademicaDAO.all();
    }

    @Override
    public List<CicloAcademico> allCicloAcademico(DataSessionPivot ds) {
        CicloAcademico ca = ds.getCicloAcademico();
        int rango = 10;
        return cicloAcademicoDAO.allPregradoFuturosByRange(ca.getYear() - rango, ca.getYear() + 4);
    }

}
