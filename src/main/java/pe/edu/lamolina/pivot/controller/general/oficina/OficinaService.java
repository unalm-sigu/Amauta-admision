package pe.edu.lamolina.pivot.controller.general.oficina;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OficinaService {

    List<Oficina> allByDynatable(DynatableFilter filter, Compania compania);

    Oficina find(Oficina oficina);

    void update(Oficina oficina, DataSessionPivot ds);

    void save(Oficina oficina, DataSessionPivot ds);

//    void delete(Oficina oficina);
    List<Colaborador> allColaborador(List<Oficina> oficinas);

    List<Oficina> allUnidadSuperior(String nombre, Compania compania);

    List<DepartamentoAcademico> allDepartamento(Compania compania);

    List<Carrera> allCarrera(Compania compania);

    List<Facultad> allFacultad(Compania compania);

    void cambiarEstado(Oficina oficina, String accion);

    List<Persona> allPersona(String nombre);

    List<Colaborador> allColaboradorByOficina(Oficina oficina);

    List<PerfilCompania> allCargo(String nombre);

    void fillReferencia(Oficina oficina);

    void asignarJefe(Oficina oficina, DataSessionPivot ds);

    void retirarJefe(Oficina oficina, DataSessionPivot ds);

    void asignarEncargado(Oficina oficina, DataSessionPivot ds);

    void retirarEncargado(AusenciaJefe ausencia, DataSessionPivot ds);

    List<Oficina> allOficina(Persona persona);

    Colaboradores countColaborador(Oficina oficina);

    ArrayNode getColaboradoresJson(DynatableFilter filter, Oficina oficinaMain);

    void updateEstado(Colaborador colaborador, DataSessionPivot dataSessionPivot);

    List<TipoOficina> allTipoOficina();

    public TipoOficina findTipoById(String id);

    public Colaborador findColarador(Colaborador colaborador);

    List<TipoDocIdentidad> allDocumentosIdentidad();

    public List<Oficina> allOficinasByOficinaMain(Oficina oficina);

    public List<PerfilCompania> allCargos(Oficina oficina);

    public void saveColaborador(Colaborador colaborador, Oficina oficinaMean, Usuario usuario, Compania compania);

    void updateColaborador(Colaborador colaborador, Oficina oficinaMean, DataSessionPivot dataSessionPivot);

    public List<PerfilCompania> allFunciones();

    Persona verifiDocumento(Persona persona);

    Usuario verifiEmail(Persona persona);

    public Boolean saveColaboradorExistente(Colaborador colaborador, Oficina oficinaMean, Usuario usuario, Compania compania);

    List<PerfilCompania> allCargosByOficina(Oficina oficina);

    void addCargo(PerfilCompania perfilCompania, DataSessionPivot ds);

    List<Persona> allPersonasByNombre(String buscar);

    void addFuncion(PerfilCompania perfilCompania, DataSessionPivot dsp);

    List<PerfilCompania> allCargoByOficina(Oficina oficina);

    List<PerfilCompania> allFuncionByOficina(Oficina oficina);

    List<PerfilCompania> allFuncionByColaborador(Colaborador colaborador);

    List<Oficina> allOficinasMain(Persona persona);

    Oficina findOficinaHija(Persona persona, Oficina oficinaMain);

}
