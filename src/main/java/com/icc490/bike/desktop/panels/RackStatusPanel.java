package com.icc490.bike.desktop.panels;

import com.icc490.bike.desktop.ApiClient;
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
    private JPanel[] hookPanels;
    private JLabel[] hookLabels;
    private static final int TOTAL_HOOKS = 4;

    public RackStatusPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(AppColors.SECONDARY_BLUE);

        hookPanels = new JPanel[TOTAL_HOOKS];
        hookLabels = new JLabel[TOTAL_HOOKS];

        initComponents();
        startStatusRefreshTimer();
    }

    private void initComponents() {
        Border hookBorder = BorderFactory.createLineBorder(AppColors.LIGHT_GRAY_BORDER, 2);

        for (int i = 0; i < TOTAL_HOOKS; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(150, 100));
            panel.setBorder(BorderFactory.createCompoundBorder(hookBorder,
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            panel.setBackground(Color.GREEN);
            panel.setOpaque(true);

            JLabel label = new JLabel("Gancho " + (i + 1), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setForeground(AppColors.DARK_TEXT);

            panel.add(label, BorderLayout.CENTER);
            hookPanels[i] = panel;
            hookLabels[i] = label;
            add(panel);
        }
    }

    private void startStatusRefreshTimer() {
        Timer timer = new Timer(5000, e -> refreshRackStatus());
        timer.start();
        refreshRackStatus();
    }

    private void refreshRackStatus() {
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
                if (record.getHook() != null && record.getCheckOut() == null) {
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
            } else {
                hookPanels[i].setBackground(Color.GREEN);
            }
        }
    }
}