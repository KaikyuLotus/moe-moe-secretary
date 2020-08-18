package com.kitsunecode.mms.core.entities.swing;

import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.utils.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class BootFailedFrame extends JDialog implements MouseListener {

    private static final int fullWidth = 565;
    private static final int fullHeight = 700;

    private static final int width = 565;
    private static final int height = 450;

    private static final int fontSize = 18;
    private static final Color msgBgColor = new Color(0.0f, 0.0f, 0.0f, 0.95f);
    private static final Color msgFgColor = new Color(1.0f, 1.0f, 1.0f, 0.60f);
    private static final String fontName = "Bahnschrift Light";


    public BootFailedFrame(Exception ex) {

        String error = ex.getMessage() != null ? ex.getMessage() : "Null Pointer Exception";

        setAlwaysOnTop(true);
        setLayout(null);
        setResizable(false);
        setSize(new Dimension(width, height + 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        JLabel pane = new JLabel();

        try (InputStream is = BootFailedFrame.class.getClassLoader().getResourceAsStream("images/error-waifu-lq.png")) {
            pane.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(is))));
            pane.setVerticalAlignment(SwingConstants.TOP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane.setBounds(0, 0, fullWidth, fullHeight);
        pane.setSize(new Dimension(width, height));

        String errorFull = "<html><div style='text-align: center;'><p style='padding-left: 16px; padding-right: 16px;'><font size='4'>An error occurred during the execution:</font><br>&Prime;" + error + "&Prime;<br><br><font size='2'>Click the nekogirl to close...</font></p></div></html>";
        JLabel textMsg = setupTextMessage(errorFull, error);

        add(textMsg, BorderLayout.CENTER);
        add(pane, BorderLayout.CENTER);

        if (ex instanceof StartFailedException) {
            StartFailedException sfx = (StartFailedException) ex;
            if (sfx.hasHelpUrl()) {
                String helpUrl = sfx.getHttpHelpUrl();
                textMsg.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        Util.openUrl(helpUrl);
                    }

                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        // No action
                    }

                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {
                        // No action
                    }

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) {
                        // No action
                    }

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) {
                        // No action
                    }
                });
            }
        }

        setLocationRelativeTo(null);
        addMouseListener(this);
        setModal(true);
        setVisible(true);
    }

    public final JLabel setupTextMessage(String message, String error) {
        JLabel textMsg = new JLabel(message, SwingConstants.CENTER);
        textMsg.setFont(new Font(fontName, Font.PLAIN, fontSize));
        textMsg.setBackground(msgBgColor);
        textMsg.setForeground(msgFgColor);
        textMsg.setOpaque(true);
        textMsg.setVisible(true);
        textMsg.setBounds(0, 400, 564, (error.length() >= 100) ? 180 : 120);
        textMsg.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(1, 1, 1, 0.5f)));
        return textMsg;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No action
    }
}
