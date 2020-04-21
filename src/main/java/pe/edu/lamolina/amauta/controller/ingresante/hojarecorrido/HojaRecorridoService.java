package pe.edu.lamolina.amauta.controller.ingresante.hojarecorrido;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;

public interface HojaRecorridoService {

    List<RecorridoIngresante> allRecorridoIngresante(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<TipoActividadIngresante> allTipoActividadIngresante();

}
