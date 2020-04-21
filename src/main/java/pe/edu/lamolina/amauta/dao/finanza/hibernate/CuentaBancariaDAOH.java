package pe.edu.lamolina.amauta.dao.finanza.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.amauta.dao.finanza.CuentaBancariaDAO;

@Repository
public class CuentaBancariaDAOH extends AbstractEasyDAO<CuentaBancaria> implements CuentaBancariaDAO {

    public CuentaBancariaDAOH() {
        super();
        setClazz(CuentaBancaria.class);
    }

    @Override
    public CuentaBancaria find(long id) {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania")
                .filter("cb.id", id);

        return find(sql);
    }

    @Override
    public CuentaBancaria findByNumero(String numero) {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania")
                .filter("cb.numero", numero);

        return find(sql);
    }

    @Override
    public List<CuentaBancaria> all() {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania");

        return all(sql);
    }

}
