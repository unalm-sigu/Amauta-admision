package pe.edu.lamolina.amauta.controller.restcontroller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.seguridad.TokenIngresanteDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.zelper.bean.FormImport;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Service
@Transactional(readOnly = true)
public class RestPivotServiceImp implements RestPivotService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;

    @Override
    public Boolean verificarToken(FormImport formImport) {
        Usuario usuario = usuarioDAO.find(formImport.getIdUsuario());

        Date hoy = new Date();
        List<TokenIngresante> tokensValidos = new ArrayList();
        List<TokenIngresante> tokenx = tokenIngresanteDAO.allActivos(usuario.getPersona(), usuario);
        for (TokenIngresante token : tokenx) {
            if (token.getFechaVencimiento().after(hoy) && formImport.getToken().equals(token.getValor())) {
                tokensValidos.add(token);
            }
        }
        Assert.isFalse(tokensValidos.isEmpty(), "Token no valido");

        return true;
    }

    @Override
    @Transactional
    public Boolean consumirToken(FormImport formImport) {
        logger.debug("consumir.token={}", formImport.getToken());
        logger.debug("HoraActual={}", new DateTime());
        ObjectUtil.printAttr(formImport);
        

        TokenIngresante token = tokenIngresanteDAO.findByToken(formImport.getToken());
        System.out.println("token=" + token);
        Assert.isNotNull(token, "Token inválido");

        token.setEstadoEnum(TokenEstadoEnum.USO);
        token.setFechaUso(new Date());
        tokenIngresanteDAO.update(token);

        return true;
    }

    @Override
    public Usuario getUsuario(Usuario usuario) {
        return usuarioDAO.find(usuario);
    }

    @Transactional
    public TokenIngresante findToken(String token, Long idUsuario) {
        TokenIngresante tokenCachimbo = tokenIngresanteDAO.findByToken(token);
        if (tokenCachimbo == null) {
            throw new PhobosException("Token Inexistente al Autenticar " + token);
        }
        if (tokenCachimbo.getUserRegistro().getId().longValue() != idUsuario) {
            throw new PhobosException("Token Inexistente al Autenticar " + token);
        }
        tokenCachimbo.setEstadoEnum(TokenEstadoEnum.USO);
        tokenCachimbo.setFechaUso(new Date());
        tokenIngresanteDAO.update(tokenCachimbo);
        return tokenCachimbo;
    }

    @Override
    public CicloAcademico findCicloActivoPregrado() {
        return cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
    }

    @Override
    public List<CicloAcademico> allCiclos() {
        return cicloAcademicoDAO.all();
    }
    

}
