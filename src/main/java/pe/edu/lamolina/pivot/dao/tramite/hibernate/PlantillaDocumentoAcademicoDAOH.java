package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoPlantillaDocumentoEnum;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaDocumentoAcademicoDAO;

@Repository
public class PlantillaDocumentoAcademicoDAOH extends AbstractEasyDAO<PlantillaDocumentoAcademico> implements PlantillaDocumentoAcademicoDAO {

    public PlantillaDocumentoAcademicoDAOH() {
        super();
        setClazz(PlantillaDocumentoAcademico.class);
    }

    @Override
    public List<PlantillaDocumentoAcademico> allDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlantillaDocumentoAcademico.class, "pda")
                .join("tipoDocumentoAcademico tda", "idioma")
                .join("tda.oficinaEmisora ofe")
                .searchFields("tda.nombre")
                .filter("ofe.codigo", OficinaEnum.OERA)
                .orderBy("tda.nombre");
        return all(sql);
    }

    @Override
    public List<PlantillaDocumentoAcademico> allDynatableIncrustacion(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlantillaDocumentoAcademico.class, "pda")
                .join("idioma")
                .searchFields("pda.nombre")
                .filter("pda.tipo", TipoPlantillaDocumentoEnum.PARR)
                .orderBy("pda.nombre");
        return all(sql);
    }

    @Override
    public List<PlantillaDocumentoAcademico> allIncrustaciones() {
        Octavia sql = new Octavia()
                .from(PlantillaDocumentoAcademico.class, "pda")
                .join("idioma")
                .filter("pda.tipo", TipoPlantillaDocumentoEnum.PARR)
                .orderBy("pda.nombre");
        return all(sql);
    }

    @Override
    public PlantillaDocumentoAcademico find(Long id) {
        Octavia sql = new Octavia()
                .from(PlantillaDocumentoAcademico.class, "pda")
                .left("tipoDocumentoAcademico tda", "idioma")
                .filter("pda.id", id);
        return find(sql);
    }

    @Override
    public PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        Octavia sql = new Octavia()
                .from(PlantillaDocumentoAcademico.class, "pda")
                .join("tipoDocumentoAcademico tda", "idioma")
                .filter("pda.id", plantillaDocumentoAcademico);
        return find(sql);
    }

    @Override
    public PlantillaDocumentoAcademico findTipoDocumento(TipoDocumentoAcademico tipoDocumentoAcademico, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(PlantillaDocumentoAcademico.class, "pda")
                .join("tipoDocumentoAcademico tda", "idioma idi")
                .filter("tda.id", tipoDocumentoAcademico)
                .filter("idi.id", idioma);
        return find(sql);
    }
}
