package com.proativo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.proativo.util.log.Log;

/**
 * Classe criada compactar os arquivos que são passados por paramêtro, sendo possivel manter o original ou remover.
 * 
 * @author G0024266 - Andre Luiz Przynyczuk
 * @since 28/09/2015
 * 
 */

public class ZipUtil {
	
	public static synchronized void compactarArquivo(File arquivo, boolean removerArquivoOriginal){
		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream in = null;
		try {
			fos = new FileOutputStream(arquivo.getAbsolutePath() + ".zip");
			zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(arquivo.getName());
			zos.putNextEntry(ze);
			in = new FileInputStream(arquivo.getAbsolutePath());
			int len;
			
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			Log.error("Falha ao gerar arquivo ZIP. O Arquivo não foi encontrado. Arquivo: " + arquivo.getName(), e);
		} catch (IOException e) {
			Log.error("Falha ao gerar arquivo ZIP. O Arquivo não pode ser escrito. Arquivo: " + arquivo.getName(), e);
		} finally{
			try {
				in.close();
				zos.closeEntry();
				zos.close();
				
				if(removerArquivoOriginal){
					arquivo.delete();
				}
			} catch (IOException e) {
				Log.error("Falha ao fechar arquivo ZIP. Arquivo: " + arquivo.getName(), e);
			}
		}
	}
}
