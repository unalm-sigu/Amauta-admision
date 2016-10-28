package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.CuentaBancariaDAO;
import pe.edu.lamolina.pivot.model.finanzas.CuentaBancaria;
import org.springframework.stereotype.Repository;

@Repository
public class CuentaBancariaDAOH extends AbstractDAO<CuentaBancaria> implements CuentaBancariaDAO {

    public CuentaBancariaDAOH() {
        super();
        setClazz(CuentaBancaria.class);
    }
}

