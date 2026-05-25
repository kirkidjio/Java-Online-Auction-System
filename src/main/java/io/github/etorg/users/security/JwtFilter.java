package io.github.etorg.users.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver handlerException;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			final String jwt = authHeader.substring(7);
			final UUID userId = jwtService.getUserId(jwt);
			
			if (userId != null) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, List.of());
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
			filterChain.doFilter(request, response);
			
		}
		catch (Exception exception) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
        }
		
		
	}
}
