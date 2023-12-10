package es_grupoL.AppGestaoHorarios;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@code JButton} class for selecting options from a list. It displays a pop-up menu
 * with a list of options when clicked.
 * 
 * @version 1.0
 */
public class SelectButton extends JButton {

	private List<JMenuItem> listOfOptions = new ArrayList<>();
	private JPopupMenu popupMenu = new JPopupMenu();
	private boolean selectedAtLeastOnce;

	/**
	 * Constructs a SelectButton with the specified text and options.
	 *
	 * @param text    The text to be displayed on the button.
	 * @param options The list of options (columns) to be displayed in the pop-up menu.
	 */
	public SelectButton(String text, List<String> options) {
		super(text);
		init(options);
		addActionListener(createActionListener());
	}

	/**
	 * Initializes the pop-up menu with the provided options.
	 *
	 * @param options The list of options for the pop-up menu.
	 */
	private void init(List<String> options) {
		for (int i = 0; i < options.size(); i++) {
			JMenuItem option = new JMenuItem(options.get(i));
			listOfOptions.add(option);
			popupMenu.add(option);
		}
	}

	/**
	 * Creates an ActionListener for the button, displaying the pop-up menu when clicked.
	 *
	 * @return ActionListener for the button.
	 */
	protected ActionListener createActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popupMenu.show(SelectButton.this, 0, SelectButton.this.getHeight());
			}
		};
	}

	/**
	 * Sets whether the button has been selected at least once.
	 *
	 * @param selectedAtLeastOnce true if the button has been selected, false otherwise.
	 */
	public void setSelectedAtLeastOnce(boolean selectedAtLeastOnce) {
		this.selectedAtLeastOnce = selectedAtLeastOnce;
	}

	/**
	 * Checks if all buttons in the provided list have been selected at least once.
	 *
	 * @param list List of SelectButtons to be checked.
	 * @return true if all buttons have been selected, false otherwise.
	 */
	public static boolean checkAllButtonsSelectedAtLeastOnce(List<SelectButton> list) {
		for (SelectButton sb : list)
			if (!sb.selectedAtLeastOnce)
				return false;
		return true;
	}

	/**
	 * Creates a list of SelectButtons based on the columns from ColunasHorario.
	 *
	 * @return List of SelectButtons.
	 */
	public static List<SelectButton> listOfSelectButtons() {
		List<SelectButton> list = new ArrayList<>();
		List<String> columnOptions = ColunasHorario.valuesList();

		for (int i = 0; i < columnOptions.size(); i++) {
			SelectButton sb = new SelectButton((i + 1) + "ª Coluna", columnOptions);
			list.add(i, sb);
		}
		return list;
	}

	/**
	 * Adds an ActionListener to the specified option at the given index.
	 *
	 * @param listener ActionListener to be added.
	 * @param index    Index of the option in the pop-up menu.
	 */
	public void addOptionClickListener(ActionListener listener, int index) {
		if (index >= 0 && index < listOfOptions.size()) {
			listOfOptions.get(index).addActionListener(listener);
		}
	}
}
