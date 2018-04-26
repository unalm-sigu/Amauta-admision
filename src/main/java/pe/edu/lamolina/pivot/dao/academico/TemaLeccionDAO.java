package pe.edu.lamolina.pivot.dao.academico;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;

public interface TemaLeccionDAO extends EasyDAO<TemaLeccion> {

    List<TemaLeccion> allBySeccionDocenteFecha(Seccion seccion, Docente docente, Date fecha);

    List<TemaLeccion> allBySeccionDocenteDyna(Seccion seccion, Docente docente,  DynatableFilter filter);

    TemaLeccion findBySeccionDocenteFecha(Seccion seccion, Docente docente, Date fecha);

    void updateTema(TemaLeccion temaLeccion);

}
