package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.BLO;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.CAN;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.FUS;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TCUR;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.seccion.SeccionDTO;

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
                .leftJoin("cur.modalidadEstudio mest")
                .leftJoin("pc.sistemaNotas", "pc2.sistemaNotas")
                .leftJoin("grupoHoras gh", "aula au", "au.oficinaSupervisora", "au.aulaSuperior")
                .leftJoin("seccionSuperior", "cur.departamentoAcademico daca")
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
    public List<Seccion> findByNombreCiclo(String nombre, CicloAcademico ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .beginBlock()
                .__().filter("sec.codigo2", "like", nombre)
                .__().filter("cur.codigo", "like", nombre)
                .__().filter("cur.nombre", "like", nombre)
                .endBlock()
                .filter("ca.id", ciclo)
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Seccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras", "cur.modalidadEstudio")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<Seccion> allByCiclo(CicloAcademico ciclo, SeccionEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
                .filter("ca.id", ciclo)
                .in("sec.estado", estados);

        return all(sql);
    }

    @Override
    public List<Seccion> allForAsignacionAulaByCiclo(CicloAcademico ciclo, SeccionEstadoEnum... estados) {
        List<SeccionEstadoEnum> lEstados = Arrays.asList(estados);
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("cur.tipoCarpetaTeoria tct", "cur.tipoCarpetaPractica tcp")
                .leftJoin("aula", "grupoHoras", "tipoCarpeta")
                .filter("ca.id", ciclo)
                .beginBlock()
                .isNotNull("tct.id")
                .endBlock()
                .beginBlock()
                .isNotNull("tcp.id")
                .endBlock()
                .in("sec.estado", lEstados);
        return all(sql);
    }

    @Override
    public List<Seccion> allSeccionesAulaAutoByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .filter("ca.id", ciclo)
                .filter("sec.aulaAsignadaAuto", Boolean.TRUE);
        return all(sql);
    }

    @Override
    public void updateAulaAignacionAutoByCiclo(CicloAcademico cicloAcademico, Boolean asignacion) {
//        StringBuilder queryStr = new StringBuilder();
//        queryStr.append("update Seccion sec set sec.aulaAsignadaAuto=:PRM_ASIGNACION where sec.id in (");
//        queryStr.append("Select sec.id from Seccion sec inner join sec.grupoSeccion gSec inner join gSec.cicloAcademico ca where sec.aulaAsignadaAuto:PRM_ASIGNACION and ca.id=:PRM_CICLO");
//        queryStr.append(")");
//
//        Query query = getCurrentSession().createQuery(queryStr.toString());
//        query.setParameter("PRM_ASIGNACION", asignacion);
//        query.setParameter("PRM_CICLO", cicloAcademico);
//        query.executeUpdate();
    }

    @Override
    public List<Seccion> allActivosByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras", "seccionSuperior")
                .filter("estado", SeccionEstadoEnum.ACT)
                .in("gs.id", gruposSeccion)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public List<Seccion> allWithMatriculadosByGposSeccion(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula au", "grupoHoras", "seccionSuperior", "au.oficinaSupervisora")
                .filter("estado", SeccionEstadoEnum.ACT)
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
                .filter("estado", SeccionEstadoEnum.ACT)
                .filter("gs.id", gruposSeccion);

        return all(sql);
    }

    @Override
    public List<Seccion> allByGpoSeccion(GrupoSeccion gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("aula", "grupoHoras")
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
    public List<Seccion> allByGposSeccionOrderedByCodigo2(GrupoSeccion gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docenteSeccion ds")
                .left("grupoHoras gh", "aula au")
                .filter("gs.id", gruposSeccion)
                .orderBy("sec.codigo2");
        return all(sql);
    }

    @Override
    public List<Seccion> allForBoletinByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("gs.cicloAcademico ca", "gs.anexoBoletin ab", "ab.anexoSuperior")
                .left("grupoHoras gh", "aula au")
                .filter("ca.id", cicloAcademico)
                .in("sec.estado", Arrays.asList(ACT, BLO, ANU, CAN, FUS))
                .filter("cur.codigo", "<>", "CI0000")
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
    public void updateAsignacionAula(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        //octavia.set(seccion, "restriccionCapa");
        octavia.set(seccion, "aulaAsignadaAuto");
        octavia.set(seccion, "fechaAsignacionAuto");
        octavia.set(seccion, "aula");
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
                .filter("sec.estado", SeccionEstadoEnum.ACT)
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
                .filter("sc.estado", SeccionEstadoEnum.ACT)
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
                .leftJoin("tipoCarpeta tc", "tc.tipoCarpetaSuperior tcsu")
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

        sql.append(" update ").append(Seccion.class.getName()).append(" as s ");
        sql.append("    set precio = :PRECIO ");
        sql.append("  where s.grupoSeccion in (  ");
        sql.append("         select gs.id ");
        sql.append("           from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("           join gs.curso cu ");
        sql.append("          where concat( cu.horasTeoria, '-' , cu.horasPractica, '-' , cu.creditos ) = :TPC ");
        sql.append("            and gs.cicloAcademico = :CICLO ) ");
        sql.append("    and s.tipoSeccion != :TCUR ");

        Query query = getCurrentSession().createQuery(sql.toString());

        query.setParameter("TPC", tpc);
        query.setParameter("PRECIO", precio);
        query.setParameter("CICLO", cicloAcademico);
        query.setParameter("TCUR", TipoSeccionEnum.TCUR.name());

        query.executeUpdate();
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(Seccion.class.getName()).append(" sec ")
                .append(" WHERE EXISTS ( ")
                .append("   SELECT 1 FROM ").append(GrupoSeccion.class.getName()).append(" gs ")
                .append("     JOIN gs.cicloAcademico ci ")
                .append("    WHERE ci.id=:CICLO ")
                .append("      AND sec.grupoSeccion.id=gs.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteAllNotSuperiorByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(Seccion.class.getName()).append(" sec ")
                .append(" WHERE sec.seccionSuperior.id is not null ")
                .append("   AND EXISTS ( ")
                .append("     SELECT 1 FROM ").append(GrupoSeccion.class.getName()).append(" gs ")
                .append("       JOIN gs.cicloAcademico ci ")
                .append("      WHERE ci.id = :CICLO ")
                .append("        AND sec.grupoSeccion.id=gs.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public List<Seccion> allByCursoCicloExceptSeccion(Curso curso, CicloAcademico ciclo, Seccion seccion) {

        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("grupoHoras gh", "aula au", "seccionSuperior")
                .filter("ca.id", ciclo)
                .filter("sec.estado", SeccionEstadoEnum.ACT)
                .filter("sec.id", "<>", seccion)
                .filter("sec.tipoSeccion", "<>", TipoSeccionEnum.TCUR)
                .filter("cur.id", curso)
                .orderBy("sec.codigo2");

        return all(sql);

    }

    @Override
    public List<Seccion> allForRolExamenAndTipoGrupoHora(CicloAcademico ciclo, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        Octavia sql = Octavia.query()
                .selectDistinct("sec")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec")
                .join("mr.cicloAcademico ca", "sec.grupoSeccion gs")
                .join("gs.curso cur", "cur.modalidadEstudio mest")
                .join("sec.aula au", "sec.grupoHoras gh", "gh.tipoGrupoHoras tgh")
                .left("au.oficinaSupervisora oSup", "sec.seccionSuperior sSup")
                .filter("ca.id", ciclo)
                .filter("mest.codigo", ModalidadEstudioEnum.PRE)
                .filter("tgh.tipo", tipoGrupoHorasEnum)
                .filter("sec.tipoSeccion", "!=", TipoSeccionEnum.PCUR)
                .in("ms.estado", Arrays.asList(EstadoMatriculaEnum.MAT))
                .orderBy("sec.horasSemanales desc");
        return all(sql);
    }

    @Override
    public List<Seccion> allByCicloAndCurso(CicloAcademico ciclo, Curso curso) {
        Octavia sql = Octavia.query()
                .selectDistinct("sec")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec")
                .join("mr.cicloAcademico ca", "sec.grupoSeccion gs", "gs.curso cur")
                .join("sec.grupoHoras gh", "gh.tipoGrupoHoras tgh", "sec.aula aul")
                .filter("ca.id", ciclo)
                .filter("sec.tipoSeccion", "!=", TipoSeccionEnum.PCUR)
                .filter("cur.id", curso)
                .in("ms.estado", Arrays.asList(EstadoMatriculaEnum.MAT))
                .orderBy("gh.codigo");
        return all(sql);
    }

    @Override
    public List<Seccion> allByCicloAndNombreLimit(CicloAcademico ciclo, RolExamenes rolExamenes, String nombre) {
        Octavia sqlNotIn = Octavia.query()
                .from(SeccionExcluido.class, "sexc0")
                .join("seccion s0", "rolExamenes rex0")
                .filter("rex0.id", rolExamenes)
                .filter("sexc0.estado", EstadoEnum.ACT);

        Octavia sql = Octavia.query()
                .selectDistinct("sec")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec")
                .join("mr.cicloAcademico ca", "sec.grupoSeccion gs", "gs.curso cur")
                .join("sec.grupoHoras gh", "gh.tipoGrupoHoras tgh", "sec.aula aul")
                .filter("ca.id", ciclo)
                .filter("sec.tipoSeccion", "!=", TipoSeccionEnum.PCUR)
                //.filter("cur.id", curso)
                .in("ms.estado", Arrays.asList(EstadoMatriculaEnum.MAT))
                .beginBlock()
                .__().like("sec.codigo2", nombre)
                .__().like("gh.codigo", nombre)
                .__().like("aul.codigo", nombre)
                .endBlock()
                .limit(15)
                .orderBy("sec.codigo2")
                .notExists(sqlNotIn)
                .linkedBy("sec.id", "s0.id");
        return all(sql);
    }

    @Override
    public List<Seccion> allByCicloAndGrupoHoras(CicloAcademico ciclo, GrupoHoras grupoHoras) {
        Octavia sql = Octavia.query()
                .selectDistinct("sec")
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec")
                .join("mr.cicloAcademico ca", "sec.grupoSeccion gs")
                .join("sec.grupoHoras gh", "gh.tipoGrupoHoras tgh")
                .filter("ca.id", ciclo)
                .filter("gh.id", grupoHoras)
                .filter("sec.tipoSeccion", "!=", TipoSeccionEnum.PCUR)
                //.in("ms.estado", Arrays.asList(EstadoMatriculaEnum.MAT))
                .orderBy("gh.codigo");
        return all(sql);
    }

    @Override
    public void updateMatriculados(Seccion seccion, Integer matriculados) {
        StringBuilder sql = new StringBuilder();
        sql.append("update Seccion ");
        sql.append("   set matriculados = :MATRICULADOS, ");
        sql.append("       estado = :ESTADO ");
        sql.append("where id = :SECCION ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("MATRICULADOS", matriculados);
        query.setParameter("ESTADO", seccion.getEstado());
        query.setParameter("SECCION", seccion.getId());
        query.executeUpdate();
    }

    @Override
    public void updatePrecioBySeccion(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "precio");
        octavia.set(seccion, "precioBase");
        octavia.set(seccion, "userPrecio");
        octavia.set(seccion, "fechaPrecio");
        octavia.set(seccion, "precioPersonalizado");
        this.update(octavia);
    }

    @Override
    public void setNullCodigo2ByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder()
                .append(" UPDATE ").append(Seccion.class.getName()).append(" sec ")
                .append("    SET sec.codigo2 = NULL ")
                .append("  WHERE EXISTS  ( ")
                .append("      SELECT 1 FROM ").append(GrupoSeccion.class.getName()).append(" gs ")
                .append("        JOIN gs.cicloAcademico ci ")
                .append("       WHERE ci.id = :CICLO ")
                .append("         AND sec.grupoSeccion.id = gs.id ")
                .append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public void updateCodigo2(List<Seccion> secciones) {
        StringBuilder sql = new StringBuilder("update Seccion set codigo2 = case \n");
        for (Seccion secc : secciones) {
            sql.append(" when id = ").append(secc.getId()).append(" then '").append(secc.getCodigo2()).append("' \n");
        }
        sql.append(" end where id in (\n");
        int loop = 0;
        for (Seccion secc : secciones) {
            if (loop > 0) {
                sql.append(",");
            }
            sql.append(secc.getId());
            loop++;
        }
        sql.append(")");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.executeUpdate();
    }

    @Override
    public Seccion findByGpoSeccionTipoSeccion(GrupoSeccion gpoSecc, TipoSeccionEnum tipoSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur")
                .leftJoin("grupoHoras gh", "aula au")
                .leftJoin("seccionSuperior")
                .filter("gs.id", gpoSecc)
                .filter("sec.tipoSeccion", tipoSeccion.name());

        return find(sql);
    }

    @Override
    public void updateMatriculados(Seccion seccion) {
        Octavia octavia = Octavia.update(Seccion.class);
        octavia.set(seccion, "matriculados");
        this.update(octavia);
    }

    @Override
    public void updateColumns(Seccion seccion, String... columns) {
        Octavia sql = Octavia.update(Seccion.class, "se");
        for (String column : columns) {
            sql.set(seccion, column);
        }
        this.update(sql);
    }

    @Override
    public void resetAsignacionAulaAuto(List<Seccion> secciones) {
        if (secciones != null && !secciones.isEmpty()) {
            List<Long> seccionesIds = secciones.stream().map(x -> x.getId()).collect(Collectors.toList());
            StringBuilder strb = new StringBuilder();
            strb.append(" update Seccion sec set sec.aula=null, sec.aulaAsignadaAuto=false  where sec.id in :SECCIONES");

            Query query = getCurrentSession().createQuery(strb.toString());
            query.setParameterList("SECCIONES", seccionesIds);
            query.executeUpdate();
        }
    }

    @Override
    public List<Seccion> allByGrupoSecciones(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .in("gs.id", gruposSeccion)
                .orderBy("sec.codigo", "sec.codigo2");
        return all(sql);
    }

    @Override
    public List<Seccion> allConCruce(CicloAcademico cicloAcademico) {
        StringBuilder strb = new StringBuilder();
        strb.append(" Select  {sec.*},{aul.*},{ghor.*},{gsec.*},{cur.*} ");
        strb.append(" from aca_seccion sec ");
        strb.append(" inner join gen_aula aul on aul.id=sec.id_aula ");
        strb.append(" inner join aca_grupo_seccion gsec on gsec.id=sec.id_grupo_seccion ");
        strb.append(" inner join aca_curso cur on cur.id=gsec.id_curso ");
        strb.append(" inner join hor_grupo_horas ghor on ghor.id=sec.id_grupo_horas ");
        strb.append(" inner join gen_tipo_aula ta on ta.id=aul.id_tipo_aula ");
        strb.append(" inner join ( ");
        strb.append("         Select aul.codigo aula_codigo,ghor.codigo grupo_horario,count(sec.id) secciones_cruzadas ");
        strb.append("         from aca_seccion sec ");
        strb.append("         inner join gen_aula aul on aul.id=sec.id_aula ");
        strb.append("         inner join aca_grupo_seccion gsec on gsec.id=sec.id_grupo_seccion ");
        strb.append("         inner join hor_grupo_horas ghor on ghor.id=sec.id_grupo_horas ");
        strb.append("         inner join gen_tipo_aula ta on ta.id=aul.id_tipo_aula ");
        strb.append("         where gsec.id_ciclo=:CICLO and aul.id_oficina_supervisora=50 "); //and ta.codigo='AUL' 
        strb.append("         group by aul.codigo,ghor.codigo ");
        strb.append("         having count(*)>1 ");
        strb.append(" ) tbl on tbl.aula_codigo=aul.codigo and tbl.grupo_horario=ghor.codigo ");
        strb.append(" where gsec.id_ciclo=:CICLO order by ghor.codigo, aul.codigo ");

        Query query = getCurrentSession().createSQLQuery(strb.toString())
                .addEntity("sec", Seccion.class)
                .addEntity("aul", Aula.class)
                .addEntity("ghor", GrupoHoras.class)
                .addEntity("gsec", GrupoSeccion.class)
                .addEntity("cur", Curso.class);

        query.setParameter("CICLO", cicloAcademico.getId());

        List<Seccion> secciones = new ArrayList<>();
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            Seccion seccion = (Seccion) row[0];
            Aula aula = (Aula) row[1];
            GrupoHoras grupoHoras = (GrupoHoras) row[2];
            GrupoSeccion grupoSeccion = (GrupoSeccion) row[3];
            Curso curso = (Curso) row[4];

            grupoSeccion.setCurso(curso);

            seccion.setGrupoSeccion(grupoSeccion);
            seccion.setAula(aula);
            seccion.setGrupoHoras(grupoHoras);
            secciones.add(seccion);
        }
        return secciones;
    }

    @Override
    public List<Seccion> allConCruceHorario(CicloAcademico cicloAcademico) {
        StringBuilder strb = new StringBuilder();
        strb.append(" select distinct {s.*},{au.*},{gh.*},{gs.*},{cur.*}  ");
        strb.append(" from aca_seccion s  ");
        strb.append(" join aca_grupo_seccion gs on gs.id = s.id_grupo_seccion ");
        strb.append(" join aca_curso cur on cur.id=gs.id_curso ");
        strb.append(" join hor_horario_seccion hs on hs.id_seccion = s.id ");
        strb.append(" join hor_grupo_horas gh on gh.id = s.id_grupo_horas ");
        strb.append(" join gen_aula au on au.id = s.id_aula ");
        strb.append(" join ( ");
        strb.append("         Select aul.id, ");
        strb.append("             aul.codigo aula_codigo, ");
        strb.append("             ha.id_dia, ");
        strb.append("             ha.id_hora, ");
        strb.append("             count(*) secciones_cruzadas ");
        strb.append("         from aca_seccion sec ");
        strb.append("         join aca_grupo_seccion gsec on gsec.id=sec.id_grupo_seccion ");
        strb.append("         join hor_horario_seccion ha on ha.id_seccion = sec.id ");
        strb.append("         join gen_aula aul on aul.id=sec.id_aula ");
        strb.append("         join gen_tipo_aula ta on ta.id=aul.id_tipo_aula ");
        strb.append("         where gsec.id_ciclo=:CICLO   ");
        strb.append("         and aul.id_oficina_supervisora = 50 ");
        strb.append("         group by aul.id,aul.codigo,ha.id_dia,ha.id_hora ");
        strb.append("         having count(*) > 1 ");
        strb.append(" ) w on w.id = au.id and w.id_dia = hs.id_dia and w.id_hora = hs.id_hora ");
        strb.append(" where gs.id_ciclo = :CICLO  and s.estado = 'ACT' ");
        strb.append(" order by gh.codigo, au.codigo ");

        Query query = getCurrentSession().createSQLQuery(strb.toString())
                .addEntity("s", Seccion.class)
                .addEntity("au", Aula.class)
                .addEntity("gh", GrupoHoras.class)
                .addEntity("gs", GrupoSeccion.class)
                .addEntity("cur", Curso.class);

        query.setParameter("CICLO", cicloAcademico.getId());

        List<Seccion> secciones = new ArrayList<>();
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            Seccion seccion = (Seccion) row[0];
            Aula aula = (Aula) row[1];
            GrupoHoras grupoHoras = (GrupoHoras) row[2];
            GrupoSeccion grupoSeccion = (GrupoSeccion) row[3];
            Curso curso = (Curso) row[4];

            grupoSeccion.setCurso(curso);

            seccion.setGrupoSeccion(grupoSeccion);
            seccion.setAula(aula);
            seccion.setGrupoHoras(grupoHoras);
            secciones.add(seccion);
        }
        return secciones;
    }

    @Override
    public List<Seccion> allByCicloAndFilter(CicloAcademico ciclo, ModalidadEstudioEnum modalidadEstudioEnum, SeccionDTO seccionDTO, SeccionEstadoEnum... seccionEstadoEnum) {
        Octavia sql = Octavia.query()
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .join("gs.anexoBoletin ab", "ab.anexoSuperior abosup")
                .leftJoin("aula aul", "grupoHoras gho", "cur.modalidadEstudio modes")
                .filter("ca.id", ciclo)
                .filter("modes.id", modalidadEstudioEnum);
        if (seccionEstadoEnum != null) {
            sql.in("sec.estado", Arrays.asList(seccionEstadoEnum));
        }
        if (seccionDTO.getConAula()) {
            sql.isNotNull("aul.id");
        } else {
            sql.isNull("aul.id");
        }
        if (seccionDTO.getConHorario()) {
            sql.isNotNull("gho.id");
        } else {
            sql.isNull("gho.id");
        }
        sql.orderBy("abosup.nombre asc");
        return all(sql);
    }

}
