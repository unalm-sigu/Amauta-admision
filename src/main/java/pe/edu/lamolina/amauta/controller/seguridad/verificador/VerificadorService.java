package pe.edu.lamolina.amauta.controller.seguridad.verificador;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp.CantidadItemsEnum;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;

public interface VerificadorService {

    String generateCodeRequest();

    void revisarPermiso(HttpServletRequest request, DataSessionPivot ds);

    CantidadItemsEnum verificarCantidad(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds);

    <T> List<T> allInstanciasByMenuRol(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds, String codeRequest);

    boolean puedeOperarMatricula(DataSessionPivot ds);

    boolean puedeMatricularPosgrado(DataSessionPivot ds);

    boolean puedeEditarAlumno(DataSessionPivot ds);

    boolean puedeEditarOficinas(DataSessionPivot ds);

    boolean puedeGestionarSuOficina(DataSessionPivot ds);

    boolean puedeVerOficina(Oficina oficina, DataSessionPivot ds);

    List<Oficina> allOficinasAcceso(DataSessionPivot ds);

    String getOrigen(String origen, String defecto);

    Oficina findOficina(Oficina oficina);

    boolean isGestorOficinaEPG(DataSessionPivot ds);

    boolean isEditorEncuestas(DataSessionPivot ds);

    boolean isRevisorEncuestas(DataSessionPivot ds);

    boolean isRevisorCurriculas(DataSessionPivot ds);

    boolean puedeVerAllFacultades(DataSessionPivot ds, String contexto);

    boolean puedeVerAllDepartamentos(DataSessionPivot ds, String contexto);

    boolean isEditorCurriculas(DataSessionPivot ds);

    boolean isEditorCurriculasAll(DataSessionPivot ds);

    boolean isEditorCurriculasEpg(DataSessionPivot ds);

    List<ModalidadEstudio> modalidadesPermitidasForCursos(DataSessionPivot ds, List<ModalidadEstudio> modalidades);

    List<Oficina> allOficinasAccesoByRolEnum(DataSessionPivot ds, RolEnum rolEnum);

    List<AnexoBoletin> anexosSuperioresByOficina(DataSessionPivot ds);

    List<AnexoBoletin> anexosInferioresByOficina(DataSessionPivot ds, List<AnexoBoletin> anexos);

    boolean isOperadorActaNotas(DataSessionPivot ds);

    boolean isOperadorGastoPosgrado(DataSessionPivot ds);

    boolean puedeEditarAnexos(DataSessionPivot ds);

    boolean puedeEditarAnexosPosgrado(DataSessionPivot ds);

    boolean isEditorProgramacionOera(DataSessionPivot ds);

    boolean isEditorProgramacionMaestria(DataSessionPivot ds);

    boolean puedeVerHeadAlumno(DataSessionPivot ds);

    boolean isTrabajadorOera(DataSessionPivot ds);

    boolean isRevisorActaNotas(DataSessionPivot ds);

    boolean isRolCape(DataSessionPivot ds);

    boolean isRolRacd(DataSessionPivot ds);

    boolean isDeveloperOERA(DataSessionPivot ds);

    boolean esCoordinadorIOREA(DataSessionPivot ds);

    boolean isRevisorActaNotasDepartamento(DataSessionPivot ds);

    boolean esInformaticoOERA(DataSessionPivot ds);
    
    boolean esAdministradorTutoria(DataSessionPivot ds);

    boolean esOperadorEEGG(DataSessionPivot ds);

    boolean soloEditarDatosAlumno(DataSessionPivot ds);

    boolean esConsejeroCarrera(DataSessionPivot ds, Carrera carrera);

    boolean esCoordinadorConsejeria(DataSessionPivot ds, Carrera carrera);

    boolean esJefeCarrera(DataSessionPivot ds, Carrera carrera);

}
