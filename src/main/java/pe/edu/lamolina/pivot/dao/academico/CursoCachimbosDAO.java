package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;

public interface CursoCachimbosDAO extends EasyDAO<CursoCachimbos> {

    public List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    public CursoCachimbos findByCursoCiclo(CursoCachimbos cursoCachimbos);

    public List<CursoCachimbos> allCursoCachimbos(CicloAcademico cicloAcademico);

}

