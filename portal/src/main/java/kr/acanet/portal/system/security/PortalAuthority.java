package kr.acanet.portal.system.security;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import kr.acanet.portal.system.model.Permission;
import kr.acanet.portal.system.model.Role;

public class PortalAuthority implements GrantedAuthority{

private static final long serialVersionUID = 1L;
	
	//해당 권한객체에서 사용할 권한
	private final Role role;
	
	public PortalAuthority(Role role) {
		Assert.hasText(role.getRoleId(), "A granted authority textual representation is required");
		this.role = role;
	}
	
	
	@Override
	public String getAuthority() {
		//Role의 이름이 아닌 기본키(role_id)로 구분
		return this.role.getRoleId();
	}
	
	public Set<Permission> getPermission() {
		return this.role.getPermissions();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof PortalAuthority) {
			return this.role.getRoleId().equals(((PortalAuthority) obj).role.getRoleId());
		}

		return false;
	}

	public int hashCode() {
		return this.role.hashCode();
	}

	public String toString() {
		return this.role.getRoleName()+"(" +this.role.getRoleId()+")";
	}

}
