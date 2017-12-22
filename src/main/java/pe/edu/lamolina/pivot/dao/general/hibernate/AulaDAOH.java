package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.model.general.Aula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoOficinaEnum;

@Repository
public class AulaDAOH extends AbstractDAO<Aula> implements AulaDAO {

    public AulaDAOH() {
        super();
        setClazz(Aula.class);
    }

    @Override
    public Aula findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("au")
                .filter("au.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<Aula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior aus", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .searchFields("au.nombre", "aus.nombre", "ta.nombre", "au.codigo", "os.nombre")
                .orderBy("au.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public Integer findAforoByEdificio(Aula aula) {
        Octavia sql = Octavia.query()
                .select("sum(au.aforo)")
                .from(Aula.class, "au")
                .join("aulaSuperior aus")
                .filter("aus.id", aula);

        return (Integer) sql.find(getCurrentSession());
    }

    @Override
    public List<Aula> allAulasSuperioresByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .left("aulaSuperior aus")
                .isNull("aus.id")
                .filter("au.nombre", "like", nombre);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allByAulaSuperior(Aula aula) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("aulaSuperior aus")
                .filter("aus.id", aula);
        return sql.all(getCurrentSession());
    }

    @Override
    public Aula find(Long id) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior aus", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .filter("au.id", id);
        return (Aula) sql.find(getCurrentSession());
    }

    @Override
    public List<Aula> allAulasSuperiorByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .selectDistinct("aus")
                .from(Aula.class, "au")
                .join("au.aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("ofi.id", oficina);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allAulasSuperiorByTipoOficina(TipoOficinaEnum tipoOficinaEnum) {
        Octavia sql = Octavia.query()
                .selectDistinct("aus")
                .from(Aula.class, "au")
                .join("au.aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("ofi.tipoOficina", tipoOficinaEnum.name());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allBySuperior(Aula aulaSuperior) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("au.aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("aus.id", aulaSuperior);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allSuperiorByOficinaWithAulas(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .selectDistinct("aus")
                .from(Aula.class, "au")
                .join("au.oficinaSupervisora ofi", "au.aulaSuperior aus")
                .filter("au.estado", EstadoEnum.ACT.name())
                .in("ofi.id", oficinas)
                .notIn("ofi.id", Arrays.asList(Constantine.ID_OFICINA_OERA));
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> searchByNombreFilter(String nombre, Integer limit) {
        Octavia sql = Octavia.query();
        sql.from(Aula.class, "au");
        sql.join("aulaSuperior aus");
        sql.filter("au.estado", EstadoEnum.ACT.name());
        sql.beginBlock()
                .__().complexFilter("concat(coalesce(au.codigo,''),' ',coalesce(au.nombre,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(au.nombre,''),' ',coalesce(au.codigo,''))", "like", nombre)
                .endBlock();
        return sql.all(getCurrentSession());
    }

}
