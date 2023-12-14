package es_grupoL.AppGestaoHorarios;

/**
 * Utility class for generating HTML dropdown options for schedule metrics.
 * 
 * @version 1.2
 */
public class ScheduleMetrics {

	private final static String DISABLED = "Disabled";
	private final static String ENABLED = "Enabled";

	/**
	 * Generates HTML dropdown options for schedule metrics.
	 *
	 * @return StringBuilder containing HTML dropdown options.
	 */
	public static String metricOptions() {
		StringBuilder metricOptions = new StringBuilder();

		metricOptions.append("\t\t<option></option>\n");
		appendOptgroup(metricOptions, "Métricas default da aplicação", ENABLED, null);
		appendOption(metricOptions, "metrica1", "Nº de aulas em sobrelotação");
		appendOption(metricOptions, "metrica2", "Nº de alunos em aulas com sobrelotação");
		appendOption(metricOptions, "metrica3", "Nº de aulas sem caraterísticas solicitadas pelo docente");
		appendOption(metricOptions, "metrica4", "Nº de aulas em que não foi atribuída sala");
		appendOption(metricOptions, "metrica5", "Nº de características desperdiçadas nas salas atribuídas às aulas");
		appendOptgroup(metricOptions, null, "", null);
		appendOptgroup(metricOptions, "Métricas customizadas pelo user", DISABLED, "MetricasCustomizadas");
		appendOptgroup(metricOptions, null, "", null);

		return metricOptions.toString();
	}

	/**
	 * Generates HTML dropdown options for the columns of the schedule and classrooms files.
	 *
	 * @return StringBuilder containing HTML dropdown options.
	 */
	public static String columnOptions() {
		StringBuilder columnOptions = new StringBuilder();

		columnOptions.append("\t\t<option></option>\n");
		appendOptgroup(columnOptions, "Ficheiro Horário", ENABLED, null);
		for (ColunasHorario ch : ColunasHorario.constantsList()) {
			appendOption(columnOptions, ch, ch.getColumnName());
		}
		appendOptgroup(columnOptions, null, "", null);

		appendOptgroup(columnOptions, "Ficheiro Salas", ENABLED, null);
		for (ColunasSalas cs : ColunasSalas.constantsList()) {
			appendOption(columnOptions, cs, cs.getColumnName());
		}
		appendOptgroup(columnOptions, null, "", null);

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
	 * Generates HTML dropdown options for datalist.
	 *
	 * @return StringBuilder containing HTML dropdown options.
	 */
	public static String datalistOptions() {
		StringBuilder datalistOptions = new StringBuilder();

		appendDatalist(datalistOptions, "ColunasSalas");
		for (ColunasSalas value : ColunasSalas.constantsList())
			if (ColunasSalas.isColumnOfCharactheristics(value))
				appendOption(datalistOptions, value.getColumnName(), "");
		appendDatalist(datalistOptions, "");

		return datalistOptions.toString();
	}

	/**
	 * Helper function for appending an HTML dropdown option for an enum constant.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param value   The enum constant.
	 * @param text   The label (text) for the dropdown option.
	 */
	private static void appendOption(StringBuilder builder, Enum<?> value, String text) {
		builder.append("\t\t\t<option value=\"").append(value).append("\">").append(text).append("</option>\n");
	}

	/**
	 * Helper function for appending an HTML dropdown option for a string value.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param value   The string value.
	 * @param text   The label (text) for the dropdown option.
	 */
	private static void appendOption(StringBuilder builder, String value, String text) {
		builder.append("\t\t<option value=\"").append(value).append("\">").append(text).append("</option>\n");
	}

	/**
	 * Helper function for appending an HTML optgroup.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param label   The label for the optgroup.
	 * @param enabled Indicates if the optgroup is enabled or disabled.
	 * @param id      The id for the optgroup (used for disabled optgroup).
	 */
	private static void appendOptgroup(StringBuilder builder, String label, String enabled, String id) {
		switch (enabled) {
		case ENABLED: builder.append("\t\t<optgroup label=\"").append(label).append("\">\n");
		break;
		case DISABLED: builder.append("\t\t<optgroup id=\"").append(id).append("\" label=\"").append(label).append("\" disabled>");
		break;
		default: builder.append("\t\t</optgroup>\n");
		}
	}

	/**
	 * Helper function for appending an HTML datalist.
	 *
	 * @param builder The StringBuilder to append to.
	 * @param id      The id for the datalist.
	 */
	private static void appendDatalist(StringBuilder builder, String id) {
		if (!id.isBlank())
			builder.append("\t\t<datalist id=\"").append(id).append("\">\n");
		else
			builder.append("\t\t</datalist>\n");
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
