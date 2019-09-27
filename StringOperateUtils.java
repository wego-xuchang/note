package com.gitboy.maven.utils;

/**   
*    
* 项目名称：driverschool-admins   
* 类名称：StringOperateUtils   
* 类描述：   String的操作
* 创建人：No   
* 创建时间：2019年4月4日 上午9:04:28   
* 作者：No   
* @version    
*    
*/
public class StringOperateUtils {

	/**
	 *描述：获取String的/后面的字符串
	 *@param oldImageHost
	 *@return newImageName
	 */
	public static String getNewNameAndSux(String oldImageHost) {
		
		String newImageName=oldImageHost.substring(oldImageHost.lastIndexOf("/")+1);
		
		return newImageName;
		
	}
	
	/**
	 *描述：获取String的/后面与.之前的的字符串
	 *@param oldImageHost
	 *@return newImageName
	 */
	public static String getNewName(String oldImageHost) {
			
			String newImageName=oldImageHost.substring(oldImageHost.lastIndexOf("/")+1,oldImageHost.indexOf("."));
			
			return newImageName;
			
		}
}
