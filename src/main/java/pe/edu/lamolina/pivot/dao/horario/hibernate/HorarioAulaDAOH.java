package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoAulaEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;

@Repository
public class HorarioAulaDAOH extends AbstractEasyDAO<HorarioAula> implements HorarioAulaDAO {

    public HorarioAulaDAOH() {
        super();
        setClazz(HorarioAula.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<HorarioAula> allHorarioAula() {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha");

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec", "sec.grupoSeccion gs")
                .left("gs.curso cur")
                .left("sec.grupoHoras gh")
                .join("gs.cicloAcademico ca")
                .filter("au.id", aula)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .filter("au.id", aula)
                .filter("sec.id", seccion);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAulaCiclo(Aula aula, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico cic")
                .filter("au.estado", EstadoEnum.ACT.name())
                .filter("au.id", aula)
                .filter("cic.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionCiclo(Seccion seccion, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico cic")
                .filter("au.estado", EstadoEnum.ACT.name())
                .filter("sec.id", seccion)
                .filter("cic.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public void deleteBySeccionAula(Seccion seccion, Aula aula) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("delete from HorarioAula ha where ha.seccion.id=:prm_seccion and ha.aula.id=:prm_aula ");

        Query query = getCurrentSession().createQuery(queryStr.toString());

        query.setParameter("prm_seccion", seccion.getId());
        query.setParameter("prm_aula", aula.getId());

        query.executeUpdate();
    }

    @Override
    public void deleteBySeccionDiaHoraAula(Seccion seccion, Dia dia, Hora hora, Aula aula) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("delete from HorarioAula ha where ha.seccion.id=:prm_seccion and ha.aula.id=:prm_aula ");
        queryStr.append(" and ha.dia.id=:prm_dia ");
        queryStr.append(" and ha.hora.id=:prm_hora ");

        Query query = getCurrentSession().createQuery(queryStr.toString());

        query.setParameter("prm_seccion", seccion.getId());
        query.setParameter("prm_aula", aula.getId());
        query.setParameter("prm_dia", dia.getId());
        query.setParameter("prm_hora", hora.getId());

        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allByPabellonCicloDiasHoras(Aula pabellon, EventoCicloAcademico eventoAcademico, List<String> hdias) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "au.aulaSuperior aus", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("aus.id", pabellon)
                .filter("ha.fechaInicio", ">=", eventoAcademico.getFechaInicio())
                .filter("ha.fechaFin", "<=", eventoAcademico.getFechaFin())
                .complexFilter("concat(h.codigo,'-',d.id)", "in", hdias);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAulasCicloDiasHoras(List<Aula> aulas, EventoCicloAcademico eventoAcademico, List<String> hdias) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("au.id", aulas)
                .filter("ha.fechaInicio", ">=", eventoAcademico.getFechaInicio())
                .filter("ha.fechaFin", "<=", eventoAcademico.getFechaFin())
                .complexFilter("concat(h.codigo,'-',d.id)", "in", hdias);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCambioAulasCiclo(List<Aula> aulas, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("au.id", aulas)
                .filter("ca.id", cicloAcademico);
//                .complexFilter("concat(h.codigo,'-',d.id)", "in", hdias);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCicloAndTipoHorario(CicloAcademico cicloAcademico, Aula aula, TipoHorarioAulaEnum tipoHorarioAulaEnum) {
        return this.allByCicloAndTipoHorario(cicloAcademico, aula != null ? Arrays.asList(aula) : null, tipoHorarioAulaEnum);
    }

    @Override
    public List<HorarioAula> allByCicloAndTipoHorario(CicloAcademico cicloAcademico, List<Aula> aulas, TipoHorarioAulaEnum tipoHorarioAulaEnum) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca", "sec.grupoHoras gh")
                .join("gs.curso cur", "au.oficinaSupervisora ofi")
                .filter("ha.tipo", tipoHorarioAulaEnum)
                .filter("ofi.codigo", OficinaEnum.OERA)
                .filter("ca.id", cicloAcademico);
        if (aulas != null && !aulas.isEmpty()) {
            sql.in("au.id", aulas);
        }
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByFechas(Date fechainicio, Date fechafin, List<Aula> aulas, OficinaEnum oficinaEnum, TipoHorarioAulaEnum... tipoHorarioAulaEnum) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .left("au.oficinaSupervisora ofi")
                .left("seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico ca", "sec.grupoHoras gh", "gs.curso cur")
                .left("reservaAula ra", "ra.tramite tra", "tra.persona per", "tra.empresa", "tra.docente", "tra.alumno")
                .in("ha.tipo", Arrays.asList(tipoHorarioAulaEnum))
                .filter("ofi.codigo", oficinaEnum)
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock();
        if (aulas != null && !aulas.isEmpty()) {
            sql.in("au.id", aulas);
        }
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCicloOrderByDiaHora(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .orderBy("d.numeroDia", "h.numero")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico, EstadoHorarioAulaEnum... estados) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .in("ha.estado", estados);
        return all(sql);
    }

    @Override
    public void deleteAllInList(List<HorarioAula> horarios) {
        if (horarios.isEmpty()) {
            return;
        }

        String sql = "delete HorarioAula hs where hs in :HORARIOS";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameterList("HORARIOS", horarios);
        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allBySecciones(List<Seccion> seccions, CicloAcademico cicloOrigen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("sec.id", seccions)
                .filter("ca.id", cicloOrigen);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionesSortByDiaHora(List<Seccion> seccions, CicloAcademico cicloOrigen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("sec.id", seccions)
                .filter("ca.id", cicloOrigen)
                .orderBy("d.numeroDia", "h.numero");

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccion(Seccion seccion) {

        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .filter("sec.id", seccion);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByFilterAulaTramite(List<Aula> aulas) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("au.id", aulas);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allSoloDiaByDiasHoras(List<String> diashoras, Date fechainicio) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("au.aulaSuperior aus", "aus.tipoAula tip")
                .filter("tip.codigo", TipoAulaEnum.MOD)
                .filter("au.estado", EstadoEnum.ACT)
                .filter("ha.fechaInicio", "<=", fechainicio)
                .filter("ha.fechaFin", ">=", fechainicio)
                .complexFilter("concat(d.id,'-',h.id)", "in", diashoras);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allRangoDiaByDiasHoras(List<String> diashoras, Date fechainicio, Date fechafin) {
        List<String> diasHorasFinal = diashoras.stream()
                .map(x -> x.replace("_", "-"))
                .collect(Collectors.toList());
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("au.aulaSuperior aus", "aus.tipoAula tip")
                .filter("tip.codigo", TipoAulaEnum.MOD)
                .filter("au.estado", EstadoEnum.ACT)
                .complexFilter("concat(d.id,'-',h.id)", "in", diasHorasFinal)
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public List<HorarioAula> allRangoDiaAndAulaByDiasHoras(List<String> diashoras, Aula aula, Date fechainicio, Date fechafin) {
        List<String> diasHorasFinal = diashoras.stream()
                .map(x -> x.replace("_", "-"))
                .collect(Collectors.toList());
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("au.aulaSuperior aus", "aus.tipoAula tip", "ha.seccion sec")
                .filter("tip.codigo", TipoAulaEnum.MOD)
                .filter("au.estado", EstadoEnum.ACT)
                .filter("au.id", aula)
                .complexFilter("concat(d.id,'-',h.id)", "in", diasHorasFinal)
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock()
                .orderBy("d.numeroDia", "h.numero");
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByRango(Date fechainicio, Date fechafin) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("au.aulaSuperior aus", "aus.tipoAula tip")
                // .filter("tip.codigo", TipoAulaEnum.MOD)
                //.filter("ha.estado", EstadoHorarioAulaEnum.ACT)
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByRango(Date fechainicio, Date fechafin, Aula... aula) {
        List<Aula> aulas = Arrays.asList(aula);
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("au.aulaSuperior aus", "aus.tipoAula tip")
                // .filter("tip.codigo", TipoAulaEnum.MOD)
                //.filter("ha.estado", EstadoHorarioAulaEnum.ACT)
                .in("au.id", aulas)
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByRangoNotByTipo(Date fechainicio, Date fechafin, TipoHorarioAulaEnum tipo, List<Aula> aulas) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au")
                .leftJoin("seccion", "reservaAula", "seccionGrupoRegular", "seccionGrupoEspecial", "cursoMasivoExamen", "rolExamenes")
                .in("au.id", aulas)
                .filter("tipo", "<>", tipo.name())
                .beginBlock()
                .__().between("ha.fechaInicio", fechainicio, fechafin)
                .__().between("ha.fechaFin", fechainicio, fechafin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechainicio)
                .__().__().filter("ha.fechaFin", ">=", fechafin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechainicio)
                .__().__().filter("ha.fechaFin", "<=", fechafin)
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public void deleteAllByReservaAula(ReservaAula reservaAula) {
        String strQuery = "delete from HorarioAula ha where ha.reservaAula.id=:IDRESERVAAULA";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("IDRESERVAAULA", reservaAula.getId());
        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allByAulaFecha(Aula aulaFormFecha) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au")
                .left("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "sec.grupoHoras gh")
                .left("gs.cicloAcademico ca")
                .left("reservaAula ra", "ra.tramite tra")
                .left("tra.docente do", "do.persona")
                .left("tra.alumno al", "al.persona")
                .left("tra.oficina ofi", "tra.empresa emp")
                .filter("au.id", aulaFormFecha)
                .beginBlock()
                .__().between("ha.fechaInicio", aulaFormFecha.getFechaInicio(), aulaFormFecha.getFechaFin())
                .__().between("ha.fechaFin", aulaFormFecha.getFechaInicio(), aulaFormFecha.getFechaFin())
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", aulaFormFecha.getFechaInicio())
                .__().__().filter("ha.fechaFin", ">=", aulaFormFecha.getFechaFin())
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", aulaFormFecha.getFechaInicio())
                .__().__().filter("ha.fechaFin", "<=", aulaFormFecha.getFechaFin())
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public List<HorarioAula> allHorarioClasesBySecciones(List<Seccion> secciones, SemanaExamen semanaExamen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("ha.tipo", TipoHorarioAulaEnum.DICT)
                .filter("ha.fechaInicio", "<=", semanaExamen.getFechaInicio())
                .filter("ha.fechaFin", ">=", semanaExamen.getFechaFin())
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allWithSeccionByAulas(List<Aula> aulas) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("au.id", aulas);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAulas(List<Aula> aulas, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .leftJoin("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("sec.id", secciones)
                .in("au.id", aulas);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionGrupoRegular(SeccionGrupoRegular seccionGrupoRegular) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec", "seccionGrupoRegular sgr")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("sgr.id", seccionGrupoRegular);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("rolExamenes rex")
                .filter("rex.id", rolExamenes);
        return all(sql);
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  HorarioAula ha where ha.rolExamenes.id=:ROLEX");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROLEX", rolExamenes.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteBySeccionGrupoRegular(SeccionGrupoRegular seccionGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  HorarioAula ha where ha.seccionGrupoRegular.id=:SGR");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("SGR", seccionGrupoRegular.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteSeccionesEspecialesByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  HorarioAula ha ");
        strb.append("  where ha.seccionGrupoEspecial is not null ");
        strb.append("    and ha.rolExamenes.id = :ROLEX ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROLEX", rolExamenes.getId());
        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allBySeccionGrupoEspecial(SeccionGrupoEspecial seccionGrupoEspecial) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec", "seccionGrupoEspecial sgr")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("sgr.id", seccionGrupoEspecial);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAulasAndSecciones(List<Aula> aulas, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .in("sec.id", secciones)
                .in("au.id", aulas);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAulasAndNotInSecciones(List<Aula> aulas, List<Seccion> secciones, Date fechaInicio, Date fechaFin) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au")
                .left("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "sec.grupoHoras gh")
                .left("gs.cicloAcademico ca")
                .left("reservaAula ra", "ra.tramite tra")
                .left("tra.docente do", "do.persona")
                .left("tra.alumno al", "al.persona")
                .left("tra.oficina ofi", "tra.empresa emp")
                .notIn("sec.id", secciones)
                .in("au.id", aulas)
                .beginBlock()
                .__().between("ha.fechaInicio", fechaInicio, fechaFin)
                .__().between("ha.fechaFin", fechaInicio, fechaFin)
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", "<=", fechaInicio)
                .__().__().filter("ha.fechaFin", ">=", fechaFin)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ha.fechaInicio", ">=", fechaInicio)
                .__().__().filter("ha.fechaFin", "<=", fechaFin)
                .__().endBlock()
                .endBlock();
        return all(sql);
    }

    @Override
    public List<HorarioAula> allForRolExamenesByCicloAcademico(CicloAcademico cicloAcademicoRol) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("seccion sec", "hora h", "dia d", "aula au")
                .join("sec.grupoSeccion gSec", "gSec.curso cur", "au.oficinaSupervisora ofi")
                .join("cur.modalidadEstudio mEst", "gSec.cicloAcademico ca")
                //   .filter("ha.fechaInicio", ">=", rolExamenes.getEventoCicloAcademico().getFechaInicio())
                //    .filter("ha.fechaFin", "<=", rolExamenes.getEventoCicloAcademico().getFechaFin())
                .filter("ca.id", cicloAcademicoRol)
                .filter("ofi.codigo", OficinaEnum.OERA)
                .filter("mEst.codigo", ModalidadEstudioEnum.PRE);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCicloAndSemanaExamenLimitByHours(EventoCicloAcademico eventoCicloAcademico, SemanaExamen semanaExamen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("seccion sec", "hora h", "dia d", "aula au")
                .join("sec.grupoSeccion gSec", "gSec.curso cur", "au.oficinaSupervisora ofi")
                .join("cur.modalidadEstudio mEst", "gSec.cicloAcademico ca")
                .filter("ca.id", eventoCicloAcademico.getCicloAcademico())
                .filter("h.id", ">=", semanaExamen.getHoraInicio())
                .filter("h.id", "<=", semanaExamen.getHoraFin())
                .filter("ofi.codigo", OficinaEnum.OERA)
                .filter("mEst.codigo", ModalidadEstudioEnum.PRE);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allOcupadasByCicloAndSemanaExamen(CicloAcademico cicloAcademico, SemanaExamen semanaExamen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("seccion sec", "hora h", "dia d", "aula au")
                .join("sec.grupoSeccion gSec", "gSec.curso cur", "au.oficinaSupervisora ofi")
                .join("cur.modalidadEstudio mEst", "gSec.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("ha.fechaInicio", ">=", semanaExamen.getFechaInicio())
                .filter("ha.fechaFin", "<=", semanaExamen.getFechaFin())
                .filter("h.id", ">=", semanaExamen.getHoraInicio())
                .filter("h.id", "<=", semanaExamen.getHoraFin())
                //    .filter("ofi.codigo", OficinaEnum.OERA)
                .filter("mEst.codigo", ModalidadEstudioEnum.PRE)
                .filter("ha.tipo", TipoHorarioAulaEnum.EXAM)
                .orderBy("d.numeroDia", "h.numero");
        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("seccion sec", "hora h", "dia d", "aula au", "cursoMasivoExamen cmas")
                .join("sec.grupoSeccion gSec", "gSec.curso cur", "au.oficinaSupervisora ofi")
                .join("cur.modalidadEstudio mEst", "gSec.cicloAcademico ca")
                .filter("cmas.id", cursoMasivoExamen);
        return all(sql);
    }

    @Override
    public List<HorarioAula> allFlatByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("cursoMasivoExamen cmas")
                .filter("cmas.id", cursoMasivoExamen);
        return all(sql);
    }

//    public List<HorarioAula> allByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
//        Octavia sql = Octavia.query()
//                .from(HorarioAula.class, "ha")
//                .join("seccion sec", "hora h", "dia d", "aula au", "")
//                .join("sec.grupoSeccion gSec", "gSec.curso cur", "au.oficinaSupervisora ofi")
//                .join("cur.modalidadEstudio mEst", "gSec.cicloAcademico ca")
//                .filter("ha.cursoMasivoExamen", cursoMasivoExamen);
//        return all(sql);
//    }
    @Override
    public void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  HorarioAula ha where ha.seccionGrupoRegular.id in ");
        strb.append(" (Select ssgr.id from SeccionGrupoRegular ssgr where  ssgr.letraGrupoRegular.id=:LETRA) ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA", letraGrupoRegular.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  HorarioAula ha where ha.cursoMasivoExamen.id=:CURSOMASIVO ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("CURSOMASIVO", cursoMasivoExamen.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteBySecciones(List<Seccion> secciones) {
        if (secciones != null && !secciones.isEmpty()) {
            List<Long> seccionesIds = secciones.stream().map(x -> x.getId()).collect(Collectors.toList());
            StringBuilder strb = new StringBuilder();
            strb.append(" delete from  HorarioAula ha where ha.seccion.id in :SECCIONES");

            Query query = getCurrentSession().createQuery(strb.toString());
            query.setParameterList("SECCIONES", seccionesIds);
            query.executeUpdate();
        }
    }

    @Override
    public int saveList(List<HorarioAula> horariosAulas) {
        if (horariosAulas.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(HorarioAula.class)
                .columns("estado", "reservado", "tipo", "fechaInicio", "fechaFin",
                        "aula", "dia", "hora", "seccion", "reservaAula", "seccionGrupoRegular",
                        "seccionGrupoEspecial", "cursoMasivoExamen", "rolExamenes")
                .values(horariosAulas);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} HorarioAula's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}
