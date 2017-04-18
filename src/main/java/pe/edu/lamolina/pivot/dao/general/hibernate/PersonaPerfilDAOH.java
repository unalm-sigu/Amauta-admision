package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class PersonaPerfilDAOH extends AbstractDAO<PersonaPerfil> implements PersonaPerfilDAO {

    public PersonaPerfilDAOH() {
        super();
        setClazz(PersonaPerfil.class);
    }

    @Override
    public PersonaPerfil find(long id) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pp")
                .parents("left oficina ofi", "perfilCompania peco", "persona p")
                .filter("pp.id", id);

        return this.find(sqlUtil);

    }

    @Override
    public List<PersonaPerfil> allByFiltersDynaTable(DynatableFilter filter) {

        List<String> fieldsFiltro = Arrays.asList("pco.nombre");
        filter.complexField("concat(p.paterno,' ',p.materno,' ',p.nombres)");
        filter.complexField("concat(p.nombres,' ',p.paterno,' ',p.materno)");
        filter.setFields(fieldsFiltro);

        filter.setAlias("pp");
        filter.setParents("left oficina ofi", "left compania com", "perfilCompania pco", "persona p");

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }
        Map filtersInFix = filter.getFiltersInFixed();
        for (Object key : filtersInFix.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filtersInFix.get(key));
        }

        Map queries = filter.getQueries();
        if (queries != null) {
            for (Object key : queries.keySet()) {
                if (!((String) key).equals("search")) {
                    this.filterFixed(sqlUtil, (String) key, queries.get(key));
                }
            }
        }

        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue(), filter.getComplexFields());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        List<PersonaPerfil> lstPersonaPerfils = this.all(sqlUtil);

        return lstPersonaPerfils;
    }

    @Override
    public List<PersonaPerfil> allByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pp")
                .parents("persona per")
                .filter("per.id", persona);

        return all(sqlUtil);
    }

}
