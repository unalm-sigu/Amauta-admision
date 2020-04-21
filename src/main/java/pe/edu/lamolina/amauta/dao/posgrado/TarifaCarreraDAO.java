package pe.edu.lamolina.amauta.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.AmbitoTarifaEnum;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;

public interface TarifaCarreraDAO extends EasyDAO<TarifaCarrera> {

    List<TarifaCarrera> allByDynatable(DynatableFilter filter);

    List<TarifaCarrera> allByCarrera(Carrera carrera);

    List<TarifaCarrera> allByCarreraAmbito(Carrera carrera, AmbitoTarifaEnum ambito);

}
