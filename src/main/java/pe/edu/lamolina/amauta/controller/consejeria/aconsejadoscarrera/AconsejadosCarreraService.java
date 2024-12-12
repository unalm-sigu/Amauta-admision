package pe.edu.lamolina.amauta.controller.consejeria.aconsejadoscarrera;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;

public interface AconsejadosCarreraService {

    List<AlumnoConsejero> allAconsejadoByDynatable(Carrera carrera, DynatableFilter filter, CicloAcademico cicloAcademico);

    void updateAlumnoConsejero(AlumnoConsejero alumnoConsejeroForm, DataSessionPivot ds);

    ConsejeriaResumen getResumenByCarreraCiclo(Carrera carrera, CicloAcademico cicloAcademico);

    public boolean isRolCape(DataSessionPivot ds);

    public boolean esInformaticoOERA(DataSessionPivot ds);
    public boolean esAdministradorTutoria(DataSessionPivot ds);

    public List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico cicloAcademico);

    public void revisarConsejeria(Carrera carrera, CicloAcademico cicloAcademico, boolean b, DataSessionPivot ds);

    public List<Consejero> allByCarrera(String nombre, Carrera carrera);

    public void solicitudBeneficio(AlumnoConsejero alumnoConsejero, DataSessionPivot ds);

    public void eliminarAlumnoConsejero(Long idAlumnoConsejero);

    public void quitarTutor(Long idAlumnoConsejero);

}
