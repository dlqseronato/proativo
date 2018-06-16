package com.proativo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.proativo.util.log.Log;


/**
 * Criado em 02/05/2005
 *
 * @author G0002730
 *
 * Esta classe implementa alguns métodos úteis
 * para a manipulação de arquivos texto.
 */
public class TextFileUtil
{
    public static String getContents(String path)
    {
        File aFile = new File(path);

        return getContents(aFile);
    }
    
    /**
     * Realiza a leitura de um arquivo
     * @param arquivo a ser lido
     * @return String com o conteudo do arquivo
     * @author Andre Rosot
     */
    public static String getContents(File arquivo){
    	StringBuilder bufferArquivo = new StringBuilder();
    	String temp = null;
    	try{
    		// Define um buffer de leitura de 64k
    		char[] buffer = new char[65536];
    		BufferedReader bufferedReader = new BufferedReader(new FileReader(arquivo));
    		while (bufferedReader.ready()) {
    			bufferArquivo.append(buffer, 0, bufferedReader.read(buffer));
    		}
    		bufferedReader.close();
    	}
    	catch(Exception ex){
    		Log.error("Ocorreu um erro lendo o arquivo texto.", ex);
    	}    	

    	String resultado = null;
    	/*Arquivos contento .sql conto PL Anônima têm o bloco begin end 
    	 * e isso é verificado para não terem os caracteres ";" removidos.
    	 * Como exemplo o arquivo "kenanPlAprovisionarNrcMulta.sql".
    	 * */
    	temp = bufferArquivo.toString().toLowerCase().trim();
    	if (temp.startsWith("begin ") || temp.startsWith("declare ") || temp.startsWith("exec ")){
    		resultado = bufferArquivo.toString();
    	}
    	else
    		resultado = bufferArquivo.toString().replace(";", "");
    	
    	return resultado;
    }   

    /**
     * Grava o conteúdo num arquivo texto.
     * @param path O arquivo que existe ou será criado.
     * @param content O conteúdo que será gravado no arquivo.
     */
    public static void setContents(String path, String content)
    {
        PrintWriter out = null;

        try
        {
            if (TextFileUtil.createFile(path))
            {
                Log.info("Arquivo criado.");
            }
            
            out = new PrintWriter(new FileOutputStream(path, true));
            out.println(content);
            out.close();
        }
        catch (FileNotFoundException fileEx)
        {
            Log.error("Arquivo não encontrado!", fileEx);
        }
        catch (Exception ex)
        {
            Log.error("Ocorreu um erro inesperado gravando o arquivo texto.", ex);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * Tenta criar o arquivo.
     * @param path
     * @return true - arquivo criado, false - arquivo já existe
     */
    private static boolean createFile(String path)
    {
        boolean success = false;

        try
        {
            File file = new File(path);

            // Create file if it does not exist
            success = file.createNewFile();

            if (success)
            {
                // File did not exist and was created
            }
            else
            {
                // File already exists
            }
        }
        catch (IOException e)
        {
            Log.error("Ocorreu um criando o arquivo.", e);
        }

        return success;
    }
}
