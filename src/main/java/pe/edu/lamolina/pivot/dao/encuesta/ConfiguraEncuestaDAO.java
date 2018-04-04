package pe.edu.lamolina.pivot.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.examen.ExamenVirtual;

public interface ConfiguraEncuestaDAO extends EasyDAO<ConfiguraEncuesta> {

    public ConfiguraEncuesta findByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

    public ConfiguraEncuesta find(ConfiguraEncuesta configuraEncuestaForm);

    public ConfiguraEncuesta findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta);

    public ConfiguraEncuesta findConfiguraEncuestaByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil);

}
