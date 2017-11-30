package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.CarreraCachimbos;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface CarreraCachimbosDAO extends EasyDAO<CarreraCachimbos> {

    public List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

}

