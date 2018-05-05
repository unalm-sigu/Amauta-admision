package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.oficina.Colaboradores;

public interface ColaboradorDAO extends EasyDAO<Colaborador> {

    List<Colaborador> allColaborador(List<Oficina> oficinas);

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

    List<Colaborador> allActivosByPersona(Persona persona);

    Colaboradores countColaboradores(List<Oficina> oficina);

    List<Colaborador> allByOficina(DynatableFilter filter, List<Oficina> oficinas);

    Colaborador findMaxCodigo();

    Colaborador find(Colaborador colaborador);

    Colaborador allActivosByPersonaAndOficina(Oficina oficina, Persona persona);

    public List<Colaborador> allByName(String nombre);
}
