package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.GradoAcademico;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;

public interface GradoAcademicoDAO extends EasyDAO<GradoAcademico> {

    public GradoAcademico findByTipoAndCarrera(TipoGradoAcademicoEnum tipoGradoAcademicoEnum, Carrera carrera);

}
