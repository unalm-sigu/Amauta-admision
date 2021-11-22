package pe.edu.lamolina.amauta.controller.consejeria.aconsejadostutor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.AconsejadoEstadoBean;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;

public interface AconsejadosTutorService {

    public List<AlumnoConsejero> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, Persona persona);

    List<AlumnoConsejero> allByDynatableByCarrera(DynatableFilter filter, CicloAcademico cicloAcademico, Persona tutor, Carrera carrera);

    public AconsejadoEstadoBean allByPersona(Persona persona, CicloAcademico cicloAcademico);

    void matriculaAutorizacion(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public Persona findPersona(Long idPersona);

    public AconsejadoEstadoBean allByPersonaCarrera(Persona person, CicloAcademico cicloAcademico, Carrera carrera);

    public void eliminarAlumnoConsejero(Long idAlumnoConsejero);
}
