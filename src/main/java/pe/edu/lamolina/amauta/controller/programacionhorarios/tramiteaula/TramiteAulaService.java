package pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface TramiteAulaService {

    List<ReservaAula> allDynatableFilter(DynatableFilter filter);

    List<Aula> allByDynatableFilterAula(DynatableFilter filter, DataSessionPivot ds);

    Empresa saveInstitucion(Empresa institucion);

    List<Alumno> allAlumnoByName(String nombre);

    List<Docente> allDocenteByName(String nombre);

    void save(ReservaAula reservaAula, DataSessionPivot ds);

    void aceptartramite(ReservaAula reservaAula);

    void rechazartramite(ReservaAula reservaAula);

    ReservaAula findReservaAula(Long idReservaAula);

    void update(ReservaAula reservaAula, DataSessionPivot ds);

    List<Aula> allAulaModuloByName(String nombre, DataSessionPivot ds);

    List<Dia> allDia();

    List<Hora> allHora();

    List<Oficina> allOficinaByName(String nombre, DataSessionPivot ds);

    List<AulaReservada> allAulaReservada(ReservaAula reservaAula);

    List<Aula> allAulaModuloByOficina(Oficina oficina);

    void cambiarVisibilidadReserva(ReservaAula reservaAulaForm);

}
