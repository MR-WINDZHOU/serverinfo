package com.shxy.birt;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.*;

import com.shxy.www.conf.DBManager;
import com.wenmin.birt.data.odb.jdbc.MyJdbcDriver;

public class WebReport extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8917157445084290706L;

	/**
	 * Constructor of the object.
	 */
	private IReportEngine birtReportEngine = null;

	// protected static Logger logger = Logger.getLogger("org.eclipse.birt");

	//数据连接对象
	private DBManager db;
	
	public WebReport() {
		super();
	}

	/**
	 * Destruction of the servlet.
	 */
	public void destroy() {
		super.destroy();
		BirtEngine.destroyBirtEngine();
	}

	/**
	 * The doGet method of the servlet.
	 * 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/**
	 * The doPost method of the servlet.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
		String flag = request.getParameter("flag");
		String reportName = request.getParameter("ReportName");
		String birtParam = request.getParameter("birtParam");
		String[]params = birtParam.split("&");
		// get report name and launch the engine
		if (flag.equals("pdf")) {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "inline; filename=" + reportName+".pdf");
		} else {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=" + reportName+".xls");
		}
		ServletContext sc = request.getSession().getServletContext();
		if(this.birtReportEngine==null){
			this.birtReportEngine = BirtEngine.getBirtEngine(sc);
		}
		// setup image directory
		HTMLRenderContext renderContext = new HTMLRenderContext();
		renderContext.setBaseImageURL(request.getContextPath() + "/images");
		renderContext.setImageDirectory(sc.getRealPath("/images"));

		// logger.log(Level.FINE, "image directory " +
		// sc.getRealPath("/images"));
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT,renderContext);
		contextMap.put( EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request );
		//数据库连接
		db =new DBManager();
		db.beginTransaction();
		Connection conn = db.getConnection();
		contextMap.put(MyJdbcDriver.DATASOURCE_KEY_CONNECTION,conn);  
		IReportRunnable design;
	
			design = birtReportEngine.openReportDesign(sc
					.getRealPath("/reports")
					+ "/" + reportName + ".rptdesign");
			// create task to run and render report
			IRunAndRenderTask task = birtReportEngine
					.createRunAndRenderTask(design);
			task.setAppContext(contextMap);

			// task.setParameterValue("customernum", "103");
			// task.validateParameters();
			// 以打开的报表设计文件为参数，创建一个获取参数的对象
			IGetParameterDefinitionTask paramTask = birtReportEngine
					.createGetParameterDefinitionTask(design);
			// 获取报表设计文件中的参数定义
			Map<String, String> parameterMap = new HashMap<String, String>();
			Collection parameters = paramTask.getParameterDefns(false);
			Iterator iter = parameters.iterator();
			while (iter.hasNext()) {
				IParameterDefnBase pBase = (IParameterDefnBase) iter.next();
				if (pBase instanceof IScalarParameterDefn) {
					IScalarParameterDefn paramDefn = (IScalarParameterDefn) pBase;
					String paramName = paramDefn.getName();
					String inputValue ="";
					//String inputValue = (String)request.getParameter(paramName);
					for(int i=0;i<params.length;i++){
						   if(params[i].indexOf(paramName)!=-1){
							   inputValue = params[i].substring(params[i].indexOf("=")+1,params[i].length());
							}
						}
					if(inputValue!=null)
					{
						parameterMap.put(paramName, inputValue);
					}
				}
			}
			// 为获取的参数定义赋值
			
			// set output options
			// HTMLRenderOption options = new HTMLRenderOption();
			RenderOption options = new RenderOption();
			if (flag.equals("pdf")) {
				options.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
			} else if(flag.equals("xls")){
				options.setOutputFormat("xls");
			}else {
				options.setOutputFormat(RenderOption.OUTPUT_FORMAT_HTML);
			}
			options.setOutputStream(response.getOutputStream());
			task.setRenderOption(options);
			if (parameterMap.size()>0)
				task.setParameterValues(parameterMap);
			// run report
			task.run();
			task.close();
		} catch (Exception e) {
			db.rollback();
			e.printStackTrace();
		}finally{
			db.close();
		}
	}

	/**
	 * Initialization of the servlet.
	 * 
	 * @throws ServletException
	 *             if an error occure
	 */
	public void init(ServletConfig arg0) throws ServletException
	{
		super.init(arg0);
		ServletContext sc = arg0.getServletContext();
		this.birtReportEngine = BirtEngine.getBirtEngine(sc);
		IReportRunnable design;
		
		try
		{
			design = birtReportEngine.openReportDesign(sc.getRealPath("/reports")+"/test.rptdesign");
			IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask(design);
			IRenderOption options = new RenderOption();;
			options.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
			task.setRenderOption(options);
			task.run();
			task.close();
		}
		catch(EngineException e)
		{
			e.printStackTrace();
		}
	}
}