package pe.edu.lamolina.amauta.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZoomConfig {

    @Bean
    public String generarJWT() throws UnsupportedEncodingException {
        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String API_KEY = "kQ0IFZZzS6u364vKWZhJbw";
        String API_SECRET = "OubaCz1olKfYxv1JMNHnVTj8hacOc2QfYo9c";
        JwtBuilder builder;
        String token;
        final long UN_MINUTO = 60000;
        long mSeg = System.currentTimeMillis();
        Date now = new Date(mSeg);
        Date caduca = new Date(mSeg + (90 * UN_MINUTO));

            builder = Jwts.builder()
                    .setHeaderParam(JwsHeader.JWT_TYPE, "JWT")
                    .setHeaderParam(JwsHeader.ALGORITHM, "HS256")
                    .setIssuedAt(now)
                    .setExpiration(caduca)
                    .setIssuer(API_KEY)
                    .signWith(signatureAlgorithm, API_SECRET.getBytes("UTF-8"));
            token = builder.compact();

        return token;
    }

    public String validaAula(String aula) {
        String au = "";
        if (!Objects.equals(aula, "") && Objects.nonNull(aula)) {
            if (aula.toLowerCase().matches("(.*)-(.*)")) {
                au = StringUtils.substringBefore(aula, "-").concat(StringUtils.substringAfter(aula, "-"));
            } else {
                au = aula;
            }
        }
        /*String au = aula.toLowerCase().matches("(.*)-(.*)") ? StringUtils.substringBefore(aula, "-").concat(StringUtils.substringAfter(aula, "-")) 
                : aula;*/
        return au;
    }

    public String crearReunionZoom(String agenda, String topic, String tipoReunion, String startTime, Integer duration) 
            throws JsonProcessingException 
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        Integer type;
        objectNode.put("agenda", agenda);
        if (Objects.nonNull(tipoReunion) && tipoReunion.equals("P")) {
            type = 2;
            objectNode.put("duration", duration)
                    .put("start_time", startTime);
        } else {
            type = 3;
        }
        objectNode.put("status", "waiting")
                .put("timezone", "America/Lima")
                .put("topic", topic)
                .put("type", type)
                .putObject("settings")
                .put("allow_multiple_devices", false)
                .put("alternative_hosts", "")
                .put("alternative_hosts_email_notification", true)
                .put("approval_type", 2)
                .putObject("approved_or_denied_countries_or_regions")
                .put("enable", false);
        objectNode.with("settings")
                .put("audio", "both")
                .put("authentication_domains", "")
                .put("authentication_name", "Iniciar sesión en Zoom para poder ingresar a la reunión.")
                .put("authentication_option", "signIn_3K4dZCR7QrWbquqPO1bKBw")
                .put("auto_recording", "cloud")
                .putObject("breakout_room")
                .put("enable", false);
        objectNode.with("settings")
                .put("close_registration", false)
                .put("cn_meeting", false)
                .put("device_testing", false)
                .put("encryption_type", "enhanced_encryption")
                .put("enforce_login", true)
                .put("enforce_login_domains", "")
                .put("host_video", false)
                .put("in_meeting", false)
                .put("jbh_time", 0)
                .put("join_before_host", false)
                .put("meeting_authentication", true)
                .put("mute_upon_entry", true)
                .put("participant_video", false)
                .put("registrants_confirmation_email", true)
                .put("registrants_email_notification", true)
                .put("request_permission_to_unmute_participants", false)
                .put("show_share_button", false)
                .put("use_pmi", false)
                .put("waiting_room", true)
                .put("watermark", false);
        return objectMapper.writeValueAsString(objectNode);
    }
    
}
