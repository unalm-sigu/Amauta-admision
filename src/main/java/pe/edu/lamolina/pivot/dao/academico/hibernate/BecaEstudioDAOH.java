package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.pivot.dao.academico.BecaEstudioDAO;

@Repository
public class BecaEstudioDAOH extends AbstractEasyDAO<BecaEstudio> implements BecaEstudioDAO{
    
    public BecaEstudioDAOH() {
        super();
        setClazz(BecaEstudio.class);
    }

    @Override
    public BecaEstudio find(BecaEstudio nombre) {
        Octavia sql = Octavia.query()
                .from(BecaEstudio.class, "be")
                .filter("be.nombre", nombre);

        return (BecaEstudio) sql.find(getCurrentSession());
    }

    @Override
    public BecaEstudio findAllInfo(Long id) {
        Octavia sql = Octavia.query()
                .from(BecaEstudio.class, "be")
                .filter("be.id", id);

        return (BecaEstudio) sql.find(getCurrentSession());
    }

    @Override
    public BecaEstudio findByInstitucioOtorga(BecaEstudio nombre) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BecaEstudio> allByName(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        Octavia sql = Octavia.query()
                .from(BecaEstudio.class, "be")
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public void updateInstitucionOtorga(BecaEstudio nombre) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BecaEstudio> allDynaTable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(BecaEstudio.class, "be")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("be.id desc");
        return all(sql);
    }
    
}
