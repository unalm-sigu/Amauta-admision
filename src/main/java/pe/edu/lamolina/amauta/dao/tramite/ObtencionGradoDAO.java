package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.academico.graduado.GraduadoResumen;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Resolucion;

public interface ObtencionGradoDAO extends EasyDAO<ObtencionGrado> {

    List<ObtencionGrado> allByResolucion(Resolucion resolucion);

    List<ObtencionGrado> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    GraduadoResumen findResumenGraduados(List<Carrera> carreras, String todo);

    public ObtencionGrado findByAlumnoAndTipo(Alumno alumno, TipoGradoAcademicoEnum tipoGradoAcademicoEnum);

}
