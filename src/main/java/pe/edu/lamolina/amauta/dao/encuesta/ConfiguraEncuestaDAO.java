package pe.edu.lamolina.amauta.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.examen.ExamenVirtual;

public interface ConfiguraEncuestaDAO extends EasyDAO<ConfiguraEncuesta> {

    ConfiguraEncuesta findByEncuesta(EncuestaEstudiantil encuestaEstudiantil);

    ConfiguraEncuesta find(ConfiguraEncuesta configuraEncuestaForm);

    ConfiguraEncuesta findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta);

    void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta);
}
