package com.proativo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.proativo.util.log.Log;

public class ExcelUtil {

	/**
	 * Método responsável por criar novo documento XSSFWorkbook, utilizando como base arrays de dados,
	 * armazenando-o em um diretório parametrizado
	 * 
	 * @author G0030353 - Rhuan Pablo Ribeiro Krum
	 * @since 27/02/2014
	 * TODO: CORRIGIR JAVADOC
	 * @param List<Object[]> dataArray, String sheetName, String reportName, String reportDirectory
	 * @return File relatorioRessarcimento
	 */
	public static File criarRelatorioXlsx(Object[] cabecalho, List<Object[]> linhas, String nomeAba, String nomeRelatorio, String diretorioRelatorio){
	
		File relatorio = null;
		
		try{
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			criarAbaPlanilha(cabecalho, linhas, nomeAba, workbook);

			relatorio = new File(diretorioRelatorio + nomeRelatorio+".xlsx");
			
			FileOutputStream out = new FileOutputStream(relatorio);
		    workbook.write(out);
		    out.close();
		} catch (FileNotFoundException e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} catch (IOException e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} catch (Exception e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} 
		return relatorio;
	}
	
	public static File criarPlanilhaXlsx(Object[] cabecalho, List<Object[]> linhas, String nomeAba, String nomeRelatorio, String diretorioRelatorio){
		
		File relatorio = null;
		
		try{
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			criarAbaPlanilha(cabecalho, linhas, nomeAba, workbook);

			relatorio = new File(diretorioRelatorio + nomeRelatorio+".xlsx");
			
			FileOutputStream out = new FileOutputStream(relatorio);
		    workbook.write(out);
		    out.close();
		} catch (FileNotFoundException e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} catch (IOException e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} catch (Exception e) {
			Log.error("Falha ao criar arquivo Excel", e);
		} 
		return relatorio;
	}
	
	/**
	 * Método responsável por criar uma nova aba em um documento XSSFWorkbook carregado em memória
	 * <br><br>
	 * <b>Obs. Não adicionar o método sheet.autoSizeColumn(); em outras partes do método, pois em planilhas maiores causa o tratamento do programa</b>
	 * 
	 * @author G0030353 - Rhuan Pablo Ribeiro Krum
	 * @param cabecalho 
	 * @since 27/02/2014
	 * TODO: CORRIGIR JAVADOC
	 * @param List<Object[]> dataArray, String sheetName, XSSFWorkbook workbook
	 */
	public static void criarAbaPlanilha(Object[] cabecalho, List<Object[]> linhas, String nomeAba, XSSFWorkbook workbook){
		XSSFSheet sheet = workbook.createSheet(nomeAba);

		Integer rowNum = 0;
		
		Row row = null;
		Cell cell = null;
		
		XSSFDataFormat dataFormat = workbook.createDataFormat();
		
		// Cria estilo Default 
		XSSFCellStyle styleDefault = workbook.createCellStyle();
		styleDefault.setDataFormat(dataFormat.getFormat(""));
		
		// Cria estilo para colunas do tipo "Integer"
		XSSFCellStyle styleIntFormat = workbook.createCellStyle();
		styleIntFormat.setDataFormat(dataFormat.getFormat("0"));
		
		// Cria estilo para colunas do tipo "Date"
		XSSFCellStyle styleDateFormat = workbook.createCellStyle();
		styleDateFormat.setDataFormat(dataFormat.getFormat("m/d/yy h:mm"));
		
		// Cria estilo para colunas do tipo "Double" 
		XSSFCellStyle styleCurrencyFormat = workbook.createCellStyle();
	    styleCurrencyFormat.setDataFormat(dataFormat.getFormat("R$ #,##0.00"));
		
		// Cria estilo para colunas do tipo "BigDouble" 
		XSSFCellStyle styleCurrencyRessFormat = workbook.createCellStyle();
		styleCurrencyRessFormat.setDataFormat(dataFormat.getFormat("R$ #,##0.00000"));
		
	    // Cria estilo para Cabeçalho
	    XSSFCellStyle styleHeader = workbook.createCellStyle();
	    XSSFFont headerFont = workbook.createFont();
	    styleHeader.setFillForegroundColor(IndexedColors.BLUE.getIndex());
	    headerFont.setColor(IndexedColors.WHITE.getIndex());
	    styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    styleHeader.setFont(headerFont);

	    // Cria cabeçalho
		row = sheet.createRow(rowNum++);
		for(int cellNum = 0; cellNum < cabecalho.length; cellNum++){
			Object cellData = cabecalho[cellNum];
			cell = row.createCell(cellNum);
			cell.setCellStyle(styleHeader);
			cell.setCellValue((String)cellData);
			// Ajusta tamanho da coluna conforme comprimento da maior linha
			sheet.autoSizeColumn(cellNum);
		}
	    
		// Cria linhas com objetos
		for(Object[] rowData : linhas){
			Row rowBody = sheet.createRow(rowNum++);
			
			// Cria células
			for(int cellNum = 0; cellNum < rowData.length; cellNum++){
				Object cellData = rowData[cellNum];
				cell = rowBody.createCell(cellNum);
				
				if(cellData instanceof Date){
					cell.setCellStyle(styleDateFormat);
					cell.setCellValue((Date)cellData);
				}
				
				else if(cellData instanceof Timestamp){
					cell.setCellStyle(styleDateFormat);
					cell.setCellValue((Date)cellData);
				}
				else if(cellData instanceof Integer){
					cell.setCellStyle(styleIntFormat);
					cell.setCellValue((Integer)cellData);
				}
				else if(cellData instanceof Long){
					cell.setCellStyle(styleDefault);
					cell.setCellValue((Long)cellData);
				}
				else if(cellData instanceof Double){
					cell.setCellStyle(styleCurrencyFormat);
					cell.setCellValue((Double)cellData);
				}
				else if(cellData instanceof BigDecimal){
					cell.setCellStyle(styleCurrencyRessFormat);
					cell.setCellValue(((BigDecimal) cellData).doubleValue());
				}
				else if(cellData instanceof String){
					cell.setCellStyle(styleDefault);
					cell.setCellValue((String)cellData);
				}
				else if(cellData == null){
					cell.setCellStyle(styleDefault);
					cell.setCellValue("");
				}
				else{
					Log.error("Formato de dado nao identificado - " + cellData.toString(), new Exception());
				}
			}
			
		}
	}
}
