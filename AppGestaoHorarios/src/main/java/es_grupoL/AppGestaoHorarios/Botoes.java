package es_grupoL.AppGestaoHorarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Botoes extends JFrame {
	private JTextField urlRemoto;
	private String filePath; // Store the file path for later use

	public Botoes() {
		final JCheckBox checkBoxLocal = new JCheckBox("Ficheiro Local");
		final JCheckBox checkBoxRemoto = new JCheckBox("Ficheiro remoto");
		this.urlRemoto = new JTextField(20);
		JButton fileButton = new JButton("Carregar ficheiro");
		JButton websiteButton = new JButton("Abre Website");
		ButtonGroup CheckBoxes = new ButtonGroup();

		CheckBoxes.add(checkBoxLocal);
		CheckBoxes.add(checkBoxRemoto);

		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBoxLocal.isSelected()) {
					selectLocalFile();
					File file = new File(filePath);
					FileToTable ftt = new FileToTable(file);
					Map<Integer, ArrayList<String>> map = ftt.readCSV();
					FileToTable.createHTML(map);
				} else if (checkBoxRemoto.isSelected()) {
					selectRemoteFile();
					File file = new File(filePath);
					FileToTable ftt = new FileToTable(file);
					Map<Integer, ArrayList<String>> map = ftt.readCSV();
					FileToTable.createHTML(map);
				} else {
					System.out.println("Selecione uma das opções para carregar o ficheiro");
				}
			}
		});

		websiteButton.setPreferredSize(new Dimension(200, 100));
		websiteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openWebsite();
			}
		});

		add(checkBoxLocal);
		add(checkBoxRemoto);
		add(urlRemoto);
		add(fileButton);
		add(websiteButton);

		setTitle("JFrame");
		setSize(1000, 300);
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void openWebsite() {
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

	public void selectLocalFile() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showOpenDialog(null);

		if (f == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			filePath = selectedFile.getAbsolutePath();
			System.out.println(filePath);
			if (filePath.toLowerCase().endsWith(".csv")) {
				System.out.println("Ficheiro CSV selecionado: " + filePath);
			} else {
				System.out.println("Por favor selecione um ficheiro .csv");
			}
		}
	}

	public void selectRemoteFile() {
		String url = this.urlRemoto.getText();

		if (url.endsWith(".csv")) {
			try {
				if (url.startsWith("https://github.com/")) {
					url = url.replace("https://github.com/", "https://raw.githubusercontent.com/");
				}

				File fi = new File("RemoteFile.csv");
				FileUtils.copyURLToFile(new URL(url), fi);
				filePath = fi.getAbsolutePath();
				System.out.println("Download completo");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Este ficheiro remoto não é do tipo .csv");
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Botoes();
			}
		});
	}
}
