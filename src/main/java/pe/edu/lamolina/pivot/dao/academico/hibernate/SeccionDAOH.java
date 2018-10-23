package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TCUR;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;

@Repository
public class SeccionDAOH extends AbstractEasyDAO<Seccion> implements SeccionDAO {

    public SeccionDAOH() {
        super();
        setClazz(Seccion.class);
    }

    @Override
    public Seccion find(long id) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur")
                .leftJoin("cur.planCalificacion pc", "cur.planCalificacionRegular pcr", "gs.planCalificacion pc2")
                .leftJoin("grupoHoras gh", "aula au", "au.oficinaSupervisora", "au.aulaSuperior")
                .leftJoin("seccionSuperior")
                .filter("sec.id", id);

        return find(sql);
    }

    @Override
    public List<Seccion> allByCargaAcademica(DynatableFilter filter, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Curso.class, "sec")
                .join("grupoSeccion gs", "docenteSeccion ds", "aula au", "gs.curso cur")
                .leftJoin("cur.planCalificacion pc", "cur.planCalificacionRegular pcr")
                .orderBy("sec.id desc");

        return all(sql);
    }

    @Override
    public List<Seccion> allByFilter(Long idGrupo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur")
                .leftJoin("cur.planCalificacion pc", "cur.planCalificacionRegular pcr", "gs.planCalificacion pc2")
                .filter("gs.id", idGrupo);

        return all(sql);
    }

    @Override
    public Seccion findByCodeCiclo(String codigo, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .filter("sec.codigo", codigo)
                .filter("ca.id", ciclo);

        return find(sql);
    }

    @Override
    public List<Seccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<Seccion> allActivosByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras", "seccionSuperior")
                .filter("estado", EstadoEnum.ACT)
                .in("gs.id", gruposSeccion);

        return all(sql);
    }

    @Override
    public List<Seccion> allWithMatriculadosByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras", "seccionSuperior")
                .filter("estado", EstadoEnum.ACT)
                .filter("matriculados", ">", 0)
                .in("gs.id", gruposSeccion)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public List<Seccion> allActivosByGpoSeccion(GrupoSeccion gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
                .filter("estado", EstadoEnum.ACT)
                .filter("gs.id", gruposSeccion);

        return all(sql);
    }

    @Override
    public List<Seccion> allOperativesByGpoSeccion(GrupoSeccion grupoSeccion) {
        List<SeccionEstadoEnum> estados = Arrays.asList(SeccionEstadoEnum.ACT, SeccionEstadoEnum.BLO, SeccionEstadoEnum.CRE);
        return this.allByGpoSeccionEstados(grupoSeccion, estados);
    }

    @Override
    public List<Seccion> allByGpoSeccionEstados(GrupoSeccion grupoSeccion, List<SeccionEstadoEnum> estadoEnums) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
                .in("estado", estadoEnums)
                .filter("gs.id", grupoSeccion);

        return all(sql);
    }

    @Override
    public List<Seccion> allByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
                .in("gs.id", gruposSeccion)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public List<Seccion> allByGposSeccion(GrupoSeccion gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras", "cur.carrera")
                .filter("gs.id", gruposSeccion)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public void updateSeccionGrupoHora(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "grupoHoras");
        this.update(octavia);
    }

    @Override
    public void updateSeccionAula(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "aula");
        this.update(octavia);
    }

    @Override
    public void updateRestriccionCapa(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "restriccionCapa");
        this.update(octavia);
    }

    @Override
    public void updateSeccionVacantes(Seccion seccion) {

        StringBuilder strb = new StringBuilder();
        strb.append("update Seccion  set vacantes=:prm_vacantes where id=:prm_id ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_id", seccion.getId());
        query.setParameter("prm_vacantes", seccion.getVacantes());
        query.executeUpdate();

        /*
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "vacantes");
        this.update(octavia);*/
    }

    @Override
    public List<Seccion> allActivosByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso cur", "gs.anexoBoletin")
                .leftJoin("seccionSuperior")
                .leftJoin("sec.aula", "sec.grupoHoras", "sec.aula", "cur.carrera carr")
                .filter("ca.id", ciclo)
                .filter("sec.estado", EstadoEnum.ACT)
                .in("cur.id", cursos)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public List<Seccion> allMatriculablesBySecciones(List<Seccion> secciones) {

        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso cur")
                .leftJoin("seccionSuperior ss")
                .leftJoin("sec.aula", "sec.grupoHoras", "sec.aula", "cur.carrera carr")
                .in("sec.id", secciones)
                .filter("sec.tipoSeccion", "<>", TCUR)
                .orderBy("sec.codigo");

        return all(sql);
    }

    @Override
    public void allRegenerateReservadoByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append("  update ").append(Seccion.class.getName()).append(" sex ");
        sql.append("  set sex.reservados = 0     ");
        sql.append("  where sex.id in ( ");
        sql.append("    select shc.seccion.id from ").append(SeccionHorarioCachimbos.class.getName()).append(" shc ");
        sql.append("    where shc.horarioCachimbos.id in ( ");
        sql.append("      select hc.id  from ").append(HorarioCachimbos.class.getName()).append(" hc ");
        sql.append("      where hc.cicloAcademico.id = :CICLO ");
        sql.append("    ) ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", cicloAcademico.getId());
        query.executeUpdate();

    }

    @Override
    public void updateEstadoFechaModUsuarioMod(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "estado");
        octavia.set(seccion, "usuarioModificacion");
        octavia.set(seccion, "fechaModificacion");
        this.update(octavia);
    }

    @Override
    public void updateCodigoFechaModUsuarioMod(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "codigo");
        octavia.set(seccion, "usuarioModificacion");
        octavia.set(seccion, "fechaModificacion");
        this.update(octavia);
    }

    @Override
    public void updateSituacionDocente(Seccion seccion) {

        StringBuilder strb = new StringBuilder();
        strb.append("update Seccion  set situacionDocente=:prm_sit_docente where id=:prm_id ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_id", seccion.getId());
        query.setParameter("prm_sit_docente", seccion.getSituacionDocente());
        query.executeUpdate();
    }

    @Override
    public List<Seccion> allByCodigo(String codigo) {
        Octavia sql = Octavia.query(Seccion.class, "sc")
                .join("grupoSeccion gs", "gs.curso")
                .leftJoin("grupoHoras")
                .like("sc.codigo2", codigo)
                .filter("sc.estado", EstadoEnum.ACT)
                .limit(15);
        return all(sql);
    }

    @Override
    public List<Seccion> allUnusedByCiclo(CicloAcademico ciclo) {

        Octavia sql = Octavia.query()
                .from(Seccion.class, "se")
                .join("grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("se.codigo", "like", "Y%")
                .orderBy("se.codigo");
        return all(sql);

    }

    @Override
    public List<Seccion> allByGrupoSeccionByClone(List<GrupoSeccion> gsOrigenes) {

        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .in("gs.id", gsOrigenes)
                .filter("sec.vacantes", ">", 0)
                .filter("sec.matriculados", ">", 0)
                .orderBy("sec.codigo", "sec.codigo2");

        return all(sql);

    }

    @Override
    public List<Seccion> allSeccionOrderByciclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .filter("ca.id", ciclo)
                .orderBy("sec.codigo", "sec.codigo2");
        return all(sql);
    }

    @Override
    public Seccion find(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico")
                .leftJoin("cur.planCalificacion pc", "cur.planCalificacionRegular pcr", "gs.planCalificacion pc2")
                .leftJoin("grupoHoras gh", "aula au", "au.oficinaSupervisora", "au.aulaSuperior")
                .leftJoin("seccionSuperior")
                .filter("sec.id", seccion.getId());

        return find(sql);
    }

    @Override
    public void setCodigo2Null(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" update ").append(Seccion.class.getSimpleName()).append(" as se ");
        sql.append("    set codigo2 = null ");
        sql.append("  where exists ( ");
        sql.append("      select 1 ");
        sql.append("        from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("       where gs.id = se.grupoSeccion.id ");
        sql.append("         and gs.cicloAcademico.id = :CICLO ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());

        query.executeUpdate();
    }

    @Override
    public void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio) {
        StringBuilder sql = new StringBuilder();

        sql.append(" update ").append(Seccion.class.getName()).append(" as s set precio = :PRECIO ");
        sql.append(" where s.grupoSeccion in ( select gs.id from GrupoSeccion gs where concat( gs.curso.horasTeoria, '-', gs.curso.horasPractica, '-', gs.curso.creditos ) = :TPC and gs.cicloAcademico = :CICLO ) ");
        sql.append(" and s.tipoSeccion != :TCUR ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("TPC", tpc);
        query.setParameter("PRECIO", precio);
        query.setParameter("CICLO", cicloAcademico);
        query.setParameter("TCUR", TipoSeccionEnum.TCUR.name());

        query.executeUpdate();
    }

}
