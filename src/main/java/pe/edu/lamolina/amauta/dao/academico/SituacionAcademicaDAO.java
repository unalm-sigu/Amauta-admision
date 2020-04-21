package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;

public interface SituacionAcademicaDAO extends EasyDAO<SituacionAcademica> {

    SituacionAcademica findByCodigo(String codigo);

    List<SituacionAcademica> allByCodes(List<SituacionAcademicaEnum> asList);

}
