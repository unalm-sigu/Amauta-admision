package pe.edu.lamolina.pivot.dao.aporte.hibernate;

import org.hibernate.LockOptions;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.pivot.dao.aporte.GeneracionAportesDAO;

@Repository
public class GeneracionAportesDAOH extends AbstractEasyDAO<GeneracionAportes> implements GeneracionAportesDAO {

    public GeneracionAportesDAOH() {
        super();
        setClazz(GeneracionAportes.class);
    }

    @Override
    public GeneracionAportes findByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(GeneracionAportes.class, "ga")
                .filter("ga.cicloAcademico", cicloAcademico.getId());

        return find(sql);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public GeneracionAportes findLock(Long id) {
        return (GeneracionAportes) getCurrentSession().load(GeneracionAportes.class, id, LockOptions.UPGRADE);
    }

}
