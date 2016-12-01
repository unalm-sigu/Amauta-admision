package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

@Repository
public class CicloAcademicoDAOH extends AbstractDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }

    @Override
    public CicloAcademico findActivo() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca");
        sqlUtil.filter("ca.estado", EstadoEnum.ACT.name());
        return this.find(sqlUtil);
    }
}
