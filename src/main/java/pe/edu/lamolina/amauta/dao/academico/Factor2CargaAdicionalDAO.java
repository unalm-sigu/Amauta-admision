package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;

public interface Factor2CargaAdicionalDAO extends EasyDAO<Factor2CargaAdicional> {

    public List<Factor2CargaAdicional> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<Factor2CargaAdicional> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

}
