package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.SolicitudConstanciaDAO;

@Repository
public class SolicitudConstanciaDAOH extends AbstractEasyDAO<TramiteDocumentoAcademico> implements SolicitudConstanciaDAO {

    public SolicitudConstanciaDAOH() {
        super();
        setClazz(TramiteDocumentoAcademico.class);
    }

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteDocumentoAcademico.class, "pda")
                .join("tipoDocumentoAcademico tda", "idioma idi", "tramite tra")
                .searchFields("pda.contenido")
                .orderBy("pda.id desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public TramiteDocumentoAcademico findTramiteDocumentoAcademico(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        Octavia sql = Octavia.query()
                .from(TramiteDocumentoAcademico.class, "tda")
                .join("tipoDocumentoAcademico td", "idioma idi", "tramite tra")
                .join("tra.persona per", "tra.alumno alum", "alum.carrera car", "alum.situacionAcademica sia", "car.facultad", "car.modalidadEstudio")
                .join("tra.cicloAcademico ca", "tra.tipoTramite tt", "tt.oficina ofic")
                .filter("tda.id", tramiteDocumentoAcademico);
        return find(sql);
    }
}
