package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.model.academico.Docente;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class DocenteDAOH extends AbstractDAO<Docente> implements DocenteDAO {

    public DocenteDAOH() {
        super();
        setClazz(Docente.class);
    }

    @Override
    public Docente find(Long idDocente) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per")
                .filter("doc.id", idDocente);
        return find(sqlUtil);
    }

    @Override
    public Docente findPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("per.id", persona);
        return find(sqlUtil);
    }

    @Override
    public Docente findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("left persona per", "left modalidadEstudio", "left departamentoAcademico")
                .filter("doc.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<Docente> allByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("doc")
                .parents("persona per")
                .filter("per.id", persona);
        return all(sqlUtil);
    }
}
