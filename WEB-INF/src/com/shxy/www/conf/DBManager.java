package com.shxy.www.conf;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.shxy.www.util.ResultSetUtil;
import com.shxy.www.util.StringUtil;

/**
 * 获取数据库连接（JDBC）
 * 
 * @author 藕旺江
 * 
 */
public class DBManager {
	/**
	 * 数据库连接
	 */
	protected Connection conn = null;

	protected Logger log4j = Logger.getLogger(this.getClass());

	/**
	 * 获取JDBC连接-sqlite
	 * 
	 * @return Connection JDBC连接
	 */
	public DBManager() {
		try {
			String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:"+path.split("WEB-INF")[0].toLowerCase()+"db/serverinfor.sqlite");
		} catch (Exception e) {
			e.printStackTrace();
			log4j.error(e);
		}
	}

	/**
	 * 获得Connection对象
	 * @return Connection
	 */
	public Connection getConnection(){
		return conn;
	}
	
	/**
	 * 开启事务
	 * @throws SQLException
	 */
	public void beginTransaction() throws SQLException{
		if(conn!=null){
			conn.setAutoCommit(false);
		}
	}
	
	/**
	 * 提交事务
	 * @throws SQLException
	 */
	public void commitTransaction() throws SQLException{
		if(conn!=null){
			conn.commit();
		}
	}
	
	/**
	 * 回滚事务
	 *
	 */
	public void rollback(){
		if(conn!=null){
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 关闭连接
	 *
	 */
	public void close(){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 返回List<Object>的List集合
	 * @param sql SQL脚本
	 * @return
	 * @throws Exception
	 */
	public List<List> executeQueryList(String sql) throws Exception{
		System.out.println(sql);
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<List> list = new ResultSetUtil().getTableSQLList(rs);
        rs.close();
        ps.close();
        return list;
	}
	
	/**
	 * 返回List<Object>的List集合
	 * @param sql SQL脚本
	 * @return
	 * @throws Exception
	 */
	public List<Object> executeQuerySingleList(String sql) throws Exception{
		List<Object> result = new ArrayList<Object>();
		if(StringUtil.arrIsNULL(sql)){
			return result;
		}
		List<List> list = this.executeQueryList(sql);
		for(int i=0; i<list.size(); i++){
			result.add(list.get(i).get(0));
		}
        return result;
	}
	
	/**
	 * 执行更新操作
	 * @param sql SQL脚本
	 * @return 返回更新的记录数
	 * @throws Exception
	 */
	public int executeUpdateSQL(String sql) throws Exception{
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		int updateNum  = ps.executeUpdate();
		ps.close();
		return updateNum;
	}
	
	/**
	 * 执行插入操作并得到插入记录的ID
	 * @param sql SQL脚本
	 * @return 记录插入成功 返回插入记录的ID 记录插入失败 返回-1
	 * @throws Exception
	 */
	public synchronized String executeInsertSQL(String sql) throws Exception{
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		if(!ps.execute()){
			ResultSet rs = ps.getGeneratedKeys();
			Serializable ret = null;
	        if (rs.next()) {
	        	ret = (Serializable) rs.getObject(1);
	        }
	        rs.close();
	        ps.close();
	        return ret.toString();
		}
		ps.close();
		return "-1";
	}
	
	/**
	 * 执行删除sql语句
	 * @param sql
	 * @return
	 * @throws Exception
	 * @author zx
	 */
	public String  executeDeleteSQL(String sql)  throws Exception{
		System.out.println(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.execute();
		ps.close();
		conn.close();
		return "-1";
	}
	
	/**
	 * 执行SQL语句，返回结果的Map<String, Object>的List集合
	 * @param sql SQL脚本
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> executeQueryMap(String sql) throws Exception{
		return new ArrayList<Map<String,Object>>();
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
	public Map<String, Object> executePrepareCall(Map<String, Object> callParams) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		result.put("result", "0");
		return result;
	}
	
}