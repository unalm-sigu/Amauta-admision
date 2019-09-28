package pe.edu.lamolina.pivot.controller.general.oficina.colaborador;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ColaboradorService {

    Oficina findOficina(Oficina oficina);//

//    void update(Oficina oficina, DataSessionPivot ds);
//
//    void save(Oficina oficina, DataSessionPivot ds);
//
//    List<Colaborador> allColaborador(List<Oficina> oficinas);
//
//    List<Oficina> allUnidadSuperior(String nombre, Compania compania);
//
//    List<DepartamentoAcademico> allDepartamento(Compania compania);
//
//    List<Carrera> allCarrera(Compania compania);
//
//    List<Facultad> allFacultad(Compania compania);
//
//    void cambiarEstado(Oficina oficina, String accion);
//
//    List<Persona> allPersona(String nombre);
//
//    List<Colaborador> allColaboradorByOficina(Oficina oficina);
//
//    List<PerfilCompania> allCargo(String nombre);
//
//    void fillReferencia(Oficina oficina);
//
//    void asignarJefe(Oficina oficina, DataSessionPivot ds);
//
//    void retirarJefe(Oficina oficina, DataSessionPivot ds);
//
//    void asignarEncargado(Oficina oficina, DataSessionPivot ds);
//
//    void retirarEncargado(AusenciaJefe ausencia, DataSessionPivot ds);
//
//    List<Oficina> allOficina(Persona persona);

    ResumenColaborador getResumenColoboradores(Oficina oficina);//

    //ArrayNode getColaboradoresJson(DynatableFilter filter, Oficina oficinaMain);

    List<Colaborador> getColaboradores(DynatableFilter filter, Oficina oficina);//

    List<FuncionColaborador> allFuncionesByColaboradores(List<Colaborador> colaboradores);//

    void updateEstado(Colaborador colaborador, Oficina oficina, DataSessionPivot ds);//

    //List<TipoOficina> allTipoOficina();

    //TipoOficina findTipoById(String id);

    Colaborador findColaborador(Colaborador colaborador);//

    List<TipoDocIdentidad> allDocumentosIdentidad();//

    List<Oficina> allAreasByOficinaMain(Oficina oficina);//

    //List<PerfilCompania> allCargos(Oficina oficina);

    void saveColaborador(Colaborador colaborador, Oficina oficinaMean, Compania compania, DataSessionPivot ds);//

    void updateColaborador(Colaborador colaborador, Oficina oficinaMean, DataSessionPivot ds);//

    //List<PerfilCompania> allFunciones();

    Persona verificarDocumento(Persona persona);//

    Usuario verificarEmail(Persona persona);//

    Boolean saveColaboradorExistente(Colaborador colaborador, Oficina oficinaMean, Compania compania, DataSessionPivot ds);//

    //List<PerfilCompania> allCargosByOficina(Oficina oficina);

    List<Persona> allPersonasByNombre(String buscar);//

    void addCargo(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds);//

    void addFuncion(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allCargoByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allCargoByOficina(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByOficina(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByColaborador(Colaborador colaborador);//

    //List<Oficina> allOficinasMainByPersona(Persona persona);

    //Oficina findOficinaHija(Persona persona, Oficina oficinaMain);

}
