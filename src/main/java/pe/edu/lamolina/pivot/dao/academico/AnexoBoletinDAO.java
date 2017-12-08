package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.controller.academico.anexoboletin.AnexoResumen;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;

public interface AnexoBoletinDAO extends Crud<AnexoBoletin> {

    List<AnexoBoletin> allByDynatable(DynatableFilter filter);

    List<AnexoBoletin> allAnexosSuperiores();

    AnexoBoletin find(Long id);

    AnexoResumen resumen();

    List<AnexoBoletin> allAnexosHijos();

}
