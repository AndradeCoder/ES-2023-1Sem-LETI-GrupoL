package es_grupoL.AppGestaoHorarios;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Enum representing the columns of a file containing information about classrooms. It provides methods to retrieve information 
 * about classrooms based on specific criteria. Each constant represents a specific column in the classrooms data.
 * 
 * @version 1.1
 */
public enum ColunasSalas {

	Edificio(0, "Edifício"), NomeSala(1, "Nome sala"), CapacidadeNormal(2, "Capacidade Normal"),
	CapacidadeExame(3, "Capacidade Exame"), NumCaracteristicas(4, "Nº características"),
	AnfiteatroAulas(5, "Anfiteatro aulas"), ApoioTecnicoEventos(6, "Apoio técnico eventos"), Arq1(7, "Arq 1"),
	Arq2(8, "Arq 2"), Arq3(9, "Arq 3"), Arq4(10, "Arq 4"), Arq5(11, "Arq 5"), Arq6(12, "Arq 6"), Arq9(13, "Arq 9"),
	BYOD(14, "BYOD (Bring Your Own Device)"), FocusGroup(15, "Focus Group"),
	HorarioSalaVisivelPortalPublico(16, "Horário sala visível portal público"),
	LaboratorioArquiteturaComputadoresI(17, "Laboratório de Arquitectura de Computadores I"),
	LaboratorioArquiteturaComputadoresII(18, "Laboratório de Arquitectura de Computadores II"),
	LaboratorioBasesEngenharia(19, "Laboratório de Bases de Engenharia"),
	LaboratorioEletronica(20, "Laboratório de Electrónica"), LaboratorioInformatica(21, "Laboratório de Informática"),
	LaboratorioJornalismo(22, "Laboratório de Jornalismo"),
	LaboratorioRedesComputadoresI(23, "Laboratório de Redes de Computadores I"),
	LaboratorioRedesComputadoresII(24, "Laboratório de Redes de Computadores II"),
	LaboratorioTelecomunicacoes(25, "Laboratório de Telecomunicações"), SalaAulasMestrado(26, "Sala Aulas Mestrado"),
	SalaAulasMestradoPlus(27, "Sala Aulas Mestrado Plus"), SalaNEE(28, "Sala NEE"), SalaProvas(29, "Sala Provas"),
	SalaReuniao(30, "Sala Reunião"), SalaArquitetura(31, "Sala de Arquitectura"),
	SalaAulasNormal(32, "Sala de Aulas normal"), Videoconferencia(33, "videoconferência"), Atrio(34, "Átrio");

	private final int index;
	private final String columnName;
	private static FileToTable classroomsFileToTable = new FileToTable(new File("CaracterizaçãoDasSalas.csv")); // Ficheiro das salas

	ColunasSalas(int index, String columnName) {
		this.index = index;
		this.columnName = columnName;
	}

	/**
	 * Gets the name of the column.
	 *
	 * @return The name of the column.
	 */
	public String getColumnName() {
		return columnName;
	}

	public static FileToTable getClassroomsFileToTable() {
		return classroomsFileToTable;
	}
	
	/**
	 * Gets the enum constant associated with the specified column name.
	 *
	 * @param columnName The name of the column.
	 * @return The enum constant associated with the specified column name.
	 * @throws IllegalArgumentException if no enum constant is found with the
	 *                                  specified column name.
	 */
	public static ColunasSalas getConstant(String columnName) {
		for (ColunasSalas coluna : values()) {
			if (coluna.columnName.equals(columnName))
				return coluna;
		}
		throw new IllegalArgumentException("No enum constant with columnName: " + columnName);
	}

	/**
	 * Gets a list of all enum constants.
	 *
	 * @return A list of all enum constants.
	 */
	public static List<ColunasSalas> constantsList() {
		List<ColunasSalas> columnsList = new ArrayList<>();
		ColunasSalas[] columnsArray = ColunasSalas.values();

		for (ColunasSalas value : columnsArray) {
			columnsList.add(value);
		}
		return columnsList;
	}
	
	/**
	 * Gets a list of values (names) of all enum constants.
	 *
	 * @return A list of values (names) of all enum constants.
	 */
	public static List<String> valuesList() {
		List<String> columnsList = new ArrayList<>();
		ColunasSalas[] columnsArray = ColunasSalas.values();

		for (ColunasSalas value : columnsArray) {
			columnsList.add(value.columnName);
		}
		return columnsList;
	}

	public static boolean columnDataType(ColunasSalas column) {
		if (column.equals(ColunasSalas.CapacidadeNormal) || column.equals(ColunasSalas.CapacidadeExame) 
				|| column.equals(ColunasSalas.NumCaracteristicas))
			return true;
		return false;
	} 

	/**
	 * Gets information from the classrooms file based on a specified column and its corresponding field value, 
	 * in the form of a Map of file lines.
	 * 
	 * @param classroomsColumn Column to be used for filtering the file.
	 * @param fieldValue Value of the field to be matched with. 
	 * @return The Map that contains lines of the classrooms file.
	 */
	public static Map<Integer, ArrayList<String>> getClassroomInfo(ColunasSalas classroomsColumn, String fieldValue) {
		Map<Integer, ArrayList<String>> classroomInfo = new HashMap<>();
		Map<Integer, ArrayList<String>> classroomsFileMap = classroomsFileToTable.readCSV();

		int mapIndex = 0;
		for (Map.Entry<Integer, ArrayList<String>> fileLine : classroomsFileMap.entrySet()) {
			if (fileLine.getValue().get(classroomsColumn.index).equals(fieldValue)) {
				classroomInfo.put(mapIndex, fileLine.getValue()); // Lista que contem a linha da sala no ficheiro
				mapIndex++;
			}
		}
		return classroomInfo;
	}

	/**
	 * Gets a List of strings representing the column value of a given Map.
	 *
	 * @param classroomInfo Map that can contain multiple lines of the classrooms file. Should be the return of 
	 * 		  a call of the method {@link ColunasSalas#getClassroomInfo(ColunasSalas, String)}
	 * @param columnValue   {@code ColunasSalas} that contains the index of the column value in the given list.
	 * @return The List of strings representing the column value of the given Map.
	 * 
	 */
	public static List<String> getColumnValue(Map<Integer, ArrayList<String>> classroomInfo, ColunasSalas columnValue) {
		List<String> list = new ArrayList<>();

		for (Entry<Integer, ArrayList<String>> fileLine : classroomInfo.entrySet()) {
			list.add(fileLine.getKey(), fileLine.getValue().get(columnValue.index)); // Lista que contem a linha da sala no ficheiro
		}
		return list;
	}
}
