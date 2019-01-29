package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.OficinaNivel;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
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
                .searchFields("ofi.codigo", "ofi.nombre", "ofi.tipoOficina", "ca.nombre", "sup.nombre")
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

    @Override
    public Map findOficinaOrigenDestinoByEstadoTramiteAcad(AccionTramiteAcademico accionTramiteAcademico, Alumno alumno) {
        Oficina oficinaOrigen = null;
        if (ObjectUtil.getParentTree(accionTramiteAcademico, "oficinaOrigen.id") != null) {
            oficinaOrigen = this.find(accionTramiteAcademico.getOficinaOrigen().getId());
        } else {
            if (accionTramiteAcademico.getTipoOficinaOrigen().isTipoFacultad()) {
                oficinaOrigen = this.findByTipoAndFacultad(
                        TipoOficinaEnum.valueOf(accionTramiteAcademico.getTipoOficinaOrigen().getCodigo()),
                        alumno.getCarrera().getFacultad());
            }
        }
        Oficina oficinaDestino = null;
        if (ObjectUtil.getParentTree(accionTramiteAcademico, "oficinaDestino.id") != null) {
            oficinaDestino = this.find(accionTramiteAcademico.getOficinaDestino().getId());
        } else {
            if (accionTramiteAcademico.getTipoOficinaDestino().isTipoFacultad()) {
                oficinaDestino = this.findByTipoAndFacultad(
                        TipoOficinaEnum.valueOf(accionTramiteAcademico.getTipoOficinaDestino().getCodigo()),
                        alumno.getCarrera().getFacultad());
            }
        }
        Map resultado = new HashMap();
        resultado.put("oficinaOrigen", oficinaOrigen);
        resultado.put("oficinaDestino", oficinaDestino);
        return resultado;
    }

    @Override
    public List<Oficina> allOficinaByUserMenu(Usuario usuario, Menu menu) {
        Octavia subquery = Octavia.query()
                .from(MenuRol.class, "mr")
                .join("menu m", "rol r2")
                .filter("m.id", menu);

        Octavia sql = Octavia.query()
                .selectDistinct("ofi")
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol r1", "oficina ofi")
                .filter("u.id", usuario)
                .exists(subquery)
                .linkedBy("r1.id", "r2.id");

        return sql.all(getCurrentSession());

    }

    @Override
    public List<Oficina> allByNivel(TipoOficinaEnum tipoOficinaEnum) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "o")
                .join("tipoOficina to")
                .filter("to.nivel", tipoOficinaEnum.name());
        return all(sql);
    }

    @Override
    public List<Oficina> allByNombre(String nombre, Compania compania) {
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
    public Oficina find(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("tipoOficina")
                .leftJoin("personaJefe pj", "jefeEncargado", "cargoJefe", "oficinaSuperior")
                .filter("ofi.id", oficina);

        return find(sql);
    }

    @Override
    public List<Oficina> allForResoluciones() {
        Octavia sql = Octavia.query()
                .from(Oficina.class, "o")
                .join("tipoOficina to")
                .beginBlock()
                .__().filter("to.codigo", TipoOficinaEnum.FAC)
                .__().filter("o.codigo", OficinaEnum.UNA)
                .__().filter("o.codigo", OficinaEnum.EPG)
                .endBlock();

        return all(sql);
    }

    @Override
    public List<Oficina> allOficinaByName(String nombre, Compania compania) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sql = Octavia.query()
                .from(Oficina.class, "ofi")
                .join("compania cia", "tipoOficina to")
                .leftJoin("personaJefe pj", "jefeEncargado", "cargoJefe", "oficinaSuperior")
                .filter("cia.id", compania)
                .filter("to.nivel", OficinaNivel.OFI)
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

}
