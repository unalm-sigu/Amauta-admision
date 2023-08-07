package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.examen.EncuestaPublicada;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

public interface EncuestaPublicadaDAO extends EasyDAO<EncuestaPublicada> {

    List<EncuestaPublicada> allByCicloTipo(CicloAcademico ciclo, TipoExamenVirtual tipoEncuesta);

}
