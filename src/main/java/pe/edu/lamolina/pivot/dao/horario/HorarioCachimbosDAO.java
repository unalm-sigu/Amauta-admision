package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;

public interface HorarioCachimbosDAO extends EasyDAO<HorarioCachimbos> {

    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<HorarioCachimbos> allByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

}

