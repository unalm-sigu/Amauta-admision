package pe.edu.lamolina.pivot.controller.restcontroller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.zelper.bean.FormImport;

@Service
@Transactional(readOnly = true)
public class RestPivotServiceImp implements RestPivotService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    TokenIngresanteDAO tokenDAO;

    @Override
    @Transactional
    public Boolean validateToken(FormImport formImport) {

        Usuario usuario = usuarioDAO.find(formImport.getIdUsuario());

        Date hoy = new Date();
        System.out.println("hoy ::: " + hoy.toString());
        List<TokenIngresante> tkx = new ArrayList();
        List<TokenIngresante> tokenx = tokenDAO.allActivos(usuario.getPersona(), usuario);
        System.out.println("LLEGARON " + tokenx.size() + " TOKENS");
        for (TokenIngresante token : tokenx) {
            System.out.println("token ::: " + token.getFechaVencimiento().toString());
            if (token.getFechaVencimiento().after(hoy)) {
                System.out.println("SI VA");
                tkx.add(token);
            }
        }

        if (tkx.isEmpty()) {
            throw new PhobosException("Token no valido");
        }
        tkx.get(0).setEstado(TokenEstadoEnum.USO);
        tkx.get(0).setFechaUso(new Date());
        tokenDAO.update(tkx.get(0));

        return true;
    }
}
