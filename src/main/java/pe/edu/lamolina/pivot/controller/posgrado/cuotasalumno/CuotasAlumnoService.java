package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CuotasAlumnoService {

    List<Alumno> allAlumnosPosgrado(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<TarifaCarrera> allByCarrera(Carrera carrera);

    Alumno findAlumno(Alumno alumno);

    TarifaCarrera findTarifaCarrera(Long id);

    AlumnoResumenCuotas generarCuotasAlumno(AlumnoResumenCuotas alumnoResumenCuotas, DataSessionPivot ds);

    void grabarCuotasAlumno(AlumnoResumenCuotas alumnoResumenCuotas, DataSessionPivot ds);

    AlumnoResumenCuotas findAlumnoResumenCuotaByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    TarifaConcepto findTarifaConceptoByConceptoPosgrado(ConceptoPosgrado conceptoPosgrado);
}
