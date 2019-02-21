package kr.acanet.portal.system.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * description : Role 클래스에서 사용할 Permission. 현재는 권한으로만 체크함.
 * 차후 권한 외에 다른 기능까지 체크해야 할 경우 사용.
 * </pre>
 *
 * @author 	: wdkang
 * @date 	: 2018. 1. 15.
 * @version : 1.0
 */
@SuppressWarnings("serial")
public class Permission implements Serializable {
	
	private String permissionId;
	private String permissionName;
	private String permissionDesc;
	private String permissionUrl;
	private Date createDate;
	private Date updateDate;
	
	public String getPermissionId() {
		return permissionId;
	}
	
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	
	public String getPermissionName() {
		return permissionName;
	}
	
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	
	public String getPermissionDesc() {
		return permissionDesc;
	}

	public void setPermissionDesc(String permissionDesc) {
		this.permissionDesc = permissionDesc;
	}
	
	public String getPermissionUrl() {
		return permissionUrl;
	}

	public void setPermissionUrl(String permissionUrl) {
		this.permissionUrl = permissionUrl;
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
	
}
