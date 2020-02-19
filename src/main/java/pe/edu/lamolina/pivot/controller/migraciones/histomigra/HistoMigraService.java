package pe.edu.lamolina.pivot.controller.migraciones.histomigra;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;

public interface HistoMigraService {

    Alumno findAlumno(Alumno alumno);

    List<HistoMy> allHistoByAlumno(Alumno alumno);

    List<HistoGradMy> allHistoGradByAlumno(Alumno alumno);

    List<Curso> allCursosByHisto(List<HistoMy> historias);

    List<Curso> allCursosByHistoGrad(List<HistoGradMy> historiasGrad);

    List<AlumnoCicloCurso> allAlumnoCursoByAlumno(Alumno alumno);

}
