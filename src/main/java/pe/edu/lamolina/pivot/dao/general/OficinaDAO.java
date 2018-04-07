package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;

public interface OficinaDAO extends EasyDAO<Oficina> {

    List<Oficina> allByJefe(Persona persona);

    List<Oficina> allByFilter(DynatableFilter filter, Compania compania);

    List<Oficina> allUnidadSuperior(String nombre, Compania compania);

    List<Oficina> allOficinasByName(String nombre);

    List<Oficina> allByOficinaWithAulas(List<Oficina> oficinas);

    List<Oficina> allByUser(Persona persona);

    List<Oficina> allAndSuperiorOfi();

}
