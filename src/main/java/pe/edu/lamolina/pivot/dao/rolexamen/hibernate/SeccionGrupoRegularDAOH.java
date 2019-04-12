package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoGestionEnum.PUB;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.*;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;

@Repository
public class SeccionGrupoRegularDAOH extends AbstractEasyDAO<SeccionGrupoRegular> implements SeccionGrupoRegularDAO {

    public SeccionGrupoRegularDAOH() {
        super();
        setClazz(SeccionGrupoRegular.class);
    }

    @Override
    public SeccionGrupoRegular find(long id) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .join("lgr.rolExamenes rex")
                .filter("sgr.id", id);
        return find(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, SeccionRolExamenEstadoEnum... seccionRolExamenEstadosEnum) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "lgr.grupoHorasExamen ghe", "ghe.grupoHoras gh", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("lgr.rolExamenes rex")
                .filter("gh.id", grupoHorasExamen)
                .in("sgr.estado", seccionRolExamenEstadosEnum);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            LetraGrupoRegular letrasGruposRegular, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .left("docente doc", "doc.persona dper")
                .filter("lgr.id", letrasGruposRegular)
                .in("sgr.estado", estados);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByRolExamenes(
            RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... seccionRolExamenEstadoEnums) {
        //  List<SeccionRolExamenEstadoEnum> estados = Arrays.asList(seccionRolExamenEstadoEnums);
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "lgr.rolExamenes rex")
                .left("docente doc", "doc.persona dper")
                .in("sgr.estado", seccionRolExamenEstadoEnums)
                .filter("rex.id", rolExamenes);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            List<LetraGrupoRegular> letrasGruposRegular, SeccionRolExamenEstadoEnum... estados) {
        List<SeccionRolExamenEstadoEnum> lEstados = Arrays.asList(estados);
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "docente doc", "doc.persona dper", "aula au")
                .in("lgr.id", letrasGruposRegular)
                .in("sgr.estado", lEstados);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndSecciones(
            LetraGrupoRegular letrasGruposRegular, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .filter("lgr.id", letrasGruposRegular)
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public void updateEstadoExclusion(SeccionGrupoRegular seccionGrupoRegularUpd) {
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(SeccionGrupoRegular.class);
        octavia.set(seccionGrupoRegularUpd, "estado");
        octavia.set(seccionGrupoRegularUpd, "usuarioExclusion");
        octavia.set(seccionGrupoRegularUpd, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd) {
        Octavia octavia = Octavia.update(SeccionGrupoRegular.class);
        octavia.set(seccionGrupoRegularUpd, "estado");
        this.update(octavia);
    }

    @Override
    public Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("lgr.id", "count(sgr)")
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr")
                .in("sgr.estado", estados)
                .in("lgr.id", letraGrupoRegulars)
                .groupBy("lgr.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  SeccionGrupoRegular sgr where sgr.letraGrupoRegular.id=:LETRA_ID");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA_ID", letraGrupoRegular.getId());
        query.executeUpdate();
    }

    @Override
    public List<SeccionGrupoRegular> allByDynatableAndLetraGrupoRegular(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "docente doc", "doc.persona dper", "sec.grupoSeccion gpo", "gpo.curso cur")
                .join("sgr.userRegistro ureg", "ureg.persona uregper")
                .left("usuarioExclusion usexc", "usexc.persona usexcper")
                .left("aula au")
                .filter("lgr.id", letraGrupoRegular)
                .searchFields("sec.codigo2", "cur.nombre", "cur.codigo", "au.codigo")
                .searchComplexField("concat(coalesce(dper.paterno,''),' ',coalesce(dper.materno,''),' ',coalesce(dper.nombres,''))")
                .searchComplexField("concat(coalesce(dper.nombres,''),' ',coalesce(dper.paterno,''),' ',coalesce(dper.materno,''))");

        return all(sql);
    }

    @Override
    public List<RolExamenDocente> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .select("cur", "ghe", "au", "sec", "re.estado", "re.id", "re.nombre")
                .into(RolExamenDocente.class)
                .from(SeccionGrupoRegular.class, "sgr")
                .join("docente doc", "seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "aula au", "letraGrupoRegular lgr")
                .join("lgr.rolExamenes re", "re.eventoCicloAcademico eca", "eca.cicloAcademico ca")
                .join("lgr.grupoHorasExamen ghe", "ghe.dia di", "ghe.horaInicio hi", "ghe.horaFin hf")
                .filter("doc.id", docente)
                .filter("re.estado", PUB)
                .filter("ca.id", cicloAcademico);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<SeccionGrupoRegular> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes) {

        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "lgr.grupoHorasExamen ghe", "ghe.grupoHoras gh", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("lgr.rolExamenes rex")
                .in("ghe.id", grupoHorasExamenes);
        return all(sql);
    }

    @Override
    public SeccionGrupoRegular findByRolExamenesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec", "lgr.grupoHorasExamen ghe", "ghe.grupoHoras gh", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("lgr.rolExamenes rex")
                .filter("sec.id", seccion)
                .in("sgr.estado", estados)
                .filter("rex.id", rol);

        return find(sql);
    }

    @Override
    public void createForLetraGrupoRegular(
            List<SeccionGrupoRegular> seccionesGpoRegular,
            LetraGrupoRegular letraGpoRegular,
            Date fecha,
            Usuario user) {

        if (seccionesGpoRegular.isEmpty()) {
            return;
        }

        List<Long> ids = seccionesGpoRegular.stream().map(x -> x.getSeccion().getId()).collect(Collectors.toList());

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tb(SeccionGrupoRegular.class));
        sql.append("  (estado,fechaRegistro,letraGrupoRegular,seccion,aula,docente,userRegistro) ");
        sql.append(" select :ESTADO, :FECHA, lgr, sec, aula, doc, usr ");
        sql.append("   from ").append(tb(DocenteSeccion.class)).append(" docSec ");
        sql.append("        join docSec.docente doc ");
        sql.append("        join docSec.seccion sec ");
        sql.append("        join sec.aula aula, ");
        sql.append("        ").append(tb(LetraGrupoRegular.class)).append(" lgr, ");
        sql.append("        ").append(tb(Usuario.class)).append(" usr ");
        sql.append("  where sec.id in (:SECCIONES) ");
        sql.append("    and docSec.principal = :PRINCIPAL ");
        sql.append("    and docSec.estado = :ESTADO_DOCSEC ");
        sql.append("    and lgr.id = :LETRA ");
        sql.append("    and usr.id = :USER ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("SECCIONES", ids);
        query.setParameter("LETRA", letraGpoRegular.getId());
        query.setParameter("PRINCIPAL", BigDecimal.ONE.intValue());
        query.setParameter("USER", user.getId());
        query.setParameter("ESTADO", SeccionRolExamenEstadoEnum.ACT.name());
        query.setParameter("ESTADO_DOCSEC", EstadoEnum.ACT.name());
        query.setParameter("FECHA", fecha);

        query.executeUpdate();

    }

    private String tb(Class clazz) {
        return clazz.getSimpleName();
    }

}
