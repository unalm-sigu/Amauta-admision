package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

public interface CicloAcademicoDAO extends EasyDAO<CicloAcademico> {

    CicloAcademico findActivo(ModalidadEstudio modalidad);

    CicloAcademico findActivo(ModalidadEstudioEnum modalidadEnum);

    CicloAcademico findByCodigo(String codigo);

    List<CicloAcademico> allForChanges(Integer maxResultado, ModalidadEstudio modalidad);

    CicloAcademico findAnteriorRegular(CicloAcademico ciclo);

    CicloAcademico findAnteriorActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo);

    List<CicloAcademico> allActivos();

    List<CicloAcademico> allUltimos(Integer cantidadCiclos);

    List<CicloAcademico> allPregradoByRange(int yearinit, int yearend);

    CicloAcademico findSiguienteNivelacionActivo(CicloAcademico ciclo);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    List<CicloAcademico> allByDynatable(DynatableFilter filter);

    CicloAcademico findCicloAcademicoActivo();

    CicloAcademico findCicloAcademicoActivoPRE();

    void updateActualizarBoletin(CicloAcademico cicloAcademico);

    CicloAcademico findCicloAcademicoActivoByModalidad(ModalidadEstudio modalidadEstudio);

    void updateFechaMatriculables(CicloAcademico cicloAcademico);

    void updateFechaPrioridades(CicloAcademico cicloAcademico);

    void updateFechaTurnosAsignados(CicloAcademico cicloAcademico);

    void updateFechasTurnosAignadosDisponibles(CicloAcademico cicloAcademico);

    CicloAcademico find(CicloAcademico cicloAcademico);

    List<CicloAcademico> all();

    List<CicloAcademico> allActivesByModalidad(ModalidadEstudio modalidad, String[] orderBy);

    List<CicloAcademico> allCicloByName(String nombre);

    List<CicloAcademico> allCicloByNameExceptList(String nombre, List<CicloAcademico> ciclos);

    CicloAcademico findByCodigoModalidadEstudio(String codigoCiclo, ModalidadEstudio modalidad);

    List<CicloAcademico> allUltimosByNext(Integer cantidadCiclos, List<CicloAcademico> actives);

    List<CicloAcademico> allByModalidadEstudioName(ModalidadEstudio modalidad, String nombre);

    List<CicloAcademico> allCicloByNameDescendent(String nombre, ModalidadEstudio modalidad);

    List<CicloAcademico> allAnteriores(int ciclos, CicloAcademico cicloAcademico);

    List<CicloAcademico> allByEstados(List<String> estados);

    List<CicloAcademico> allByLikeName(String nombre, ModalidadEstudio modalidad, List<CicloAcademico> notInt, Integer limit);
}
