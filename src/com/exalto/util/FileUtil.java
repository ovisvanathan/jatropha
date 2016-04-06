package com.exalto.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.zip.Adler32;
import java.util.StringTokenizer;

/**
 * Classe com mtodos utilitrios de uso geral para manipulao de arquivos.
 *
 * @author  Breno Spindola (<a href="mailto:bcsc@cin.ufpe.br">bcsc@cin.ufpe.br</a>)
 */
public class FileUtil {
	public static final String SEPARADOR = "#";
	
	/**
	 * Obtm um valor de checksum para o arquivo especificado.
	 * O algortmo utilizado  o Adler32, de confiabilidade prxima  do
	 * CRC-32 porm
	 * muito mais rpido.
	 * Atualmente este mtodo suporta a especificao de apenas um checksum
	 * por arquivo.
	 *
	 * @param nomeArquivo  o nome do arquivo a ser calculado o valor de
	 *		checksum.
	 *
	 * @return o valor do checksum calculado.
	 *
	 * @exception Exception ser lanada caso ocorra algum erro.
	 */
	public static long calculateFileChecksum(String nomeArquivo) throws Exception {
		FileInputStream fis = new FileInputStream(nomeArquivo);
		Adler32 adler32 = new Adler32();
		adler32.reset();
		long tamanhoTotal = 0;
		try {
			while (fis.available() > 0) {
				byte[] buffer = new byte[262144];	// buffer de 256 Kb
				int numBytes = fis.read(buffer);
				if (numBytes > 0) {
					adler32.update(buffer, 0, numBytes);
					tamanhoTotal += numBytes;
				}
			}
		}
		finally {
			try {
				fis.close();
			} catch (Throwable t) {}
		}
		long checksum = adler32.getValue();
		return checksum;
	}

	/**
	 * Obtm um valor de checksum para o arquivo especificado.
	 * O algortmo utilizado  o Adler32, de confiabilidade prxima  do
	 * CRC-32 porm
	 * muito mais rpido.
	 * Atualmente este mtodo suporta a especificao de apenas um checksum
	 * por arquivo.
	 *
	 * @param nomeArquivo  o nome do arquivo a ser calculado o valor de
	 *		checksum.
	 *
	 * @return o valor do checksum calculado.
	 *
	 * @exception Exception ser lanada caso ocorra algum erro.
	 */
	public static String calculateByteChecksuns(byte[] bytes) throws Exception {
		String checksum = "";
		ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
		Adler32 adler32 = new Adler32();
		adler32.reset();
		long tamanhoTotal = 0;
		
		try {
				byte[] buffer = new byte[262144];	// buffer de 256 Kb
				int numBytes = bis.read(buffer);
				if (numBytes > 0) {
					adler32.update(buffer, 0, numBytes);
					tamanhoTotal += numBytes;
				}
		}
		finally {
			try {
				bis.close();
			} catch (Throwable t) {}
		}
		checksum = String.valueOf(adler32.getValue());		
		return checksum;
	}	
	
	public static String calculateFileChecksuns(String nomeArquivo) throws Exception {
		String checksuns = "";
		FileInputStream fis = new FileInputStream(nomeArquivo);
		long tamanhoTotal = 0;
		try {
			while (fis.available() > 0) {
				Adler32 adler32 = new Adler32();
				adler32.reset();
	
				byte[] buffer = new byte[262144];	// buffer de 256 Kb
				int numBytes = fis.read(buffer);
				if (numBytes > 0) {
					adler32.update(buffer, 0, numBytes);
					tamanhoTotal += numBytes;
					long checksum = adler32.getValue();
					checksuns +=  checksum + SEPARADOR; 
				}
			}
			
			if (checksuns.length() > 0){
				checksuns = checksuns.substring(0,checksuns.length()-1);
			}
		}
		finally {
			try {
				fis.close();
			} catch (Throwable t) {}
		}
		
		return checksuns;
	}

	/**
	 * Obtm o nome base de um arquivo a partir de um caminho completo.
	 * O nome base refere-se ao nome e extenso do arquivo.
	 *
	 * Exemplos:
	 *
	 *		caso seja passado "\temp\arquivo.txt", ser retornado "arquivo.txt"
	 *		caso seja passado "c:arquivo.txt", ser retornado "arquivo.txt"
	 *
	 * @param nomeArquivo nome completo do arquivo, podendo incluir o caminho.
	 *
	 * @return o nome base do arquivo.
	 */
	public static String baseFileName(String nomeArquivo) throws Exception {
		String nomeArq = nomeArquivo.trim();
		int inic = nomeArq.lastIndexOf(File.separator);
		if (inic < 0) {
			inic = nomeArq.lastIndexOf(':');
		}
		if (inic > 0) {
			try {
				inic++;
				nomeArq = nomeArq.substring(inic);
			} catch (Throwable t) {
				throw new Exception("Nome de arquivo invalido: '" + nomeArquivo + "'");
			}
		}
		return nomeArq;
	}



