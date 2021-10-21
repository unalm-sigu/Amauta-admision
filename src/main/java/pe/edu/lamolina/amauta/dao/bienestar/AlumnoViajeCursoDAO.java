package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.AlumnoViajeCurso;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

public interface AlumnoViajeCursoDAO extends EasyDAO<AlumnoViajeCurso> {

    List<AlumnoViajeCurso> allByViajeCurso(ViajeCurso viaje);

}
