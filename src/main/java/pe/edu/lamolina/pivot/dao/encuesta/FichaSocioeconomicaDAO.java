package pe.edu.lamolina.pivot.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;

public interface FichaSocioeconomicaDAO extends EasyDAO<FichaSocioeconomica> {

    FichaSocioeconomica findByAlumno(Alumno alumno, CicloAcademico cicloAcademico);
}
