package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import pe.edu.lamolina.pivot.model.general.Persona;

public interface OficinaService {

    List<Oficina> allByDynatable(DynatableFilter filter, Compania compania);

    Oficina find(Oficina oficina);

    void update(Oficina oficina);

    void save(Oficina oficina);

    void delete(Oficina oficina);

    List<Colaborador> allColaborador(List<Oficina> oficinas);

    List<Oficina> allUnidadSuperior(String nombre, Compania compania);

    List<DepartamentoAcademico> allDepartamento(Compania compania);

    List<Carrera> allCarrera(Compania compania);

    List<Facultad> allFacultad(Compania compania);

    void estado(Oficina oficina);

    List<Persona> allPersona(String nombre);

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

    List<PerfilCompania> allCargo(String nombre);

    public void fillReferencia(Oficina oficina);
}
