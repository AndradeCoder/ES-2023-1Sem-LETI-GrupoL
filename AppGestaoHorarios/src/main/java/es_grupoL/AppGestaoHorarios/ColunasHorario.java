package es_grupoL.AppGestaoHorarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Enum representing the columns in the user inputed schedule file. Each constant represents
 * a specific column in the schedule data.
 * 
 * @version 1.3
 */
public enum ColunasHorario {

	Curso(0, "Curso"), UnidadeCurricular(1, "Unidade Curricular"), Turno(2, "Turno"), Turma(3, "Turma"),
	InscritosNoTurno(4, "Inscritos no turno"), DiaDaSemana(5, "Dia da semana"),
	HoraInicioDaAula(6, "Hora início da aula"), HoraFimDaAula(7, "Hora fim da aula"), DataDaAula(8, "Data da aula"),
	CaracteristicasDaSalaPedidaParaAAula(9, "Características da sala pedida para a aula"),
	SalaAtribuídaÀAula(10, "Sala atribuída à aula");

	private final String columnName; // Não faz diferença ser final aqui, apenas serve para indicar que não se deve mudar
	private int index;	// Estes valores são os default, podendo variar para cada constante consuante o mapeamento

	/**
	 * Constructs a new enum constant with the specified column name.
	 * 
	 * @param index
	 * @param columnName The name of the column.
	 */
	ColunasHorario(int index, String columnName) {
		this.columnName = columnName;
		this.index = index;
	}

	/**
	 * Gets the name of the column.
	 *
	 * @return The name of the column.
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Gets the enum constant associated with the specified column name.
	 *
	 * @param columnName The name of the column.
	 * @return The enum constant associated with the specified column name.
	 * @throws IllegalArgumentException if no enum constant is found with the
	 *                                  specified column name.
	 */
	public static ColunasHorario getConstant(String columnName) {
		for (ColunasHorario coluna : values()) {
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
	public static List<ColunasHorario> constantsList() {
		List<ColunasHorario> columnsList = new ArrayList<>();
		ColunasHorario[] columnsArray = ColunasHorario.values();

		for (ColunasHorario value : columnsArray) {
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
		ColunasHorario[] columnsArray = ColunasHorario.values();

		for (ColunasHorario value : columnsArray) {
			columnsList.add(value.columnName);
		}
		return columnsList;
	}

	/**
	 * Changes the index of the {@code ColunasHorario} constants. 
	 */
	private static void changeIndex() {
		List<String> mappedColumns = Botoes.getInstance().getMappedColumnsInOrder();
		for (ColunasHorario ch : constantsList())
			ch.index = mappedColumns.indexOf(ch.columnName);
	}

	/**
	 * Gets information from the schedule file based on a specified column and its corresponding field value, 
	 * in the form of a Map of file lines.
	 *
	 * @param userFileMap Map containing schedule data.
	 * @param scheduleColumn Column to be used for filtering the file.
	 * @param fieldValue Value of the field to be matched with.
	 * @return Map containing information from the schedule file.
	 */
	public static Map<Integer, ArrayList<String>> getScheduleInfo(ColunasHorario scheduleColumn, String fieldValue) {
		Map<Integer, ArrayList<String>> scheduleInfo = new HashMap<>();
		changeIndex();

		int mapIndex = 0;
		for (Entry<Integer, ArrayList<String>> fileLine : Botoes.getInstance().getUserFileMap().entrySet()) {
			if (fileLine.getValue().get(scheduleColumn.index).equals(fieldValue)) {
				scheduleInfo.put(mapIndex, fileLine.getValue()); // Mapa que contem linhas das salas no ficheiro
				mapIndex++;
			}
		}
		return scheduleInfo;
	}

	/**
	 * Gets a list of strings representing the column value of a given map.
	 *
	 * @param scheduleInfo Map that can contain multiple lines of the schedule file. Should be the return of 
	 * 		  a call of the method {@link ColunasHorario#getScheduleInfo(ColunasHorario, String)}
	 * @param columnValue  Column value to be retrieved.
	 * @return List of strings representing the column value.
	 */
	public static List<String> getColumnValue(Map<Integer, ArrayList<String>> scheduleInfo, ColunasHorario columnValue) {
		List<String> list = new ArrayList<>();
		changeIndex();	// Não há de fazer diferença, é só para garantir que asneiras não acontecem

		for (Entry<Integer, ArrayList<String>> fileLine : scheduleInfo.entrySet()) {
			list.add(fileLine.getKey(), fileLine.getValue().get(columnValue.index));
		}
		return list;
	}
}
