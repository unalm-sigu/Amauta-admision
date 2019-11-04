package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.OficinaEnum.EPG;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;

@Repository
public class TipoConstanciaDAOH extends AbstractEasyDAO<TipoDocumentoAcademico> implements TipoConstanciaDAO {

    public TipoConstanciaDAOH() {
        super();
        setClazz(TipoDocumentoAcademico.class);
    }

    @Override
    public List<TipoDocumentoAcademico> allDynatable(DynatableFilter filter) {
        Octavia sqlSub = new Octavia()
                .from(ConfiguracionFirmaDocumento.class, "cfd")
                .join("tipoDocumentoAcademico subtda", "oficina ofi")
                .filter("ofi.codigo", EPG.name());

        DynatableSql sql = new DynatableSql(filter)
                .from(TipoDocumentoAcademico.class, "tda")
                .notExists(sqlSub)
                .linkedBy("tda.id", "subtda.id")
                .searchFields("tda.nombre")
                .orderBy("nombre");
        return all(sql);
    }

    @Override
    public TipoDocumentoAcademico find(TipoDocumentoAcademico tipoDocumentoAcademico) {
        Octavia sql = new Octavia()
                .from(TipoDocumentoAcademico.class, "tda")
                .filter("tda.id", tipoDocumentoAcademico);
        return find(sql);
    }

    @Override
    public List<TipoDocumentoAcademico> allTipoDocumentoAcademicoByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia subQuery = Octavia.query()
                .select("td.id")
                .from(PrecioDocumento.class, "pc")
                .join("tipoDocumento td", "idioma idi");
        Octavia sql = Octavia.query()
                .from(TipoDocumentoAcademico.class, "tipo")
                .in("tipo.id", subQuery)
                .beginBlock()
                .__().filter("nombre", "like", nombre)
                .endBlock()
                .limit(15);
        return all(sql);
    }

    @Override
    public List<TipoDocumentoAcademico> allTipoDocumento() {
        Octavia sqlSub = new Octavia()
                .from(ConfiguracionFirmaDocumento.class, "cfd")
                .join("tipoDocumentoAcademico subtda", "oficina ofi")
                .filter("ofi.codigo", EPG.name());

        Octavia sql = Octavia.query()
                .from(TipoDocumentoAcademico.class, "tipo")
                .notExists(sqlSub)
                .linkedBy("tipo.id", "subtda.id");
        return all(sql);
    }

    @Override
    public List<TipoDocumentoAcademico> allWhyPrecios() {
        Octavia sqlSub = new Octavia()
                .from(ConfiguracionFirmaDocumento.class, "cfd")
                .join("tipoDocumentoAcademico subtda", "oficina ofi")
                .filter("ofi.codigo", EPG.name());

        Octavia subQuery = Octavia.query()
                .select("td.id")
                .from(PrecioDocumento.class, "pc")
                .join("tipoDocumento td", "idioma idi");

        Octavia sql = Octavia.query()
                .from(TipoDocumentoAcademico.class, "tipo")
                .in("tipo.id", subQuery)
                .notExists(sqlSub)
                .linkedBy("tipo.id", "subtda.id");
        return all(sql);
    }

}
