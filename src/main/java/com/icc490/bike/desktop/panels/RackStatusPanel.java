package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
import com.icc490.bike.desktop.RackStatusApp;
import com.icc490.bike.desktop.gui.utils.AppColors;
import com.icc490.bike.desktop.model.Record;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RackStatusPanel extends JPanel {

    private ApiClient apiClient;
    private RackStatusApp parentApp;
    private JPanel[] hookPanels;
    private JLabel[] hookLabels;
    private JLabel panelTitleLabel;
    private JButton backButton;
    private long currentRackId;

    private static final int TOTAL_HOOKS = 4;

    public RackStatusPanel(ApiClient apiClient, RackStatusApp parentApp) {
        this.apiClient = apiClient;
        this.parentApp = parentApp;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(AppColors.SECONDARY_BLUE);

        hookPanels = new JPanel[TOTAL_HOOKS];
        hookLabels = new JLabel[TOTAL_HOOKS];

        initComponents();
        startStatusRefreshTimer();
    }

    public void setRackId(long rackId) {
        this.currentRackId = rackId;
        panelTitleLabel.setText("Estado del Rack: " + currentRackId);
    }

    private void initComponents() {

        // --- 1. Panel Superior (Título y Botón de Regreso) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        panelTitleLabel = new JLabel("Estado del Rack: ", SwingConstants.CENTER);
        panelTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panelTitleLabel.setForeground(AppColors.WHITE_TEXT);
        topPanel.add(panelTitleLabel, BorderLayout.CENTER);

        backButton = new JButton("Volver al Menú");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(AppColors.ACCENT_RED);
        backButton.setForeground(AppColors.WHITE_TEXT);
        backButton.addActionListener(e -> parentApp.showMainMenu());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        topPanel.add(buttonPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Contenedor para los Ganchos (GridLayout) ---
        JPanel hooksGridPanel = new JPanel();
        hooksGridPanel.setLayout(new GridLayout(2, 2, 20, 20));
        hooksGridPanel.setBackground(AppColors.SECONDARY_BLUE);

        Border hookBorder = BorderFactory.createLineBorder(AppColors.LIGHT_GRAY_BORDER, 2);

        for (int i = 0; i < TOTAL_HOOKS; i++) {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setPreferredSize(new Dimension(180, 140));
            panel.setBorder(BorderFactory.createCompoundBorder(hookBorder,
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            panel.setBackground(AppColors.PRIMARY_GREEN);
            panel.setOpaque(true);

            JLabel label = new JLabel("Gancho " + (i + 1), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setForeground(AppColors.DARK_TEXT);

            panel.add(label, BorderLayout.CENTER);
            hookPanels[i] = panel;
            hookLabels[i] = label;
            hooksGridPanel.add(panel);
        }
        add(hooksGridPanel, BorderLayout.CENTER);
    }


    private void startStatusRefreshTimer() {
        Timer timer = new Timer(5000, e -> refreshRackStatus());
        timer.start();
    }

    public void refreshRackStatus() {
        apiClient.getAllRecords().thenAccept(records -> {
            SwingUtilities.invokeLater(() -> {
                updateHookColors(records);
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                System.err.println("Error al cargar el estado del rack: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con la API: " + ex.getMessage() + "\nAsegúrese de que el servidor esté funcionando.",
                        "Error de Conexión",
                        JOptionPane.ERROR_MESSAGE);
                for (JPanel panel : hookPanels) {
                    panel.setBackground(Color.GRAY);
                }
            });
            return null;
        });
    }

    private void updateHookColors(List<Record> records) {
        Map<Long, Boolean> occupiedHooks = new HashMap<>();
        for (long i = 1; i <= TOTAL_HOOKS; i++) {
            occupiedHooks.put(i, false);
        }

        if (records != null) {
            for (Record record : records) {
                if (record.getRack() != null && record.getRack().getId().equals(currentRackId) &&
                        record.getHook() != null && record.getCheckOut() == null) {
                    if (record.getHook() >= 1 && record.getHook() <= TOTAL_HOOKS) {
                        occupiedHooks.put(record.getHook(), true);
                    }
                }
            }
        }

        for (int i = 0; i < TOTAL_HOOKS; i++) {
            Long hookNumber = (long) (i + 1);
            if (occupiedHooks.get(hookNumber)) {
                hookPanels[i].setBackground(AppColors.ACCENT_RED);
                hookLabels[i].setForeground(AppColors.WHITE_TEXT);
            } else {
                hookPanels[i].setBackground(AppColors.PRIMARY_GREEN);
                hookLabels[i].setForeground(AppColors.DARK_TEXT);
            }
        }
    }
}