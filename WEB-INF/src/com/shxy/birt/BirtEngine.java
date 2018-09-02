package com.shxy.birt;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import javax.servlet.*;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

public class BirtEngine {

	private static IReportEngine birtEngine = null;

	public static synchronized IReportEngine getBirtEngine(ServletContext sc) {
		if (birtEngine == null) {
			EngineConfig config = new EngineConfig();
			config.setEngineHome("");
			IPlatformContext context = new PlatformServletContext(sc);
			config.setPlatformContext(context);
			try {
				Platform.startup(config);
			} catch (BirtException e) {
				e.printStackTrace();
			}
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			birtEngine = factory.createReportEngine(config);
		}
		return birtEngine;
	}

	public static synchronized void destroyBirtEngine() {
		if (birtEngine == null) {
			return;
		}
		birtEngine.destroy();
		Platform.shutdown();
		birtEngine = null;
	}
}
