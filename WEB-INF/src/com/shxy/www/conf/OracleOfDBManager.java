package com.shxy.www.conf;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import net.sf.json.JSONObject;

import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.ResultSetUtil;

/**
 * 获取数据库连接（JDBC）
 * 
 * @author 藕旺江
 * 
 */
public class OracleOfDBManager extends DBManager{

	/**
	 * 获取JDBC连接-sqlite
	 * 
	 * @return Connection JDBC连接
	 */
	public OracleOfDBManager(String ip, String type) {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		try {
			String dbUserName = (String) session.getAttribute("dbUserName");//获得用户名 20140514修改
			String dbPassword = (String) session.getAttribute("dbPassword");//获得数据库密码 20140514修改
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//this.conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:XE", type.equals("1")?"ZGDBXY":"DBXY", "shanghaixinyuan");
			
			/********************20140514修改*******************************/
			this.conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:XE", dbUserName, dbPassword);
		} catch (Exception e) {
			log4j.error(e);
		}
	}

	/**
	 * 执行插入操作并得到插入记录的ID
	 * @param sql SQL脚本
	 * @return 记录插入成功 返回插入记录的ID 记录插入失败 返回-1
	 * @throws Exception
	 */
	public synchronized String executeInsertSQL(String sql) throws Exception{
		System.out.println(sql);
		DatabaseMetaData dmd= conn.getMetaData();
		PreparedStatement ps= conn.prepareStatement(sql,new String[]{"ID"});// 后面一个参数表示需要返回的列 
		if(!ps.execute()){
			if(dmd.supportsGetGeneratedKeys()) {
				ResultSet rs= ps.getGeneratedKeys();
				while(rs.next()) {
					Object priKey = rs.getObject(1); 
					return ObjectUtil.arrIsNULL(priKey)?"-1":ObjectUtil.obj2Str(priKey);
				}
			}
		}
		return "-1";
	}
	
	/**
	 * 执行SQL语句，返回结果的Map<String, Object>的List集合
	 * @param sql SQL脚本
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> executeQueryMap(String sql) throws Exception{
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = ResultSetUtil.readResultSetMap(rs);
		rs.close();
        ps.close();
		return list;
	}
	
	/**
	 * 执行存储过程
	 * @param callParams
	 * 		CALLNAME = 存储过程名
	 * 		IN 		 = 输入参数
	 * 		OUT 	 = 输出参数
	 * @return
	 * 		result = 执行结果
	 * 		OUT	   = 输出参数
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executePrepareCall(Map<String, Object> callParams) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		result.put("result", "0");
		if(MapUtil.checkMapParamsNotNULL(callParams, "CALLNAME")&&ObjectUtil.objIsNull(this.conn)){//判断函数名和数据库连接是否为空 如果为空，直接返回
			return result;
		}
		Object callName = MapUtil.get(callParams, "CALLNAME");//获得函数名
		System.out.println(JSONObject.fromObject(callParams).toString());
		CallableStatement proc = this.conn.prepareCall(callName.toString());
		List<Object> inParams = new ArrayList<Object>();
		if(MapUtil.containsKey(callParams, "IN")){//判断是否存在输入参数
			inParams = (List<Object>) MapUtil.get(callParams, "IN");//获得输入参数
		}
		//绑定输入参数
		for(int i=0; i<inParams.size(); i++){
			proc.setObject(i+1, inParams.get(i));
		}
		List<Integer> outParams = new ArrayList<Integer>();
		if(MapUtil.containsKey(callParams, "OUT")){//判断是否存在输出参数
			outParams = (List<Integer>) MapUtil.get(callParams, "OUT");
		}
		for(int i=0; i<outParams.size(); i++){
			proc.registerOutParameter(inParams.size()+i+1, outParams.get(i));
		}
		proc.execute();
		result.put("result", "1");
		if(!ListUtil.listIsNull(outParams)){
			List<Object> execResult = new ArrayList<Object>();
			for(int i=1; i<=outParams.size(); i++){
				Object obj = proc.getObject(inParams.size()+i);
				execResult.add(obj);
			}
			result.put("OUT", execResult);
		}
		return result;
	}
}