package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoMensajeIntranet;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.amauta.dao.mensajeria.AlumnoMensajeIntranetDAO;

@Repository
public class AlumnoMensajeIntranetDAOH extends AbstractEasyDAO<AlumnoMensajeIntranet> implements AlumnoMensajeIntranetDAO {

    public AlumnoMensajeIntranetDAOH() {
        super();
        setClazz(AlumnoMensajeIntranet.class);
    }

    @Override
    public void createMessage(MensajeIntranet mensajeria, List<Alumno> alumnos) {
        if (alumnos.isEmpty()) {
            return;
        }
        List<Long> ids = alumnos.stream().map(x -> x.getId()).collect(Collectors.toList());

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(AlumnoMensajeIntranet.class.getSimpleName());
        sql.append("  (alumno,mensajeIntranet) ");
        sql.append(" select alu, msg ");
        sql.append("   from ").append(Alumno.class.getSimpleName()).append(" alu, ");
        sql.append("        ").append(MensajeIntranet.class.getSimpleName()).append(" msg ");
        sql.append("  where alu.id in (:ALUMNOS) ");
        sql.append("    and msg.id = :MENSAJE ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("ALUMNOS", ids);
        query.setParameter("MENSAJE", mensajeria.getId());

        query.executeUpdate();

    }
}
