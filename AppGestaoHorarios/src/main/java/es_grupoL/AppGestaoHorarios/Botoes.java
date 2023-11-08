package es_grupoL.AppGestaoHorarios;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;


import javax.swing.JFileChooser;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import org.apache.commons.io.FileUtils;


public class Botoes extends JFrame {

	private JTextField urlRemoto;
	public JTextField getUrlRemoto() {
		return urlRemoto;
	}

	public void setUrlRemoto(JTextField urlRemoto) {
		this.urlRemoto = urlRemoto;
	}

	public Botoes() {
		

		final JCheckBox checkBoxLocal = new JCheckBox("Ficheiro Local");
		final JCheckBox checkBoxRemoto = new JCheckBox("Ficheiro remoto");
		urlRemoto = new JTextField(20);
		JButton fileButton = new JButton("Carregar ficheiro");
		JButton websiteButton = new JButton("Abre Website");

		ButtonGroup CheckBoxes = new ButtonGroup();

		CheckBoxes.add(checkBoxLocal);
		CheckBoxes.add(checkBoxRemoto);

		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(checkBoxLocal.isSelected()) {

					JFileChooser fc = new JFileChooser();
					int f = fc.showOpenDialog(null);
					if (f == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fc.getSelectedFile();
						String filePath = selectedFile.getAbsolutePath();
						System.out.println(filePath);
						if (filePath.toLowerCase().endsWith(".csv")) {
							System.out.println("Ficheiro CSV selecionado: " + filePath);
						} else {
							System.out.println("Por favor selecione um ficheiro .csv");
						}}
				}
				if(checkBoxRemoto.isSelected()) {

					carregaFicheiroRemoto();

				} else {
					System.out.println("Selecione uma das opções para carregar o ficheiro");
				}

			}
		});





		websiteButton.setPreferredSize(new Dimension(200, 100));
		websiteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("funciona");
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

	public static void main(String[] args) {
		new Botoes();
	}

	public void carregaFicheiroRemoto() {

		String url = this.getUrlRemoto().getText();

		if(url.endsWith(".csv")) {
			try {
				FileUtils.copyURLToFile(new URL(url), new File("Remote File.csv"));
				System.out.println("Download completo");
			} catch(IOException e){
				e.printStackTrace();
			}
		} else {
			System.out.println("Este ficheiro remoto não é do tipo .csv");
		}
	}
}
