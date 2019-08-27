package pe.edu.lamolina.pivot.dao.aporte;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface ResumenAporteAlumnoDAO extends EasyDAO<ResumenAporteAlumno> {

    ResumenAporteAlumno findByAlumnoCicloAcademico(Alumno alumno, CicloAcademico cicloAcademico);

    ResumenAporteAlumno findByAlumnoCiclo(AlumnoCiclo alumnoCiclo);

    ResumenAporteAlumno findByMatriculaResumen(MatriculaResumen matriculaResumen);

    List<ResumenAporteAlumno> allByAlumno(Alumno alumno);

    List<ResumenAporteAlumno> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<ResumenAporteAlumno> allByDynatableCicloAcademico(ModalidadEstudio estudio, DynatableFilter filter, CicloAcademico cicloAcademico);

    void inicializarByCodigoCicloModalidad(String codigoCiclo, ModalidadEstudio modalidad, Usuario usuario);

    void inicializarIngresantesByCiclo(CicloAcademico cicloAcademico, Usuario usuario);

    void consolidarAportesByCiclo(CicloAcademico cicloAcademico);

    boolean yaGenerados(CicloAcademico cicloAcademico);

    void deleteByCicloAcademico(CicloAcademico cicloAcademico);

    List<ResumenAporteAlumno> allByCodigoCicloModalidad(String codigo, ModalidadEstudio modalidad);

    ResumenAporteAlumno find(ResumenAporteAlumno aporteAlumno);

    List<ResumenAporteAlumno> allByCicloMatriculaResumen(CicloAcademico cicloAcademico, List<MatriculaResumen> matriculaResumens);
}
