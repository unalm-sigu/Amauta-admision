package pe.edu.lamolina.amauta.controller.general.oficina.colaborador;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface ColaboradorService {

    Oficina findOficina(Oficina oficina);

    ResumenColaborador getResumenColoboradores(Oficina oficina);//

    List<Colaborador> getColaboradores(DynatableFilter filter, Oficina oficina);//

    List<FuncionColaborador> allFuncionesByColaboradores(List<Colaborador> colaboradores);//

    void updateEstado(Colaborador colaborador, Oficina oficina, DataSessionPivot ds);//

    Colaborador findColaborador(Colaborador colaborador);//

    List<TipoDocIdentidad> allDocumentosIdentidad();//

    List<Oficina> allAreasByOficinaMain(Oficina oficina);//

    void saveColaborador(Colaborador colaborador, Oficina oficinaMean, Compania compania, DataSessionPivot ds);//

    void updateColaborador(Colaborador colaborador, Oficina oficinaMean, DataSessionPivot ds);//

    Persona verificarDocumento(Persona persona);//

    Usuario verificarEmail(Persona persona);//

    Boolean saveColaboradorExistente(Colaborador colaborador, Oficina oficinaMean, Compania compania, DataSessionPivot ds);//

    List<Persona> allPersonasByNombre(String buscar);//

    void addCargo(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds);//

    void addFuncion(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allCargoByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allCargoByOficina(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByOficina(Oficina oficina, DataSessionPivot ds);//

    List<PerfilCompania> allFuncionByColaborador(Colaborador colaborador);//

}
