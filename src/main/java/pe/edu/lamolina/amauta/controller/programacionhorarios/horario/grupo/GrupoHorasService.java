package pe.edu.lamolina.amauta.controller.programacionhorarios.horario.grupo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface GrupoHorasService {

    List<GrupoHoras> allGrupoHoras(DynatableFilter filter, CicloAcademico ciclo);

    GrupoHoras findGrupoHoras(GrupoHoras grupoHoras);

    GrupoHoras findGrupoHoras(Long grupoHoras);

    void delete(GrupoHoras grupoHoras, CicloAcademico ciclo, DataSessionPivot ds);

    void save(GrupoHoras grupoHoras);

    void update(GrupoHoras grupoHoras);

    void ocultar(GrupoHoras grupoHoras, CicloAcademico ciclo, DataSessionPivot ds);

    GrupoHoras findGrupoHorasByCode(String codigo);

    List<Hora> allHora();

    List<Dia> allDia();

    void saveDiaHoraGrupo(DiaHoraGrupo diaHoraGrupo);

    List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico);

    void desasignarHora(DiaHoraGrupo diaHoraGrupo, CicloAcademico ciclo);

    List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico);

    TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupo);

    List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico);

    void gencolor();

    TipoGrupoHoras findTipoGpoRegular();

    void clonar(CicloAcademico cicloOrigen, CicloAcademico cicloDestino);

    GrupoHorasExcluido findGrupoHorasOculto(GrupoHoras grupoCode, CicloAcademico cicloAcademico);

    List<GrupoHoras> allOcultosByCiclo(TipoGrupoHoras tipoGpo, CicloAcademico cicloAcademico);

    void activarGrupos(List<GrupoHoras> gpos, CicloAcademico cicloAcademico);

}
