package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OficinaService {

    List<Oficina> allByDynatable(DynatableFilter filter, Compania compania);

    Oficina find(Oficina oficina);

    void update(Oficina oficina, DataSessionPivot ds);

    void save(Oficina oficina, DataSessionPivot ds);

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

    void fillReferencia(Oficina oficina);

    void asignarJefe(Oficina oficina, DataSessionPivot ds);

    void retirarJefe(Oficina oficina, DataSessionPivot ds);

    void asignarEncargado(Oficina oficina, DataSessionPivot ds);

    void retirarEncargado(AusenciaJefe ausencia, DataSessionPivot ds);

}
