package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.BolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.pivot.dao.tramite.BolsaInvestigacionDAO;

@Repository
public class BolsaInvestigacionDAOH extends AbstractEasyDAO<BolsaInvestigacion> implements BolsaInvestigacionDAO {

    public BolsaInvestigacionDAOH() {
        super();
        setClazz(BolsaInvestigacion.class);
    }
    
    @Override
    public List<BolsaInvestigacion> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(BolsaInvestigacion.class, "bi")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<BolsaInvestigacion> allByCicloAcademicoEstado(CicloAcademico cicloAcademico, BolsaInvestigacionEstadoEnum estado) {
        Octavia sql = Octavia.query(BolsaInvestigacion.class, "bi")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("bi.estado", estado);

        return all(sql);
    }

    @Override
    public List<BolsaInvestigacion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(BolsaInvestigacion.class, "bi")
                .join("facultad f", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("f.nombre", "f.codigo")
                .orderBy("bi.id desc");

        return all(sql);
    }

    @Override
    public Boolean allEstado(CicloAcademico cicloAcademico, BolsaInvestigacionEstadoEnum estado) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BolsaInvestigacion findByFacultadCicloAcademico(Facultad facultad, CicloAcademico cicloAcademico) {
          Octavia sql = Octavia.query(BolsaInvestigacion.class, "bi")
                .join("cicloAcademico ca", "facultad fa")
                .filter("ca.id", cicloAcademico)
                .filter("fa.id", facultad);

        return find(sql);
    }
}
