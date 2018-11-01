package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.pivot.dao.general.ArchivoDAO;

@Repository
public class ArchivoDAOH extends AbstractEasyDAO<Archivo> implements ArchivoDAO {

    public ArchivoDAOH() {
        super();
        setClazz(Archivo.class);
    }

    @Override
    public List<Archivo> allByInstanciaTipoInstancia(Long idInstancia, InstanciaEnum instanciaEnum) {

        Octavia sql = Octavia.query()
                .from(Archivo.class, "ar")
                .filter("ar.idInstancia", idInstancia)
                .filter("ar.instancia", instanciaEnum.name());
        return all(sql);
        
    }
}
