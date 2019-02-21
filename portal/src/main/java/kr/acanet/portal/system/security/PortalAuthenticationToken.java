package kr.acanet.portal.system.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import kr.acanet.portal.system.model.User;

public class PortalAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final Object principal;
	private Object credentials;

	public PortalAuthenticationToken(Object principal, Object credentials) {
		
		super(null);
		super.setAuthenticated(false);
		
		this.principal = principal;
		this.credentials = credentials;
	}

	public PortalAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		
		super(authorities);
		super.setAuthenticated(true); 
		
		this.principal = principal;
		this.credentials = credentials;
	}

	public PortalAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities, User user) {
		
		super(authorities);
		super.setDetails(user);
		super.setAuthenticated(true); 
		
		this.principal = principal;
		this.credentials = credentials;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		credentials = null;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

}
