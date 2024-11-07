package pe.edu.lamolina.amauta.controller.academico.pronabec;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.academico.TipoBecaPronabecDAO;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class TipoBecaPronabecServiceImp implements TipoBecaPronabecService{

    private final TipoBecaPronabecDAO tipoBecaPronabecDAO;

    @Override
    public List<TipoBeca> allTipoBecaPronabec() {
        return tipoBecaPronabecDAO.allTiposBecas();
    }
}
