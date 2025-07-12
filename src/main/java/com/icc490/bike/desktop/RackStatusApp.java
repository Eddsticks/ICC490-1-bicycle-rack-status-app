package com.icc490.bike.desktop;

import com.icc490.bike.desktop.gui.utils.AppColors;
import com.icc490.bike.desktop.panels.RackStatusPanel;

import javax.swing.*;
import java.awt.*;

public class RackStatusApp extends JFrame {

    private ApiClient apiClient;
    private RackStatusPanel rackStatusPanel;

    public RackStatusApp() {
        super("Estado del Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look and Feel no pudo ser Establecido. Usando predeterminado.");
        }
        SwingUtilities.updateComponentTreeUI(this);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.SECONDARY_BLUE);

        rackStatusPanel = new RackStatusPanel(apiClient);
        add(rackStatusPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RackStatusApp app = new RackStatusApp();
            app.setVisible(true);
        });
    }
}