package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorServiceImp.CantidadItemsEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface VerificadorService {

    void revisarPermiso(HttpServletRequest request, DataSessionPivot ds);

    CantidadItemsEnum verificarCantidad(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds);

    <T> List<T> allInstanciasByMenuRol(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds);

    boolean puedeOperarMatricula(DataSessionPivot ds);

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

}
