package com.shxy.birt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.web.util.HtmlUtils;

import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.shxy.www.model.BirtLoggingModel;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;

public class BirtReadData{

	private DBManager dbManager = null;//Oracle DB
	private String business = "";//业务ID
	private String node = "";//环节ID
	private String reportName = "";//报表名
	private DBManager sqliteManage = null;//Sqlite DB
	private BirtLoggingModel logging = null;//日志类
	private String author = "";//登录用户
	
	private BirtReadData(String business, String node, String reportName) {
		super();
		this.sqliteManage = new DBManager();
		this.logging = new BirtLoggingModel(this.sqliteManage);
		this.business = business;
		this.node = node;
		this.reportName = reportName;
	}
	
	public BirtReadData(String business, String node, String reportName, HttpSession session) {
		this(business, node, reportName);
		try {
			if(!ObjectUtil.objIsNull(session)){
				author = (String)session.getAttribute("ID");//获得用户登录的ID
				this.logging.clearLogging(author);//清空用户日志
				String IP = (String)session.getAttribute("IP");//获得用户操作的数据库IP
				String type = (String)session.getAttribute("type");//获得用户操作的数据库类型
				if(!StringUtil.arrIsNULL(IP, type)){//判断IP和数据库类型是否为空
					this.dbManager = new OracleOfDBManager(IP, type);
					this.dbManager.executeQueryList("SELECT COUNT(1) FROM DUAL");//测试数据库是否开启
				}else{
					this.logging.logging(author, "warn", "IP和数据库类型为空");
				}
			}else{
				this.logging.logging(author, "warn", "session为空");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.dbManager = null;
			this.logging.logging(author, "exception", e.getMessage());
		}
	}

	
	/**
	 * 查询报表数据（不带where查询）
	 * @param tableName 信息集名称
	 * @param IDs 信息集主键ID
	 * @param batchValue 批次号
	 * @return
	 * 		报表数据String[][]
	 */
	public String[][] queryData(String tableName, String IDs, String batchValue){
		return this.queryData(tableName, IDs, batchValue, "");
	}
	
	/**
	 * 查询数据（带where查询）
	 * @param tableName 信息集名称
	 * @param IDs 信息集主键
	 * @param batchValue 批次号
	 * @param whereSQL where条件
	 * @return
	 * 		报表数据String[][]
	 */
	@SuppressWarnings("unchecked")
	public String[][] queryData(String tableName, String IDs, String batchValue, String whereSQL){
		String[][] result = new String[0][0];
		//判断业务表名、业务ID、环节ID、报表名、数据库连接是否为空
		if(ObjectUtil.objIsNull(this.dbManager, tableName, business, node, reportName)){
			this.logging.logging(author, "warn", "报表某个参数为空，直接返回：数据源"+(this.dbManager==null?"":"不")+"为空;\n表名"+(StringUtil.arrIsNULL(tableName)?"":"不")+"为空;\n业务"+(this.business==null?"":"不")+"为空;\n环节"+(this.node==null?"":"不")+"为空;\n报表名"+(this.reportName==null?"":"不")+"为空");
			return result;
		}
		try {
			//查询是否存在配置资源
			String sql = "SELECT T2.ID,T2.TAB_045_005 FROM TAB_4003 T1 RIGHT JOIN TAB_4006 T2 ON T2.TAB_010_ID=T1.ID AND T2.TAB_045_001='param2' AND T2.TAB_045_002='param3' WHERE T1.TAB_010_001='param1'"
				.replaceAll("param1", reportName)
				.replaceAll("param2", this.business)
				.replaceAll("param3", this.node);
			List<List> tab045ID = this.dbManager.executeQueryList(sql);
			this.logging.logging(author, "select", "查询报表是否存在配置资源:"+sql);
			if(ListUtil.listIsNull(tab045ID)){//如果没有配置，则返回空结果集
				this.logging.logging(author, "warn", this.business+"业务, "+this.node+"环节,"+this.reportName+"模板，报表没有配置！");
				return result;
			}
			//报表配置主键ID
			String tab045id = tab045ID.get(0).get(0)+"";
			//查询资源表中配置的字段（ID、字段名、是否转换、是否在转换格式中使用、转换格式）
			sql = "SELECT ID,TAB_046_002,TAB_046_006,TAB_046_007,TAB_046_005 FROM TAB_4007 WHERE TAB_046_001='param1' AND tab_045_id='param2' ORDER BY TAB_046_003"
				.replaceAll("param1", tableName)
				.replaceAll("param2", tab045ID.get(0).get(0)+"");
			List<List> configfields = this.dbManager.executeQueryList(sql);
			this.logging.logging(author, "select", "查询报表是否配置字段:"+sql, tab045id);
			if(ListUtil.listIsNull(configfields)){//如果没有配置的字段
				this.logging.logging(author, "warn", this.business+"业务, "+this.node+"环节,"+this.reportName+"模板，报表没有配置字段！", tab045id);
				return result;
			}
			//字段唯一性Map(字段名+@+是否转换)
			Map<String, String> fieldsUniqueMap = new Hashtable<String, String>();
			//显示字段List
			List<List> showFields = new ArrayList<List>();
			//字段索引（表主键ID、字段属性）
			Map<String, List> fieldsIndexMap = new Hashtable<String, List>();
			for(List s: configfields){
				fieldsUniqueMap.put(s.get(1)+"@"+s.get(2), s.get(3)+"");
				fieldsIndexMap.put(s.get(0)+"", s);
				if(s.get(3).equals("0")){//排除非显示的字段
					showFields.add(s);
				}
			}
			//查询字段
			String fields = "";
			Set<String> keySet = fieldsUniqueMap.keySet();
			List<String> fieldsList = new ArrayList<String>();
			Map<String, Integer> locationMap = new Hashtable<String, Integer>();//字段位置标记Map
			int locationFlag = 0;
			for(String s: keySet){
				fields += s.split("@")[0]+",";
				fieldsList.add(s);
				locationMap.put(s, locationFlag);
				locationFlag++;
			}
			if(StringUtil.arrIsNULL(fields)){
				return result;
			}
			sql = "SELECT ZGA19002, nvl(zga19008,0) FROM ZGA019 WHERE ZGA18001='param1' AND ZGA19002 IN(param2)"
				.replaceAll("param1", tableName)
				.replaceAll("param2", this.charAddQuote(fields));
			List<List> fieldsInTable = this.dbManager.executeQueryList(sql);
			this.logging.logging(author, "select", "查询报表配置字段类型:"+sql, tab045id);
			//字段信息（从ZGA019表查询结果）
			Map<String, String> fieldsInfoMap = new Hashtable<String, String>();
			for(int i=0; i<fieldsInTable.size(); i++){
				fieldsInfoMap.put(fieldsInTable.get(i).get(0)+"", fieldsInTable.get(i).get(1)+"");
			}
			String seachFields = "";
			for(String s:fieldsList){
				String[] ss = s.split("@");
				String showType = fieldsInfoMap.get(ss[0]);
				if(ss[1].equals("1")){//转换
					seachFields += this.getSeachColumn(tableName, ss[0], showType);
				}else{//不转换
					seachFields += ss[0];
				}
				seachFields += ",";
			}
			seachFields = seachFields.substring(0, seachFields.length()-1);
			//查询报表是否存在环节排序条件
			sql = "SELECT tab_050_002, tab_050_003 FROM TAB_4011 WHERE tab_045_id='param1' AND tab_050_001='param2' ORDER BY TAB_050_004"
				.replaceAll("param1", tab045ID.get(0).get(0)+"")
				.replaceAll("param2", tableName);
			this.logging.logging(author, "select", "查询报表排序信息(order by):"+sql, tab045id);
			//查询报表是否存在环节筛选条件
			List<List> orderFields = this.dbManager.executeQueryList(sql);
			sql = "SELECT TAB_051_002 FROM TAB_051 WHERE TAB_045_ID='param1' AND TAB_051_001='param2'"
				.replaceAll("param1", tab045ID.get(0).get(0)+"")
				.replaceAll("param2", tableName);
			this.logging.logging(author, "select", "查询报表筛选信息(where):"+sql, tab045id);
			List<List> filtersTable = this.dbManager.executeQueryList(sql);
			String orderSql = "";//排序语句
			String whereSql = "";//筛选条件
			//判断是否存在环节排序字段，如果存在，拼接排序条件
			if(!ListUtil.listIsNull(orderFields)){
				for(int i=0; i<orderFields.size(); i++){
					orderSql += orderFields.get(i).get(0)+" "+(orderFields.get(i).get(0).equals("1")?"ASC":"DESC")+",";
				}
				orderSql = orderSql.substring(0, orderSql.length()-1);
			}
			//判断是否存在环节筛选条件，如果存在，取出筛选条件信息
			if(!ListUtil.listIsNull(filtersTable)){
				whereSql = filtersTable.get(0).get(0)+"";
			}
			if(!StringUtil.arrIsNULL(whereSQL)){
				if(StringUtil.arrIsNULL(whereSql)){
					whereSql = whereSQL;
				}else{
					whereSql += " AND "+whereSQL;
				}
			}
			String seachSQL = "SELECT ID,"+(batchValue.equals("-1")?"-1":"ZGWFRW")+","+seachFields+" FROM "+tableName+(batchValue.equals("-1")?"":"_01")+" WHERE "+(StringUtil.arrIsNULL(whereSql)?"1=1":whereSql);
			if(!StringUtil.arrIsNULL(orderSql)){//如果存在排序条件
				seachSQL += " ORDER BY "+orderSql;
			}
			this.logging.logging(author, "select", "报表查询信息SQL:"+seachSQL);
			List<List> seachResult = this.dbManager.executeQueryList(seachSQL);
			if(ListUtil.listIsNull(seachResult)){//如果查询结果为空 则直接返回
				this.logging.logging(author, "warn", "【 "+tableName+" 】表无记录,直接返回。", tab045id);
				return result;
			}
			//查询报表转换字段信息
			sql = "SELECT T2.TAB_046_ID,T2.TAB_046_ID_01,T1.TAB_046_005,T2.TAB_049_001,NVL(T3.ID,0) FROM TAB_4007 T1 RIGHT JOIN TAB_4010 T2 ON T1.ID=T2.TAB_046_ID LEFT JOIN TAB_4009 T3 ON T3.ID=T2.TAB_048_ID WHERE T1.TAB_045_ID='param1' ORDER BY T1.TAB_046_003"
				.replaceAll("param1", tab045ID.get(0).get(0)+"");
			List<List> transFormat = this.dbManager.executeQueryList(sql);
			this.logging.logging(author, "select", "查询报表转换字段信息:"+sql, tab045id);
			//保存字段的转换信息Map
			Map<String, Map<String, Map<String, String>>> formatMap = new Hashtable<String, Map<String,Map<String, String>>>();
			for(int i=0; i<transFormat.size(); i++){
				Map<String, Map<String, String>> singleFormat = new Hashtable<String, Map<String,String>>();
				if(formatMap.containsKey(transFormat.get(i).get(0))){//如果存在寄主字段的转换信息，直接取出信息
					singleFormat = formatMap.get(transFormat.get(i).get(0));
				}
				Map<String, String> formatInfo = new Hashtable<String, String>();
				formatInfo.put("type", transFormat.get(i).get(4)+"");//转换类型
				formatInfo.put("format", HtmlUtils.htmlUnescape(transFormat.get(i).get(2)+""));//转换格式
				formatInfo.put("unique", transFormat.get(i).get(3)+"");//唯一性标识，主要在转换格式中使用
				singleFormat.put(transFormat.get(i).get(1)+"", formatInfo);
				formatMap.put(transFormat.get(i).get(0)+"", singleFormat);
			}
			//转换文本内容加码数据，对加码数据进行解码操作
			for(int i=0; i<seachResult.size(); i++){
				for(int j=2; j<seachResult.get(i).size(); j++){
					String fieldName = fieldsList.get(j-2).split("@")[0];
					if(fieldsInfoMap.get(fieldName).equals("0")){//如果字段是文本框，则要进行解码操作
						seachResult.get(i).set(j, Escape2Encode.decode_text(seachResult.get(i).get(j)+""));
					}
				}
			}
			//定义结果信息长度
			result = new String[seachResult.size()][showFields.size()+2];
			for(int i=0; i<seachResult.size(); i++){
				result[i][0] = seachResult.get(i).get(0)+"";
				result[i][1] = seachResult.get(i).get(1)+"";
				for(int j=2; j<result[i].length; j++){
					List fieldInfo = showFields.get(j-2);
					int location = this.storageLocation(fieldsList, fieldInfo.get(1)+"@"+fieldInfo.get(2));
					String fieldValue = seachResult.get(i).get(location+2)+"";
					//如果该字段需要转换，则将转换的信息放入结果集中
					if(formatMap.containsKey(fieldInfo.get(0))){
						//转换信息
						result[i][j] = this.formatValue(fieldInfo.get(0)+"", formatMap.get(fieldInfo.get(0)), seachResult.get(i), locationMap, fieldsIndexMap);
					}else{
						result[i][j] = fieldValue;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.logging.logging(author, "exception", "查询报表信息异常："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 转换结果信息
	 * @param fieldID 字段ID
	 * @param format 转换格式Map
	 * @param source 原数据
	 * @param locationMap 字段位置Map
	 * @param fieldsIndexMap
	 * @return
	 * 		转换后的结果
	 */
	private String formatValue(String fieldID, Map<String, Map<String, String>> format, List source, Map<String, Integer> locationMap, Map<String, List> fieldsIndexMap){
		String result = "";
		//获得字段的详细信息
		List fieldsInfo1 = fieldsIndexMap.get(fieldID);
		//获得字段的转换格式
		String formatText = HtmlUtils.htmlUnescape(fieldsInfo1.get(4)+"");
		if(StringUtil.arrIsNULL(formatText)){//如果字段的转换格式为空，直接返回原数据
			return result;
		}
		Set<String> keySet = format.keySet();
		for(String s:keySet){
			//查找字段信息
			List transField = fieldsIndexMap.get(s);
			//查找字段的位置
			int fieldLocation = locationMap.get(transField.get(1)+"@"+transField.get(2));
			//获得字段的内容
			String fieldValue = source.get(fieldLocation+2)+"";
			//获得字段的转换格式
			String formatText1 = transField.get(4)+"";
			if(!StringUtil.arrIsNULL(formatText1)){
				fieldValue = this.formatString(format.get(s).get("type"), format.get(s).get("format"), fieldValue);
			}
			//替换转换格式中的内容
			formatText = formatText.replaceAll("\\{format"+format.get(s).get("unique")+"\\}", fieldValue);
		}
		return formatText;
	}
	
	/**
	 * 转换字符串内容
	 * @param type 转换类型
	 * @param format 转换格式
	 * @param source 转换内容
	 * @return
	 * 		转换的结果
	 */
	private String formatString(String type, String format, String source){
		if(StringUtil.arrIsNULL(type, source)){//判断转换类型和原数据是否为空
			return source;
		}
		String transValue = "";
		String oldFormat = "";
		String formatString = "";
		switch(this.char2Int(type)){
		case 1://yyyy --> yyyy年
			oldFormat = "yyyy-MM-dd";
			oldFormat = oldFormat.substring(0, source.length()-1);
			formatString = "yyyy年";
			try {
				transValue = new SimpleDateFormat(formatString).format(new SimpleDateFormat(oldFormat).parse(source)).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				e.printStackTrace();
				this.logging.logging(author, "exception", "报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
			}
			break;
		case 2://yyyy-MM --> yyyy年MM月
			oldFormat = "yyyy-MM-dd";
			oldFormat = oldFormat.substring(0, source.length()-1);
			formatString = "yyyy年MM月";
			try {
				transValue = new SimpleDateFormat(formatString).format(new SimpleDateFormat(oldFormat).parse(source)).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				this.logging.logging(author, "exception", "报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				e.printStackTrace();
			}
			break;
		case 3://yyyy-MM-dd --> yyyy年MM月dd日
			oldFormat = "yyyy-MM-dd";
			oldFormat = oldFormat.substring(0, source.length()-1);
			formatString = "yyyy年MM月dd日";
			try {
				transValue = new SimpleDateFormat(formatString).format(new SimpleDateFormat(oldFormat).parse(source)).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				this.logging.logging(author, "exception", "报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				e.printStackTrace();
			}
			break;
		case 4://yyyy-MM --> yyyy.MM
			oldFormat = "yyyy-MM-dd";
			oldFormat = oldFormat.substring(0, source.length()-1);
			formatString = "yyyy.MM";
			try {
				transValue = new SimpleDateFormat(formatString).format(new SimpleDateFormat(oldFormat).parse(source)).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				this.logging.logging(author, "exception", "报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				e.printStackTrace();
			}
			break;
		case 5://yyyy-MM-dd --> yyyy.MM.dd
			oldFormat = "yyyy-MM-dd";
			oldFormat = oldFormat.substring(0, source.length()-1);
			formatString = "yyyy.MM.dd";
			try {
				transValue = new SimpleDateFormat(formatString).format(new SimpleDateFormat(oldFormat).parse(source)).toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				this.logging.logging(author, "exception", "报表转换日期类型数据异常： 日期值="+transValue+" 转换格式="+format+" 转换类型="+type);
				e.printStackTrace();
			}
			break;
		}
		return transValue;
	}
	
	/**
	 * 将字符串转换为整数
	 * @param source 待转换的字符串
	 * @return
	 * 		int
	 */
	private int char2Int(String source){
		if(StringUtil.arrIsNULL(source)){//如果待转换的字符串为空，直接返回"0"
			return 0;
		}
		try{
			return Integer.valueOf(source);
		}catch(Exception e){
			e.printStackTrace();
			this.logging.logging(author, "exception", "字符串转换为整数异常\n转换内容="+source+";\n异常信息:"+e.getMessage());
		}
		return 0;
	}
	
	/**
	 * 查询字段的存储位置
	 * @param source
	 * @param match
	 * @return
	 * 	字段位置
	 */
	private int storageLocation(List source, Object match){
		if(ObjectUtil.objIsNull(source, match)||source.size()<=0){
			return 0;
		}
		for(int i=0; i<source.size(); i++){
			if(source.get(i).equals(match)){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 将字符串中的","转换为"','"
	 * @param source 待转换的字符串
	 * @return
	 * 		转换后的字符串
	 */
	private String charAddQuote(String source){
		if(source==null||source.equals("")){//如果字符串为空，直接返回"''"
			return "''";
		}
		//去除字符串最后的","
		if(source.endsWith(",")){
			source = source.substring(0,source.length()-1);
		}
		return "'"+source.replaceAll(",", "','")+"'";
	}
	
	/**
	 * 拼接查询字段
	 * 		对于树、下拉和日期类型要添加转换存储过程
	 * @param table 表名
	 * @param fieldName 字段名
	 * @param showType 字段显示类型
	 * @return
	 * 		数据库中查询的字段脚步
	 */
	private String getSeachColumn(String table, String fieldName, String showType){
		String fieldTrans = "";
		switch(Integer.parseInt(showType)){
		case 1://下拉
		case 2://树
			fieldTrans = "get_code_message('"+fieldName+"', "+fieldName+")";break;
		case 3://日期
			fieldTrans = "get_time_message('"+fieldName+"', "+fieldName+",'"+table+"')";break;
		default:
			fieldTrans = fieldName;
		}
		return fieldTrans;
	}
	
}
