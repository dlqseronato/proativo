package com.proativo.util.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.AddressException;

import com.proativo.util.ExcelUtil;
import com.proativo.util.Mailer;
import com.proativo.util.dao.OraUtilProativo;
import com.proativo.util.log.Log;
import com.proativo.util.vo.CenarioVo;

public class ReportUtil {
	private static File relatorio;
	private static OraUtilProativo proativo = new OraUtilProativo();
	private static DateFormat formatador = new SimpleDateFormat("MM/yyyy");
	
	private static Properties propBuild;
	private static Properties propEmail;
	
	private static boolean configuracaoLocal = false;
	
	public static String geraRelatorio(CenarioVo cenario, String nomeRelatorio, Object[] cabecalhoRelatorio, List<Object[]> linhasRelatorio, boolean removeArquivoOriginal) {
		SimpleDateFormat sdf;
		String nomeDiretorio;
		File diretorioRelatorio;
		String separador;
		
		OraUtilProativo proativo = new OraUtilProativo();
		
		if(linhasRelatorio.size() > 0){
			try{
				separador = System.getProperty("file.separator");
				sdf = new SimpleDateFormat("yyyy_MM_dd");
				nomeDiretorio = proativo.buscarParametro("diretorioRelatorio");
				nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_" + nomeRelatorio;

				diretorioRelatorio = new File(nomeDiretorio);
				
				if(!diretorioRelatorio.exists()){
					diretorioRelatorio.mkdir();
				}
				
				relatorio = new File(nomeRelatorio);
				
				// Evita sobrepor relatorios, adicionando "_N" ao final do nome do arquivo quando necessário
				int count = 0;
				while(relatorio.exists()){
					nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_"+nomeRelatorio+(++count);
					relatorio = new File(nomeRelatorio);
				}

				relatorio = ExcelUtil.criarRelatorioXlsx(cabecalhoRelatorio, linhasRelatorio, cenario.getCenario(), nomeRelatorio, "");
				
				enviaEmail(cenario, null);
				
			} catch (Exception e) {
				Log.error("Falha ao criar arquivo", e);
			} finally{
				System.gc();
				
				if(removeArquivoOriginal){
					relatorio.delete();
				}
			}
		}else {
			nomeRelatorio = "Arquivo sem casos";
			enviaEmailSemCasos(cenario);
		}
		
		return (relatorio != null ? relatorio.getName() : null);
	}
	
	public static String geraRelatorio(CenarioVo cenario, String nomeRelatorio, Object[] cabecalhoRelatorio, List<Object[]> linhasRelatorio, boolean removeArquivoOriginal, String mensagemComplementarEmail) {
		SimpleDateFormat sdf;
		String nomeDiretorio;
		File diretorioRelatorio;
		String separador;
		
		OraUtilProativo proativo = new OraUtilProativo();
		
		if(linhasRelatorio.size() > 0){
			try{
				separador = System.getProperty("file.separator");
				sdf = new SimpleDateFormat("yyyy_MM_dd");
				nomeDiretorio = proativo.buscarParametro("diretorioRelatorio");
				nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_" + nomeRelatorio;

				diretorioRelatorio = new File(nomeDiretorio);
				
				if(!diretorioRelatorio.exists()){
					diretorioRelatorio.mkdir();
				}
				
				relatorio = new File(nomeRelatorio);
				
				// Evita sobrepor relatorios, adicionando "_N" ao final do nome do arquivo quando necessário
				int count = 0;
				while(relatorio.exists()){
					nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_"+nomeRelatorio+(++count);
					relatorio = new File(nomeRelatorio);
				}

				relatorio = ExcelUtil.criarRelatorioXlsx(cabecalhoRelatorio, linhasRelatorio, cenario.getCenario(), nomeRelatorio, "");
				
				enviaEmail(cenario, mensagemComplementarEmail);
				
			} catch (Exception e) {
				Log.error("Falha ao criar arquivo", e);
			} finally{
				System.gc();
				
				if(removeArquivoOriginal){
					relatorio.delete();
				}
			}
		}else {
			nomeRelatorio = "Arquivo sem casos";
			enviaEmailSemCasos(cenario);
		}
		
		return relatorio.getName();
	}
	
