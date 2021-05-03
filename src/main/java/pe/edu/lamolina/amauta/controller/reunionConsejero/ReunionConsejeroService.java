package pe.edu.lamolina.amauta.controller.reunionConsejero;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.Hora;

public interface ReunionConsejeroService {

    List<Hora> allHora30();

    void save(AgendaConsejero agendaConsejero, DataSessionPivot ds);

    void update(AgendaConsejero agendaConsejero, DataSessionPivot ds);

    void anularAgenda(AgendaConsejero agendaConsejero, DataSessionPivot ds);

    Consejero findConsejeroCarrera(Long carrera, Persona persona);

    List<ReunionAlumnoConsejero> listDynatable(DynatableFilter filter, Consejero consejero, DataSessionPivot ds);

    List<Consejero> allConsejeros(Persona persona);

    void asistenciaReunion(ReunionAlumnoConsejero reunionAlumnoConsejero, DataSessionPivot ds);

    void inasistenciaReunion(ReunionAlumnoConsejero reunionAlumnoConsejeroForm, DataSessionPivot ds);

    public void anularReunion(ReunionAlumnoConsejero reunionAlumnoConsejero, DataSessionPivot ds);

    public List<AlumnoConsejero> list(Consejero consejero, DataSessionPivot ds);

    public AgendaConsejero findAgenda(Long agendaId, CicloAcademico cicloAcademico);

}
