package es_grupoL.AppGestaoHorarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * The `Botoes` class represents the main application window for schedule
 * management. It provides options for loading schedule data from local or
 * remote sources, visualizing the schedule in a web browser, and applying
 * filters. Users can load schedule data from either a local CSV file or a
 * remote URL. The loaded schedule can be converted to HTML format for viewing
 * in a web browser. Filters can be applied to the schedule data, and quality
 * analysis criteria can be defined by the user.
 */
public class Botoes extends JFrame {
	private JTextField urlRemoto; // URL remoto a ser inserido pelo utilizador, para ficheiros remotos
	private String filePath; // Guardar o caminho do ficheiro para usar nas funções da classe FileToTable
	private List<String> mappedColumnsInOrder = new ArrayList<>(); // Lista ordenada dos campos mapeados. Ex: mappedColumnsInOrder.get(0) = coluna 1
	private FileToTable ftt;
	private Map<Integer, ArrayList<String>> map;

	/**
	 * Constructs the main application window, initializes components, and sets up
	 * event listeners.
	 */
	public Botoes() {
		final JCheckBox checkBoxLocal = new JCheckBox("Ficheiro Local");
		final JCheckBox checkBoxRemoto = new JCheckBox("Ficheiro remoto");
		this.urlRemoto = new JTextField(20); 
		this.urlRemoto.setToolTipText("Inserir o url do ficheiro remoto");
		JButton fileButton = new JButton("Selecione o ficheiro local");
		JButton websiteButton = new JButton("Abre Website");
		ButtonGroup checkBoxes = new ButtonGroup();

		// Ações das checkboxes
		checkBoxes.add(checkBoxLocal);
		checkBoxes.add(checkBoxRemoto);
		checkBoxLocal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Selecione o ficheiro local");
				urlRemoto.setEnabled(false);
			}
		});
		checkBoxRemoto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Download do ficheiro remoto");
				urlRemoto.setEnabled(true);
			}
		});

		// Ações do botão abir/carregar ficheiro
		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBoxLocal.isSelected()) {
					selectLocalFile();
				} else if (checkBoxRemoto.isSelected()) {
					selectRemoteFile();
				} else {
					System.out.println("Selecione uma das opções para carregar o ficheiro");
				}

				if (filePath != null) {
					File file = new File(filePath);
					ftt = new FileToTable(file);
					map = ftt.readCSV();

					if (ftt.isColumnsMapped()) // Se o mapeamento é automático (ficheiro tem header) então o botão do website aparece
						websiteButton.setVisible(true);
					else {
						
					}
				}
			}
		});

		// Ações do botão de abrir o website
		websiteButton.setVisible(false); // O botão só ficará visível quando o mapeamento estiver tratado
		websiteButton.setPreferredSize(new Dimension(200, 100));
		websiteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openWebsite();
			}
		});

		setLayout(new FlowLayout());
		add(checkBoxLocal);
		add(checkBoxRemoto);
		add(this.urlRemoto);
		add(fileButton);
		add(websiteButton);

		setTitle("JFrame");
		setSize(15000, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Opens the schedule visualization in a web browser using the generated HTML
	 * file.
	 */
	private void openWebsite() {	
		this.ftt.createHTML(map);
		Desktop desk = Desktop.getDesktop();
		String filepath = System.getProperty("user.dir") + "/ScheduleApp.html";
		filepath = filepath.replace("\\", "/");
		
		try {
			desk.browse(new java.net.URI("file://" + filepath));
		} catch (IOException | URISyntaxException e1) {
			e1.printStackTrace();
		}
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
	}

	/**
	 * Allows the user to select a local CSV file using a file chooser dialog.
	 */
	private void selectLocalFile() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showOpenDialog(null);

		if (f == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			filePath = selectedFile.getAbsolutePath();
			if (filePath.toLowerCase().endsWith(".csv")) {
				System.out.println("Ficheiro CSV selecionado: " + filePath);
			} else {
				System.out.println("Por favor selecione um ficheiro .csv");
			}
		}
	}

	/**
	 * Allows the user to select a remote CSV file by providing a URL. If the URL is
	 * from GitHub, it is modified to access the raw content.
	 */
	private void selectRemoteFile() {
		String url = this.urlRemoto.getText();
		String urlOriginal = url;
		File fi = new File("RemoteFile.csv"); // Criar ficheiro temporário para guardar o seu file path em filePath

		if (url.endsWith(".csv")) {
			try {
				if (url.startsWith("https://github.com/") && url.contains("/blob/")) {
					url = url.replace("https://github.com/", "https://raw.githubusercontent.com/");
					url = url.replace("/blob/", "/");
				} else if (url.startsWith("https://github.com/")) {
					url = url.replace("https://github.com/", "https://raw.githubusercontent.com/");
				}

				FileUtils.copyURLToFile(new URL(url), fi);
				filePath = fi.getAbsolutePath();
				System.out.println("Download completo do ficheiro remoto: " + urlOriginal);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Este ficheiro remoto não é do tipo .csv");
		}
		fi.deleteOnExit(); // Apagar ficheiro temporário
	}

	/**
	 * The main method that initializes and runs the application.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Botoes();
			}
		});
	}
}
