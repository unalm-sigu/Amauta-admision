package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface OficinaDAO extends Crud<Oficina> {

    List<Oficina> allByJefe(Persona persona);

    public List<Oficina> allByFilter(DynatableFilter filter, Compania compania);

    public List<Oficina> allUnidadSuperior(String nombre, Compania compania);
    public List<Oficina> allOficinasByName(String nombre);

}
