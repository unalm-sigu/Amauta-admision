package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;

@Repository
public class PerfilCompaniaDAOH extends AbstractEasyDAO<PerfilCompania> implements PerfilCompaniaDAO {

    public PerfilCompaniaDAOH() {
        super();
        setClazz(PerfilCompania.class);
    }

    @Override
    public List<PerfilCompania> allByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO)
                .like("pc.nombreDocumento", nombre)
                .orderBy("pc.nombreDocumento")
                .limit(15);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allTipoCargoByOfi(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .join("oficinaContiene ofi")
                .filter("ofi.id", oficina)
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allTipoCargo() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .isNull("oficinaContiene")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allTipoFuncion() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.FUNCION);

        return all(sql);
    }

    @Override
    public PerfilCompania findUltimoCodigo() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("tipo", TipoPerfilCompaniaEnum.CARGO)
                .filter("esAutomatico", 1)
                .orderBy("id desc")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<PerfilCompania> allFuncion(String nombre, Compania compania) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .join("compania cia")
                .leftJoin("oficinaContiene oc")
                .filter("tipo", TipoPerfilCompaniaEnum.FUNCION)
                .filter("cia.id", compania)
                .beginBlock()
                .__().filter("pc.nombre", "like", nombre)
                .__().filter("pc.nombreDocumento", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<PerfilCompania> allCargo(String nombre, Compania compania) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .join("compania cia")
                .leftJoin("oficinaContiene oc")
                .filter("pc.tipo", TipoPerfilCompaniaEnum.CARGO)
                .filter("cia.id", compania)
                .beginBlock()
                .__().filter("pc.nombre", "like", nombre)
                .__().filter("pc.nombreDocumento", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<PerfilCompania> allPerfilCompaniaByTipo(PerfilCompania perfilCompania, Compania compania) {
        String nombre = "%" + perfilCompania.getNombre().replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .join("compania cia")
                .leftJoin("oficinaContiene oc")
                .filter("pc.tipo", perfilCompania.getTipo())
                .filter("cia.id", compania)
                .beginBlock()
                .__().filter("pc.nombre", "like", nombre)
                .__().filter("pc.nombreDocumento", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public PerfilCompania findFuncionByNombre(String nombre) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("pc.tipo", TipoPerfilCompaniaEnum.FUNCION)
                .filter("pc.nombre", nombre);
        return find(sql);
    }

    @Override
    public PerfilCompania findUltimoCodigoFuncion() {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .filter("pc.tipo", TipoPerfilCompaniaEnum.FUNCION)
                .filter("pc.esAutomatico", 1)
                .orderBy("id desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<PerfilCompania> allCargoByOficinaAltoPerfil(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .left("pc.oficinaContiene")
                .beginBlock()
                .__().filter("pc.oficinaContiene", oficina)
                .__().isNull("pc.oficinaContiene")
                .endBlock()
                .filter("pc.tipo", TipoPerfilCompaniaEnum.CARGO);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allCargoByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .left("pc.oficinaContiene")
                .filter("pc.oficinaContiene", oficina)
                .filter("pc.tipo", TipoPerfilCompaniaEnum.CARGO);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allFuncionesByOficinaAltoPerfil(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .left("pc.oficinaContiene")
                .beginBlock()
                .__().filter("pc.oficinaContiene", oficina)
                .__().isNull("pc.oficinaContiene")
                .endBlock()
                .filter("pc.tipo", TipoPerfilCompaniaEnum.FUNCION);

        return all(sql);
    }

    @Override
    public List<PerfilCompania> allFuncionesByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .left("pc.oficinaContiene")
                .filter("pc.oficinaContiene", oficina)
                .filter("pc.tipo", TipoPerfilCompaniaEnum.FUNCION);

        return all(sql);
    }

    @Override
    public PerfilCompania findByCodigo(PerfilColaboradorEnum perfilEnum) {
        Octavia sql = Octavia.query()
                .from(PerfilCompania.class, "pc")
                .left("pc.oficinaContiene")
                .filter("codigo", perfilEnum);

        return find(sql);
    }

}
