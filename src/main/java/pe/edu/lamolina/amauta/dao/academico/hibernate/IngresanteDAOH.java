package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.IngresanteDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.inscripcion.Ingresante;

@Repository
public class IngresanteDAOH extends AbstractEasyDAO<Ingresante> implements IngresanteDAO {

    public IngresanteDAOH() {
        super();
        setClazz(Ingresante.class);
    }

    @Override
    public List<Ingresante> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Ingresante.class, "i")
                .join("postulante po", " po.persona", "po.modalidadIngreso", "po.cicloPostula cp", "po.modalidadIngreso", "cp.cicloAcademico ca", "carrera")
                .left("prelamolina pm", "evaluado e")
                .filter("ca.id", cicloAcademico)
                .filter("po.estado", PostulanteEstadoEnum.ING)
                .notLike("i.codigo", "Q%");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Ingresante> allByCicloAcademicoModalidadIngreso(List<CicloAcademico> ciclosQuintosAnteriores, String codigoIngreso) {
        Octavia sql = Octavia.query()
                .from(Ingresante.class, "i")
                .join("postulante po", " po.persona", "po.modalidadIngreso", "po.cicloPostula cp", "po.modalidadIngreso mi", "cp.cicloAcademico ca", "carrera")
                .left("prelamolina pm", "evaluado e")
                .in("ca.id", ciclosQuintosAnteriores)
                .filter("po.estado", PostulanteEstadoEnum.ING)
                .filter("mi.codigo", codigoIngreso);
        return sql.all(getCurrentSession());
    }

}
