package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.DiaEnum;
import pe.edu.lamolina.model.general.Dia;

@Repository
public class DiaDAOH extends AbstractEasyDAO<Dia> implements DiaDAO {

    public DiaDAOH() {
        super();
        setClazz(Dia.class);
    }

    @Override
    public List<Dia> allDia() {
        Octavia sql = Octavia.query()
                .from(Dia.class, "di")
                .orderBy("di.numeroDia");

        return all(sql);
    }

    @Override
    public Dia findByNumeroDia(Integer numero) {
        Octavia sql = Octavia.query()
                .from(Dia.class, "di")
                .filter("numeroDia", numero)
                .orderBy("di.numeroDia");

        return (Dia) sql.find(getCurrentSession());
    }

    @Override
    public List<Dia> allDiaForPrinter() {
        Octavia sql = Octavia.query()
                .from(Dia.class, "di")
                .notIn("di.nombre", Arrays.asList("Domingo"))
                .orderBy("di.numeroDia");

        return all(sql);
    }
}
