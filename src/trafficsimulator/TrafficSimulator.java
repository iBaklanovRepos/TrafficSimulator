package trafficsimulator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

public class TrafficSimulator implements ActionListener, Runnable {

    /**
     * Константы графического интерфейса
     */
    private static final Font H1 = new Font("Verdana", Font.PLAIN, 26);
    private static final Font H2 = new Font("Verdana", Font.PLAIN, 20);
    private static final Font H3 = new Font("Verdana", Font.PLAIN, 12);
    private static final Color BACKGROUND_COL = new Color(189, 189, 189);
    private static final Color BTN_HOVER = new Color(210, 210, 210);;
    private static final Color BTN_COL = new Color(150, 150, 150);

    public static JFrame frame = new JFrame("Traffic Simulator");



    private JButton startButton = new JButton("Начать");
    private JButton stopButton = new JButton("Пауза");
    private JButton restartButton = new JButton("В главное меню");
    private JTextField timerTextField = new JTextField("00:00");
    private JSlider slider = new JSlider(JSlider.HORIZONTAL, 40, 80, 60);
    private Container buttonContainer = new Container();
    private Container bottomContainer = new Container();

    /**
     * Параметры моделирования
     */
    public static boolean running = false;
    private String roadMode = "highway";
    public static int directions = 1;
    private static int forwardLanes = 1;
    private static int backwardLanes = 1;
    public static String trafficType = "determined";
    public static int determInterval = 3;
    private static int trafficLightPhase = 20000;
    public static String randomType = "normal";
    public static String randomTypeSpeed = "normal";
    public static int mo = 10;
    public static int moSpeed = 10;
    public static int d = 10;
    public static int dSpeed = 10;
    public static int leftBorder = 1;
    public static int leftBorderSpeed = 1;
    public static int rightBorder = 2;
    public static int rightBorderSpeed = 2;
    public static double intensity = 1;
    public static int tempIntensity = 1;
    public static int tempIntensitySpeed = 1;
    public static Highway highway;
    public static Tunnel tunnel;
    private String valueType = "generation";

    public TrafficSimulator() {
        setImages();
        startButton.setBackground(BTN_COL);
        startButton.setFont(H2);
        stopButton.setBackground(BTN_COL);
        stopButton.setFont(H2);
        restartButton.setBackground(BTN_COL);
        restartButton.setFont(H2);
        timerTextField.setBackground(BTN_COL);
        initStartFrame();

//        initHighwayFrame();
//        roadMode = "tunnel";
//        initTunnelFrame();
    }

    private void initStartFrame(){
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND_COL);

        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 0, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        JLabel label = new JLabel("Выберите тип автодороги");
        label.setFont(H1);

        content.add(label, constraints);

        constraints.insets = new Insets(0, 0, 0, 10);

        constraints.gridy = 1;
        constraints.gridwidth = 1;

        final JButton tunnel = new JButton("Туннель", new ImageIcon(new ImageIcon(getClass().getResource("../tunnel.png")).getImage().getScaledInstance(400,400, Image.SCALE_SMOOTH)));
        tunnel.setHorizontalTextPosition(SwingConstants.CENTER);
        tunnel.setVerticalTextPosition(SwingConstants.BOTTOM);
        tunnel.setFont(H2);
        tunnel.setBackground(BACKGROUND_COL);
        tunnel.setBorder(null);

        content.add(tunnel, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(0, 10, 0, 0);

        final JButton highway = new JButton("Автострада", new ImageIcon(new ImageIcon(getClass().getResource("../highway.png")).getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH)));
        content.add(highway, constraints);
        highway.setHorizontalTextPosition(SwingConstants.CENTER);
        highway.setVerticalTextPosition(SwingConstants.BOTTOM);
        highway.setFont(H2);
        highway.setBackground(BACKGROUND_COL);
        highway.setBorder(null);

