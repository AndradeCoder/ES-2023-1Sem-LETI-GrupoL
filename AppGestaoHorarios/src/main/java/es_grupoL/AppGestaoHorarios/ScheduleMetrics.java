package es_grupoL.AppGestaoHorarios;

/**
 * Utility class for generating HTML dropdown options for schedule metrics.
 * 
 * @version 1.1
 */
public class ScheduleMetrics {
	
	public static String metricOptions() {
		StringBuilder metricOptions = new StringBuilder();

		metricOptions.append("\t\t<option></option>\n");
		appendOption(metricOptions, "filtro1", "Nº de aulas em sobrelotação");
		appendOption(metricOptions, "filtro2", "Nº de alunos em aulas com sobrelotação");
		appendOption(metricOptions, "filtro3", "Nº de aulas sem caraterísticas solicitadas pelo docente");
		appendOption(metricOptions, "filtro4", "Nº de aulas em que não foi atribuída sala");
		appendOption(metricOptions, "filtro5", "Nº de características desperdiçadas nas salas atribuídas às aulas");
		appendOption(metricOptions, "filtro6", "Duração da aula");

		return metricOptions.toString();
	}

	/**
	 * Generates HTML dropdown options for the columns of the schedule and classrooms files.
	 *
	 * @return StringBuilder containing HTML dropdown options.
	 */
	public static String columnOptions() {
		StringBuilder columnOptions = new StringBuilder();

		columnOptions.append("\t\t<option></option>\n");	// Opção vazia
		for (ColunasHorario ch : ColunasHorario.constantsList()) {
			appendOption(columnOptions, ch, ch.getColumnName());
		}
		for (ColunasSalas cs : ColunasSalas.constantsList()) {
			appendOption(columnOptions, cs, cs.getColumnName());
		}
		return columnOptions.toString();
	}

	/**
	 * Generates HTML dropdown options for operators.
	 *
	 * @return StringBuilder containing HTML dropdown options.
	 */
	public static String operatorOptions() {
		StringBuilder operatorOptions = new StringBuilder();

		operatorOptions.append("\t\t<option></option>\n");
		appendOption(operatorOptions, "||", "OR");
		appendOption(operatorOptions, "&&", "AND");
		appendOption(operatorOptions, "+", "+");
		appendOption(operatorOptions, "-", "-");
		appendOption(operatorOptions, "==", "=");
		appendOption(operatorOptions, "&lt;", "&lt;");
		appendOption(operatorOptions, "&lt;=", "&lt;=");
		appendOption(operatorOptions, "&gt;", "&gt;");
		appendOption(operatorOptions, "&gt;=", "&gt;=");
		appendOption(operatorOptions, "!=", "!=");

		return operatorOptions.toString();
	}

	/**
	 * Helper function for appending an HTML dropdown option for an enum constant.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param value   The enum constant.
	 * @param label   The label (text) for the dropdown option.
	 */
	private static void appendOption(StringBuilder builder, Enum<?> value, String label) {
		builder.append("\t\t<option value=\"").append(value).append("\">").append(label).append("</option>\n");
	}

	/**
	 * Helper function for appending an HTML dropdown option for a string value.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param value   The string value.
	 * @param label   The label (text) for the dropdown option.
	 */
	private static void appendOption(StringBuilder builder, String value, String label) {
		builder.append("\t\t<option value=\"").append(value).append("\">").append(label).append("</option>\n");
	}

	/**
	 * The main method for testing the HTML dropdown options generation.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(columnOptions());
		System.out.print(operatorOptions());
		System.out.print("OLAAAAAA");
	}
}
