package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.general.ConfiguracionFirmaDocumentoDAO;

@Repository
public class ConfiguracionFirmaDocumentoDAOH extends AbstractEasyDAO<ConfiguracionFirmaDocumento> implements ConfiguracionFirmaDocumentoDAO {

    public ConfiguracionFirmaDocumentoDAOH() {
        super();
        setClazz(ConfiguracionFirmaDocumento.class);
    }

    @Override
    public List<ConfiguracionFirmaDocumento> allByTipoDocumentoAcademico(TipoDocumentoAcademico tipoDocumentoAcademico) {
        Octavia sql = Octavia.query()
                .from(ConfiguracionFirmaDocumento.class, "cfd")
                .join("tipoDocumentoAcademico tda")
                .leftJoin("tipoOficina to", "oficina ofi")
                .filter("tda.id", tipoDocumentoAcademico)
                .orderBy("cfd.orden");
        return all(sql);
    }

    @Override
    public void deleteByTipoDocumentoAcademicos(TipoDocumentoAcademico tipoDocumentoAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ConfiguracionFirmaDocumento ");
        sql.append(" where  tipoDocumentoAcademico.id = :TIPODOCUMENTO");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("TIPODOCUMENTO", tipoDocumentoAcademico.getId());
        query.executeUpdate();

    }

}
