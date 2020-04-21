package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;

public interface ResumenEncuestaDocenteDAO extends EasyDAO<ResumenEncuestaDocente> {

    public List<ResumenEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente);

}

