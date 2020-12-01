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

public class TrafficSimulator implements ActionListener, Runnable {

    /**
     * Константы графического интерфейса
     */
    private static final Font H1 = new Font("Verdana", Font.PLAIN, 26);
    private static final Font H2 = new Font("Verdana", Font.PLAIN, 20);
    private static final Color BACKGROUND_COL = new Color(189, 189, 189);
    private static final Color BTN_HOVER = new Color(210, 210, 210);;
    private static final Color BTN_COL = new Color(150, 150, 150);
//    private static final Color BTN_SELECTED = new Color(146, 146, 146);
//    private static final Color BACKGROUND_COL = new Color(224, 236, 236);
//    private static final Color BTN_SELECTED = new Color(219, 253, 255);

    public static JFrame frame = new JFrame("Traffic Simulator");



    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JSlider slider = new JSlider(JSlider.HORIZONTAL, 10, 70, 40);
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
    public static int determInterval = 10;
    private static int trafficLightPhase = 5000;
    public static String randomType = "normal";
    public static int mo = 10;
    public static int d = 10;
    public static int leftBorder = 1;
    public static int rightBorder = 2;
    public static double intensity = 0.01;

    public static Highway highway;
    public static Tunnel tunnel;

    public TrafficSimulator() {
        setImages();
        initStartFrame();
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

        frame.add(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    }

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
                    if(roadMode.equals("highway")) {
                        initHighwayFrame();
                    }else{
                        initTunnelFrame();
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
     * Выбор закона распределения (рис. 25)
     * в переменную randomType записать "normal", "uniform", "exponential" в зависимости от выбора
     */
    private void initRandomTrafficSettingsFrame(){

    }

    /**
     * Настройка нормального закона распределения (рис. 26)
     * в переменную mo записать мат ожидание в переменную d дисперсию
     */
    private void initNormalDistributionFrame(){

    }


    /**
     * Настройка равномерного закона распределения (рис. 27)
     * в переменную leftBorder записать левую границу, в переменную rightBorder правую границу
     */
    private void initUniformDistributionFrame(){

    }

    /**
     * Настройка показательного закона распределения (рис. 28)
     * в переменную intensity записать интенсивность
     */
    private void initExponentialDistributionFrame(){

    }

    private void initTunnelFrame(){
        tunnel = new Tunnel(trafficLightPhase);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setSize(2560,600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(tunnel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        bottomContainer.setLayout(new GridLayout(2,1));
        buttonContainer.setLayout(new GridLayout(1,2));
        bottomContainer.add(slider);
        bottomContainer.add(buttonContainer);
        buttonContainer.add(startButton);
        buttonContainer.add(stopButton);
        frame.add(bottomContainer, BorderLayout.SOUTH);
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                System.out.println(source.getValue());
            }
        });
        frame.repaint();
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

        buttonContainer.setLayout(new GridLayout(1,2));
        buttonContainer.add(startButton);
        buttonContainer.add(stopButton);
        frame.add(buttonContainer, BorderLayout.SOUTH);
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
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
            }
        }
        if(e.getSource().equals(stopButton)){
            if(running){
                running = false;
            }
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
