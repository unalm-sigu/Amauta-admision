package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;

public interface PeriodoEncuestaDAO extends EasyDAO<PeriodoEncuesta> {

    List<PeriodoEncuesta> allByEncuesta(EncuestaEstudiantil encuestaEstudiantil);

    void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta);

}
