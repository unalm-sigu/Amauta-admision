package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.SUBV;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.pivot.dao.tramite.TramiteSubvencionDAO;

@Repository
public class TramiteSubvencionDAOH extends AbstractEasyDAO<TramiteSubvencion> implements TramiteSubvencionDAO {

    public TramiteSubvencionDAOH() {
        super();
        setClazz(TramiteSubvencion.class);
    }

    @Override
    public List<TramiteSubvencion> allSubvencionByColaboradorCiclo(List<Colaborador> colaboradores, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(TramiteSubvencion.class, "trs")
                .join("tramite tra", "tra.alumno alu", "alu.persona per", "per.tipoDocumento")
                .join("tra.cicloAcademico ca", "tra.tipoTramite tt", "tipoSubvencion ts", "supervisor sup")
                .join("alu.carrera car", "car.facultad")
                .filter("ca.id", cicloAcademico)
                .filter("tra.estado", TramiteEstadoEnum.SOL)
                .in("sup.id", colaboradores)
                .filter("tt.codigo", SUBV)
                .filter("ts.codigo", "BOLSATRA");
        return all(sql);
    }

    @Override
    public TramiteSubvencion findId(TramiteSubvencion tramiteSubvencion) {
        Octavia sql = Octavia.query()
                .from(TramiteSubvencion.class, "ts")
                .join("tramite tra", "tra.cicloAcademico ca", "tra.tipoTramite tt", "supervisor sup")
                .filter("ts.id", tramiteSubvencion);
        return find(sql);
    }

    @Override
    public TramiteSubvencion findSubvencionByAlumnoCicloAcademico(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(TramiteSubvencion.class, "ts")
                .join("tramite tra", "tra.alumno alu", "tra.cicloAcademico ca", "tra.tipoTramite tt", "tipoSubvencion bb")
                .filter("ca.id", cicloAcademico)
                .filter("alu.id", alumno)
                .filter("tt.codigo", "SUBV");
        return (TramiteSubvencion) sql.find(getCurrentSession());
    }

}
