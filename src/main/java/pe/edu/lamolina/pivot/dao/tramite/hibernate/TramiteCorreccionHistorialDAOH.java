package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.pivot.dao.tramite.TramiteCorreccionHistorialDAO;

@Repository
public class TramiteCorreccionHistorialDAOH extends AbstractEasyDAO<TramiteCorreccionHistorial> implements TramiteCorreccionHistorialDAO {

    public TramiteCorreccionHistorialDAOH() {
        super();
        setClazz(TramiteCorreccionHistorial.class);
    }

    @Override
    public List<TramiteCorreccionHistorial> allByCicloDynatable(CicloAcademico cicloAcademico, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteCorreccionHistorial.class, "tch")
                .join("tramite tra", "tra.alumno alu", "alu.persona per", "estadoTramite")
                .join("userRegistro ur", "ur.persona up")
                .left("userModificacion um", "um.persona", "archivo")
                .leftJoin("per.tipoDocumento td")
                .searchFields("td.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("tch.id desc");

        return all(sql);
    }

    @Override
    public void updateColumns(TramiteCorreccionHistorial correccionHistorial, String... columns) {
        Octavia sql = Octavia.update(TramiteCorreccionHistorial.class, "re");
        for (String column : columns) {
            sql.set(correccionHistorial, column);
        }
        this.update(sql);
    }

    @Override
    public TramiteCorreccionHistorial findTramite(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(TramiteCorreccionHistorial.class, "tch")
                .join("tramite tr")
                .filter("tr.id", tramite);

        return find(sql);
    }

}
