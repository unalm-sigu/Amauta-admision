package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoAcademicoDAO;

@Repository
public class TramiteDocumentoAcademicoDAOH extends AbstractEasyDAO<TramiteDocumentoAcademico> implements TramiteDocumentoAcademicoDAO {

    public TramiteDocumentoAcademicoDAOH() {
        super();
        setClazz(TramiteDocumentoAcademico.class);
    }

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter) {
        Octavia sqlSub = new Octavia()
                .from(ConfiguracionFirmaDocumento.class, "cfd")
                .join("tipoDocumentoAcademico subtda", "oficina ofi")
                .filter("ofi.codigo", EPG.name());

        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteDocumentoAcademico.class, "pda")
                .join("tipoDocumentoAcademico tda", "idioma idi", "tramite tra", "tra.alumno alu", "alu.persona per", "estadoTramite")
                .leftJoin("per.tipoDocumento td")
                .searchFields("td.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .notExists(sqlSub)
                .linkedBy("tda.id", "subtda.id")
                .orderBy("pda.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public TramiteDocumentoAcademico find(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        Octavia sql = Octavia.query()
                .from(TramiteDocumentoAcademico.class, "tda")
                .join("tipoDocumentoAcademico td", "idioma idi", "tramite tra", "estadoTramite")
                .join("tra.persona per", "tra.alumno alum", "alum.carrera car", "alum.situacionAcademica sia", "car.facultad", "car.modalidadEstudio")
                .join("tra.cicloAcademico ca", "tra.tipoTramite tt", "tt.oficina ofic")
                .filter("tda.id", tramiteDocumentoAcademico);
        return find(sql);
    }

    @Override
    public void updateColumns(TramiteDocumentoAcademico tramiteDocumentoAcademico, String... columns) {
        Octavia sql = Octavia.update(TramiteDocumentoAcademico.class, "se");
        for (String column : columns) {
            sql.set(tramiteDocumentoAcademico, column);
        }
        this.update(sql);
    }

    @Override
    public TramiteDocumentoAcademico findTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(TramiteDocumentoAcademico.class, "tda")
                .join("tipoDocumentoAcademico td", "idioma idi", "tramite tra", "estadoTramite")
                .join("tra.persona per", "tra.alumno alum", "alum.carrera car", "alum.situacionAcademica sia", "car.facultad", "car.modalidadEstudio")
                .join("tra.cicloAcademico ca", "tra.tipoTramite tt", "tt.oficina ofic")
                .filter("tra.id", tramite);
        return find(sql);
    }
}
