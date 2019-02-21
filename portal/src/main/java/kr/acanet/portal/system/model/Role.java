package kr.acanet.portal.system.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * <pre>
 * description : 시스템 내부에서 사용할 권한 클래스(학사시스템의 기본권한과는 다른 역할을한다.)
 * </pre>
 *
 * @author 	: wdkang
 * @date 	: 2018. 1. 15.
 * @version : 1.0
 */
@SuppressWarnings("serial")
public class Role implements Serializable {
	
	private String roleId;
	private String roleName;
	private String roleDesc;
	private int systemMasterRole;
	private Date createDate;
	private Date updateDate;
	private Set<Permission> permissions;

	public String getRoleId() {
		return roleId;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public String getRoleDesc() {
		return roleDesc;
	}
	
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	
	public int getSystemMasterRole() {
		return systemMasterRole;
	}

	public void setSystemMasterRole(int systemMasterRole) {
		this.systemMasterRole = systemMasterRole;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Set<Permission> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

}	
