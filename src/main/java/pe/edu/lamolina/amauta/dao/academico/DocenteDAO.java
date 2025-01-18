package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloBean;
import pe.edu.lamolina.amauta.controller.academico.profesor.DocenteCicloCargaBean;
import pe.edu.lamolina.amauta.controller.academico.profesor.HistoricoCargaAcademicoBean;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.FiltroHistoricoCargaAcademicaDTO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.HorarioDocenteDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.general.Persona;

public interface DocenteDAO extends EasyDAO<Docente> {

    List<Docente> allByFacultadesDyantable(DynatableFilter filter, List<DepartamentoAcademico> departamento, String activo);

    Docente findByCode(String codigo);

    List<Docente> allByPersona(Persona persona);

    List<Docente> allByPersonas(List<Persona> personas);

    List<Docente> allActivos(ModalidadEstudio modalidad);

    List<Docente> allByFilter(DynatableFilter filter, List<DepartamentoAcademico> dptos);

    Docente findByDocente(Docente docente);

    List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

    List<Docente> allByNombreFilter(String nombre, Integer cantidad, String codigoDep);

    List<Docente> allByDptoEstado(Long idDpto, String estado);

    List<Docente> allByNombreDepartamento(String nombre, DepartamentoAcademico departamento, int limit);

    List<Docente> allByName(String nombre);

    List<Docente> allByNombreDepartamentos(String nombre, List<DepartamentoAcademico> departamentos);

    List<Docente> allByDepartamentosAcademicoEstado(List<DepartamentoAcademico> departamentos, EnteAcademicoEstadoEnum estado);

    List<Docente> allByNombreActivoFilter(String nombre, Integer cantidad, String codigoDep);

    List<DocenteCicloBean> AllDocentecicloAcademico(List<CicloAcademico> cicloAcademicos);

    List<DocenteCicloCargaBean> AllDocentecicloCargaAcademico(Long docente);

    List<HorarioDocenteDTO> horarioDocente(CicloAcademico cicloAcademico, String id);

    List<HistoricoCargaAcademicoBean> allHistoricoCargaAcademica(FiltroHistoricoCargaAcademicaDTO filtro);

    List<Docente> allByCodeNN(String codigo);
}