	/**
	 * Obtm um valor de checksum para um determinado arquivo armazenado em um arquivo
	 * texto com valores de checksums.
	 * O arquivo que contm o(s) valor(es) de checksum(s) deve conter uma ou mais linhas
	 * contendo valor(es) especificado(s) no seguinte formato:
	 *
	 *		checksum "nomeArquivo" = valor
	 *
	 * "nomeArquivo"  um nome de arquivo. Informaes sobre unidade e caminho do arquivo
	 * especificados neste parmetro sero ignoradas. O uso das aspas para delimitar o
	 * nome do arquivo  obrigatrio.
	 *
	 * "valor"  um valor decimal equivalente ao checksum previamente calculado para o
	 * arquivo identificado por "nomeArquivo". No devem ser utilizadas aspas para
	 * delimitar o valor.
	 *
	 * O arquivo poder conter outras linhas que no seguem este padro, porm, elas
	 * sero ignoradas.
	 *
	 * Obs.: As comparaes de nomes de arquivos no so sensveis ao caso.
	 *
	 * Abaixo segue um exemplo de contedo para este tipo de arquivo:
	 *
	 * --------------------------------------------------------
	 * b35v103_134
	 *
	 * checksum "componente.jar" = 3243241234
	 * checksum "MessagingConstantes.properties" = 23423415113
	 * checksum "DllProtocoloVisa.dll" = 546234425
	 * --------------------------------------------------------
	 *
	 * @arqValores  o nome do arquivo que contm os valores de checksum do qual se deseja
	 *		obter um desses valores.
	 *
	 * @arqChecksum  o nome do arquivo pelo qual ser procurado um valor de checksum
	 *		contido em "arqValores". Caso o caminho seja especificado junto com o nome,
	 *		este ser ignorado, sendo considerado apenas o nome e a extenso do arquivo.
	 *
	 * @apelido  um possvel apelido para arqChecksum. Caso este parmetro possua um
	 *		valor no nulo e no vazio, representar um nome alternativo, para o arquivo
	 *		especificado por arqChecksum e tambm ser utilizado na busca pelo checksum
	 *		no arquivo especificado por arqValores. Ou seja, poder haver um checksum
	 *		identificado pelo nome de "arqChecksum" ou pelo nome de "apelido" no arquivo
	 *		arqValores. O primeiro checksum identificado por um dos dois nomes ser
	 *		considerado o desejado.
	 *		Caso este parmetro seja nulo ou vazio, apenas o checksum identificado pelo
	 *		nome especificado por arqChecksum ser procurado no arquivo arqValores.
	 *
	 * @return o valor do checksum obtido a partir do parmetro "checksum"
	 *		contido no arquivo texto especificado.
	 *
	 * @exception Exception ser lanada caso ocorra algum erro.
	 */
	public static long getChecksumFromFile(
			String arqValores, String arqChecksum, String apelido)
			throws Exception {

		FileReader fr = new FileReader(arqValores);
		BufferedReader br = new BufferedReader(fr);
		String arqCS = baseFileName(arqChecksum);
		StringTokenizer st = null;
		String arqBase;
		try {
			if (apelido == null) {
				apelido = "";
			}
			else {
				apelido = FileUtil.baseFileName(apelido.toUpperCase().trim());
			}
			while (true) {
				arqBase = null;
				String linha = br.readLine();
				if (linha == null) {
					break;
				}
				linha = linha.trim().toUpperCase();
				if (!linha.startsWith("CHECKSUM")) {
					continue;
				}
				st = new StringTokenizer(linha, "=");
				if (st.countTokens() != 2) {
					continue;
				}
				try {
					String str = st.nextToken();
					int inic = str.indexOf('\"') + 1;
					int fim = str.lastIndexOf('\"');
					arqBase = FileUtil.baseFileName(str.substring(inic, fim).trim());
					if (arqBase.equals(arqCS.toUpperCase()) || arqBase.equals(apelido)) {
						break;
					}
				} catch (Throwable t) {}
			}
		}
		finally {
			try {
				br.close();
			} catch (Throwable t) {}
		}

		if (arqBase == null) {
			throw new Exception("Checksum de '" + arqCS + 
					"' nao encontrado em '" + arqValores + "'");
		}
		
		long checksum = 0;
		try {
			String strChecksum = st.nextToken().trim();
			checksum = Long.parseLong(strChecksum);
		} catch (Throwable t) {
			throw new Exception("Valor improprio para o checksum de '" + arqBase + 
					"' no arquivo '" + arqValores + "'");
		}

		return checksum;
	}
}

 
 
