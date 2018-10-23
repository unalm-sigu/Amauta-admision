package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;

@Repository
public class CursoCicloAcademicoDAOH extends AbstractEasyDAO<CursoCicloAcademico> implements CursoCicloAcademicoDAO {

    public CursoCicloAcademicoDAOH() {
        super();
        this.setClazz(CursoCicloAcademico.class);
    }

    
    @Override
    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", cicloDestino);
        
        return all(sql);
    }

    @Override
    public void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio) {
        StringBuilder sql = new StringBuilder();
        
        sql.append(" update ").append(CursoCicloAcademico.class.getName()).append(" as cca set costo = :PRECIO ");
        sql.append(" where cca.curso in ( select cu.id from Curso cu where concat( cu.horasTeoria, '-', cu.horasPractica, '-', cu.creditos ) = :TPC ) ");
        sql.append(" and cca.cicloAcademico = :CICLO ");
        
        Query query = getCurrentSession().createQuery(sql.toString());
        
        query.setParameter("TPC", tpc);
        query.setParameter("PRECIO", precio);
        query.setParameter("CICLO", cicloAcademico);
        
        query.executeUpdate();
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {
        
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM ")
                .append(CursoCicloAcademico.class.getName()).append(" cca ")
                .append(" WHERE cca.cicloAcademico.id=:CICLO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
        
    }
    
}
