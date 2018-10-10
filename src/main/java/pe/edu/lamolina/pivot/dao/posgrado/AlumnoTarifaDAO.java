package pe.edu.lamolina.pivot.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;

public interface AlumnoTarifaDAO extends EasyDAO<AlumnoTarifa> {

    List<AlumnoTarifa> allDynaTable(DynatableFilter filter);

}
