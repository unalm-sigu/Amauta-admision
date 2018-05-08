package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;

public interface CicloAcademicoDAO extends EasyDAO<CicloAcademico> {

    CicloAcademico findActivo(ModalidadEstudio modalidad);

    CicloAcademico findByCodigo(String codigo);

    List<CicloAcademico> allForChanges(Integer maxResultado);

    CicloAcademico findAnteriorRegular(CicloAcademico ciclo);

    CicloAcademico findAnteriorActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteActivo(CicloAcademico ciclo);

    CicloAcademico findSiguienteRegularActivo(CicloAcademico ciclo);

    List<CicloAcademico> allUltimos(Integer cantidadCiclos);

    List<CicloAcademico> allCicloAcademicoByRange(int yearinit, int yearend);

    CicloAcademico findSiguienteNivelacionActivo(CicloAcademico ciclo);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    List<CicloAcademico> allByDynatable(DynatableFilter filter);

    CicloAcademico findCicloAcademicoActivo();

    CicloAcademico findCicloAcademicoActivoByModalidad(ModalidadEstudio modalidadEstudio);

    void updateFechaMatriculables(CicloAcademico cicloAcademico);

    void updateFechaPrioridades(CicloAcademico cicloAcademico);

    void updateFechaTurnosAsignados(CicloAcademico cicloAcademico);

    void updateFechasTurnosAignadosDisponibles(CicloAcademico cicloAcademico);

    CicloAcademico find(CicloAcademico cicloAcademico);

    List<CicloAcademico> all();

    List<CicloAcademico> allCicloByNameExceptList(String nombre, List<CicloAcademico> ciclos);

}
