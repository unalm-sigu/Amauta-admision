package pe.edu.lamolina.amauta.controller.academico.profesor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.FiltroHistoricoCargaAcademicaDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;

public interface ProfesorService {

    List<Docente> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> dptos);

    public List<Docente> allByDepartamentoDynatable(DynatableFilter filter, List<DepartamentoAcademico> facultades, CicloAcademico cicloAcademico, String activo);

    List<Docente> allDocentesCargaByCiclo(DynatableFilter filter, List<DepartamentoAcademico> facultades, CicloAcademico cicloAcademico, String tipoPrograma);

    List<ModalidadEstudio> allModalidadEstudioByCodes(List<ModalidadEstudioEnum> codes, Compania compania);

    Docente find(Docente docente);

    List<TipoDocIdentidad> allDocumentos();

    void save(Docente docente, DataSessionPivot ds);

    String validarEmailByDocente(String email, Docente docente);

    String validarEmailEmpresaByDocente(String email, Docente docente);

    void estado(Docente docente);

    Persona findPersonaByDocIdentidad(Persona personaTmp);

    Docente findDocenteByDocente(Docente docente);

    List<ModalidadEstudio> allModalidadEstudio(Compania compania);

    Persona findPersona(Persona persona);

    Persona update(Docente docente, DataSessionPivot ds);

    List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    ContenidoCarta findContenidoCartaByEnum(ContenidoCartaEnum enumval);

    DepartamentoAcademico findDepartamento(DepartamentoAcademico departamentoAcademico);

    Oficina findOficina(OficinaEnum oficinaEnum);

    List<Docente> allDocenteByDepartamentosAcademicoEstado(List<DepartamentoAcademico> departamentos, EnteAcademicoEstadoEnum enteAcademicoEstadoEnum);

    List<DocenteSeccion> allDocenteSeccionActivosByDocentesCiclo(List<Docente> docentes, CicloAcademico cicloAcademico);

    List<HorarioSeccion> allHorarioSeccionBySecciones(List<Seccion> secciones);

    CicloAcademico findCicloAcademico(Long idCicloAcademico);

    List<CicloAcademico> allCicloAcademico();

    List<Docente> allByNombre(String nombre);

    List<DocenteSeccion> allDocenteSeccionActivosByDocentesCiclos(List<Docente> docentes, List<CicloAcademico> cicloAcademicos);

    List<CicloAcademico> allCicloAcademicoNivel();

    List<DocenteCicloBean> allDocentecicloAcademico(List<CicloAcademico> cicloAcademicos);

    List<DocenteCicloCargaBean> allDocenteCargacicloAcademico(Long docente);

    List<HistoricoCargaAcademicoBean> allHistoricoCargaAcademico(FiltroHistoricoCargaAcademicaDTO filtro, DataSessionPivot ds);

}
