package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.CargaAbonosDAO;
import pe.edu.lamolina.pivot.model.finanzas.CargaAbonos;
import org.springframework.stereotype.Repository;

@Repository
public class CargaAbonosDAOH extends AbstractDAO<CargaAbonos> implements CargaAbonosDAO {

    public CargaAbonosDAOH() {
        super();
        setClazz(CargaAbonos.class);
    }
}

