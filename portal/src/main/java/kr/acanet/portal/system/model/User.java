package kr.acanet.portal.system.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.acanet.portal.system.security.PortalAuthority;

/**
 * <pre>
 * description : 스프링시큐리티에서 제공하는 UserDetails 인터페이스를 구현한 클래스
 * 앱 내에서 사용할 사용자정보를 저장하며 인증토큰(AuthenticationToken)에 등록하여 사용한다.
 * </pre>
 *
 * @author 	: wdkang
 * @date 	: 2018. 1. 11.
 * @version : 1.0
 */
@SuppressWarnings("serial")
public class User implements UserDetails{
	
	String username;
	String password;
	
	Role role;
	
	String isSystem;
	
	int passwordExpired;
	Date passwordExpiredDate;
	
	int idExpired;
	Date idExpiredDate;
	
	Date lastLoginDate;
	
	int enabled;
	
	Date insertDate;
	String insertId;
	
	Date updateDate;
	String updateId;
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		if(this.role != null) {
            	
			PortalAuthority userRole = new PortalAuthority(role);
            authorities.add(userRole);
        }
		
		return authorities;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}

	public String getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(String isSystem) {
		this.isSystem = isSystem;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.idExpired == 0 ? true : false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.idExpired == 0 ? true : false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.passwordExpired == 0 ? true : false;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled == 1 ? true : false;
	}

	public int getPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(int passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public Date getPasswordExpiredDate() {
		return passwordExpiredDate;
	}

	public void setPasswordExpiredDate(Date passwordExpiredDate) {
		this.passwordExpiredDate = passwordExpiredDate;
	}

	public int getIdExpired() {
		return idExpired;
	}

	public void setIdExpired(int idExpired) {
		this.idExpired = idExpired;
	}

	public Date getIdExpiredDate() {
		return idExpiredDate;
	}

	public void setIdExpiredDate(Date idExpiredDate) {
		this.idExpiredDate = idExpiredDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public Role getRole() {
		return role;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getInsertId() {
		return insertId;
	}

	public void setInsertId(String insertId) {
		this.insertId = insertId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}
	
	public boolean isAdministrator() {
		boolean isAdministrator  = false;
		
		if(role != null) {
			isAdministrator = (role.getSystemMasterRole() == 1);
		}
		
		return isAdministrator;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", role=");
		builder.append(role);
		builder.append(", passwordExpired=");
		builder.append(passwordExpired);
		builder.append(", passwordExpiredDate=");
		builder.append(passwordExpiredDate);
		builder.append(", idExpired=");
		builder.append(idExpired);
		builder.append(", idExpiredDate=");
		builder.append(idExpiredDate);
		builder.append(", lastLoginDate=");
		builder.append(lastLoginDate);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", insertDate=");
		builder.append(insertDate);
		builder.append(", insertId=");
		builder.append(insertId);
		builder.append(", updateDate=");
		builder.append(updateDate);
		builder.append(", updateId=");
		builder.append(updateId);
		builder.append("]");
		return builder.toString();
	}
	
	
}
