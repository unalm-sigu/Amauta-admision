package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.academico.anexoboletin.AnexoResumen;

public interface AnexoBoletinDAO extends EasyDAO<AnexoBoletin> {

    List<AnexoBoletin> allByDynatable(DynatableFilter filter);

    List<AnexoBoletin> allAnexosSuperiores();

    AnexoBoletin find(Long id);

    AnexoResumen resumen();

    List<AnexoBoletin> allAnexosHijos();

    List<AnexoBoletin> allBySuperiorCiclo(AnexoBoletin anexoSuperior, CicloAcademico ciclo);

}
