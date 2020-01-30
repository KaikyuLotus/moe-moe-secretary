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

public class BootFailedFrame extends JDialog {

    private int fullWidth = 565;
    private int fullHeight = 700;

    private int width = 565;
    private int height = 450;

    private int fontSize = 18;
    private Color msgBgColor = new Color(0.0f, 0.0f, 0.0f, 0.95f);
    private Color msgFgColor = new Color(1.0f, 1.0f, 1.0f, 0.60f);
    private String fontName = "Bahnschrift Light";


    public BootFailedFrame(Exception ex) {

        String error = ex.getMessage();

        setLayout(null);
        setResizable(false);
        setSize(new Dimension(width, height + 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        JLabel pane = new JLabel();

        InputStream is = BootFailedFrame.class.getClassLoader().getResourceAsStream("images/error-waifu-lq.png");
        if (is != null) {
            try {
                pane.setIcon(new ImageIcon(ImageIO.read(is)));
                pane.setVerticalAlignment(SwingConstants.TOP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pane.setBounds(0, 0, fullWidth, fullHeight);
        pane.setSize(new Dimension(width, height));

        String errorFull = "<html><div style='text-align: center;'><p style='padding-left: 16px; padding-right: 16px;'><font size='4'>An error occurred during the execution:</font><br>&Prime;" + error + "&Prime;<br><br><font size='2'>Click anywhere to close...</font></p></div></html>";
        JLabel textMsg = new JLabel(errorFull, SwingConstants.CENTER);
        textMsg.setFont(new Font(fontName, Font.PLAIN, fontSize));
        textMsg.setBackground(msgBgColor);
        textMsg.setForeground(msgFgColor);
        textMsg.setOpaque(true);
        textMsg.setVisible(true);
        textMsg.setBounds(0, 400, 564, (error.length() >= 100) ? 180 : 120);
        textMsg.setBorder(BorderFactory.createMatteBorder(2,2,2,2, new Color(1,1,1, 0.5f)));

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
                    public void mousePressed(MouseEvent mouseEvent) { }

                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) { }

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) { }

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) { }
                });
            }
        }

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                dispose();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) { }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) { }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) { }

            @Override
            public void mouseExited(MouseEvent mouseEvent) { }
        });

        setLocationRelativeTo(null);

        setModal(true);
        setVisible(true);

    }
}
