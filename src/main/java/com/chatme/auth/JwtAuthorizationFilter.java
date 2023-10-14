package com.chatme.auth;

import com.chatme.utils.ObjectChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter
{

	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	ObjectMapper mapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException
	{
		Map<String, Object> errorDetails = new HashMap<>();
		try
		{
			String token = jwtUtil.resolveToken(request);
			if (ObjectChecker.isEmptyOrNull(token))
			{
				filterChain.doFilter(request, response);
				return;
			}
			Claims claims = jwtUtil.resolveClaims(request);
			if (ObjectChecker.isNotEmptyOrNull(claims) && jwtUtil.validateClaims(claims))
			{
				String username = claims.getSubject();
				Authentication authentication = new UsernamePasswordAuthenticationToken(username, "", new ArrayList<>());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		catch (Exception e)
		{
			errorDetails.put("message", "Authentication Error");
			errorDetails.put("details",e.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
		}
		filterChain.doFilter(request, response);
	}
}
