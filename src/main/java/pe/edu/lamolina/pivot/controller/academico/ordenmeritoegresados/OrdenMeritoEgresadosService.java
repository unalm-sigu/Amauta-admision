package pe.edu.lamolina.pivot.controller.academico.ordenmeritoegresados;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlMeritoEgresado;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OrdenMeritoEgresadosService {

    void generarDatos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void calcularMeritos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<ControlMeritoEgresado> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    public List<CicloAcademico> allCicloAcademicoForSelect();

    CicloAcademico findCicloActivo();

    public List<Egresado> allAlumnoCicloByControl(DynatableFilter filter, ControlMeritoEgresado controlOrdenMerito);

    ControlMeritoEgresado find(Long id);

    List<Alumno> allAlumnoLikeNombres(String parametro);

    void saveEgresado(Egresado egresado, Usuario usuario);

}
