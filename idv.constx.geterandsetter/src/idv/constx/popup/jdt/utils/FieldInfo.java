
package idv.constx.popup.jdt.utils;

import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

public class FieldInfo {
	
   /** 字段名*/
	private String name;
	/** 字段类别*/
	private String type;
	/** 字段注释*/
	private String commets;
	/** 字段修饰符*/
	private List<String> modifiers;
	/** 原始字段信息*/
	private IField field;
	
	


	/** 
	* 设置 字段名
	* @param String
	*/ 
	public void setName(String value) {
		this.name = value; 
	} 

	/** 
	 * 获取 字段名
	 * @return String 
	 */ 
	public String getName() {
		return  this.name; 
	} 

	/** 
	* 设置 字段类别
	* @param String
	*/ 
	public void setType(String value) {
		this.type = value; 
	} 

	/** 
	 * 获取 字段类别
	 * @return String 
	 */ 
	public String getType() {
		return  this.type; 
	} 

	/** 
	* 设置 字段注释
	* @param String
	*/ 
	public void setCommets(String value) {
		this.commets = value; 
	} 

	/** 
	 * 获取 字段注释
	 * @return String 
	 */ 
	public String getCommets() {
		return  this.commets; 
	} 

	/** 
	* 设置 字段修饰符
	* @param List<String>
	*/ 
	public void setModifiers(List<String> value) {
		this.modifiers = value; 
	} 

	/** 
	 * 获取 字段修饰符
	 * @return List<String> 
	 */ 
	public List<String> getModifiers() {
		return  this.modifiers; 
	} 

	/** 
	* 设置 原始字段信息
	* @param IField
	*/ 
	public void setField(IField value) {
		this.field = value; 
	} 

	/** 
	 * 获取 原始字段信息
	 * @return IField 
	 */ 
	public IField getField() {
		return  this.field; 
	} 

	
	public Integer getFlags(){
		try {
			return field.getFlags();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
