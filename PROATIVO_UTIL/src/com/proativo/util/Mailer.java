package com.proativo.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;


/*
 * Criado em 29/01/2004
 * por Érol Tavares Stocchero (G0002730)
 * GVT - IT System BackOffice - DataQuality
 */
 
/**
 * Esta classe util possui métodos estáticos para envio de email
 * com ou sem anexos.
 *
 * ver sites de referência:
 * http://w3.iac.net/~crawford/programming/javamail.html
 * http://www.javacommerce.com/articles/sendingmail.htm
 *
 * @author G0002730
 * @version 1.0.0
 */
public class Mailer
{
    //~ Methods ********************************************************************************************************

    /**
     * Prepara a mensagem para um destinatário
     *
     * @param smtp_host Endereço SMTP
     * @param from Endereço de envio
     * @param to Endereço de destino
     * @param subject Assunto
     * @throws IOException Erro ao abrir o arquivo
     * @throws AddressException Endereço inválido
     * @throws MessagingException Mensagem formatada incorretamente
     */
    private static Message prepareHeader(String smtp_host, String from, String to, String subject)
                                  throws IOException, AddressException, MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp_host);

        Session session = Session.getDefaultInstance(props, null);
        
        Message msg = new MimeMessage(session);
		//'us-ascii'

        InternetAddress addr = new InternetAddress(to);
        msg.addRecipients(Message.RecipientType.TO, new InternetAddress[] { addr });

        InternetAddress from_addr = new InternetAddress(from);
        msg.setFrom(from_addr);
        
        msg.setSubject(subject);

        return msg;
    }

    /**
     * Prepara a mensagem para vários destinários
     *
     * @param smtp_host Endereço SMTP
     * @param from Endereço de envio
     * @param to Endereços de destino (array de endereços)
     * @param subject Assunto
     * @throws IOException Erro ao abrir o arquivo
     * @throws AddressException Endereço inválido
     * @throws MessagingException Mensagem formatada incorretamente
     */
    private static Message prepareHeader(String smtp_host, String from, String[] to, String subject)
                                  throws IOException, AddressException, MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp_host);

        Session session = Session.getDefaultInstance(props, null);

        Message msg = new MimeMessage(session);

        for (int i = 0; i < to.length; i++)
        {
            InternetAddress addr = new InternetAddress(to[i]);
            msg.addRecipients(Message.RecipientType.TO, new InternetAddress[] { addr });
        }

        InternetAddress from_addr = new InternetAddress(from);
        msg.setFrom(from_addr);

        msg.setSubject(subject);

        return msg;
    }
    
    private static Message prepareHeader(String smtp_host, String from, String[] to, String[] cc, String[] cco, String subject) throws IOException, AddressException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtp_host);
		
		Session session = Session.getDefaultInstance(props, null);
		
		Message msg = new MimeMessage(session);
		
		for (int i = 0; i < to.length; i++) {
			InternetAddress addr = new InternetAddress(to[i]);
			msg.addRecipients(Message.RecipientType.TO, new InternetAddress[] { addr });
		}
		
		
		if (cc != null) {
		
			for (int i = 0; i < cc.length; i++) {
				InternetAddress addr = new InternetAddress(cc[i]);
				msg.addRecipients(Message.RecipientType.CC, new InternetAddress[] { addr });
			}
		
		}
		
		if (cco != null) {
			for (int i = 0; i < cco.length; i++) {
				InternetAddress addr = new InternetAddress(cco[i]);
				msg.addRecipients(Message.RecipientType.BCC, new InternetAddress[] { addr });
			}
		}
		
		InternetAddress from_addr = new InternetAddress(from);
		msg.setFrom(from_addr);
		
		msg.setSubject(subject);
		
		return msg;
    }
    
    

	/**
	 * Metodo para anexar imagem no corpo do email HTML
	 * 
	 * @param smtp_host
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 * @param contentType
	 * @param attach
	 * @throws EmailException
	 * @throws AddressException
	 * @throws IOException
	 * @throws MessagingException
	 */
	public static void sendWithImageBody(String smtp_host, String from, String to, String subject, String message, String contentType, Vector<?> attach) throws EmailException, IOException 
			{
		HtmlEmail html = new HtmlEmail();
		html.setHostName(smtp_host);
		html.addTo(to);
		html.setFrom(from);
		html.setSubject(subject);

		String cidAll = message;
		for (int i = 0; i < attach.size(); i++)
		{
			File file = new File((String)attach.elementAt(i));
			String cid = html.embed(file, String.valueOf((i+1)));
			cidAll += "<img src=\'cid:"+cid+"'>";
		}
		html.setHtmlMsg(cidAll);
		html.setTextMsg("Seu servidor de e-mail nao suporta mensagem HTML");
		html.send();
	}

	/**
	 * Método que envia o email em formato html, arquivos diversos em anexo e imagens no corpo
	 * do email. 
	 * 
	 * @param smtp_host
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 * @param contentType
	 * @param attach
	 * @throws EmailException
	 * @throws IOException
	 */
	public static void sendWithImageAndXls(String smtp_host, String from, String to, String subject, String message, String contentType, Vector<?> attach) throws EmailException, IOException 
			{
		HtmlEmail html = new HtmlEmail();
		html.setHostName(smtp_host);
		html.addTo(to);
		html.setFrom(from);
		html.setSubject(subject);

		MimeMultipart mp = new MimeMultipart();
		
		String cidAll = message;
		for (int i = 0; i < attach.size(); i++)
		{
			
			String arquivo = ((String)attach.elementAt(i));
			
			File file = new File((String)attach.elementAt(i));
			
			if(arquivo.substring(arquivo.length()-3, arquivo.length()).equals("jpg")){
				String cid = html.embed(file, String.valueOf((i+1)));
				cidAll += "<img src=\'cid:"+cid+"'><BR>";
			}else{
				html.embed(file, String.valueOf((i+1)));
			}
		}
		
		html.setContent(mp);
		html.setHtmlMsg(cidAll);
		html.setTextMsg("Seu servidor de e-mail nao suporta mensagem HTML");
		html.send();
	}

	/**
	 * 
	 * Método que envia o email em formato html, arquivos diversos em anexo e imagens no corpo
	 * do email. As imagens do corpo podem ser colocadas de acordo com a ordem dos anexos e
	 * especificado a posiçao no corpo do email segundo a posiçao da string INDICE_IMAGEM + o indice da imagem.   
	 * 
	 * @param smtp_host
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 * @param contentType
	 * @param attach
	 * @param indiceImagens
	 * @throws EmailException
	 * @throws IOException
	 */
	public static void sendWithImageAndXlsIndex(String smtp_host, String from, String to, String subject, String message, String contentType, Vector<?> attach) throws EmailException, IOException 
			{
		HtmlEmail html = new HtmlEmail();
		html.setHostName(smtp_host);
		html.addTo(to);
		html.setFrom(from);
		html.setSubject(subject);

		MimeMultipart mp = new MimeMultipart();
		
		String cidAll = message;
		for (int i = 0; i < attach.size(); i++)
		{
			
			String arquivo = ((String)attach.elementAt(i));
			
			File file = new File((String)attach.elementAt(i));
			
			if(arquivo.substring(arquivo.length()-3, arquivo.length()).equals("jpg")){
				String cid = html.embed(file, String.valueOf((i+1)));
				if(cidAll.indexOf("INDICE_IMAGEM")>0){
					cidAll = cidAll.replaceAll("INDICE_IMAGEM" + i, "<img src=\'cid:"+cid+"'><BR>");
				}else{
					cidAll += "<img src=\'cid:"+cid+"'><BR>";
				}
			}else{
				html.embed(file, String.valueOf((i+1)));
			}
		}
		
		html.setContent(mp);
		html.setHtmlMsg(cidAll);
		html.setTextMsg("Seu servidor de e-mail nao suporta mensagem HTML");
		html.send();
	}

	/**
     * Envia o email para 1 destinatário
     *
         * @param smtp_host Endereço SMTP
         * @param from Endereço de envio
         * @param to Endereço de destino
         * @param subject Assunto
         * @param message Corpo da mensagem
         * @param contentType Tipo do conteúdo do email. Pode ser "text/plain" ou "text/html".
         * @throws IOException Erro ao abrir o arquivo
         * @throws AddressException Endereço inválido
         * @throws MessagingException Mensagem formatada incorretamente
     */
    public static void sendMail(
                                String smtp_host, String from, String to, String subject, String message,
                                String contentType
                               ) throws IOException, AddressException, MessagingException
    {
        Message msg = prepareHeader(smtp_host, from, to, subject);
        msg.setContent(message, contentType);
        Transport.send(msg);
    }

    /**
     * Envia o email para vários destinatários
     *
     * @param smtp_host Endereço SMTP
     * @param from Endereço de envio
     * @param to Endereços de destino (array de strings)
     * @param subject Assunto
     * @param message Corpo da mensagem
     * @param contentType Tipo do conteúdo do email. Pode ser "text/plain" ou "text/html".
     * @throws IOException Erro ao abrir o arquivo
     * @throws AddressException Endereço inválido
     * @throws MessagingException Mensagem formatada incorretamente
     */
    public static void sendMail(
                                String smtp_host, String from, String[] to, String subject, String message,
                                String contentType
                               ) throws IOException, AddressException, MessagingException
    {
        Message msg = prepareHeader(smtp_host, from, to, subject);
        msg.setContent(message, contentType);
        Transport.send(msg);
    }
    
    public static void sendMail(String smtp_host, String from, String[] to, String[] cc, String[] cco, String subject, String message,String contentType) throws IOException, AddressException, MessagingException
    {
		Message msg = prepareHeader(smtp_host, from, to, cc, cco, subject);
		msg.setContent(message, contentType);
		Transport.send(msg);
    }

    /**
     *
     * Envia o email para 1 destinatário com anexos.
     *
     * @param smtp_host Endereço SMTP
     * @param from Endereço de envio
     * @param to Endereço de destino
     * @param subject Assunto
     * @param message Corpo da mensagem
     * @param contentType Tipo do conteúdo do email. Pode ser "text/plain" ou "text/html".
     * @param attach Vector de objetos File que farao parte dos anexos
     * @throws IOException Erro ao abrir o arquivo
     * @throws AddressException Endereço inválido
     * @throws MessagingException Mensagem formatada incorretamente
     */
    public static void sendWithAttachments(
                                           String smtp_host, String from, String to, String subject, String message,
                                           String contentType, List<File> attach
                                          ) throws IOException, AddressException, MessagingException
    {
        Message msg = prepareHeader(smtp_host, from, to, subject);

        MimeMultipart mp = new MimeMultipart();

        MimeBodyPart text = new MimeBodyPart();
        text.setDisposition(Part.INLINE);
        text.setContent(message, contentType);
        mp.addBodyPart(text);

        for (int i = 0; i < attach.size(); i++)
        {
            MimeBodyPart file_part = new MimeBodyPart();
            File file = (File) attach.get(i);
            FileDataSource fds = new FileDataSource(file);
            DataHandler dh = new DataHandler(fds);
            file_part.setFileName(file.getName());
            file_part.setDisposition(Part.ATTACHMENT);
            file_part.setDescription("Attached file: " + file.getName());
            file_part.setDataHandler(dh);
            mp.addBodyPart(file_part);
        }

        msg.setContent(mp);
        Transport.send(msg);
    }

    /**
     *
     * Envia o email para vários destinatários com anexos.
     *
     * @param smtp_host Endereço SMTP
     * @param from Endereço de envio
     * @param to Endereços de destino (array de strings)
     * @param subject Assunto
     * @param message Corpo da mensagem
     * @param contentType Tipo do conteúdo do email. Pode ser "text/plain" ou "text/html".
     * @param attach Vector de objetos File que farao parte dos anexos
     * @throws IOException Erro ao abrir o arquivo
     * @throws AddressException Endereço inválido
     * @throws MessagingException Mensagem formatada incorretamente
     */
    public static void sendWithAttachments(
                                           String smtp_host, String from, String[] to, String subject, String message,
                                           String contentType, List<?> attach
                                          ) throws IOException, AddressException, MessagingException
    {
        Message msg = prepareHeader(smtp_host, from, to, subject);

        MimeMultipart mp = new MimeMultipart();

        MimeBodyPart text = new MimeBodyPart();
        text.setDisposition(Part.INLINE);
        text.setContent(message, contentType);
        mp.addBodyPart(text);

        for (int i = 0; i < attach.size(); i++)
        {
            MimeBodyPart file_part = new MimeBodyPart();
            File file = (File) attach.get(i);
            FileDataSource fds = new FileDataSource(file);
            DataHandler dh = new DataHandler(fds);
            file_part.setFileName(file.getName());
            file_part.setDisposition(Part.ATTACHMENT);
            file_part.setDescription("Attached file: " + file.getName());
            file_part.setDataHandler(dh);
            mp.addBodyPart(file_part);
        }

        msg.setContent(mp);
        Transport.send(msg);
    }
    
    public static void sendWithAttachments(String smtp_host, String from, String[] to, String[] cc, String[] cco, String subject, String message, String contentType, List<?> attach) throws IOException, AddressException, MessagingException {
		Message msg = prepareHeader(smtp_host, from, to, cc, cco,  subject);
		
		MimeMultipart mp = new MimeMultipart();
		
		MimeBodyPart text = new MimeBodyPart();
		text.setDisposition(Part.INLINE);
		text.setContent(message, contentType);
		mp.addBodyPart(text);
		
		for (int i = 0; i < attach.size(); i++)
		{
		MimeBodyPart file_part = new MimeBodyPart();
		File file = (File) attach.get(i);
		FileDataSource fds = new FileDataSource(file);
		DataHandler dh = new DataHandler(fds);
		file_part.setFileName(file.getName());
		file_part.setDisposition(Part.ATTACHMENT);
		file_part.setDescription("Attached file: " + file.getName());
		file_part.setDataHandler(dh);
		mp.addBodyPart(file_part);
		}
		
		msg.setContent(mp);
		Transport.send(msg);
	}
}
