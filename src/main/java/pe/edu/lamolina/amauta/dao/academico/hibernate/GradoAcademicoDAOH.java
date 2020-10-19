package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.GradoAcademicoDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.GradoAcademico;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;

@Repository
public class GradoAcademicoDAOH extends AbstractEasyDAO<GradoAcademico> implements GradoAcademicoDAO {

    public GradoAcademicoDAOH() {
        super();
        setClazz(GradoAcademico.class);
    }

    @Override
    public GradoAcademico findByTipoAndCarrera(TipoGradoAcademicoEnum tipoGradoAcademicoEnum, Carrera carrera) {
        Octavia sql = new Octavia()
                .from(GradoAcademico.class, "ga")
                .join("carrera car")
                .filter("tipo", tipoGradoAcademicoEnum)
                .filter("car.id", carrera);

        return find(sql);
    }

}
