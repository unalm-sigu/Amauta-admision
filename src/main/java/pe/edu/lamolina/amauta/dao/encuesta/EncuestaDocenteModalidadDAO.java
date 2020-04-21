package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;

public interface EncuestaDocenteModalidadDAO extends EasyDAO<EncuestaDocenteModalidad> {

    List<EncuestaDocenteModalidad> allByCiclo(CicloAcademico cicloAcademico);

    List<EncuestaDocenteModalidad> allByDocenteCiclo(Docente docente, CicloAcademico ciclo);

    List<EncuestaDocenteModalidad> allConEncuestadosByCiclo(CicloAcademico cicloAcademico);

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(
            DynatableFilter filter,
            CicloAcademico ciclo,
            List<DepartamentoAcademico> departamentos,
            Docente docente);

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademicoDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

}
