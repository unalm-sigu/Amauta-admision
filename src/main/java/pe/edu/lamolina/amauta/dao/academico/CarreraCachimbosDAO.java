package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface CarreraCachimbosDAO extends EasyDAO<CarreraCachimbos> {

    List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<CarreraCachimbos> allByCicloAcademico(CicloAcademico cicloAcademico);

    public CarreraCachimbos findByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico);

    public void allRegenerateByCiclo(CicloAcademico cicloAcademico);

    public List<CarreraCachimbos> allCarreraCachimbosByCarreras(List<Carrera> carreras, CicloAcademico cicloAcademico);

}
