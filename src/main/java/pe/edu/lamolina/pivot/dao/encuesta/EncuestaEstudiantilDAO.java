package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;

public interface EncuestaEstudiantilDAO extends EasyDAO<EncuestaEstudiantil> {

    EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta);

    EncuestaEstudiantil findByCicloTipo(CicloAcademico cicloAcademico, TipoExamenVirtualEnum tipoExamenVirtualEnum);

    List<EncuestaEstudiantil> allByEncuestas(List<ExamenVirtual> encuestas);

}
