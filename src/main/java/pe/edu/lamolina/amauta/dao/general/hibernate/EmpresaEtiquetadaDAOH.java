package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.EmpresaEtiquetadaDAO;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;

@Repository
public class EmpresaEtiquetadaDAOH extends AbstractEasyDAO<EmpresaEtiquetada> implements EmpresaEtiquetadaDAO {

    public EmpresaEtiquetadaDAOH() {
        super();
        setClazz(EmpresaEtiquetada.class);
    }

    @Override
    public List<EmpresaEtiquetada> allBancos() {
        Octavia sql = Octavia.query()
                .from(EmpresaEtiquetada.class, "ee")
                .join("empresa emp", "etiqueta et")
                .join("emp.paisUbicacion", "emp.tipoDocIdentidad")
                .filter("et.codigo", "BANCO");

        return all(sql);
    }

}
