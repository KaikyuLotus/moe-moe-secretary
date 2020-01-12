package com.kitsunecode.mms.core.entities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class BootFailedFrame extends JFrame {

    private static final String[] ERRORS = new String[]{
            "That hurts! &gt;~&lt;",
            "Maybe take a look at the stack trace located at... i don't know!",
            "I hate errors... T.T",
            "Are you trying to kill us?!",
            "That was fun, let's fucking do that again!"
    };

    private int fullWidth = 565;
    private int fullHeight = 565;

    private int width = 565;
    private int height = 434;

    private int fontSize = 18;
    private Color msgBgColor = new Color(0.0f, 0.0f, 0.0f, 0.95f);
    private Color msgFgColor = Color.white;
    private String fontName = "Arial";


    public BootFailedFrame(String error) {
        super("Moe Moe Error");

        setLayout(null);
        setResizable(false);
        setSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel pane = new JLabel();

        InputStream is = BootFailedFrame.class.getClassLoader().getResourceAsStream("images/error-waifu.jpg");
        if (is != null) {
            try {
                pane.setIcon(new ImageIcon(ImageIO.read(is)));
            } catch (IOException e) {
                // Ignore
            }
        }

        pane.setBounds(0, 0, fullWidth, fullHeight);

        String errorsMsg = ERRORS[new Random().nextInt(ERRORS.length)];

        String errorFull = "<html><div style='text-align: center;'>&Prime;" + error + "&Prime;<br><br>" + errorsMsg + "</div></html>";
        JLabel textMsg = new JLabel(errorFull, SwingConstants.CENTER);
        textMsg.setFont(new Font(fontName, Font.PLAIN, fontSize));
        textMsg.setBackground(msgBgColor);
        textMsg.setForeground(msgFgColor);
        textMsg.setOpaque(true);
        textMsg.setVisible(true);
        textMsg.setBounds(0, 300, 564, 100);

        add(textMsg, BorderLayout.CENTER);
        add(pane, BorderLayout.CENTER);

        setLocationRelativeTo(null);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });

    }
}
