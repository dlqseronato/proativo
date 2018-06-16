package com.proativo.util.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SMTPAppender;

import com.proativo.util.ZipUtil;
import com.proativo.util.dao.OraUtilProativo;
import com.proativo.util.vo.CenarioVo;

/**
 * Classe criada para o gerenciamento do Log nao sendo necessário inicializar a classe.
 * 
 * @author G0024266 - Andre Luiz Przynyczuk
 * @since 28/09/2015
 * 
 */

public class Log {
	private static Logger logger;
	
	private static PatternLayout layout = new PatternLayout("[%d] %p : %m%n");
	
	private static int maximo = 0;
	private static String aplicacao;
	private static Hashtable<String, String> erros = new Hashtable<String, String>();
	
	private static Properties propBuild;
	private static Properties propEmail;
	
	private static OraUtilProativo proativo = new OraUtilProativo();
	
	private static CenarioVo cenario = new CenarioVo();
	
	private static boolean configuracaoLocal = false;
	
	public static void setCenario(CenarioVo cen){
		cenario = cen;
	}

	private static void adicionaAppenderEmail(){
		SMTPAppender emailAppender = new SMTPAppender();
		emailAppender.setFrom(aplicacao);
		
		if(configuracaoLocal){
			emailAppender.setSMTPHost(propEmail.getProperty("email.servidor"));
			emailAppender.setTo(propEmail.getProperty("email.para"));
		}else{
			emailAppender.setSMTPHost(proativo.buscarParametro("emailServidor"));
			emailAppender.setTo(cenario.getEmailLog().get(0));
		}
		
		emailAppender.setSubject("Erro - " + aplicacao);
		emailAppender.setThreshold(Level.ERROR);
		emailAppender.setLayout(new Log.EmailGVTLayout());
		emailAppender.activateOptions();
		logger.addAppender(emailAppender);
	}
	
	private static void adicionaAppenderConsole(){
		ConsoleAppender console = new ConsoleAppender(layout);
		console.setThreshold(Level.ALL);
		console.activateOptions();
		logger.addAppender(console);
	}
	
	private static void adicionaAppenderFile(){
		File log = new File("log");
		if(!log.exists()){
			log.mkdir();
		}
		
		DailyRollingFileAppender file = null;
		
		try {
			file = new DailyRollingFileAppender(layout, "log/app.log","'.'yyyy-MM-dd");
			file.setAppend(true);
			file.setThreshold(Level.ALL);
			file.activateOptions();
			logger.addAppender(file);
		} catch (IOException e1) {
			Log.error("Erro ao criar appender de Arquivo", e1);
		}
	}
	
	private synchronized static void init() {
		maximo = 100;
		carregaProperties();
		logger = Logger.getLogger(aplicacao.replace("_", "") + "Log");
		adicionaAppenderConsole();
		adicionaAppenderFile();
		adicionaAppenderEmail();
		compactarArquivosLog();
	}
	
	private static void carregaProperties(){
		propBuild = new Properties();
		
		try {
			propBuild.load(new FileInputStream(new File("config/build.properties")));
			aplicacao = propBuild.getProperty("build.title");
		} catch (FileNotFoundException e) {
			Log.error("arquivo build.properties nao foi encontrado no diretório config", e);
		} catch (IOException e) {
			Log.error("arquivo build.properties nao foi encontrado no diretório config", e);
		}
		
		
		try {
			File email = new File("config/email.properties");
			
			if(email.exists()){
				propEmail = new Properties();
				propEmail.load(new FileInputStream(new File("config/email.properties")));
				configuracaoLocal = true;
			} else{
				configuracaoLocal = false;
			}
			
		} catch (FileNotFoundException e) {
			configuracaoLocal = false;
		} catch (IOException e) {
			configuracaoLocal = false;
		}
		
	}
	
	public synchronized static void alterarQuantidadeErros(Integer qtdeMaxErros) {
		if (qtdeMaxErros != null) {
			maximo = qtdeMaxErros;
		} 
	}
	
	public synchronized static void infoThread(String strMsg) {
		if(logger == null){
			init();
		}
		
		logger.info(strMsg);
	}