	protected static void enviaEmail(CenarioVo cenario, String mensagemComplementarEmail) {
		try{
			carregaProperties();
			
			List<File> attach = new ArrayList<File>();
			attach.add(relatorio);
			
			String mailServer = null;
			String[] destinatarios = null;
			String[] destinatariosCc = null;
			
			if (configuracaoLocal) {
				mailServer = propEmail.getProperty("email.servidor");
				destinatarios = propEmail.getProperty("email.relatorio.para").split(";");
				destinatariosCc = propEmail.getProperty("email.relatorio.cc").split(";");
			}else{
				mailServer = proativo.buscarParametro("emailServidor");
				destinatarios = cenario.getEmailPara().toArray(new String[cenario.getEmailPara().size()]);
				destinatariosCc = cenario.getEmailCc().toArray(new String[cenario.getEmailCc().size()]);
			}
			
			String mailFrom = propBuild.getProperty("build.title");
				
			String body =  
					"Cenario " + cenario.getCenario() + " Finalizado!" +
					"<br><br>" +
					"Favor verificar o arquivo '" + relatorio.getName() + "' em anexo." +
					(mensagemComplementarEmail != null ? "<br><br>" + mensagemComplementarEmail + "<br><br>" : "<br><br>") +
					"Obrigado!" +
					"<br><br>" +
					"Atenciosamente,<br><br>"
					+ "IT Suporte Ciclo da Receita";
			
			String subject = "Relatorio - Cenário " + cenario.getCenario() + " - " + cenario.getSegmento() + " - " + cenario.getNomeCenario() + " - " + (cenario.getProcessamentoCiclo() == null ? "Full" : cenario.getProcessamentoCiclo() ) + " - " + formatador.format(new Date());
			
			Mailer.sendWithAttachments(mailServer, mailFrom, destinatarios, destinatariosCc, null, subject, body, "text/html", attach);
			
			
		} catch (AddressException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (FileNotFoundException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (IOException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (Exception e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		}
	}
	
	protected static void enviaEmailAlarme(CenarioVo cenario, String mensagemComplementarEmail, Integer qtdeContas) {
		try{
			carregaProperties();
			
			List<File> attach = new ArrayList<File>();
			attach.add(relatorio);
			
			String mailServer = null;
			String[] destinatarios = null;
			String[] destinatariosCc = null;
			
			if (configuracaoLocal) {
				mailServer = propEmail.getProperty("email.servidor");
				destinatarios = propEmail.getProperty("email.relatorio.para").split(";");
				destinatariosCc = propEmail.getProperty("email.relatorio.cc").split(";");
			}else{
				mailServer = proativo.buscarParametro("emailServidor");
				destinatarios = cenario.getEmailPara().toArray(new String[cenario.getEmailPara().size()]);
				destinatariosCc = cenario.getEmailCc().toArray(new String[cenario.getEmailCc().size()]);
			}
			
			String mailFrom = propBuild.getProperty("build.title");
				
			String body =  
					"Alarme do Cenario " + cenario.getCenario() + " gerado!" +
					"<br><br>" +
					"O cenario reportou " + qtdeContas + " contas que devem ser analisadas.";
					
					if(cenario.getDataInativacao() != null){
						body += "<br><br>" + "O mesmo encontra-se desligado desde " + new SimpleDateFormat("dd/MM/yyyy").format(cenario.getDataInativacao()) + ".";
					}
					
					body += "<br><br>" + "Obrigado!" +
					"<br><br>" +
					"Atenciosamente,<br><br>"
					+ "IT Suporte Ciclo da Receita";
			
			String subject = "Alarme - Cenário " + cenario.getCenario() + " - " + cenario.getSegmento() + " - " + cenario.getNomeCenario() + " - " + (cenario.getProcessamentoCiclo() == null ? "Full" : cenario.getProcessamentoCiclo() ) + " - " + formatador.format(new Date());
			
			Mailer.sendWithAttachments(mailServer, mailFrom, destinatarios, destinatariosCc, null, subject, body, "text/html", attach);
			
			
		} catch (AddressException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (FileNotFoundException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (IOException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (Exception e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		}
	}
	
	protected static void enviaEmailSemCasos(CenarioVo cenario) {
		try{
			carregaProperties();
			
			String mailServer = null;
			String[] destinatarios = null;
			String[] destinatariosCc = null;
			
			if (configuracaoLocal) {
				mailServer = propEmail.getProperty("email.servidor");
				destinatarios = propEmail.getProperty("email.relatorio.para").split(";");
				destinatariosCc = propEmail.getProperty("email.relatorio.cc").split(";");
			}else{
				mailServer = proativo.buscarParametro("emailServidor");
				destinatarios = cenario.getEmailPara().toArray(new String[cenario.getEmailPara().size()]);
				destinatariosCc = cenario.getEmailCc().toArray(new String[cenario.getEmailCc().size()]);
			}
			
			
			Properties buildProperties = new Properties();
			buildProperties.load(new FileInputStream("config/build.properties"));
			
			String mailFrom = buildProperties.getProperty("build.title");
			
			String body = 
					"Cenario " + cenario.getCenario() + " Finalizado!" +
					"<br><br>" +
					"Nao foram encontrados casos nessa execucao." +
					"<br><br>" +
					"Obrigado!" +
					"<br><br>" +
					"Atenciosamente,<br><br>"
					+ "IT Suporte Ciclo da Receita";
			
			String subject = "Relatorio - Cenário " + cenario.getCenario() + " - " + cenario.getSegmento() + " - " + cenario.getNomeCenario() + " - " + (cenario.getProcessamentoCiclo() == null ? "Full" : cenario.getProcessamentoCiclo() ) + " - " + formatador.format(new Date());
			
			Mailer.sendMail(mailServer, mailFrom, destinatarios, destinatariosCc, null, subject, body, "text/html");
			
		} catch (AddressException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (FileNotFoundException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (IOException e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		} catch (Exception e) {
			Log.error("Falha ao enviar e-mail de relatorio", e);
		}
	}
	
	private static void carregaProperties(){
		propBuild = new Properties();
		
		try {
			propBuild.load(new FileInputStream(new File("config/build.properties")));
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
	
	
	public static String geraRelatorioAlarme(CenarioVo cenario, String nomeRelatorio, Object[] cabecalhoRelatorio, List<Object[]> linhasRelatorio, boolean removeArquivoOriginal) {
		SimpleDateFormat sdf;
		String nomeDiretorio;
		File diretorioRelatorio;
		String separador;
		
		OraUtilProativo proativo = new OraUtilProativo();
		
		if(linhasRelatorio.size() > 0){
			try{
				separador = System.getProperty("file.separator");
				sdf = new SimpleDateFormat("yyyy_MM_dd");
				nomeDiretorio = proativo.buscarParametro("diretorioRelatorio");
				nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_" + nomeRelatorio;

				diretorioRelatorio = new File(nomeDiretorio);
				
				if(!diretorioRelatorio.exists()){
					diretorioRelatorio.mkdir();
				}
				
				relatorio = new File(nomeRelatorio);
				
				// Evita sobrepor relatorios, adicionando "_N" ao final do nome do arquivo quando necessário
				int count = 0;
				while(relatorio.exists()){
					nomeRelatorio = nomeDiretorio + separador + sdf.format(new Date(System.currentTimeMillis())) + "_"+nomeRelatorio+(++count);
					relatorio = new File(nomeRelatorio);
				}

				relatorio = ExcelUtil.criarRelatorioXlsx(cabecalhoRelatorio, linhasRelatorio, cenario.getCenario(), nomeRelatorio, "");
				
				enviaEmailAlarme(cenario, null, linhasRelatorio.size());
				
			} catch (Exception e) {
				Log.error("Falha ao criar arquivo", e);
			} finally{
				System.gc();
				
				if(removeArquivoOriginal){
					relatorio.delete();
				}
			}
		}
		
		return (relatorio != null ? relatorio.getName() : null);
	}
}
