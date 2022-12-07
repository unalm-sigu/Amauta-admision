package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.math.BigInteger;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Repository
public class EncuestaEstudiantilDAOH extends AbstractEasyDAO<EncuestaEstudiantil> implements EncuestaEstudiantilDAO {

    public EncuestaEstudiantilDAOH() {
        super();
        setClazz(EncuestaEstudiantil.class);
    }

    @Override
    public EncuestaEstudiantil find(long id) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen")
                .filter("ep.id", id);
        return find(sql);
    }

    @Override
    public EncuestaEstudiantil findByCicloEncuesta(CicloAcademico ciclo, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen")
                .filter("en.id", encuesta)
                .filter("ci.id", ciclo);
        return find(sql);
    }

    @Override
    public EncuestaEstudiantil findByCicloTipo(CicloAcademico ciclo, TipoExamenVirtualEnum tipoEnum) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen tipo")
                .filter("en.estado", EncuestaEstadoEnum.ACT)
                .filter("tipo.codigo", tipoEnum)
                .filter("ci.id", ciclo);
        return find(sql);
    }

    @Override
    public List<EncuestaEstudiantil> allByEncuestas(List<ExamenVirtual> encuestas) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci")
                .in("en.id", encuestas);
        return all(sql);
    }

    @Override
    public BigInteger countEncuestaAlumno(CicloAcademico cicloAcademico, ModalidadEstudioEnum modalidad, EncuestaEstudiantilEstadoEnum estado) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  COUNT( DISTINCT aa.id ) AS c FROM exam_encuesta_alumno eea ");
        sb.append(" JOIN exam_encuesta_docente eed on eed.id=eea.id_encuesta_docente  ");
        sb.append(" JOIN exam_encuesta_estudiantil eee on eee.id=eed.id_encuesta_estudiantil  ");
        sb.append(" JOIN aca_ciclo_academico aca on aca.id=eee.id_ciclo_academico  ");
        sb.append(" JOIN aca_alumno aa on aa.id=eea.id_alumno ");
        sb.append(" JOIN aca_modalidad_estudio ame on ame.id= aa.id_modalidad_estudio ");
        sb.append(" WHERE aca.id = :CICLO_ACADEMICO");
        sb.append(" AND ame.codigo = :MODALIDAD ");
        sb.append(" AND eea.estado <> 'ANU' ");
        if (estado != null) {
            sb.append(" and eea.estado = :ESTADO_ENCUESTA ");
        }

        Query query = getCurrentSession().createSQLQuery(sb.toString());
        
        query.setParameter("CICLO_ACADEMICO", cicloAcademico.getId());
        query.setParameter("MODALIDAD", modalidad.name());
        if (estado != null) {
            query.setParameter("ESTADO_ENCUESTA", estado.name());
        }
        return (BigInteger) query.uniqueResult();
    }

    @Override
    public EncuestaEstudiantil findByCicloEncuestaTipoExamen(CicloAcademico ciclo, TipoExamenVirtualEnum tipoEnum) {
        Octavia sql = Octavia.query()
                .from(EncuestaEstudiantil.class, "ep")
                .join("encuesta en", "cicloAcademico ci", "en.tipoExamen tipo")
                .filter("tipo.codigo", tipoEnum)
                .filter("ci.id", ciclo);
        return find(sql);
    }

}
