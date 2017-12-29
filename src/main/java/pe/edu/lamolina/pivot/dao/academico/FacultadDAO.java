package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Compania;

public interface FacultadDAO extends EasyDAO<Facultad> {

    List<Facultad> allDynatable(DynatableFilter filter);

    List<Facultad> allByCompania(Compania compania);

    List<Facultad> allActivos();

    List<Facultad> allFacultad(String nombre, Compania compania);

}
