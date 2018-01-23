package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;

@Repository
public class EmpresaDAOH extends AbstractEasyDAO<Empresa> implements EmpresaDAO {

    public EmpresaDAOH() {
        super();
        setClazz(Empresa.class);
    }

    @Override
    public List<Empresa> allEmpresaByName(Pais pais, String nombre) {
        Octavia sql = Octavia.query()
                .from(Empresa.class, "cia")
                .left("paisUbicacion pu")
                .filter("pu.id", pais)
                .beginBlock()
                .__().filter("cia.razonSocial", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

}