        ActionListener choiceListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                if(button.equals(tunnel)){
                    roadMode = "tunnel";
                    frame.remove(content);
                    frame.repaint();
                    initTrafficTypeFrame();

                }else if(button.equals(highway)){
                    roadMode = "highway";
                    frame.remove(content);
                    frame.repaint();
                    initHighwaySettingsFrame();
                }
            }
        };

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(BTN_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(BACKGROUND_COL);
            }
        };

        highway.addActionListener(choiceListener);
        tunnel.addActionListener(choiceListener);
        highway.addMouseListener(adapter);
        tunnel.addMouseListener(adapter);

        final JButton infoFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../info.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(infoFrame)){
                    frame.remove(content);
                    initInfoFrame();//
                }
            }
        };

        infoFrame.setBorder(null);
        infoFrame.setBackground(BACKGROUND_COL);
        infoFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0,0,100,400);
        content.add(infoFrame, constraints);

        frame.add(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initInfoFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Самарский университет");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);
        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Лабораторный практикум по дисциплине");
        bodyLabel.setFont(H2);
        content.add(bodyLabel, constraints);

        constraints.insets = new Insets(50, 150, 0, 0);
        JLabel subjectLabel = new JLabel("Технологии программирования");
        subjectLabel.setFont(H2);
        content.add(subjectLabel, constraints);

        constraints.insets = new Insets(100, 150, 0, 0);
        JLabel themeLabel = new JLabel("Тема: Система моделирования транспортного потока");
        themeLabel.setFont(H2);
        content.add(themeLabel, constraints);

        constraints.insets = new Insets(180, 150, 0, 0);
        JLabel developersLabel = new JLabel("Разработчики: студенты группы 6403-090301D");
        developersLabel.setFont(H2);
        content.add(developersLabel, constraints);

        constraints.insets = new Insets(230, 150, 0, 0);
        JLabel kalininLabel = new JLabel("Калинин А.А. Email: kalinin.alexandr99@mail.ru");
        kalininLabel.setFont(H2);
        content.add(kalininLabel, constraints);

        constraints.insets = new Insets(260, 150, 0, 0);
        JLabel baklanovLabel = new JLabel("Бакланов И.Д. Email: ibaklanov99@gmail.com");
        baklanovLabel.setFont(H2);
        content.add(baklanovLabel, constraints);

        final JButton htmlButton = new JButton("Открыть руководство пользователя");

        constraints.gridy = 1;
        constraints.insets = new Insets(450, 150, 0, 0);
        JLabel versionLabel = new JLabel("Версия 1.0");
        versionLabel.setFont(H3);
        content.add(versionLabel, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(480, 150, 0, 0);
        JLabel rootLabel = new JLabel("Все права защищены");
        rootLabel.setFont(H3);
        content.add(rootLabel, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(500, 150, 0, 0);
        JLabel samaraLabel = new JLabel("Самара 2020");
        samaraLabel.setFont(H3);
        content.add(samaraLabel, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../null.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initStartFrame();
                }
                else if(e.getSource().equals(htmlButton)){
                    frame.remove(content);
                    File f = new File("D:\\4 курс 1 семестр\\Технологии программирования\\Программа\\Traffic Sim\\TrafficSimulator\\images\\index.html");
//                    System.out.println(getClass().getResource("../"));
                    Desktop dt = Desktop.getDesktop();
                    try {
                        dt.open(f);
                        initInfoFrame();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(550,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(550,100,0,0);
        content.add(nextFrame, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(330, 150, 0, 0);
        htmlButton.addActionListener(footerButtonsListener);
        content.add(htmlButton, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initHighwaySettingsFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 80, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 3;

        JLabel headLabel = new JLabel("Параметры автодороги");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.insets = new Insets(10,80,0,0);

        JLabel bodyImg = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("../highway.png")).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));

        content.add(bodyImg, constraints);

        constraints.gridy = 2;
        constraints.insets = new Insets(40,80,0,0);

        JLabel bodyLabel = new JLabel("Количество направлений");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        ButtonGroup buttonGroupCenter = new ButtonGroup();
        final JToggleButton button1 = new JToggleButton("1");
        final JToggleButton button2 = new JToggleButton("2");

        ActionListener centerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(button1)){
                    directions = 1;
                }
                if(e.getSource().equals(button2)){
                    directions = 2;
                }
            }
        };

        button1.setBorder(null);
        button1.setFont(H2);
        button1.setPreferredSize(new Dimension(70,25));
        button1.setBackground(BTN_COL);
        button1.setSelected(true);
        button1.addActionListener(centerButtonsListener);

        button2.setBorder(null);
        button2.setFont(H2);
        button2.setPreferredSize(new Dimension(70,25));
        button2.setBackground(BTN_COL);
        button2.addActionListener(centerButtonsListener);

        buttonGroupCenter.add(button1);
        buttonGroupCenter.add(button2);
        constraints.gridy = 3;
        constraints.insets = new Insets(10,80,0,100);

        content.add(button1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(10,100,0,0);
        content.add(button2, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 80, 0,0);

        JLabel bottomLabel = new JLabel("Количество полос на каждом направлении");
        bottomLabel.setFont(H2);

        content.add(bottomLabel, constraints);

        ButtonGroup buttonGroupBottom = new ButtonGroup();
        final JToggleButton bottomButton1 = new JToggleButton("1");
        final JToggleButton bottomButton2 = new JToggleButton("2");
        final JToggleButton bottomButton3 = new JToggleButton("3");

        ActionListener bottomButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(bottomButton1)){
                    forwardLanes = 1;
                    backwardLanes = 1;
                }
                if(e.getSource().equals(bottomButton2)){
                    forwardLanes = 2;
                    backwardLanes = 2;
                }
                if(e.getSource().equals(bottomButton3)){
                    forwardLanes = 3;
                    backwardLanes = 3;
                }
            }
        };

        bottomButton1.setBorder(null);
        bottomButton1.setFont(H2);
        bottomButton1.setPreferredSize(new Dimension(70,25));
        bottomButton1.setBackground(BTN_COL);
        bottomButton1.setSelected(true);
        bottomButton1.addActionListener(bottomButtonsListener);

        bottomButton2.setBorder(null);
        bottomButton2.setFont(H2);
        bottomButton2.setPreferredSize(new Dimension(70,25));
        bottomButton2.setBackground(BTN_COL);
        bottomButton2.addActionListener(bottomButtonsListener);

        bottomButton3.setBorder(null);
        bottomButton3.setFont(H2);
        bottomButton3.setPreferredSize(new Dimension(70,25));
        bottomButton3.setBackground(BTN_COL);
        bottomButton3.addActionListener(bottomButtonsListener);

        buttonGroupBottom.add(bottomButton1);
        buttonGroupBottom.add(bottomButton2);
        buttonGroupBottom.add(bottomButton3);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.insets = new Insets(10, 0, 0,100);

        content.add(bottomButton1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(10, 200, 0,200);

        content.add(bottomButton2, constraints);

        constraints.gridx = 2;
        constraints.insets = new Insets(10, 200, 0,0);

        content.add(bottomButton3, constraints);


        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initTrafficTypeFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initStartFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.insets = new Insets(130,0,0,800);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(130,900,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initTrafficTypeFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 80, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        JLabel headLabel = new JLabel("Выберите тип генерируемого потока");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        ButtonGroup buttonGroupCenter = new ButtonGroup();
        final JToggleButton button1 = new JToggleButton("Детерминированный");
        final JToggleButton button2 = new JToggleButton("Случайный");

        ActionListener centerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(button1)){
                    trafficType = "determined";
                }
                if(e.getSource().equals(button2)){
                    trafficType = "random";
                }
            }
        };

        button1.setBorder(null);
        button1.setFont(H2);
        button1.setPreferredSize(new Dimension(300,45));
        button1.setBackground(BTN_COL);
        button1.setSelected(true);
        button1.addActionListener(centerButtonsListener);

        button2.setBorder(null);
        button2.setFont(H2);
        button2.setPreferredSize(new Dimension(300,45));
        button2.setBackground(BTN_COL);
        button2.addActionListener(centerButtonsListener);

        buttonGroupCenter.add(button1);
        buttonGroupCenter.add(button2);
        constraints.gridy = 2;
        constraints.insets = new Insets(130,80,0,400);

        content.add(button1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(130,400,0,0);
        content.add(button2, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if(trafficType.equals("determined")){
                        initDetermTrafficSettingsFrame();
                    }else if(trafficType.equals("random")){
                        initRandomTrafficSettingsFrame();
                    }
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initStartFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(350,0,0,800);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(350,900,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initSpeedRandomTrafficSettingsFrame() {
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(50, 80, 10, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        JLabel headLabel = new JLabel("Выберите закон распределения случайной величины");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);
        JLabel speedLabel = new JLabel("Настройки генерации значения скорости");
        speedLabel.setFont(H1);
        constraints.insets = new Insets(0, 80, 100, 0);
        content.add(speedLabel, constraints);

        ButtonGroup buttonGroupCenter = new ButtonGroup();
        final JToggleButton button1 = new JToggleButton("Нормальный");
        final JToggleButton button2 = new JToggleButton("Равномерный");
        final JToggleButton button3 = new JToggleButton("Показательный");

        ActionListener centerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(button1)){
                    randomTypeSpeed = "normal";
                }
                if(e.getSource().equals(button2)){
                    randomTypeSpeed = "uniform";
                }
                if(e.getSource().equals(button3)){
                    randomTypeSpeed = "exponential";
                }
            }
        };

        button1.setBorder(null);
        button1.setFont(H2);
        button1.setPreferredSize(new Dimension(300,45));
        button1.setBackground(BTN_COL);
        button1.setSelected(true);
        button1.addActionListener(centerButtonsListener);

        button2.setBorder(null);
        button2.setFont(H2);
        button2.setPreferredSize(new Dimension(300,45));
        button2.setBackground(BTN_COL);
        button2.addActionListener(centerButtonsListener);

        button3.setBorder(null);
        button3.setFont(H2);
        button3.setPreferredSize(new Dimension(300,45));
        button3.setBackground(BTN_COL);
        button3.addActionListener(centerButtonsListener);

        buttonGroupCenter.add(button1);
        buttonGroupCenter.add(button2);
        buttonGroupCenter.add(button3);
        constraints.gridy = 3;
        constraints.insets = new Insets(0,0,0,600);

        content.add(button1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(0,25,0,0);
        content.add(button2, constraints);

        constraints.gridx = 0;
        constraints.insets = new Insets(0,650,0,0);
        content.add(button3, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if(randomTypeSpeed.equals("normal")){
                        initSpeedNormalDistributionFrame();
                    }else if(randomTypeSpeed.equals("uniform")){
                        initSpeedUniformDistributionFrame();
                    }
                    else if(randomTypeSpeed.equals("exponential")){
                        initSpeedExponentialDistributionFrame();
                    }
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initTrafficTypeFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(350,0,0,800);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(350,900,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /*private void initSpeedTrafficTypeFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 80, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        JLabel headLabel = new JLabel("Выберите тип генерации скорости");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        ButtonGroup buttonGroupCenter = new ButtonGroup();
        final JToggleButton button1 = new JToggleButton("Детерминированный");
        final JToggleButton button2 = new JToggleButton("Случайный");

        ActionListener centerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(button1)){
                    trafficType = "determined";
                }
                if(e.getSource().equals(button2)){
                    trafficType = "random";
                }
            }
        };

        button1.setBorder(null);
        button1.setFont(H2);
        button1.setPreferredSize(new Dimension(300,45));
        button1.setBackground(BTN_COL);
        button1.setSelected(true);
        button1.addActionListener(centerButtonsListener);

        button2.setBorder(null);
        button2.setFont(H2);
        button2.setPreferredSize(new Dimension(300,45));
        button2.setBackground(BTN_COL);
        button2.addActionListener(centerButtonsListener);

        buttonGroupCenter.add(button1);
        buttonGroupCenter.add(button2);
        constraints.gridy = 2;
        constraints.insets = new Insets(130,80,0,400);

        content.add(button1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(130,400,0,0);
        content.add(button2, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if(trafficType.equals("determined")){
                        initDetermTrafficSettingsFrame();
                    }else if(trafficType.equals("random")){
                        initRandomTrafficSettingsFrame();
                    }
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initHighwaySettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(350,0,0,800);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(350,900,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }*/

    private void initDetermTrafficSettingsFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Детерминированный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);


        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Интервал времени, через который будут появляться машины");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        constraints.insets = new Insets(50, 150, 0, 0);

        constraints.gridy = 2;

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
        final JTextField textField = new JTextField();
        slider.setPreferredSize(new Dimension(400,50));
        slider.setBackground(BACKGROUND_COL);
        slider.setMajorTickSpacing(19);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textField.setText(source.getValue() + "\nсекунд");
                determInterval = source.getValue();
            }
        });

        content.add(slider, constraints);

        constraints.insets = new Insets(20, 150, 0, 0);

        constraints.gridy = 3;


        textField.setText(slider.getValue() + "\nсекунд");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(140,30));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(H2);
        textField.setBackground(BTN_COL);

        content.add(textField, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initTrafficTypeFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /*private void initSpeedDetermTrafficSettingsFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Детерминированный скорость");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);


        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Интервал времени, через который будут появляться машины");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        constraints.insets = new Insets(50, 150, 0, 0);

        constraints.gridy = 2;

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
        final JTextField textField = new JTextField();
        slider.setPreferredSize(new Dimension(400,50));
        slider.setBackground(BACKGROUND_COL);
        slider.setMajorTickSpacing(19);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textField.setText(source.getValue() + "\nсекунд");
                determInterval = source.getValue();
            }
        });

        content.add(slider, constraints);

        constraints.insets = new Insets(20, 150, 0, 0);

        constraints.gridy = 3;


        textField.setText(slider.getValue() + "\nсекунд");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(140,30));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(H2);
        textField.setBackground(BTN_COL);

        content.add(textField, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initSpeedTrafficTypeFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initTrafficTypeFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }*/

    /**
     * Выбор закона распределения (рис. 25)
     * в переменную randomType записать "normal", "uniform", "exponential" в зависимости от выбора
     */
    private void initRandomTrafficSettingsFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(50, 80, 10, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        JLabel headLabel = new JLabel("Выберите закон распределения случайной величины");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);
        JLabel speedLabel = new JLabel("Настройки генерации потока автомобилей");
        speedLabel.setFont(H1);
        constraints.insets = new Insets(0, 80, 100, 0);
        content.add(speedLabel, constraints);

        ButtonGroup buttonGroupCenter = new ButtonGroup();
        final JToggleButton button1 = new JToggleButton("Нормальный");
        final JToggleButton button2 = new JToggleButton("Равномерный");
        final JToggleButton button3 = new JToggleButton("Показательный");

        ActionListener centerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(button1)){
                    randomType = "normal";
                }
                if(e.getSource().equals(button2)){
                    randomType = "uniform";
                }
                if(e.getSource().equals(button3)){
                    randomType = "exponential";
                }
            }
        };

        button1.setBorder(null);
        button1.setFont(H2);
        button1.setPreferredSize(new Dimension(300,45));
        button1.setBackground(BTN_COL);
        button1.setSelected(true);
        button1.addActionListener(centerButtonsListener);

        button2.setBorder(null);
        button2.setFont(H2);
        button2.setPreferredSize(new Dimension(300,45));
        button2.setBackground(BTN_COL);
        button2.addActionListener(centerButtonsListener);

        button3.setBorder(null);
        button3.setFont(H2);
        button3.setPreferredSize(new Dimension(300,45));
        button3.setBackground(BTN_COL);
        button3.addActionListener(centerButtonsListener);

        buttonGroupCenter.add(button1);
        buttonGroupCenter.add(button2);
        buttonGroupCenter.add(button3);
        constraints.gridy = 3;
        constraints.insets = new Insets(0,0,0,600);

        content.add(button1, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(0,25,0,0);
        content.add(button2, constraints);

        constraints.gridx = 0;
        constraints.insets = new Insets(0,650,0,0);
        content.add(button3, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if(randomType.equals("normal")){
                        initNormalDistributionFrame();
                    }else if(randomType.equals("uniform")){
                        initUniformDistributionFrame();
                    }
                    else if(randomType.equals("exponential")){
                        initExponentialDistributionFrame();//**************************************
                    }
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initStartFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(350,0,0,800);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(350,900,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Настройка нормального закона распределения (рис. 26)
     * в переменную mo записать мат ожидание в переменную d дисперсию
     */
    private void initNormalDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Нормальный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Задайте параметры математического ожидания и дисперсии");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        JLabel bodyLabelM = new JLabel("Мат. ожидание");
        bodyLabelM.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 0, 0, 300);
        content.add(bodyLabelM, constraints);

        constraints.insets = new Insets(50, 0, 0, 300);
        constraints.gridy = 3;

        final JSlider sliderM = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
        final JTextField textFieldMo = new JTextField();
        sliderM.setPreferredSize(new Dimension(320,50));
        sliderM.setBackground(BACKGROUND_COL);
        sliderM.setMajorTickSpacing(30);
        sliderM.setPaintTicks(true);
        sliderM.setPaintLabels(true);
        sliderM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textFieldMo.setText(source.getValue() + "\n");
                mo = source.getValue();
            }
        });

        content.add(sliderM, constraints);

        constraints.insets = new Insets(20, 0, 0, 300);

        constraints.gridy = 4;

        textFieldMo.setText(sliderM.getValue() + "\n");
        textFieldMo.setEditable(false);
        textFieldMo.setPreferredSize(new Dimension(70,30));
        textFieldMo.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldMo.setFont(H2);
        textFieldMo.setBackground(BTN_COL);

        content.add(textFieldMo, constraints);

        JLabel bodyLabelD = new JLabel("Дисперсия");
        bodyLabelD.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 500, 0, 0);
        content.add(bodyLabelD, constraints);

        constraints.insets = new Insets(50, 520, 0, 0);
        constraints.gridy = 3;

        final JSlider sliderD = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
        final JTextField textFieldD = new JTextField();
        sliderD.setPreferredSize(new Dimension(320,50));
        sliderD.setBackground(BACKGROUND_COL);
        sliderD.setMajorTickSpacing(30);
        sliderD.setPaintTicks(true);
        sliderD.setPaintLabels(true);
        sliderD.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textFieldD.setText(source.getValue() + "\n");
                d = source.getValue();
            }
        });

        content.add(sliderD, constraints);

        constraints.insets = new Insets(20, 500, 0, 0);

        constraints.gridy = 4;

        textFieldD.setText(sliderD.getValue() + "\n");
        textFieldD.setEditable(false);
        textFieldD.setPreferredSize(new Dimension(70,30));
        textFieldD.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldD.setFont(H2);
        textFieldD.setBackground(BTN_COL);

        content.add(textFieldD, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();////////////////////////////////////////////////////////////////////////////

                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initRandomTrafficSettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initSemaphoreFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Укажите значение длительности светофорных фаз");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        constraints.insets = new Insets(50, 150, 0, 0);

        constraints.gridy = 2;

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 30, 99, 66);
        final JTextField textField = new JTextField();
        slider.setPreferredSize(new Dimension(400,50));
        slider.setBackground(BACKGROUND_COL);
        slider.setMajorTickSpacing(23);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                trafficLightPhase = source.getValue()*1000;
                textField.setText( source.getValue() + "\nсекунд");
            }
        });

        content.add(slider, constraints);
        constraints.insets = new Insets(20, 150, 0, 0);
        constraints.gridy = 3;


        textField.setText(slider.getValue() + "\nсекунд");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(140,30));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(H2);
        textField.setBackground(BTN_COL);

        content.add(textField, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initTunnelFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Настройка равномерного закона распределения (рис. 27)
     * в переменную leftBorder записать левую границу, в переменную rightBorder правую границу
     */
    private void initUniformDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Равномерный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Задайте значение для левой и правой границы интервала");
        bodyLabel.setFont(H2);
        content.add(bodyLabel, constraints);
        //______________________

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        final JLabel exceptionLabel = new JLabel("Правая граница интервала не может быть меньше или равно левой");
        exceptionLabel.setFont(new Font("Serif", Font.BOLD, 18));
        exceptionLabel.setForeground(Color.RED);
        exceptionLabel.setVisible(false);

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initRandomTrafficSettingsFrame();
                }
            }
        };

        JLabel bodyLabelRightBorder = new JLabel("Правая граница");
        bodyLabelRightBorder.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 500, 0, 0);
        content.add(bodyLabelRightBorder, constraints);

        constraints.insets = new Insets(50, 520, 0, 0);
        constraints.gridy = 3;

        final JSlider sliderRightBorder = new JSlider(JSlider.HORIZONTAL, 2, 20, 2);
        final JTextField textFieldRightBorder = new JTextField();
        sliderRightBorder.setPreferredSize(new Dimension(300,50));
        sliderRightBorder.setBackground(BACKGROUND_COL);
        sliderRightBorder.setMajorTickSpacing(1);
        sliderRightBorder.setPaintTicks(true);
        sliderRightBorder.setPaintLabels(true);
        sliderRightBorder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider sourceRight = (JSlider) e.getSource();
                textFieldRightBorder.setText(sourceRight.getValue() + "\nсекунд");
                rightBorder = sourceRight.getValue();
                if (sourceRight.getValue() <= leftBorder){
                    nextFrame.setEnabled(false);
                    exceptionLabel.setVisible(true);
                }
                if (sourceRight.getValue() > leftBorder){
                    nextFrame.setEnabled(true);
                    exceptionLabel.setVisible(false);
                }
            }
        });

        content.add(sliderRightBorder, constraints);

        constraints.insets = new Insets(20, 500, 0, 0);

        constraints.gridy = 4;

        textFieldRightBorder.setText(sliderRightBorder.getValue() + "\nсекунд");
        textFieldRightBorder.setEditable(false);
        textFieldRightBorder.setPreferredSize(new Dimension(140,30));
        textFieldRightBorder.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldRightBorder.setFont(H2);
        textFieldRightBorder.setBackground(BTN_COL);

        content.add(textFieldRightBorder, constraints);

        JLabel bodyLabelLeftBorder = new JLabel("Левая граница");
        bodyLabelLeftBorder.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 0, 0, 300);
        content.add(bodyLabelLeftBorder, constraints);

        constraints.insets = new Insets(50, 0, 0, 300);
        constraints.gridy = 3;

        final JSlider sliderLeftBorder = new JSlider(JSlider.HORIZONTAL, 1, 19, 1);
        final JTextField textFieldLeftBorder = new JTextField();
        sliderLeftBorder.setPreferredSize(new Dimension(300,50));
        sliderLeftBorder.setBackground(BACKGROUND_COL);
        sliderLeftBorder.setMajorTickSpacing(1);
        sliderLeftBorder.setPaintTicks(true);
        sliderLeftBorder.setPaintLabels(true);
        sliderLeftBorder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                sliderRightBorder.setValue(source.getValue()+1);
                textFieldLeftBorder.setText(source.getValue() + "\nсекунд");
                leftBorder = source.getValue();
            }
        });

        content.add(sliderLeftBorder, constraints);

        constraints.insets = new Insets(20, 0, 0, 300);

        constraints.gridy = 4;

        textFieldLeftBorder.setText(sliderLeftBorder.getValue() + "\nсекунд");
        textFieldLeftBorder.setEditable(false);
        textFieldLeftBorder.setPreferredSize(new Dimension(140,30));
        textFieldLeftBorder.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldLeftBorder.setFont(H2);
        textFieldLeftBorder.setBackground(BTN_COL);

        content.add(textFieldLeftBorder, constraints);

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(200, 150, 0, 0);
        content.add(exceptionLabel, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Настройка показательного закона распределения (рис. 28)
     * в переменную intensity записать интенсивность
     */
    private void initExponentialDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Показательный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Укажите значение интенсивности транспортного потока");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        constraints.insets = new Insets(50, 150, 0, 0);

        constraints.gridy = 2;

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        Hashtable labelTable = new Hashtable();
        labelTable.put( 1, new JLabel("0.01") );
        labelTable.put(2, new JLabel("0.02") );
        labelTable.put(3, new JLabel("0.03") );
        labelTable.put(4, new JLabel("0.04") );
        labelTable.put(5, new JLabel("0.05") );
        labelTable.put(6, new JLabel("0.06") );
        labelTable.put(7, new JLabel("0.07") );
        labelTable.put(8, new JLabel("0.08") );
        labelTable.put(9, new JLabel("0.09") );
        labelTable.put(10, new JLabel("0.1") );
        slider.setLabelTable(labelTable);
        final JTextField textField = new JTextField();
        slider.setPreferredSize(new Dimension(400,50));
        slider.setBackground(BACKGROUND_COL);
        slider.setMajorTickSpacing(19);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        final HashMap<Integer, Double> intensityHM = new HashMap<Integer, Double>();
        intensityHM.put(1, 0.01);
        intensityHM.put(2, 0.02);
        intensityHM.put(3, 0.03);
        intensityHM.put(4, 0.04);
        intensityHM.put(5, 0.05);
        intensityHM.put(6, 0.06);
        intensityHM.put(7, 0.07);
        intensityHM.put(8, 0.08);
        intensityHM.put(9, 0.09);
        intensityHM.put(10, 0.1);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                tempIntensity = source.getValue();
                Double intensity = intensityHM.get(tempIntensity);
                textField.setText( intensity + "\nГц");
            }
        });

        content.add(slider, constraints);
        constraints.insets = new Insets(20, 150, 0, 0);
        constraints.gridy = 3;


        textField.setText("0,0" + slider.getValue() + "\nГц");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(140,30));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(H2);
        textField.setBackground(BTN_COL);

        content.add(textField, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initRandomTrafficSettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }


    private void initSpeedNormalDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Нормальный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Задайте параметры математического ожидания и дисперсии");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        JLabel bodyLabelM = new JLabel("Мат. ожидание");
        bodyLabelM.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 0, 0, 300);
        content.add(bodyLabelM, constraints);

        constraints.insets = new Insets(50, 0, 0, 300);
        constraints.gridy = 3;

        final JSlider sliderM = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
        final JTextField textFieldMo = new JTextField();
        sliderM.setPreferredSize(new Dimension(320,50));
        sliderM.setBackground(BACKGROUND_COL);
        sliderM.setMajorTickSpacing(30);
        sliderM.setPaintTicks(true);
        sliderM.setPaintLabels(true);
        sliderM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textFieldMo.setText(source.getValue() + "\n");
                moSpeed = source.getValue();
            }
        });

        content.add(sliderM, constraints);

        constraints.insets = new Insets(20, 0, 0, 300);

        constraints.gridy = 4;

        textFieldMo.setText(sliderM.getValue() + "\n");
        textFieldMo.setEditable(false);
        textFieldMo.setPreferredSize(new Dimension(70,30));
        textFieldMo.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldMo.setFont(H2);
        textFieldMo.setBackground(BTN_COL);

        content.add(textFieldMo, constraints);

        JLabel bodyLabelD = new JLabel("Дисперсия");
        bodyLabelD.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 500, 0, 0);
        content.add(bodyLabelD, constraints);

        constraints.insets = new Insets(50, 520, 0, 0);
        constraints.gridy = 3;

        final JSlider sliderD = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
        final JTextField textFieldD = new JTextField();
        sliderD.setPreferredSize(new Dimension(320,50));
        sliderD.setBackground(BACKGROUND_COL);
        sliderD.setMajorTickSpacing(30);
        sliderD.setPaintTicks(true);
        sliderD.setPaintLabels(true);
        sliderD.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                textFieldD.setText(source.getValue() + "\n");
                dSpeed = source.getValue();
            }
        });

        content.add(sliderD, constraints);

        constraints.insets = new Insets(20, 500, 0, 0);

        constraints.gridy = 4;

        textFieldD.setText(sliderD.getValue() + "\n");
        textFieldD.setEditable(false);
        textFieldD.setPreferredSize(new Dimension(70,30));
        textFieldD.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldD.setFont(H2);
        textFieldD.setBackground(BTN_COL);

        content.add(textFieldD, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if (roadMode == "tunnel"){
                        initSemaphoreFrame();
                    }
                    else initHighwayFrame();////////////////////////////////////////////////////////////////////////////

                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Настройка равномерного закона распределения (рис. 27)
     * в переменную leftBorder записать левую границу, в переменную rightBorder правую границу
     */
    private void initSpeedUniformDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Равномерный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Задайте значение для левой и правой границы интервала");
        bodyLabel.setFont(H2);
        content.add(bodyLabel, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        final JLabel exceptionLabel = new JLabel("Правая граница интервала не может быть меньше или равно левой");
        exceptionLabel.setFont(new Font("Serif", Font.BOLD, 18));
        exceptionLabel.setForeground(Color.RED);
        exceptionLabel.setVisible(false);

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if (roadMode == "tunnel"){
                        initSemaphoreFrame();
                    }
                    else initHighwayFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }
            }
        };

        JLabel bodyLabelRightBorder = new JLabel("Правая граница");
        bodyLabelRightBorder.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 500, 0, 0);
        content.add(bodyLabelRightBorder, constraints);

        constraints.insets = new Insets(50, 520, 0, 0);
        constraints.gridy = 3;

        final JSlider sliderRightBorder = new JSlider(JSlider.HORIZONTAL, 2, 20, 2);
        final JTextField textFieldRightBorder = new JTextField();
        sliderRightBorder.setPreferredSize(new Dimension(300,50));
        sliderRightBorder.setBackground(BACKGROUND_COL);
        sliderRightBorder.setMajorTickSpacing(1);
        sliderRightBorder.setPaintTicks(true);
        sliderRightBorder.setPaintLabels(true);
        sliderRightBorder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider sourceRight = (JSlider) e.getSource();
                textFieldRightBorder.setText(sourceRight.getValue() + "\nсекунд");
                rightBorderSpeed = sourceRight.getValue();
                if (sourceRight.getValue() <= leftBorderSpeed){
                    nextFrame.setEnabled(false);
                    exceptionLabel.setVisible(true);
                }
                if (sourceRight.getValue() > leftBorderSpeed){
                    nextFrame.setEnabled(true);
                    exceptionLabel.setVisible(false);
                }
            }
        });

        content.add(sliderRightBorder, constraints);

        constraints.insets = new Insets(20, 500, 0, 0);

        constraints.gridy = 4;

        textFieldRightBorder.setText(sliderRightBorder.getValue() + "\nсекунд");
        textFieldRightBorder.setEditable(false);
        textFieldRightBorder.setPreferredSize(new Dimension(140,30));
        textFieldRightBorder.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldRightBorder.setFont(H2);
        textFieldRightBorder.setBackground(BTN_COL);

        content.add(textFieldRightBorder, constraints);

        JLabel bodyLabelLeftBorder = new JLabel("Левая граница");
        bodyLabelLeftBorder.setFont(H2);
        constraints.gridy = 1;
        constraints.insets = new Insets(70, 0, 0, 300);
        content.add(bodyLabelLeftBorder, constraints);

        constraints.insets = new Insets(50, 0, 0, 300);
        constraints.gridy = 3;

        final JSlider sliderLeftBorder = new JSlider(JSlider.HORIZONTAL, 1, 19, 1);
        final JTextField textFieldLeftBorder = new JTextField();
        sliderLeftBorder.setPreferredSize(new Dimension(300,50));
        sliderLeftBorder.setBackground(BACKGROUND_COL);
        sliderLeftBorder.setMajorTickSpacing(1);
        sliderLeftBorder.setPaintTicks(true);
        sliderLeftBorder.setPaintLabels(true);
        sliderLeftBorder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                sliderRightBorder.setValue(source.getValue()+1);
                textFieldLeftBorder.setText(source.getValue() + "\nсекунд");
                leftBorderSpeed = source.getValue();
            }
        });

        content.add(sliderLeftBorder, constraints);

        constraints.insets = new Insets(20, 0, 0, 300);

        constraints.gridy = 4;

        textFieldLeftBorder.setText(sliderLeftBorder.getValue() + "\nсекунд");
        textFieldLeftBorder.setEditable(false);
        textFieldLeftBorder.setPreferredSize(new Dimension(140,30));
        textFieldLeftBorder.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldLeftBorder.setFont(H2);
        textFieldLeftBorder.setBackground(BTN_COL);

        content.add(textFieldLeftBorder, constraints);

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(200, 150, 0, 0);
        content.add(exceptionLabel, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Настройка показательного закона распределения (рис. 28)
     * в переменную intensity записать интенсивность
     */
    private void initSpeedExponentialDistributionFrame(){
        final Container content = new Container();
        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JLabel headLabel = new JLabel("Показательный");
        headLabel.setFont(H1);

        content.add(headLabel, constraints);

        constraints.insets = new Insets(0, 150, 30, 0);

        constraints.gridy = 1;

        JLabel bodyLabel = new JLabel("Укажите значение интенсивности транспортного потока");
        bodyLabel.setFont(H2);

        content.add(bodyLabel, constraints);

        constraints.insets = new Insets(50, 150, 0, 0);

        constraints.gridy = 2;

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        Hashtable labelTable = new Hashtable();
        labelTable.put( 1, new JLabel("0.01") );
        labelTable.put(2, new JLabel("0.02") );
        labelTable.put(3, new JLabel("0.03") );
        labelTable.put(4, new JLabel("0.04") );
        labelTable.put(5, new JLabel("0.05") );
        labelTable.put(6, new JLabel("0.06") );
        labelTable.put(7, new JLabel("0.07") );
        labelTable.put(8, new JLabel("0.08") );
        labelTable.put(9, new JLabel("0.09") );
        labelTable.put(10, new JLabel("0.1") );
        slider.setLabelTable(labelTable);
        final JTextField textField = new JTextField();
        slider.setPreferredSize(new Dimension(400,50));
        slider.setBackground(BACKGROUND_COL);
        slider.setMajorTickSpacing(19);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        final HashMap<Integer, Double> intensityHM = new HashMap<Integer, Double>();
        intensityHM.put(1, 0.01);
        intensityHM.put(2, 0.02);
        intensityHM.put(3, 0.03);
        intensityHM.put(4, 0.04);
        intensityHM.put(5, 0.05);
        intensityHM.put(6, 0.06);
        intensityHM.put(7, 0.07);
        intensityHM.put(8, 0.08);
        intensityHM.put(9, 0.09);
        intensityHM.put(10, 0.1);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                tempIntensitySpeed = source.getValue();
                Double intensity = intensityHM.get(tempIntensitySpeed);
                textField.setText( intensity + "\nГц");
            }
        });

        content.add(slider, constraints);
        constraints.insets = new Insets(20, 150, 0, 0);
        constraints.gridy = 3;


        textField.setText("0,0" + slider.getValue() + "\nГц");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(140,30));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(H2);
        textField.setBackground(BTN_COL);

        content.add(textField, constraints);

        final JButton nextFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../next.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        final JButton prevFrame = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("../prev.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        ActionListener footerButtonsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(nextFrame)){
                    frame.remove(content);
                    if (roadMode == "tunnel"){
                        initSemaphoreFrame();
                    }
                    else initHighwayFrame();
                }else if(e.getSource().equals(prevFrame)){
                    frame.remove(content);
                    initSpeedRandomTrafficSettingsFrame();
                }
            }
        };

        nextFrame.setBorder(null);
        nextFrame.setBackground(BACKGROUND_COL);
        nextFrame.addActionListener(footerButtonsListener);

        prevFrame.setBorder(null);
        prevFrame.setBackground(BACKGROUND_COL);
        prevFrame.addActionListener(footerButtonsListener);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets = new Insets(320,0,0,760);

        content.add(prevFrame, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(320,100,0,0);
        content.add(nextFrame, constraints);

        frame.add(content);
        frame.setVisible(true);
        frame.repaint();
    }

    private void initTunnelFrame(){
        tunnel = new Tunnel(trafficLightPhase);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setSize(2560,600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Container mainContainer = new Container();
        mainContainer.setLayout(new GridBagLayout());
        GridBagConstraints mainConstrains = new GridBagConstraints();

        mainConstrains.insets = new Insets(100,0,0,150);
        mainConstrains.gridx = 0;
        mainConstrains.gridx = 1;
        mainConstrains.insets = new Insets(100,150,0,0);

//        mainConstrains.gridy = 1;
//        mainConstrains.gridx = 0;
//        mainConstrains.gridwidth = 2;
//        mainConstrains.insets = new Insets(50,0,900,0);
//        mainContainer.add(tunnel, mainConstrains);

        frame.add(mainContainer, BorderLayout.NORTH);

        frame.add(tunnel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);








        buttonContainer.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(20,0,50,100);
        buttonContainer.add(slider,constraints);
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(20,0,35,100);
        buttonContainer.add(startButton, constraints);
        constraints.insets = new Insets(20,50,35,50);
        buttonContainer.add(stopButton, constraints);
        buttonContainer.add(restartButton, constraints);
        constraints.insets = new Insets(20, 150, 35, 0);
        timerTextField.setEditable(false);
        timerTextField.setPreferredSize(new Dimension(70,30));
        timerTextField.setFont(H2);
        buttonContainer.add(timerTextField, constraints);

        frame.add(buttonContainer, BorderLayout.SOUTH);
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        restartButton.addActionListener(this);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBackground(BACKGROUND_COL);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                tunnel.changeAllCarSpeed(source.getValue()/10);
//                tunnel.changeHeadCarSpeed(source.getValue()/10);
            }
        });
        frame.repaint();
        frame.setVisible(true);
    }


    /**
     * initialization of highway frame
     */
    private void initHighwayFrame(){
        highway = new Highway(forwardLanes, backwardLanes);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(1500,(forwardLanes + backwardLanes) * Highway.LANE_HEIGHT + 100);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(highway, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        buttonContainer.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0,0,15,100);
        buttonContainer.add(startButton, constraints);
        constraints.insets = new Insets(0,50,15,50);
        buttonContainer.add(stopButton, constraints);
        buttonContainer.add(restartButton, constraints);
        constraints.insets = new Insets(0, 150, 15, 0);
        timerTextField.setEditable(false);
        timerTextField.setPreferredSize(new Dimension(70,30));
        timerTextField.setFont(H2);
        buttonContainer.add(timerTextField, constraints);
        frame.add(buttonContainer, BorderLayout.SOUTH);
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        restartButton.addActionListener(this);
        frame.repaint();
    }

    private void setImages(){
        for(CarsFWD carsFWD : CarsFWD.values()){
            carsFWD.image = getImage(carsFWD.name().toLowerCase());
        }
        for(CarsBWD carsBWD : CarsBWD.values()){
            carsBWD.image = getImage(carsBWD.name().toLowerCase());
        }
    }

    private Image getImage(String name){
        String fileName = "../" + name + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(fileName));
        return icon.getImage();
    }
    public static void main(String[] args) {
        // write your code here
        new TrafficSimulator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(startButton)){
            if(!running) {
                running = true;
                Thread stepsThread = new Thread(this);
                stepsThread.start();
                Thread generationThread = new Thread(new RandomGenerator(trafficLightPhase, roadMode));
                generationThread.start();
                startTimer(timerTextField);
            }
        }
        if(e.getSource().equals(stopButton)){
            if(running){
                running = false;
            }
        }if(e.getSource().equals(restartButton)){
            running = false;
            if(roadMode.equals("highway")){
                frame.remove(highway);
            }if(roadMode.equals("tunnel")){
                frame.remove(tunnel);
            }
            frame.remove(buttonContainer);
            initStartFrame();
        }
    }

    @Override
    public void run() {
        while(running){
            if(roadMode.equals("highway")) {
                highway.step();
            }else{
                tunnel.step();
            }
            frame.repaint();
            try{
                Thread.sleep(50);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void startTimer(final JTextField textField){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(running){
                    String pattern = "mm:ss";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                    String startTime = textField.getText();
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(dateFormat.parse(startTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.SECOND, 1);

                    textField.setText(dateFormat.format(c.getTime()));
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    private static class RoundedBorder implements Border {

        private int radius;


        RoundedBorder(int radius) {
            this.radius = radius;
        }


        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }


        public boolean isBorderOpaque() {
            return true;
        }


        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }


}
