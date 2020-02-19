package pe.edu.lamolina.pivot.controller.migraciones.histomigra;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.migraciones.HistoGradMyDAO;
import pe.edu.lamolina.pivot.dao.migraciones.HistoMyDAO;

@Service
public class HistoMigraServiceImp implements HistoMigraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    HistoMyDAO histoMyDAO;
    @Autowired
    HistoGradMyDAO histoGradMyDAO;

    @Override
    public Alumno findAlumno(Alumno alumno) {
        return alumnoDAO.find(alumno);
    }

    @Override
    public List<HistoMy> allHistoByAlumno(Alumno alumno) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        return histoMyDAO.allByMatricula(alumnoBD.getCodigo());
    }

    @Override
    public List<HistoGradMy> allHistoGradByAlumno(Alumno alumno) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        return histoGradMyDAO.allByMatricula(alumnoBD.getCodigo());
    }

    @Override
    public List<Curso> allCursosByHisto(List<HistoMy> historias) {
        List<String> codigosAntiguos = historias.stream().map(x -> x.getHistoPK().getCurCodigo()).collect(Collectors.toList());
        return cursoDAO.allByCodigosAntiguos(codigosAntiguos);
    }

    @Override
    public List<Curso> allCursosByHistoGrad(List<HistoGradMy> historiasGrad) {
        List<String> codigosAntiguos = historiasGrad.stream().map(x -> x.getCurCodigo()).collect(Collectors.toList());
        return cursoDAO.allByCodigosAntiguos(codigosAntiguos);
    }

    @Override
    public List<AlumnoCicloCurso> allAlumnoCursoByAlumno(Alumno alumno) {
        return alumnoCicloCursoDAO.allByAlumno(alumno);
    }

}
