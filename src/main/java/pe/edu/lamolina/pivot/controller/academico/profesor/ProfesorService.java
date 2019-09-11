package pe.edu.lamolina.pivot.controller.academico.profesor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProfesorService {

    List<Docente> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> dptos);

    public List<Docente> allByDepartamentoDynatable(DynatableFilter filter, List<DepartamentoAcademico> facultades, CicloAcademico cicloAcademico);

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

    String getRutaFoto(String foto, String sexo);

    void update(Docente docente, DataSessionPivot ds);

    List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo, DataSessionPivot ds);

    ContenidoCarta findContenidoCartaByEnum(ContenidoCartaEnum enumval);

    DepartamentoAcademico findDepartamento(DepartamentoAcademico departamentoAcademico);

    Oficina findOficina(OficinaEnum oficinaEnum);

}
