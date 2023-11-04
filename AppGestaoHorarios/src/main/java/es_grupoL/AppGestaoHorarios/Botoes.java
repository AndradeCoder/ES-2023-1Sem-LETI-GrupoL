package es_grupoL.AppGestaoHorarios;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;

public class Botoes extends JFrame {
    public Botoes() {
    	setLayout(new FlowLayout());
    	
        JButton botaocf = new JButton("Carregar ficheiro");
        JButton botaoaw = new JButton("Abre Website");

        botaocf.setPreferredSize(new Dimension(200, 100));
        botaocf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser fc = new JFileChooser();
                int f = fc.showOpenDialog(null);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File fs = fc.getSelectedFile();
                    System.out.println("Ficheiro selecionado: " + fs.getAbsolutePath());
                }
            }
        }
        );
        botaoaw.setPreferredSize(new Dimension(200, 100));
        botaoaw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("funciona");
            }
        }
        );

        add(botaocf);
        add(botaoaw);

        setTitle("JFrame");
        setSize(500, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Botoes();
    }
}

