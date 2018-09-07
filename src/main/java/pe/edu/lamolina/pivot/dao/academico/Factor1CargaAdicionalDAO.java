package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;

public interface Factor1CargaAdicionalDAO extends EasyDAO<Factor1CargaAdicional> {

    public List<Factor1CargaAdicional> allByCicloAcademico(CicloAcademico cicloAcademico);

    public List<Factor1CargaAdicional> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

}

