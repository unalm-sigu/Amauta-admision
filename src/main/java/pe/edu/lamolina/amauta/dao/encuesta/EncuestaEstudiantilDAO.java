package pe.edu.lamolina.amauta.dao.encuesta;

import java.math.BigInteger;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;

public interface EncuestaEstudiantilDAO extends EasyDAO<EncuestaEstudiantil> {

    EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta);

    EncuestaEstudiantil findByCicloTipo(CicloAcademico cicloAcademico, TipoExamenVirtualEnum tipoEnum);

    List<EncuestaEstudiantil> allByEncuestas(List<ExamenVirtual> encuestas);

    BigInteger countEncuestaAlumno(CicloAcademico cicloAcademico, ModalidadEstudioEnum modalidadEstudioEnum, EncuestaEstudiantilEstadoEnum encuestaEstudiantilEstadoEnum);

}
