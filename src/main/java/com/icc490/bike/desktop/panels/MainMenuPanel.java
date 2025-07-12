package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.RackStatusApp; // Importa la clase de la aplicación principal
import com.icc490.bike.desktop.gui.utils.AppColors;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private RackStatusApp parentApp;

    public MainMenuPanel(RackStatusApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new GridBagLayout());
        setBackground(AppColors.SECONDARY_BLUE);

        initComponents();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Seleccione un Rack", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(AppColors.WHITE_TEXT);

        JButton rack1Button = new JButton("Rack 1");
        rack1Button.setFont(new Font("Arial", Font.BOLD, 24));
        rack1Button.setBackground(AppColors.PRIMARY_BLUE);
        rack1Button.setForeground(AppColors.WHITE_TEXT);
        rack1Button.setPreferredSize(new Dimension(200, 80));

        rack1Button.addActionListener(e -> {
            parentApp.showRackStatus(1L);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(rack1Button, gbc);
    }
}