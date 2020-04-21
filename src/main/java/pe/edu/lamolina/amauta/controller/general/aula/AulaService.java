package pe.edu.lamolina.amauta.controller.general.aula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.ResponsableAulaAsignacion;
import pe.edu.lamolina.model.general.Sede;
import pe.edu.lamolina.model.general.TipoAula;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AulaService {

    List<Aula> allByDynatable(DynatableFilter filter, DataSessionPivot ds);

    List<TipoAula> allTiposAula();

    List<Aula> allAulasSuperioresByName(String nombre);

    List<Oficina> allOficinasByName(String nombre);

    List<Sede> allSedes();

    void save(Aula aula, Usuario usuario);

    void update(Aula aula, Usuario usuario);

    Aula findAulaById(Long id);

    void cambioEstado(Aula aula, DataSessionPivot ds);

    void eliminarAula(Aula aula);

    List<Dia> allDia();

    Aula findAulaFull(Aula aulaForm);

    List<TipoCarpeta> allTipoCarpeta();

    List<Aula> allAulaByOficinaSuperior(DataSessionPivot ds);

    List<Aula> allAulaByAulaSuperior(List<Aula> listAulaSuperior);

    List<Dia> allDiaForPrinter();

    List<DiaHoraGrupo> allDiaHoraGrupoByCicloRegular(CicloAcademico cicloAcademico);

    List<Hora> returnHorasEncontradas(Aula aulaBD, List<Dia> dias, CicloAcademico ciclo);

    List<Hora> returnHorasEcontradasModal(Aula aula, List<Dia> dias);

    List<Hora> allHorasHorario();

    Aula findById(Aula aulaSuperiorForm);

    Aula findAulaHorarioProgramacion(Aula aula);

    List<HorarioAula> allHorariosAulaByCiclo(CicloAcademico cicloAcademico, Aula aula);

    List<Aula> allAulas(CicloAcademico cicloAcademico);

    List<ResponsableAula> allResponsablesAulas(EstadoEnum... estado);

    List<ResponsableAulaAsignacion> allResponsablesAulasAsignadas(EstadoEnum... estado);

}
