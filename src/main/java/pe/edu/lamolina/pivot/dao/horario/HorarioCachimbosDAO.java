package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;

public interface HorarioCachimbosDAO extends EasyDAO<HorarioCachimbos> {

    List<HorarioCachimbos> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<HorarioCachimbos> allByCicloCarrera(CicloAcademico cicloAcademico, Carrera carrera);

    List<HorarioCachimbos> allByCiclo(CicloAcademico cicloAcademicoa);

    public HorarioCachimbos findMaxCodeOrderByCiclo(CicloAcademico ciclo);

    public HorarioCachimbos find(HorarioCachimbos horarioCachimbos);

}
