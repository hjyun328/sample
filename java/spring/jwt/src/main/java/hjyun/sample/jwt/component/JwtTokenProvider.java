package hjyun.sample.jwt.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.token.secret-key}")
  private String secretKey;

  @Value("${jwt.token.expiration-ms}")
  private int expirationTimeMs;

  @Value("${jwt.token.refresh-ms}")
  private int refreshMs;

  @Value("${jwt.token.prefix}")
  private String prefix;

  @Value("${jwt.token.header}")
  private String header;

  @Value ("${jwt.token.roles}")
  private String roles;

  public void applyHeader(HttpServletResponse response, UserDetails userDetails) {
    response.addHeader(header, prefix + " " + create(userDetails));
  }

  public boolean canRefresh(Claims claims) {
    return (claims.getExpiration().getTime() - System.currentTimeMillis()) < refreshMs;
  }

  public String create(UserDetails userDetails) {
    final Claims claims = Jwts.claims().setSubject(userDetails.getUsername());

    claims.put(roles,
        userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

    final Date now = new Date();

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expirationTimeMs))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  @SuppressWarnings("unchecked")
  public UserDetails getUserDetails(Claims claims) {
    return new User(claims.getSubject(),
      StringUtils.EMPTY,
      ((Collection<String>) claims.get(roles))
          .stream()
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList())
    );
  }

  public String resolve(HttpServletRequest request) {
    final String bearerToken = request.getHeader(header);

    if (bearerToken != null && bearerToken.startsWith(prefix)) {
      return bearerToken.substring(prefix.length());
    }

    return null;
  }

  public boolean validate(String token) {
    try {
      if (StringUtils.isNotEmpty(token)) {
        Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token);
        return true;
      }
    } catch (Exception e) {
      log.debug("Expired or invalid JWT token", e);
    }

    return false;
  }

}
