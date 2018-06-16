package com.proativo.util.vo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.proativo.util.log.Log;

/** Classe pai dos beans dos cenários, onde irá possuir os atributos/métodos comuns à todos cenários.
 */
public abstract class AbstractBeanVo 
{
	/**
	 * Constante que representa a flag OK com o valor 1.
	 */
	public static final Integer OK = 1;
	
	/**
	 * Constante que representa a flag NOT OK com o valor 0;
	 */
	public static final Integer NOT_OK = 0;  
	
	/**
	 * Constante que representa a flag OK com o valor 1.
	 */
	public static final String STR_OK = "OK";
	
	/**
	 * Constante que representa a flag NOT OK com o valor 0;
	 */
	public static final String STR_NOT_OK = "NOT_OK"; 
	
	/**
	 * Atributo que armazena o objeto SimpleDateFormat para formatar as datas. 
	 */
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Atributo que armazena o objeto SimpleDateFormat para formatar os timestamps. 
	 */
	private static SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
	/**
	 * Atributo que armazena o resultado da validação do cenário, podendo ser OK (1) ou NOT OK (0).
	 */
	protected String result;
	
	/**
	 * Atributo que armazena uma mensagem de erro quando alguma condição para tratamento não foi satisfeita.
	 */
	protected String log;
	
	/**
	 * Atributo que armazena o id do banco em que o cliente se encontra. Numero positivo iniciado em 1.
	 */
	protected int kenanDbId;
	
	/**
	 * Construto do Bean VO.
	 * @param cust Parametro de qual cust o bean se encontra.
	 */
	public AbstractBeanVo(int kenanDbId) {
		this.kenanDbId = kenanDbId;
	}
	
	@Deprecated
	public AbstractBeanVo() {
		
	}
	
	public int getkenanDbId() {
		return kenanDbId;
	}

	public void setkenanDbId(int kenanDbId) {
		this.kenanDbId = kenanDbId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	} 

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	/** 
	 * Método que busca os nomes dos atributos 
     * 
     * @param targetClass Classe de tipo AbstractBeanVO contendo os dados
	 * @return Retorna um array de String com os nomes dos atributos.
	 */
	public String[] getAttributesNames(AbstractBeanVo targetClass) {
        Field attributes[] = targetClass.getClass().getDeclaredFields();
        String result[] = new String[attributes.length];
        
        for(int i = 0; i < attributes.length; i++) {
            result[i] = attributes[i].getName().toString();
        }
        
        return result;
    }
	
	/** 
	 * Método que busca os nomes dos atributos 
     * 
     * @param targetClass Classe de tipo AbstractBeanVO contendo os dados 
     * @param columnsExclude ArrayList contendo os nomes das colunas para excluir da planilha
	 * @return Retorna um array de String com os nomes dos atributos.
	 */ 
	public String[] getAttributesNames(AbstractBeanVo targetClass, List<String> columnsExclude) { 
        Field attributes[] = targetClass.getClass().getDeclaredFields();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < attributes.length; i++) {
        	if (columnsExclude != null && columnsExclude.contains(attributes[i].getName())) {
        		continue;
        	}
        	list.add(attributes[i].getName().toString());
        }
        String result[] = new String[list.size()];
        result = (String[])list.toArray(result);
        
        return result;
    }
	
	/** 
	 * Método que busca os valores dos atributos
     * 
     * @param targetClass Classe de tipo AbstractBeanVO contendo os dados
	 * @return Retorna um array de Object com os valores dos atributos.
	 */
	public Object[] getAttributesValues(AbstractBeanVo targetClass) {
        Method methods[] = targetClass.getClass().getDeclaredMethods();
        Field attributes[] = targetClass.getClass().getDeclaredFields();
        List<Method> arrMethods = new ArrayList<Method>();
        
        for(int i = 0; i < attributes.length; i++) {
            for(int y = 0; y < methods.length; y++) {
                if(!methods[y].getName().equalsIgnoreCase("get" + attributes[i].getName().toString())) 
                    continue;
                arrMethods.add(methods[y]);
                break;
            }
        }

        Object result[] = new Object[methods.length];
        
        try {
            for(int i = 0; i < arrMethods.size(); i++) {
            	if (((Method)arrMethods.get(i)).getReturnType().equals(Date.class)) {
            		
            		Date date = (Date)arrMethods.get(i).invoke(targetClass);            		
            		result[i] = (date != null) ? sdf.format(date): null; 
            		
            	} else if (((Method)arrMethods.get(i)).getReturnType().equals(Timestamp.class)) {
            		
            		Timestamp date = (Timestamp)arrMethods.get(i).invoke(targetClass);
            		
            		if (date != null) {
            			result[i] = sdfTimeStamp.format(date);
            		} else {
            			result[i] = "";
            		}
            	} else {
            		result[i] = arrMethods.get(i).invoke(targetClass);
            	}
            }
        }
        catch(IllegalAccessException ex) {
            Log.error("Erro obtendo dados dos atributos", ex);
        }
        catch(InvocationTargetException itex) {
            Log.error("Erro obtendo dados dos atributos", itex);
        }
        return result;
    }
	 
	/** 
	 * Método que busca os valores dos atributos
     * 
     * @param targetClass Classe de tipo AbstractBeanVO contendo os dados
     * @param columnsExclude ArrayList contendo os nomes das colunas para excluir da planilha
	 * @return Retorna um array de Object com os valores dos atributos.
	 */
	public Object[] getAttributesValues(AbstractBeanVo targetClass, List<String> columnsExclude) {
        Method methods[] = targetClass.getClass().getDeclaredMethods();
        Field attributes[] = targetClass.getClass().getDeclaredFields();
        List<Method> arrMethods = new ArrayList<Method>();
        for(int i = 0; i < attributes.length; i++) {
        	if (columnsExclude != null && columnsExclude.contains(attributes[i].getName())) {
        		continue;
        	}
        	
            for(int y = 0; y < methods.length; y++) {
                if(!methods[y].getName().equalsIgnoreCase("get" + attributes[i].getName().toString()))
                    continue;
                arrMethods.add(methods[y]);
                break;
            }

        }

        Object result[] = new Object[attributes.length];
        try {
            for(int i = 0; i < arrMethods.size(); i++) {
            	if (((Method)arrMethods.get(i)).getReturnType().equals(Date.class)) {
            		Date date = (Date)arrMethods.get(i).invoke(targetClass);
            		
            		if (date != null) {
            			result[i] = sdf.format(date); 
            		} else {
            			result[i] = "";
            		}
            	} else if (((Method)arrMethods.get(i)).getReturnType().equals(Timestamp.class)) {
            		Timestamp date = (Timestamp)arrMethods.get(i).invoke(targetClass);
            		if (date != null) {
            			result[i] = sdfTimeStamp.format(date); 
            		} else {
            			result[i] = "";
            		}
            	}
            	else {
            		Object o = arrMethods.get(i).invoke(targetClass);
            		result[i] = (o == null) ? "" : o;
            	}
            }
        }
        catch(IllegalAccessException ex) {
            Log.error("Erro obtendo dados dos atributos", ex);
        }
        catch(InvocationTargetException itex) {
            Log.error("Erro obtendo dados dos atributos", itex);
        }        
        return result;
    }
}