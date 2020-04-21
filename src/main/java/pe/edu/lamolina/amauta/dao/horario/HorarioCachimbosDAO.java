package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.HorarioCachimbos;

public interface HorarioCachimbosDAO extends EasyDAO<HorarioCachimbos> {

    List<HorarioCachimbos> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<HorarioCachimbos> allByCicloCarrera(CicloAcademico cicloAcademico, Carrera carrera);

    List<HorarioCachimbos> allByCiclo(CicloAcademico cicloAcademicoa);

    HorarioCachimbos findMaxCodeOrderByCiclo(CicloAcademico ciclo);

    HorarioCachimbos find(HorarioCachimbos horarioCachimbos);

    void deleteAllByCiclo(CicloAcademico cicloAcademico);

    void updateColumns(HorarioCachimbos horario, String... columns);

}
