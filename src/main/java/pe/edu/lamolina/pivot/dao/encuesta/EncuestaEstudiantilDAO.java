package pe.edu.lamolina.pivot.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;

public interface EncuestaEstudiantilDAO extends EasyDAO<EncuestaEstudiantil> {

    EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta);

    EncuestaEstudiantil allByCicloTipo(CicloAcademico cicloAcademico, ModalidadEstudio modalidad, TipoExamenVirtualEnum tipoExamenVirtualEnum);

}
