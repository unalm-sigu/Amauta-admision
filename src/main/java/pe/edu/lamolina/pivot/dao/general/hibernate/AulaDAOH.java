package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;

@Repository
public class AulaDAOH extends AbstractEasyDAO<Aula> implements AulaDAO {

    public AulaDAOH() {
        super();
        setClazz(Aula.class);
    }

    @Override
    public Aula findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .filter("au.codigo", codigo);

        return find(sql);
    }

    @Override
    public Aula findActiveByCode(String code) {
        Octavia sql = Octavia.query();
        sql.from(Aula.class, "au");
        sql.join("aulaSuperior aus");
        sql.filter("au.codigo", code);
        sql.filter("au.estado", EstadoEnum.ACT.name());
        return find(sql);
    }

    @Override
    public List<Aula> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior aus", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .searchFields("au.nombre", "aus.nombre", "ta.nombre", "au.codigo", "os.nombre")
                .orderBy("au.id desc");

        return all(sql);
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
                .filter("au.tipoAmbiente", "EDI")
                .filter("au.nombre", "like", nombre)
                .orderBy("au.nombre", "au.codigo");

        return all(sql);
    }

    @Override
    public List<Aula> allByAulaSuperior(Aula aula) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("aulaSuperior aus")
                .filter("aus.id", aula)
                .orderBy("au.nombre", "au.codigo");

        return all(sql);
    }

    @Override
    public List<Aula> allByAulasSuperiores(List<Aula> aulas) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("aulaSuperior aus")
                .in("aus.id", aulas)
                .orderBy("au.nombre", "au.codigo");

        return all(sql);
    }

    @Override
    public Aula find(Long id) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior aus", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .filter("au.id", id);

        return find(sql);
    }

    @Override
    public List<Aula> allPabellonesByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .selectDistinct("aus")
                .from(Aula.class, "au")
                .join("au.aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("ofi.id", oficina);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allByOficinaModulo(Oficina oficina, Aula modulo) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("aus.id", modulo)
                .filter("ofi.id", oficina)
                .orderBy("au.nombre", "au.codigo");

        return all(sql);
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
    public List<Aula> allByPabellon(Aula pabellon) {
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("au.aulaSuperior aus", "au.oficinaSupervisora ofi")
                .filter("aus.id", pabellon);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allPabellonesByOficinasNoOera(List<Oficina> oficinas) {
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
        Octavia sql = Octavia.query()
                .from(Aula.class, "au")
                .join("aulaSuperior aus")
                .filter("au.estado", EstadoEnum.ACT.name())
                .beginBlock()
                .__().complexFilter("concat(coalesce(au.codigo,''),' ',coalesce(au.nombre,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(au.nombre,''),' ',coalesce(au.codigo,''))", "like", nombre)
                .endBlock()
                .orderBy("au.codigo", "au.nombre")
                .limit(limit);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<Aula> allByDynatableFilterTramite(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Aula.class, "au")
                .leftJoin("aulaSuperior aus", "sede se", "tipoAula ta", "oficinaSupervisora os")
                .searchFields("au.nombre", "aus.nombre", "ta.nombre", "au.codigo", "os.nombre")
                .orderBy("au.id desc");

        return all(sql);
    }

}