	public synchronized static void infoDbPool(String strMsg) {
		if(logger == null){
			init();
		}
		
		logger.info(strMsg);
	}

	public synchronized static void info(String strMsg) {
		if(logger == null){
			init();
		}
		
		logger.info(strMsg);
	}
	
	public synchronized static void info(String strMsg, Object obj) {
		if(logger == null){
			init();
		}
		
		logger.info(strMsg + " - " + obj.getClass().getName());
	}
	
	public synchronized static void error(String strMsg, Throwable e) {
		if(logger == null){
			init();
		}
		
		logger.error(strMsg, e);

		int cont;

		if (erros.get("processo.generico") == null) {
			cont = 1;
		} else {
			cont = Integer.parseInt((String) erros.get("processo.generico"));
			cont++;
		}

		erros.put("processo.generico", String.valueOf(cont));

		verificaLimite(e);
	}
	
	private synchronized static void verificaLimite(Throwable ex) {
		if (maximo > 0) {
			if (Integer.parseInt((String) erros.get("processo.generico")) >= maximo) {
				logger.error("*********** Aplicacao finalizada por repetidos erros ***********", ex);
				System.exit(-1);
			}
		}
	}
	
	private static void compactarArquivosLog(){
		File log = new File("log");
		
		if(log.exists()){
		    FilenameFilter filtro = new FilenameFilter() {
				
		    	@Override
				public boolean accept(File dir, String name) {
					if(name.matches("^app.log.[0-9]{4}-[0-9]{2}-[0-9]{2}$")){
						return true;
					}
					
					return false;
				}
			};
			
		    if (log.isDirectory()) {
		    	List<File> arquivos = Arrays.asList(log.listFiles(filtro));
		    	
		    	for(File arquivo: arquivos){
		    		ZipUtil.compactarArquivo(arquivo, true);
		    	}
		    } 
		}
	}
	
	private static class EmailGVTLayout extends HTMLLayout{
		@Override
		public String getHeader() {
			StringBuffer sbuf = new StringBuffer();
		    sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"  + Layout.LINE_SEP);
		    sbuf.append("<html>" + Layout.LINE_SEP);
		    sbuf.append("<head>" + Layout.LINE_SEP);
		    sbuf.append("<title>" + "adnjasndjkandkjas" + "</title>" + Layout.LINE_SEP);
		    sbuf.append("<style type=\"text/css\">"  + Layout.LINE_SEP);
		    sbuf.append("<!--"  + Layout.LINE_SEP);
		    sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}" + Layout.LINE_SEP);
		    sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
		    sbuf.append("-->" + Layout.LINE_SEP);
		    sbuf.append("</style>" + Layout.LINE_SEP);
		    sbuf.append("</head>" + Layout.LINE_SEP);
		    sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
		    sbuf.append("<br>" + Layout.LINE_SEP);
		    
		    sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
		    sbuf.append("<tr><th>M&aacute;quina de execu&ccedil;&atilde;o</th><td>").append(getNomeMaquina()).append("</td></tr>");
		    sbuf.append("<tr><th>Usu&aacute;rio de execu&ccedil;&atilde;o</th><td>").append(System.getProperty("user.name")).append("</td></tr>");
		    sbuf.append("<tr><th>Hor&aacute;rio</th><td>").append(new SimpleDateFormat("dd/MM/yyyy HH'h'mm").format(new Date())).append("</td></tr>");
		    sbuf.append("</table><br/>");
		    
		    sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
		    sbuf.append("<tr>" + Layout.LINE_SEP);
		    sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
		    sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
		    sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
		    sbuf.append("<th>Category</th>" + Layout.LINE_SEP);
		    sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
		    sbuf.append("</tr>" + Layout.LINE_SEP);
		    return sbuf.toString();
		}

		@Override
		public String getContentType() {
			return "text/html; charset=utf-8";
		}
		
		private static String getNomeMaquina(){
			try {
				return InetAddress.getLocalHost().getHostName().toUpperCase();
			} catch (UnknownHostException ex) {
				return " ";
			}
		}
	}
}