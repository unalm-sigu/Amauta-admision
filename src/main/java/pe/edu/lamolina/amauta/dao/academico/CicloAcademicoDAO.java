package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

public interface CicloAcademicoDAO extends EasyDAO<CicloAcademico> {

    List<CicloAcademico> allCiclos();

    CicloAcademico findActivo(ModalidadEstudio modalidad);

    CicloAcademico findActivo(ModalidadEstudioEnum modalidadEnum);

    CicloAcademico findByCodigo(String codigo);

    List<CicloAcademico> allForChanges(Integer maxResultado, ModalidadEstudio modalidad);

    List<CicloAcademico> findAnteriorRegular(CicloAcademico ciclo);

    CicloAcademico findAnteriorActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo);

    List<CicloAcademico> allActivos();

    List<CicloAcademico> allUltimos(Integer cantidadCiclos);

    List<CicloAcademico> allUltimosByModalidad(ModalidadEstudio modalidad, Integer cantidadCiclos);

    List<CicloAcademico> allPregradoByRange(int yearinit, int yearend);

    CicloAcademico findSiguienteNivelacionActivo(CicloAcademico ciclo);

    CicloAcademico findByCiclo(CicloAcademico cicloAcademico);

    List<CicloAcademico> allByDynatable(DynatableFilter filter);

    CicloAcademico findActivo();

    CicloAcademico findActivoPregrado();

    CicloAcademico findActivoObu();

    CicloAcademico findActivoAdmision();

    CicloAcademico findActivoSubvenciones();

    void updateActualizarBoletin(CicloAcademico cicloAcademico);

    CicloAcademico findActivoByModalidad(ModalidadEstudio modalidadEstudio);

    void updateFechaMatriculables(CicloAcademico cicloAcademico);

    void updateFechaPrioridades(CicloAcademico cicloAcademico);

    void updateFechaTurnosAsignados(CicloAcademico cicloAcademico);

    void updateFechasTurnosAignadosDisponibles(CicloAcademico cicloAcademico);

    CicloAcademico find(CicloAcademico cicloAcademico);

    CicloAcademico find(CicloAcademico cicloAcademico, ModalidadEstudioEnum modalidadEnum);

    List<CicloAcademico> allActivesByModalidad(ModalidadEstudio modalidad, String[] orderBy);

    List<CicloAcademico> allCicloByName(String nombre);

    List<CicloAcademico> allCicloByNameExceptList(String nombre, List<CicloAcademico> ciclos);

    CicloAcademico findByCodigoModalidadEstudio(String codigoCiclo, ModalidadEstudio modalidad);

    CicloAcademico findByCodigoCicloModalidadEnum(String codigoCiclo, ModalidadEstudioEnum modalidadEnum);

    CicloAcademico findByCodigoAnteriorModalidadEnum(String codigoCiclo, ModalidadEstudioEnum modalidadEnum);

    List<CicloAcademico> allUltimosByNext(Integer cantidadCiclos, List<CicloAcademico> actives);

    List<CicloAcademico> allByModalidadEstudioName(ModalidadEstudio modalidad, String nombre);

    List<CicloAcademico> allCicloByNameDescendent(String nombre, ModalidadEstudio modalidad);

    List<CicloAcademico> allAnteriores(int ciclos, CicloAcademico cicloAcademico);

    List<CicloAcademico> allByEstados(List<String> estados);

    List<CicloAcademico> allByCodigo(String codigo);

    List<CicloAcademico> allByLikeName(String nombre, ModalidadEstudio modalidad, List<CicloAcademico> notInt, Integer limit);

    CicloAcademico findVerBoletin(ModalidadEstudioEnum modalidadEnum);

    List<CicloAcademico> allWithInitAndOrderBy(int yearIni, String orderBy, CicloAcademicoEstadoEnum... cicloAcademicoEstadoEnum);

    CicloAcademico findSiguienteConfOrAct(CicloAcademico cicloAcademico);

    List<CicloAcademico> allAnteriorRegistroActivoPre(int count, CicloAcademico cicloAcademico);

    List<CicloAcademico> allAnteriorRegistroActivoPos(int ciclos, CicloAcademico cicloAcademico);

    CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo, ModalidadEstudioEnum modalidadEstudioEnum);

    List<CicloAcademico> allVisibles(ModalidadEstudioEnum modalidadEstudioEnum);

    List<CicloAcademico> allActivosRegularAnteriores(int i, CicloAcademico ciclo);

    CicloAcademico findSiguienteNivelacionActivo(CicloAcademico cicloActivo, ModalidadEstudioEnum modalidadEstudioEnum);

    List<CicloAcademico> allActivosAlModalidades();

    List<CicloAcademico> allRegularPre(int i, CicloAcademico academico);

    List<CicloAcademico> allWithInitAndOrderBy(String codigo, String orderBy, CicloAcademicoEstadoEnum... cicloAcademicoEstadoEnum);

    List<CicloAcademico> allCicloOrdenMerito(CicloAcademico cicloActivo, CicloAcademico cicloDesde);

    List<CicloAcademico> allAnterioresRegistroActivoPre(int i, CicloAcademico cicloAcademico);

    List<CicloAcademico> allMenorIgual(int ciclos, CicloAcademico ciclo);

    CicloAcademico findByCodigoAndModalidad(String string, ModalidadEstudioEnum modalidadEstudioEnum);

    List<CicloAcademico> allUltimosByModalidadEnum(ModalidadEstudioEnum modalidadEstudioEnum, int i);

    CicloAcademico findConfiguradoPregrado();

    CicloAcademico findAnterior(CicloAcademico ciclo);

    CicloAcademico findActivoByModalidadEstudio(ModalidadEstudioEnum codigoEnum);

    List<CicloAcademico> allContrato();

    List<CicloAcademico> allPregradoFuturosByRange(int yearinit, int yearend);

    List<CicloAcademico> allPregradoByRangeCode(int codeInit, int codeEnd);

    List<CicloAcademico> allPregradoNivelByRange(int yearinit, int yearend);

    List<CicloAcademico> allMenorRegularPreByCantidad(int maxResultado, CicloAcademico cicloAcademico);

    List<CicloAcademico> allByYearModalidadEnum(int year, ModalidadEstudioEnum modalidadEnum);

}
