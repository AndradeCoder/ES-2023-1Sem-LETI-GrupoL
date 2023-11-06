package es_grupoL.AppGestaoHorarios;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class FileToTable {

	private boolean autoMapped = false;

	public static Map<Integer, ArrayList<String>> readCSV(String file, FileToTable ftt) {
		Map<Integer, ArrayList<String>> fileLineInfo = new HashMap<>();

		CSVFormat format = CSVFormat.EXCEL.withDelimiter(';');
		if (hasHeader(file, ftt))
			format = format.withFirstRecordAsHeader();

		try (CSVParser parser = CSVParser.parse(new FileReader(file), format)) {
			for (CSVRecord record : parser) {
				List<String> colunasHorario = record.toList();
				fileLineInfo.put((int) record.getRecordNumber(), (ArrayList<String>) colunasHorario);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// System.out.println(file);
		return fileLineInfo;
	}

	public boolean isAutoMapped() {
		return autoMapped;
	}

	public void setAutoMapped(boolean autoMapped) {
		this.autoMapped = autoMapped;
	}

	private static boolean hasHeader(String file, FileToTable ftt) {
		List<String> colunasHorario = ColunasHorario.valuesList();
		try (CSVParser parser = CSVParser.parse(new FileReader(file), CSVFormat.EXCEL.withDelimiter(';'))) {
			for (CSVRecord record : parser)
				if (record.toList().containsAll(colunasHorario)) {
					ftt.setAutoMapped(true);
					return true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		FileToTable ftt = new FileToTable();
		FileToTable a = new FileToTable();
		FileToTable.readCSV("C:\\Users\\leoth\\Downloads\\HorarioDeExemplo.csv", ftt); // Teste temporário e este
																						// caminho só funciona no meu pc
																						// como é óbvio
		Map<Integer, ArrayList<String>> map = FileToTable.readCSV("C:\\Users\\leoth\\Downloads\\HorarioDeExemplo.csv",
				ftt); // Teste temporário e este caminho só funciona no meu pc como é óbvio

		for (String s : map.get(1))
			System.out.println(s);
		System.out.println("AutoMapped: " + ftt.isAutoMapped());
		System.out.println("AutoMapped: " + a.isAutoMapped());
	}
}
