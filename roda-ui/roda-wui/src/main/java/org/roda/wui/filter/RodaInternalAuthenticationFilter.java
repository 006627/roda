/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.roda.core.common.UserUtility;
import org.roda.wui.client.common.utils.StringUtils;
import org.roda.wui.client.welcome.Welcome;
import org.roda.wui.common.client.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hélder Silva <hsilva@keep.pt>
 */
public class RodaInternalAuthenticationFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(RodaInternalAuthenticationFilter.class);

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    LOGGER.info("{} initialized ok", getClass().getSimpleName());
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;

    String url = httpRequest.getRequestURL().toString();
    String requestURI = httpRequest.getRequestURI();
    String service = httpRequest.getParameter("service");
    String hash = httpRequest.getParameter("hash");
    String locale = httpRequest.getParameter("locale");

    LOGGER.debug("URL: {} ; Request URI: {} ; Service: {} ; Hash: {}, Locale: {}", url, requestURI, service, hash,
      locale);

    if (requestURI.equals("/login")) {
      StringBuilder b = new StringBuilder();
      b.append("/");

      if (StringUtils.isNotBlank(locale)) {
        b.append("?locale=").append(locale);
      }

      b.append("#login");

      if (StringUtils.isNotBlank(hash)) {
        b.append(Tools.HISTORY_SEP).append(hash);
      }

      httpResponse.sendRedirect(b.toString());
    } else if (requestURI.equals("/logout")) {
      UserUtility.logout(httpRequest);

      StringBuilder b = new StringBuilder();
      b.append("/");

      if (StringUtils.isNotBlank(locale)) {
        b.append("?locale=").append(locale);
      }

      b.append("#").append(Welcome.RESOLVER.getHistoryToken());

      httpResponse.sendRedirect(b.toString());

    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
    // do nothing
  }

}
