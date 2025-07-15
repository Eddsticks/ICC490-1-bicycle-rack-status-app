package com.icc490.bike.desktop;

import com.icc490.bike.desktop.panels.RackStatusPanel;
import com.icc490.bike.desktop.panels.MainMenuPanel;

import javax.swing.*;
import java.awt.*;

public class RackStatusApp extends JFrame {

    private ApiClient apiClient;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private RackStatusPanel rackStatusPanel;
    private MainMenuPanel mainMenuPanel;

    public RackStatusApp() {
        super("Estado del Rack de Bicicletas UFRO");
        apiClient = new ApiClient();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        rackStatusPanel = new RackStatusPanel(apiClient, this);

        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(rackStatusPanel, "RackStatus");

        add(cardPanel, BorderLayout.CENTER);

        showMainMenu();
    }

    public void showMainMenu() {
        cardLayout.show(cardPanel, "MainMenu");
        setTitle("Menú Principal - Estado de Racks de Bicicletas UFRO");
    }

    public void showRackStatus(long rackID) {
        rackStatusPanel.setRackId(rackID);
        cardLayout.show(cardPanel, "RackStatus");
        setTitle("Rack " + rackID);
        rackStatusPanel.refreshRackStatus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RackStatusApp app = new RackStatusApp();
            app.setVisible(true);
        });
    }
}