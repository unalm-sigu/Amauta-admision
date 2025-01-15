package pe.edu.lamolina.amauta.dao.admision.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.ModalidadIngresoDAO;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class ModalidadIngresoDAOH extends AbstractEasyDAO<ModalidadIngreso> implements ModalidadIngresoDAO {

    public ModalidadIngresoDAOH() {
        super();
        setClazz(ModalidadIngreso.class);
    }

    @Override
    public ModalidadIngreso findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngreso.class, "mi")
                .filter("mi.codigo", codigo);

        return find(sql);
    }

}
