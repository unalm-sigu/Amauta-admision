package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Repository
public class OficinaDAOH extends AbstractEasyDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }

    @Override
    public Oficina find(long id) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("tipoOficina")
                .leftJoin("personaJefe pj", "jefeEncargado", "cargoJefe", "oficinaSuperior")
                .filter("ofi.id", id);

        return find(sql);
    }

    @Override
    public List<Oficina> all() {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .leftJoin("personaJefe pj", "jefeEncargado", "cargoJefe", "oficinaSuperior");

        return all(sql);
    }

    @Override
    public List<Oficina> allByJefe(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("personaJefe pj", "tipoOficina")
                .filter("pj.id", persona);

        return all(sql);
    }

    @Override
    public List<Oficina> allByFilter(DynatableFilter filter, Compania compania) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .leftJoin("oficinaSuperior sup", "personaJefe pj", "jefeEncargado pje", "cargoJefe ca", "tipoOficina")
                .filter("cia.id", compania)
                .searchFields("ofi.codigo", "ofi.nombre", "ofi.tipoOficina", "ca.nombre")
                .searchComplexField("concat(coalesce(pj.paterno,''),' ',coalesce(pj.materno,''),' ',coalesce(pj.nombres,''))")
                .searchComplexField("concat(coalesce(pj.nombres,''),' ',coalesce(pj.paterno,''),' ',coalesce(pj.materno,''))")
                .searchComplexField("concat(coalesce(pje.paterno,''),' ',coalesce(pje.materno,''),' ',coalesce(pje.nombres,''))")
                .searchComplexField("concat(coalesce(pje.nombres,''),' ',coalesce(pje.paterno,''),' ',coalesce(pje.materno,''))")
                .orderBy("ofi.id DESC");

        return all(sql);
    }

    @Override
    public List<Oficina> allUnidadSuperior(String nombre, Compania compania) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .filter("cia.id", compania)
                .orderBy("ofi.nombre")
                .limit(10);

        if (!"".equalsIgnoreCase(nombre)) {
            sql.beginBlock()
                    .__().like("ofi.codigo", nombre)
                    .__().like("ofi.nombre", nombre)
                    .endBlock();
        }

        return all(sql);
    }

    @Override
    public List<Oficina> allOficinasByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("oficinaSuperior")
                .filter("ofi.nombre", "like", nombre);

        return all(sql);
    }

    @Override
    public List<Oficina> allByOficinaWithAulas(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .selectDistinct("ofi")
                .from(Aula.class, "au")
                .join("au.oficinaSupervisora ofi")
                .filter("au.estado", EstadoEnum.ACT.name()).
                in("ofi.id", oficinas)
                .notIn("ofi.id", Arrays.asList(Constantine.ID_OFICINA_OERA));
        return all(sql);
    }

    @Override
    public List<Oficina> allByUser(Persona persona) {
        Octavia sql = Octavia.query()
                .selectDistinct("ofi")
                .from(Colaborador.class, "co")
                .join("oficina ofi")
                .filter("co.persona", persona);
        return all(sql);
    }

    @Override
    public List<Oficina> allAndSuperiorOfi() {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .left("oficinaSuperior", "tipoOficina");

        return all(sql);
    }

    @Override
    public List<Oficina> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .leftJoin("oficinaSuperior sup", "personaJefe pj", "jefeEncargado pje", "cargoJefe ca", "tipoOficina")
                .filter("ofi.nombre", "like", nombre);
        return all(sql);
    }

    @Override
    public Oficina findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .leftJoin("oficinaSuperior sup", "personaJefe pj", "jefeEncargado pje", "cargoJefe ca", "tipoOficina")
                .filter("ofi.codigo", codigo);
        return find(sql);
    }

    @Override
    public List<Oficina> allByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("compania cia")
                .leftJoin("oficinaSuperior sup", "personaJefe pj", "jefeEncargado pje", "cargoJefe ca", "tipoOficina")
                .filter("cia.id", compania);
        return all(sql);
    }

    @Override
    public Oficina findByTipoAndFacultad(TipoOficinaEnum tipoOficinaEnum, Facultad facultad) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "o")
                .join("tipoOficina to")
                .filter("to.codigo", tipoOficinaEnum)
                .filter("o.instanciaOficina", facultad);
        return (Oficina) sql.find(getCurrentSession());
    }

}
