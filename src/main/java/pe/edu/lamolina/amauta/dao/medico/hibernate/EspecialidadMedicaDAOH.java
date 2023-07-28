package pe.edu.lamolina.amauta.dao.medico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.medico.EspecialidadMedicaDAO;
import pe.edu.lamolina.model.enums.medico.ConsultorioEnum;
import pe.edu.lamolina.model.medico.EspecialidadMedica;

@Repository
public class EspecialidadMedicaDAOH extends AbstractEasyDAO<EspecialidadMedica> implements EspecialidadMedicaDAO {

    public EspecialidadMedicaDAOH() {
        super();
        setClazz(EspecialidadMedica.class);
    }

    @Override
    public EspecialidadMedica findByCodigoEnum(ConsultorioEnum codigo) {
        Octavia sql = Octavia.query()
                .from(EspecialidadMedica.class, "em")
                .filter("codigo", codigo);

        return find(sql);
    }

}
